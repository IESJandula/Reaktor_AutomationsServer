package es.iesjandula.reaktor.automations_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.automations_server.models.Dispositivo;


/**
 * Repositorio JPA para la entidad Dispositivo.
 */
public interface IDispositivoRepository extends JpaRepository<Dispositivo, String>
{

}
