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

			    (
			        (
			            CASE 
			                WHEN LOWER(:input) LIKE CONCAT('%', SUBSTRING_INDEX(ca.keyword,' ',1), '%')
			                THEN 1 ELSE 0
			            END
			            +
			            CASE
			                WHEN LOWER(:input) LIKE CONCAT('%', SUBSTRING_INDEX(ca.keyword,' ',-1), '%')
			                OR LOWER(:input) LIKE CONCAT('%', REPLACE(SUBSTRING_INDEX(ca.keyword,' ',-1),'.',' '), '%')
			                THEN 1 ELSE 0
			            END
			        ) / 2
			    ) * 100 AS porcentaje

			FROM comando_actuador ca
			ORDER BY porcentaje DESC
			LIMIT 1
			""", nativeQuery = true)

			List<Object[]> buscarMejorComando(@Param("input") String input);
}