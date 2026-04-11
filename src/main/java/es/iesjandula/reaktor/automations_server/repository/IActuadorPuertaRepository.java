package es.iesjandula.reaktor.automations_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.automations_server.models.ActuadorPuerta;

/**
 * Repositorio JPA para la entidad ActuadorPuerta.
 */
public interface IActuadorPuertaRepository extends JpaRepository<ActuadorPuerta, String>
{
}