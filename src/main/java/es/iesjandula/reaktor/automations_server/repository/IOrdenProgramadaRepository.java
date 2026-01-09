package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.OrdenProgramadaResponseDto;
import es.iesjandula.reaktor.automations_server.models.OrdenProgramada;

/**
 * Repositorio JPA para la entidad OrdenProgramada.
 */
public interface IOrdenProgramadaRepository extends JpaRepository<OrdenProgramada, Long>
{
    /**
     * Devuelve una lista de órdenes programadas usando un DTO.
     */
    @Query("SELECT new es.iesjandula.reaktor.automations_server.dtos.OrdenProgramadaResponseDto(" +
            "o.id, o.fecha, o.frase, o.fechaProgramada, o.repeticion) " +
            "FROM OrdenProgramada o")
     List<OrdenProgramadaResponseDto> buscarOrdenesProgramadas();
}
