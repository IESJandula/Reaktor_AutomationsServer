package es.iesjandula.reaktor.automations_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.automations_server.models.OrdenProgramada;

/**
 * Repositorio JPA para la entidad OrdenProgramada.
 */
public interface IOrdenProgramadaRepository extends JpaRepository<OrdenProgramada, Long>
{

}
