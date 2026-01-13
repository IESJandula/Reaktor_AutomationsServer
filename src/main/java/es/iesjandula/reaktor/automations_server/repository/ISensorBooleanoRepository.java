package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorNumericoResponseDto;
import es.iesjandula.reaktor.automations_server.models.SensorBooleano;

/**
 * Repositorio JPA para la entidad SensorBooleano.
 */
public interface ISensorBooleanoRepository extends JpaRepository<SensorBooleano, String> {
	/*
	 * Consulta personalizada definida mediante para recuperar datos específicos.
	 * Utiliza un constructor de DTO para seleccionar solo los campos necesarios y
	 * evitar cargar entidades completas.
	 */
	@Query("""
			    SELECT new es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoResponseDto(
			        s.mac,
			        s.estado,
			        s.valorActual,
			        s.ultimaActualizacion,
			        s.ubicacion.nombreUbicacion,
			        s.umbralMinimo,
			        s.umbralMaximo
			    )
			    FROM SensorBooleano s
			""")
	List<SensorBooleanoResponseDto> buscarSensoresBooleanos();

	@Query("""
		    SELECT s.ubicacion.nombreUbicacion, 
		           new es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoResponseDto(s.mac,
		        s.estado,
		        s.ubicacion.nombreUbicacion,
		        s.ultimaActualizacion,
		        s.valorActual,
		        s.umbralMinimo,
		        s.umbralMaximo
		    )
		    FROM SensorBooleano s
		    GROUP BY s.ubicacion.nombreUbicacion
		""")
	Map<String, List<SensorBooleanoResponseDto>> buscarSensoresBooleanosPorUbicacion();

}
