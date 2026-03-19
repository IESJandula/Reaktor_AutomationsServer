package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.iesjandula.reaktor.automations_server.dtos.AccionResponseDto;
import es.iesjandula.reaktor.automations_server.models.Accion;
import es.iesjandula.reaktor.automations_server.models.Actuador;

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
		       "a.id, a.estado, a.resultado, a.actuador.mac, a.orden.id) " +
		       "FROM Accion a")
		List<AccionResponseDto> buscarAcciones();
	
	/**
	 * Busca las últimas acciones pendientes a realizar para un actuador.
	 * @param actuador Actuador para el cual se buscan las acciones pendientes.
	 * @return Lista de acciones pendientes a realizar para el actuador.
	 */
	@Query("""
			SELECT a 
			FROM Accion a 
			WHERE a.actuador = :actuador AND 
				  a.estado = es.iesjandula.reaktor.automations_server.utils.Constants.ESTADO_ACCION_PENDIENTE
		   """)
	Optional<List<Accion>> buscarUltimasAccionesPendientes(Actuador actuador);
	
    /**
     * Obtiene una lista de acciones usando un DTO,
     * evitando devolver la entidad completa por paginas.
     */
    @Query("SELECT new es.iesjandula.reaktor.automations_server.dtos.AccionResponseDto(" +
            "a.id, a.estado, a.resultado, a.actuador.mac, a.orden.id) " +
            "FROM Accion a")
     Page<AccionResponseDto> buscarAccionesPagina(Pageable pageable);
}
