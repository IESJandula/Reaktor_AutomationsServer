package es.iesjandula.reaktor.automations_school_server.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_school_server.models.SensorBooleano;
import es.iesjandula.reaktor.automations_school_server.models.SensorNumerico;

import es.iesjandula.reaktor.automations_school_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_school_server.repository.ISensorNumericoRpository;

import es.iesjandula.reaktor.automations_school_server.utils.AutomationSchoolServerException;
import es.iesjandula.reaktor.automations_school_server.utils.Constants;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST dedicado a recibir y procesar actualizaciones de datos
 * enviadas por los dispositivos SensorBooleano y SensorNumerico.
 */
@Slf4j
@RestController
@RequestMapping("/automations_school/admin/actualizacion")
public class ActualizacionesSensoresRestController
{
	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;

	// ----------------------------------------------------------------------------------
	// --- ENDPOINT PARA ACTUALIZAR SENSOR BOOLEANO ---
	// ----------------------------------------------------------------------------------

	/**
	 * Endpoint para actualizar el valor actual de un Sensor Booleano.
	 * Los datos (valorActual y mac) se esperan en las cabeceras de la petición.
	 * @param valorActual Nuevo valor booleano del sensor, obtenido de la cabecera.
	 * @param mac MAC del sensor a actualizar, obtenido como parámetro de la URL o cabecera (depende de la configuración, aquí se asume que se pasa implícitamente o es parte de la cabecera/cuerpo).
	 * @return ResponseEntity 200 (OK) o 400 (Bad Request) si el sensor no existe.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')") // Solo usuarios con rol ADMINISTRADOR pueden actualizar
	@PostMapping(value = "/sensor/booleano")
	public ResponseEntity<?> actualizarSensorBooleano(@RequestHeader Boolean valorActual, String mac)
	{
		try
		{
			// 1. Buscar el sensor por su MAC
			Optional<SensorBooleano> optionalSensorBooleano = sensorBooleanoRepo.findById(mac);
			
			// 2. Validación: Si el sensor no existe, lanzar excepción
			if (!optionalSensorBooleano.isPresent())
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			// 3. Actualizar el valor y la fecha (aunque no está explícito, el save() puede actualizar el campo ultimaActualizacion si está configurado en el modelo Sensor)
			SensorBooleano sensorBooleano = optionalSensorBooleano.get();
			sensorBooleano.setValorActual(valorActual); // Establece el nuevo valor reportado

			// 4. Guardar los cambios
			sensorBooleanoRepo.saveAndFlush(sensorBooleano);

			log.info(Constants.ELEMENTO_MODIFICADO);

			return ResponseEntity.ok().build(); // Devuelve 200 OK

		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			// Manejo de errores controlados (negocio)
			return ResponseEntity.badRequest().body(automationSchoolServerException); // Devuelve 400 Bad Request
		}
		catch (Exception exception) 
		{
			// Manejo de errores inesperados (sistema)
			log.error("Internal Server Error");
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			return ResponseEntity.status(500).body(automationSchoolServerException); // Devuelve 500 Internal Server Error
		}
		
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINT PARA ACTUALIZAR SENSOR NUMÉRICO ---
	// ----------------------------------------------------------------------------------

	/**
	 * Endpoint para actualizar el valor actual de un Sensor Numérico.
	 * Los datos (valorActual y mac) se esperan en las cabeceras de la petición.
	 * @param valorActual Nuevo valor numérico del sensor, obtenido de la cabecera.
	 * @param mac MAC del sensor a actualizar.
	 * @return ResponseEntity 200 (OK) o 400/500 en caso de error.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')") // Solo usuarios con rol ADMINISTRADOR pueden actualizar
	@PostMapping(value = "/sensor/numerico")
	public ResponseEntity<?> actualizarSensorNumerico(@RequestHeader Double valorActual, String mac)
	{
		try
		{
			// 1. Buscar el sensor por su MAC
			Optional<SensorNumerico> optionalSensorNumerico = sensorNumericoRepo.findById(mac);
			
			// 2. Validación: Si el sensor no existe, lanzar excepción
			if (!optionalSensorNumerico.isPresent())
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}
			
			// 3. Actualizar el valor
			SensorNumerico sensorNumerico = optionalSensorNumerico.get();
			sensorNumerico.setValorActual(valorActual); // Establece el nuevo valor numérico
			
			// 4. Guardar los cambios
			sensorNumericoRepo.saveAndFlush(sensorNumerico);
			
			log.info(Constants.ELEMENTO_MODIFICADO);
			
			return ResponseEntity.ok().build();
			
		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		}
		catch (Exception exception) 
		{
			log.error("Internal Server Error");
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
		
	}
}