package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class ComandoActuadorCustomRepositoryImpl
        implements ComandoActuadorCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> rankPorTokens(List<String> tokens) {

        if (tokens == null || tokens.isEmpty())
            return List.of();

        StringBuilder scoreBuilder = new StringBuilder();

        for (int i = 0; i < tokens.size(); i++) {

            scoreBuilder.append(
                "(CASE WHEN LOWER(keyword) LIKE LOWER(:t" + i + ") THEN 1 ELSE 0 END)"
            );

            if (i < tokens.size() - 1)
                scoreBuilder.append(" + ");
        }

        String sql =
                "SELECT mac, keyword, texto_ok, (" +
                scoreBuilder +
                ") / " + tokens.size() + " AS score " +
                "FROM comando_actuador " +
                "ORDER BY score DESC " +
                "LIMIT 1";

        Query query = entityManager.createNativeQuery(sql);

        for (int i = 0; i < tokens.size(); i++) {
            query.setParameter("t" + i, "%" + tokens.get(i) + "%");
        }

        return query.getResultList();
    }
}