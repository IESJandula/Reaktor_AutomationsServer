package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.ComandoResponseDto;
import es.iesjandula.reaktor.automations_server.models.Comando;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoId;

public interface IComandoRepository extends JpaRepository<Comando, ComandoId>
{
	@Query("""
		select new es.iesjandula.reaktor.automations_server.dtos.ComandoResponseDto(
			c.comandoId.comandoActuadorId.keyword,
			c.comandoId.comandoActuadorId.mac,
			c.comandoId.ordenId
		)
		from Comando c
	""")
	List<ComandoResponseDto> buscarComandos();

	@Query("""
		select new es.iesjandula.reaktor.automations_server.dtos.ComandoResponseDto(
			c.comandoId.comandoActuadorId.keyword,
			c.comandoId.comandoActuadorId.mac,
			c.comandoId.ordenId
		)
		from Comando c
		where c.comandoId.ordenId = :ordenId
	""")
	List<ComandoResponseDto> buscarComandosPorOrden(Long ordenId);

	@Query("""
		select new es.iesjandula.reaktor.automations_server.dtos.ComandoResponseDto(
			c.comandoId.comandoActuadorId.keyword,
			c.comandoId.comandoActuadorId.mac,
			c.comandoId.ordenId
		)
		from Comando c
		where c.comandoId.comandoActuadorId.mac = :mac
	""")
	List<ComandoResponseDto> buscarComandosPorMac(String mac);
}