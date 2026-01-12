package es.iesjandula.reaktor.automations_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.automations_server.models.Dispositivo;


/**
 * Repositorio JPA para la entidad Dispositivo.
 */
public interface IDispositivoRepository extends JpaRepository<Dispositivo, String>
{


//	@Query("""
//			    SELECT new es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoResponseDto(
//			           s.ubicacion.nombreUbicacion,
//			           new es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoResponseDto(
//			        s.mac,
//			        s.estado,
//			        s.valorActual,
//			        s.ultimaActualizacion,
//			        s.umbralMinimo,
//			        s.umbralMaximo
//			    )
//			    FROM Dispositivo d
//			    WHERE d.ubicacion.nombreUbicacion = :nombreUbicacion
//			    GROUP BY d.ubicacion.nombreUbicacion
//			""")
//	List<SensorBooleanoResponseDto> buscarDispositivos();

}
