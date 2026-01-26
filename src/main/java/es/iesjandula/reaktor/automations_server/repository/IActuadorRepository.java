package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto;
import es.iesjandula.reaktor.automations_server.models.Actuador;

/**
 * Repositorio JPA para la entidad Actuador.
 */
public interface IActuadorRepository extends JpaRepository<Actuador, String> {
	/**
	 * Devuelve una lista de actuadores usando un DTO con su MAC, estado y
	 * ubicación.
	 */
	@Query("SELECT new es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto("
			+ "a.mac, a.estado, a.ubicacion.nombreUbicacion, a.aplicabilidad) " + "FROM Actuador a")
	List<ActuadorResponseDto> buscarActuadores();

	@Query("""
		    SELECT new es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto(
		        a.mac,
		        a.estado,
		        a.ubicacion.nombreUbicacion,
		        a.aplicabilidad
		    )
		    FROM Actuador a
		""")
		List<ActuadorResponseDto> buscarActuadoresPorUbicacion();

}
