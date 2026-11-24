package clinica.cobros;

import clinica.expediente.Consulta;
import clinica.database.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Reglas de registro y consulta de cobros. La restricción UNIQUE de
 * cobros.consulta_id en PostgreSQL es la protección definitiva contra duplicados. */
public class CobrosService {

    public void registrarCobroDeCita(int citaId, String concepto, BigDecimal importe) {
        if (citaId <= 0) throw new IllegalArgumentException("La cita no es válida.");
        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException("El concepto es obligatorio.");
        }
        if (concepto.trim().length() > 120) {
            throw new IllegalArgumentException("El concepto no puede superar 120 caracteres.");
        }
        if (importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor que cero.");
        }

        if (importe.stripTrailingZeros().scale() > 2 || importe.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("El importe debe tener hasta dos decimales y un máximo de 99999999.99.");
        }

        try (EntityManager session = JPAUtil.getEntityManager()) {
            EntityTransaction transaction = session.getTransaction();
            transaction.begin();
            try {
                Consulta consulta = session.createQuery(
                                "from Consulta c where c.citaId = :citaId", Consulta.class)
                        .setParameter("citaId", citaId)
                        .getSingleResultOrNull();
                if (consulta == null) {
                    throw new IllegalStateException("No se puede cobrar: la cita todavía no tiene una atención guardada.");
                }

                Cobro existente = session.createQuery(
                                "from Cobro c where c.consultaId = :consultaId", Cobro.class)
                        .setParameter("consultaId", consulta.getId())
                        .getSingleResultOrNull();
                if (existente != null) {
                    throw new IllegalStateException("Esta consulta ya tiene un cobro registrado.");
                }

                Cobro cobro = new Cobro();
                cobro.setConsultaId(consulta.getId());
                cobro.setConcepto(concepto.trim());
                cobro.setImporte(importe);
                cobro.setRegistradoEn(LocalDateTime.now());
                session.persist(cobro);
                session.flush();
                transaction.commit();
            } catch (PersistenceException ex) {
                if (transaction.isActive()) transaction.rollback();
                for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
                    if (causa instanceof java.sql.SQLException sql && "23505".equals(sql.getSQLState())) {
                        throw new IllegalStateException("Esta consulta ya tiene un cobro registrado.", ex);
                    }
                }
                throw ex;
            } catch (RuntimeException ex) {
                if (transaction.isActive()) transaction.rollback();
                throw ex;
            }
        }
    }

    public List<CobroDiarioItem> listarCobrosDelDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        try (EntityManager session = JPAUtil.getEntityManager()) {
            List<Cobro> cobros = session.createQuery(
                            "from Cobro c where c.registradoEn >= :inicio " +
                                    "and c.registradoEn < :fin order by c.registradoEn, c.id",
                            Cobro.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getResultList();

            List<CobroDiarioItem> filas = new ArrayList<>();
            for (Cobro cobro : cobros) {
                filas.add(new CobroDiarioItem(cobro.getRegistradoEn(),
                        cobro.getConcepto(), cobro.getImporte()));
            }
            return filas;
        }
    }

    public BigDecimal calcularTotalDelDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        try (EntityManager session = JPAUtil.getEntityManager()) {
            BigDecimal total = session.createQuery(
                            "select sum(c.importe) from Cobro c " +
                                    "where c.registradoEn >= :inicio and c.registradoEn < :fin",
                            BigDecimal.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getSingleResultOrNull();
            return total == null ? BigDecimal.ZERO : total;
        }
    }
}
