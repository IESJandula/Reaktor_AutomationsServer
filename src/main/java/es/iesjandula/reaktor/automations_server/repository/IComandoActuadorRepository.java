package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}