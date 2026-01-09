package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.UbicacionResponseDto;
import es.iesjandula.reaktor.automations_server.models.Ubicacion;

/**
 * Repositorio JPA para la entidad Ubicacion.
 */
public interface IUbicacionRepository extends JpaRepository<Ubicacion, String>
{
	/**
	 * Devuelve una lista de ubicaciones usando un DTO.
	 */
	@Query("SELECT new es.iesjandula.reaktor.automations_school_server.dtos.UbicacionResponseDto(u.nombreUbicacion) "
			+ "FROM Ubicacion u")
	List<UbicacionResponseDto> buscarUbicaciones();
}
