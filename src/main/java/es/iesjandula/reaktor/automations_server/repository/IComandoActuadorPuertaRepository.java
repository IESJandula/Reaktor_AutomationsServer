package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.ComandoActuadorPuertaResponseDto;
import es.iesjandula.reaktor.automations_server.models.ComandoActuadorPuerta;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorPuertaId;

/**
 * Repositorio JPA para la entidad ComandoActuadorPuerta.
 */
public interface IComandoActuadorPuertaRepository extends JpaRepository<ComandoActuadorPuerta, ComandoActuadorPuertaId>
{
	@Query("""
			SELECT new es.iesjandula.reaktor.automations_server.dtos.ComandoActuadorPuertaResponseDto(
				cap.comandoActuadorPuertaId.comandoActuadorMac,
				cap.comandoActuadorPuertaId.comandoActuadorKeyword,
				cap.comandoActuadorPuertaId.indiceRele
			)
			FROM ComandoActuadorPuerta cap
		   """)
	List<ComandoActuadorPuertaResponseDto> buscarComandosActuadorPuerta();
}