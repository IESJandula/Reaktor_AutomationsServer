package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.ValidacionResponseDto;
import es.iesjandula.reaktor.automations_server.models.Validacion;

/**
 * Repositorio JPA para la entidad Validacion.
 */
public interface IValidacionRepository extends JpaRepository<Validacion, Long>
{

    /**
     * Devuelve una lista de validaciones usando un DTO.
     */
    @Query("SELECT new es.iesjandula.reaktor.automations_school_server.dtos.ValidacionResponseDto(" +
           "v.id, v.score, v.resultado, v.motivoRechazo, v.orden.id) " +
           "FROM Validacion v")
    List<ValidacionResponseDto> buscarValidaciones();
}
