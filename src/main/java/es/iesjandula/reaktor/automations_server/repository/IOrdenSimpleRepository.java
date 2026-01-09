package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.OrdenSimpleResponseDto;
import es.iesjandula.reaktor.automations_server.models.OrdenSimple;
/**
 * Repositorio JPA para la entidad OrdenSimple.
 */
public interface IOrdenSimpleRepository extends JpaRepository<OrdenSimple, Long>
{
    /**
     * Devuelve una lista de órdenes simples usando un DTO.
     */
    @Query("SELECT new es.iesjandula.reaktor.automations_school_server.dtos.OrdenSimpleResponseDto(" +
            "o.id, o.fecha, o.frase) " +
            "FROM OrdenSimple o")
     List<OrdenSimpleResponseDto> buscarOrdenesSimples();
}
