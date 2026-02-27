package es.iesjandula.reaktor.automations_server.rest;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.models.Actuador;
import es.iesjandula.reaktor.automations_server.models.SensorBooleano;
import es.iesjandula.reaktor.automations_server.models.SensorNumerico;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorNumericoRpository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST dedicado a recibir y procesar actualizaciones de datos
 * enviadas por los dispositivos SensorBooleano y SensorNumerico.
 */
@Slf4j
@RestController
@RequestMapping("/automations/admin/actualizacion")
public class ActualizacionesDispositivosRestController
{
	@Autowired
	private IActuadorRepository actuadorRepository;
	
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
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_SENSOR + "')") // Solo usuarios con rol SENSOR pueden actualizar
	@PostMapping(value = "/sensor/booleano")
	public ResponseEntity<?> actualizarSensorBooleano(@RequestHeader(value="valorActual") Boolean valorActual, @RequestHeader(value="mac") String mac)
	{
		try
		{
			// 1. Buscar el sensor por su MAC
			Optional<SensorBooleano> optionalSensorBooleano = sensorBooleanoRepo.findById(mac);
			
			// 2. Validación: Si el sensor no existe, lanzar excepción
			if (!optionalSensorBooleano.isPresent())
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			// 3. Actualizar el valor y la fecha (aunque no está explícito, el save() puede actualizar el campo ultimaActualizacion si está configurado en el modelo Sensor)
			SensorBooleano sensorBooleano = optionalSensorBooleano.get();
			sensorBooleano.setValorActual(valorActual); // Establece el nuevo valor reportado

			// 4. Guardar los cambios
			sensorBooleanoRepo.saveAndFlush(sensorBooleano);

			log.info(Constants.ELEMENTO_MODIFICADO);

			return ResponseEntity.ok().build(); // Devuelve 200 OK

		} 
		catch (AutomationsServerException AutomationsServerException)
		{
			// Manejo de errores controlados (negocio)
			return ResponseEntity.badRequest().body(AutomationsServerException); // Devuelve 400 Bad Request
		}
		catch (Exception exception) 
		{
			// Manejo de errores inesperados (sistema)
			AutomationsServerException AutomationsServerException = new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException );
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
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
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_SENSOR + "')") // Solo usuarios con rol SENSOR pueden actualizar
	@PostMapping(value = "/sensor/numerico")
	public ResponseEntity<?> actualizarSensorNumerico(@RequestHeader(value="valorActual") Double valorActual, @RequestHeader(value="mac") String mac)
	{
		try
		{
			// Buscar el sensor por su MAC
			Optional<SensorNumerico> optionalSensorNumerico = sensorNumericoRepo.findById(mac);
			
			// Validación: Si el sensor no existe, lanzar excepción
			if (!optionalSensorNumerico.isPresent())
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}
			
			// Actualizar el valor
			SensorNumerico sensorNumerico = optionalSensorNumerico.get();
			sensorNumerico.setValorActual(valorActual);
			
			// Guardar los cambios
			sensorNumericoRepo.saveAndFlush(sensorNumerico);
			
			log.info(Constants.ELEMENTO_MODIFICADO);
			
			return ResponseEntity.ok().build();
			
			
		} 
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException);
		}
		catch (Exception exception) 
		{
			AutomationsServerException AutomationsServerException = new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException );
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
			
		}
		
	}
	
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ACTUADOR + "')")
	@PostMapping("/actuador/estado")
	public ResponseEntity<?> actuadorEstado(@RequestHeader("mac") String mac) {

		try 
		{
	
		    Optional<Actuador> optionalActuador = this.actuadorRepository.findById(mac);	
		    if (optionalActuador.isEmpty()) 
		    {
		    	log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NULO_VACIO);
		    }
		    Actuador actuador = optionalActuador.get();
		    actuador.setEstado("on");
		    actuador.setUltimaActualizacion(new Date());
		    actuadorRepository.saveAndFlush(actuador);
	    	
	    return ResponseEntity.ok().build();
		}
		
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException);
		}
		catch (Exception exception) 
		{
				
		AutomationsServerException AutomationsServerException = new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
		log.error("Excepción genérica al crear la incidencia", AutomationsServerException );
		return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
			
		}
		
	}
}