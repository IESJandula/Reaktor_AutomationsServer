package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.AccionResponseDto;
import es.iesjandula.reaktor.automations_server.models.Accion;

/**
 * Repositorio JPA para la entidad Accion.
 */
public interface IAccionRepository extends JpaRepository<Accion, Long>
{
    /**
     * Obtiene una lista de acciones usando un DTO,
     * evitando devolver la entidad completa.
     */
    @Query("SELECT new es.iesjandula.reaktor.automations_server.dtos.AccionResponseDto(" +
            "a.id, a.resultado, a.actuador.mac, a.orden.id) " +
            "FROM Accion a")
     List<AccionResponseDto> buscarAcciones();
}
