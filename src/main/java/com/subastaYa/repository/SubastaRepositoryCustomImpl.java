package com.subastaYa.repository;

import com.subastaYa.entity.Subasta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubastaRepositoryCustomImpl implements SubastaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Subasta> buscarConFiltros(String keyword, Long categoriaId, String estado) {
        StringBuilder jpql = new StringBuilder(
            "SELECT s FROM Subasta s WHERE 1=1"
        );

        if (keyword != null && !keyword.isEmpty()) {
            jpql.append(" AND LOWER(s.titulo) LIKE LOWER(CONCAT('%', :keyword, '%'))");
        }
        if (categoriaId != null) {
            jpql.append(" AND s.categoria.id = :categoriaId");
        }
        if (estado != null && !estado.isEmpty()) {
            jpql.append(" AND s.estado = :estado");
        }
        jpql.append(" ORDER BY s.fechaCreacion DESC");

        TypedQuery<Subasta> query = entityManager.createQuery(jpql.toString(), Subasta.class);

        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("keyword", keyword);
        }
        if (categoriaId != null) {
            query.setParameter("categoriaId", categoriaId);
        }
        if (estado != null && !estado.isEmpty()) {
            query.setParameter("estado", 
                com.subastaYa.entity.EstadoSubasta.valueOf(estado));
        }

        return query.getResultList();
    }

    @Override
    public List<Subasta> findTopSubastasPorPujas(int limit) {
        String jpql = """
            SELECT s FROM Subasta s
            WHERE s.estado = 'ACTIVA'
            ORDER BY SIZE(s.pujas) DESC
        """;

        return entityManager.createQuery(jpql, Subasta.class)
                .setMaxResults(limit)
                .getResultList();
    }
}