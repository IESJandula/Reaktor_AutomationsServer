package es.iesjandula.reaktor.automations_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.automations_server.models.ActuadorProyector;

/**
 * Repositorio JPA para la entidad ActuadorProyector.
 */
public interface IActuadorProyectorRepository extends JpaRepository<ActuadorProyector, String>
{
	
}