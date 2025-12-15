package es.iesjandula.reaktor.automations_school_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_school_server.dtos.SensorBooleanoResponseDto;
import es.iesjandula.reaktor.automations_school_server.models.SensorBooleano;

/**
 * Repositorio JPA para la entidad SensorBooleano.
 */
public interface ISensorBooleanoRepository extends JpaRepository<SensorBooleano, String>
{
	@Query("""
		    SELECT new es.iesjandula.reaktor.automations_school_server.dtos.SensorBooleanoResponseDto(
		        s.mac,
		        s.estado,
		        s.valorActual,
		        s.ultimaActualizacion,
		        s.ubicacion.nombreUbicacion
		    )
		    FROM SensorBooleano s
		""")
		List<SensorBooleanoResponseDto> buscarSensoresBooleanos();
}
