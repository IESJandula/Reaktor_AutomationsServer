package es.iesjandula.reaktor.automations_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.automations_server.models.Orden;

/**
 * Repositorio JPA para la entidad Orden.
 */
public interface IOrdenRepository extends JpaRepository<Orden, Long>
{

}
