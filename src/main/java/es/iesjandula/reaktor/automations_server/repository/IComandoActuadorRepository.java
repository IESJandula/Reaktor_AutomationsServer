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

			-- Seleccionamos la keyword del comando configurado
			SELECT
			    ca.keyword,

			    -- MAC del actuador que ejecutará la acción
			    ca.mac,

			    -- Texto de respuesta que se devolverá si la orden es aceptada
			    ca.texto_ok,

			    -- Contamos cuántas palabras de la keyword coinciden con el input del usuario
			    SUM(
			        CASE 
			            WHEN (
			                    -- Comprobamos si el input contiene la palabra de la keyword
			                    LOWER(:input) LIKE CONCAT('%', LOWER(kw.palabra), '%')

			                    -- Caso especial para aulas tipo 2.17
			                    OR (
			                        kw.palabra LIKE '%.%' 

			                        -- Permite que "2 17" coincida con "2.17"
			                        AND LOWER(:input) LIKE CONCAT('%', REPLACE(LOWER(kw.palabra),'.',' '), '%')
			                    )
			                 )
			            THEN 1   -- si coincide sumamos 1
			            ELSE 0   -- si no coincide sumamos 0
			        END
			    ) AS coincidencias,

			    -- Calculamos el porcentaje de coincidencia
			    (
			        SUM(
			            CASE 
			                WHEN (
			                        LOWER(:input) LIKE CONCAT('%', LOWER(kw.palabra), '%')
			                        OR (
			                            kw.palabra LIKE '%.%' 
			                            AND LOWER(:input) LIKE CONCAT('%', REPLACE(LOWER(kw.palabra),'.',' '), '%')
			                        )
			                     )
			                THEN 1
			                ELSE 0
			            END
			        )
			        /
			        -- Número total de palabras que tiene la keyword
			        COUNT(kw.palabra)
			    ) * 100 AS porcentaje

			-- Tabla donde están configurados los comandos
			FROM comando_actuador ca

			-- JSON_TABLE convierte la keyword en una lista de palabras
			-- ejemplo: "abre 2.17" → ["abre","2.17"]
			JOIN JSON_TABLE(

			    -- Convertimos el texto en formato JSON
			    CONCAT('["', REPLACE(LOWER(ca.keyword), ' ', '","'), '"]'),

			    -- Recorremos cada palabra del JSON
			    '$[*]' COLUMNS (

			        -- cada elemento se guarda como "palabra"
			        palabra VARCHAR(100) PATH '$'
			    )

			) kw

			-- Agrupamos resultados por comando
			GROUP BY ca.keyword, ca.mac, ca.texto_ok

			-- Ordenamos por mayor porcentaje de coincidencia
			ORDER BY porcentaje DESC

			-- Nos quedamos con el mejor resultado
			LIMIT 1

			""", nativeQuery = true)
			List<Object[]> buscarMejorComando(@Param("input") String input);
}