package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.iesjandula.reaktor.automations_server.dtos.ComandoActuadorResponseDto;
import es.iesjandula.reaktor.automations_server.models.ComandoActuador;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorId;

public interface IComandoActuadorRepository extends JpaRepository<ComandoActuador, ComandoActuadorId>
{
	@Query("""
		select new es.iesjandula.reaktor.automations_server.dtos.ComandoActuadorResponseDto(
			c.comandoActuadorId.keyword,
			c.comandos,
			c.comandoActuadorId.mac,
			c.textoOk
		)
		from ComandoActuador c
	""")
	List<ComandoActuadorResponseDto> buscarComandosActuador();
	
	@Query(value = """
			SELECT
			    ca.keyword,
			    ca.mac,
			    ca.texto_ok,

			    SUM(
			        CASE 
			            WHEN (
			                    LOWER(:input) LIKE CONCAT('%', kw.palabra, '%')
			                    OR (
			                        kw.palabra LIKE '%.%' 
			                        AND LOWER(:input) LIKE CONCAT('%', REPLACE(kw.palabra,'.',' '), '%')
			                    )
			                 )
			            THEN 1 
			            ELSE 0 
			        END
			    ) AS coincidencias,

			    (
			        SUM(
			            CASE 
			                WHEN (
			                        LOWER(:input) LIKE CONCAT('%', kw.palabra, '%')
			                        OR (
			                            kw.palabra LIKE '%.%' 
			                            AND LOWER(:input) LIKE CONCAT('%', REPLACE(kw.palabra,'.',' '), '%')
			                        )
			                     )
			                THEN 1 
			                ELSE 0 
			            END
			        )
			        /
			        COUNT(kw.palabra)
			    ) * 100 AS porcentaje

			FROM comando_actuador ca

			JOIN JSON_TABLE(
			    CONCAT('["', REPLACE(ca.keyword, ' ', '","'), '"]'),
			    '$[*]' COLUMNS (
			        palabra VARCHAR(100) PATH '$'
			    )
			) kw

			GROUP BY ca.keyword, ca.mac, ca.texto_ok
			ORDER BY porcentaje DESC
			LIMIT 1
			""", nativeQuery = true)
			List<Object[]> buscarMejorComando(@Param("input") String input);
}