package es.iesjandula.reaktor.automations_server.rest;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.AccionEstadoRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.ActuadorEstadoResponseDto;
import es.iesjandula.reaktor.automations_server.models.Accion;
import es.iesjandula.reaktor.automations_server.models.Actuador;
import es.iesjandula.reaktor.automations_server.models.SensorBooleano;
import es.iesjandula.reaktor.automations_server.models.SensorNumerico;
import es.iesjandula.reaktor.automations_server.repository.IAccionRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorNumericoRpository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST dedicado a recibir y procesar actualizaciones de datos
 * enviadas por los dispositivos SensorBooleano, SensorNumerico y Actuador.
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
	
	@Autowired
	private IAccionRepository accionRepository;

	// ----------------------------------------------------------------------------------
	// --- ENDPOINT PARA ACTUALIZAR SENSOR BOOLEANO ---
	// ----------------------------------------------------------------------------------

	/**
	 * Endpoint para actualizar el valor actual de un Sensor Booleano.
	 * Los datos (valorActual y mac) se esperan en las cabeceras de la petición.
	 * 
	 * @param valorActual Nuevo valor booleano del sensor.
	 * @param mac         MAC del sensor a actualizar.
	 * @return ResponseEntity 200 (OK) o 400 (Bad Request) si el sensor no existe.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION_SENSOR + "')") // Solo usuarios con rol SENSOR pueden actualizar
	@PostMapping(value = "/sensor/booleano")
	public ResponseEntity<?> actualizarSensorBooleano(
			@RequestHeader(value = "valorActual") Boolean valorActual,
			@RequestHeader(value = "mac") String mac)
	{
		try
		{
			Optional<SensorBooleano> optionalSensorBooleano = sensorBooleanoRepo.findById(mac);

			if (!optionalSensorBooleano.isPresent())
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			SensorBooleano sensorBooleano = optionalSensorBooleano.get();
			sensorBooleano.setValorActual(valorActual);

			sensorBooleanoRepo.saveAndFlush(sensorBooleano);

			log.info(Constants.ELEMENTO_MODIFICADO);

			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException automationsServerException)
		{
			return ResponseEntity.badRequest().body(automationsServerException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al actualizar sensor booleano", automationsServerException);
			return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINT PARA ACTUALIZAR SENSOR NUMÉRICO ---
	// ----------------------------------------------------------------------------------

	/**
	 * Endpoint para actualizar el valor actual de un Sensor Numérico.
	 * Los datos (valorActual y mac) se esperan en las cabeceras de la petición.
	 * 
	 * @param valorActual Nuevo valor numérico del sensor.
	 * @param mac         MAC del sensor a actualizar.
	 * @return ResponseEntity 200 (OK) o 400/500 en caso de error.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION_SENSOR + "')") // Solo usuarios con rol SENSOR pueden actualizar
	@PostMapping(value = "/sensor/numerico")
	public ResponseEntity<?> actualizarSensorNumerico(
			@RequestHeader(value = "valorActual") Double valorActual,
			@RequestHeader(value = "mac") String mac)
	{
		try
		{
			Optional<SensorNumerico> optionalSensorNumerico = sensorNumericoRepo.findById(mac);

			if (!optionalSensorNumerico.isPresent())
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			SensorNumerico sensorNumerico = optionalSensorNumerico.get();
			sensorNumerico.setValorActual(valorActual);

			sensorNumericoRepo.saveAndFlush(sensorNumerico);

			log.info(Constants.ELEMENTO_MODIFICADO);

			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException automationsServerException)
		{
			return ResponseEntity.badRequest().body(automationsServerException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al actualizar sensor numérico", automationsServerException);
			return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINT PARA ACTUALIZAR ACTUADOR Y ENTREGAR ACCION PENDIENTE ---
	// ----------------------------------------------------------------------------------

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION_ACTUADOR + "')")
	@PostMapping("/actuador/estado")
	public ResponseEntity<?> actuadorEstado(@RequestHeader("mac") String mac)
	{
		try
		{
			Optional<Actuador> optionalActuador = this.actuadorRepository.findById(mac);

			if (optionalActuador.isEmpty())
			{
				log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NULO_VACIO);
			}

			// Mantengo tu lógica actual: marcar actuador como activo y actualizar heartbeat
			Actuador actuador = optionalActuador.get();
			actuador.setEstado("on");
			actuador.setUltimaActualizacion(new Date());
			actuadorRepository.saveAndFlush(actuador);

			// Buscar la última acción pendiente para ese actuador
			Optional<Accion> optionalAccion =
					this.accionRepository.findTopByActuador_MacAndEstadoOrderByIdDesc(
							mac,
							Constants.ESTADO_ACCION_PENDIENTE);

			// Si no hay acción pendiente, se devuelve respuesta vacía controlada
			if (optionalAccion.isEmpty())
			{
				ActuadorEstadoResponseDto responseDto =
						new ActuadorEstadoResponseDto(null, null, null, mac, null);

				return ResponseEntity.ok(responseDto);
			}

			// Si hay acción pendiente, se cambia a en_ejecucion y se devuelve
			Accion accion = optionalAccion.get();
			accion.setEstado(Constants.ESTADO_ACCION_EN_EJECUCION);
			this.accionRepository.saveAndFlush(accion);

			ActuadorEstadoResponseDto responseDto =
					new ActuadorEstadoResponseDto(
							accion.getId(),
							accion.getEstado(),
							accion.getResultado(),
							accion.getActuador().getMac(),
							accion.getOrden().getId());

			return ResponseEntity.ok(responseDto);
		}
		catch (AutomationsServerException automationsServerException)
		{
			return ResponseEntity.badRequest().body(automationsServerException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsServerException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al consultar estado del actuador", automationsServerException);
			return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/accion/estado", consumes = "application/json")
	public ResponseEntity<?> actualizarEstadoAccion(@RequestBody AccionEstadoRequestDto requestDto)
	{
	    try
	    {
	        if (requestDto.getAccionId() == null)
	        {
	            throw new AutomationsServerException(Constants.ERR_ACCION_CODE, "accionId nulo");
	        }

	        if (requestDto.getEstado() == null || requestDto.getEstado().isEmpty())
	        {
	            throw new AutomationsServerException(Constants.ERR_ACCION_CODE, "estado nulo o vacío");
	        }

	        Optional<Accion> optionalAccion = this.accionRepository.findById(requestDto.getAccionId());

	        if (optionalAccion.isEmpty())
	        {
	            throw new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_ACCION_NO_EXISTE);
	        }

	        Accion accion = optionalAccion.get();
	        accion.setEstado(requestDto.getEstado());

	        if (requestDto.getResultado() != null)
	        {
	            accion.setResultado(requestDto.getResultado());
	        }

	        this.accionRepository.saveAndFlush(accion);

	        return ResponseEntity.ok().build();
	    }
	    catch (AutomationsServerException automationsServerException)
	    {
	        return ResponseEntity.badRequest().body(automationsServerException);
	    }
	    catch (Exception exception)
	    {
	        AutomationsServerException automationsServerException =
	                new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_CODE);
	        log.error("Excepción genérica al actualizar estado de acción", exception);
	        return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
	    }
	}
}