package es.iesjandula.reaktor.automations_server.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto;
import es.iesjandula.reaktor.automations_server.models.Actuador;

/**
 * Repositorio JPA para la entidad Actuador.
 */
public interface IActuadorRepository extends JpaRepository<Actuador, String>
{
	/**
	 * Devuelve una lista de actuadores usando un DTO con su MAC, estado,
	 * ubicación, tipo y número de relés si es puerta.
	 */
	@Query("""
		    SELECT new es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto(
		        a.mac,
		        a.estado,
		        a.nombreUbicacion,
		        a.tipo,
		        ap.numeroReles
		    )
		    FROM Actuador a
		    LEFT JOIN ActuadorPuerta ap ON a.mac = ap.mac
		""")
	List<ActuadorResponseDto> buscarActuadores();

	List<Actuador> findByEstadoAndUltimaActualizacionBefore(String estado, Date fecha);
	
	@Query("""
		    SELECT new es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto(
		        a.mac,
		        a.estado,
		        a.nombreUbicacion,
		        a.tipo,
		        ap.numeroReles
		    )
		    FROM Actuador a
		    LEFT JOIN ActuadorPuerta ap ON a.mac = ap.mac
		""")
	Page<ActuadorResponseDto> buscarActuadoresPagina(Pageable pageable);
}