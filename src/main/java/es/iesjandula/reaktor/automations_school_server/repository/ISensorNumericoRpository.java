package es.iesjandula.reaktor.automations_school_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_school_server.dtos.SensorNumericoResponseDto;
import es.iesjandula.reaktor.automations_school_server.models.SensorNumerico;

/**
 * Repositorio JPA para la entidad SensorNumerico.
 */
public interface ISensorNumericoRpository extends JpaRepository<SensorNumerico, String>
{
	@Query("""
		    SELECT new es.iesjandula.reaktor.automations_school_server.dtos.SensorNumericoResponseDto(
		        s.mac,
		        s.estado,
		        s.valorActual,
		        s.umbralMinimo,
		        s.umbralMaximo,
		        s.ultimaActualizacion,
		        s.ubicacion.nombreUbicacion
		    )
		    FROM SensorNumerico s
		""")
		List<SensorNumericoResponseDto> buscarSensoresNumericos();
}
