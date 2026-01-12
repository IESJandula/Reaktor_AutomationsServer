package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.SensorNumericoResponseDto;
import es.iesjandula.reaktor.automations_server.models.SensorNumerico;

/**
 * Repositorio JPA para la entidad SensorNumerico.
 */
public interface ISensorNumericoRpository extends JpaRepository<SensorNumerico, String> {
	/**
	 * Consulta personalizada definida utilizando JPQL para seleccionar datos
	 * específicos y mapearlos directamente a objetos DTO mediante su constructor.
	 */
	@Query("""
			    SELECT new es.iesjandula.reaktor.automations_server.dtos.SensorNumericoResponseDto(
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

//	@Query("""
//			    SELECT new es.iesjandula.reaktor.automations_server.dtos.SensorNumericoResponseDto(
//			        s.mac,
//			        s.estado,
//			        s.ubicacion.nombreUbicacion,
//			        s.ultimaActualizacion,
//			        s.valorActual,
//			        s.umbralMinimo,
//			        s.umbralMaximo
//			    )
//			    FROM SensorNumerico s
//			    WHERE s.ubicacion.nombreUbicacion = :nombreUbicacion
//			""")
//	List<SensorNumericoResponseDto> buscarSensoresNumericosPorUbicacion(@Param("nombreUbicacion") String nombreUbicacion);

}
