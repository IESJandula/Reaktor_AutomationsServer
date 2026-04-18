package es.iesjandula.reaktor.automations_server.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.AccionEstadoRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.ActuadorAccionesPendientesResponse;
import es.iesjandula.reaktor.automations_server.dtos.ActuadorProyectorEstadoRequestDto;
import es.iesjandula.reaktor.automations_server.models.Accion;
import es.iesjandula.reaktor.automations_server.models.Actuador;
import es.iesjandula.reaktor.automations_server.models.ActuadorProyector;
import es.iesjandula.reaktor.automations_server.models.Comando;
import es.iesjandula.reaktor.automations_server.models.ComandoActuador;
import es.iesjandula.reaktor.automations_server.models.ComandoActuadorPuerta;
import es.iesjandula.reaktor.automations_server.models.SensorBooleano;
import es.iesjandula.reaktor.automations_server.models.SensorNumerico;
import es.iesjandula.reaktor.automations_server.repository.IAccionRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorProyectorRepository;
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
	private IActuadorProyectorRepository actuadorProyectorRepository;
	
	@Autowired
	private IActuadorRepository actuadorRepository;

	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;
	
	@Autowired
	private IAccionRepository accionRepository;

	@Value("${reaktor.tiempoExpiracion}")
	private long tiempoExpiracion;

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
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION_SENSOR + "')")
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
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION_SENSOR + "')")
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

	/**
	 * Endpoint para actualizar el estado del actuador y devolver la acción pendiente a realizar
	 * @param mac MAC del actuador
	 * @return ResponseEntity 200 (OK) o 400 (Bad Request) si la mac es nula o vacía o el actuador no existe
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION_ACTUADOR + "')")
	@PostMapping("/actuador/estado")
	public ResponseEntity<?> actuadorEstado(@RequestHeader("mac") String mac)
	{
		try
		{
			List<ActuadorAccionesPendientesResponse> actuadorAccionesPendientesResponseList =
					new ArrayList<ActuadorAccionesPendientesResponse>();

			Actuador actuador = this.actuadorEstadoValidarYActualizar(mac);
			Accion accion = this.actuadorEstadoValidarUltimasAccionesPendientesYDevolverUna(actuador);

			if (accion != null)
			{
				accion.setEstado(Constants.ESTADO_ACCION_EN_EJECUCION);
				this.accionRepository.saveAndFlush(accion);

				for (Comando comando : accion.getOrden().getComandos())
				{
					ComandoActuador comandoActuador = comando.getComandoActuador();

					if (comandoActuador.getListaComandosPuerta() == null ||
						comandoActuador.getListaComandosPuerta().isEmpty())
					{
						ActuadorAccionesPendientesResponse actuadorAccionesPendientesResponse =
								new ActuadorAccionesPendientesResponse();

						actuadorAccionesPendientesResponse.setAccionId(accion.getId());
						actuadorAccionesPendientesResponse.setOrden(comandoActuador.getComandos());
						actuadorAccionesPendientesResponse.setKeyword(
								comandoActuador.getComandoActuadorId().getKeyword());
						actuadorAccionesPendientesResponse.setIndiceRele(null);

						actuadorAccionesPendientesResponseList.add(actuadorAccionesPendientesResponse);
					}
					else
					{
						for (ComandoActuadorPuerta comandoActuadorPuerta : comandoActuador.getListaComandosPuerta())
						{
							ActuadorAccionesPendientesResponse actuadorAccionesPendientesResponse =
									new ActuadorAccionesPendientesResponse();

							actuadorAccionesPendientesResponse.setAccionId(accion.getId());
							actuadorAccionesPendientesResponse.setOrden(comandoActuador.getComandos());
							actuadorAccionesPendientesResponse.setKeyword(
									comandoActuador.getComandoActuadorId().getKeyword());
							actuadorAccionesPendientesResponse.setIndiceRele(
									comandoActuadorPuerta.getComandoActuadorPuertaId().getIndiceRele());

							actuadorAccionesPendientesResponseList.add(actuadorAccionesPendientesResponse);
						}
					}
				}
			}

			return ResponseEntity.ok(actuadorAccionesPendientesResponseList);
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
	
	/**
	 * Endpoint para que el ESP32 asociado a un proyector solicite el comando de estado
	 * enviando su MAC.
	 * 
	 * @param mac MAC del actuador proyector
	 * @return ResponseEntity 200 (OK) con el comando de estado o 400 (Bad Request)
	 *         si la mac es nula, vacía o el proyector no existe
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION_ACTUADOR + "')")
	@PostMapping("/actuador/proyector/comando-estado")
	public ResponseEntity<?> obtenerComandoEstadoProyector(@RequestHeader("mac") String mac)
	{
		try
		{
			if (mac == null || mac.isEmpty())
			{
				log.error(Constants.ERR_ACTUADOR_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NULO_VACIO);
			}

			Optional<ActuadorProyector> optionalActuadorProyector = this.actuadorProyectorRepository.findById(mac);

			if (optionalActuadorProyector.isEmpty())
			{
				log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NO_EXISTE);
			}

			ActuadorProyector actuadorProyector = optionalActuadorProyector.get();

			java.util.Map<String, Object> response = new java.util.HashMap<String, Object>();
			response.put("mac", actuadorProyector.getMac());
			response.put("comandoEstado", actuadorProyector.getComandoEstado());

			return ResponseEntity.ok(response);
		}
		catch (AutomationsServerException automationsServerException)
		{
			return ResponseEntity.badRequest().body(automationsServerException.getBodyExceptionMessage());
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsServerException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al obtener comando de estado del proyector", exception);
			return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}

	/**
	 * Endpoint para que el ESP32 asociado a un proyector envíe periódicamente
	 * que está vivo junto con el estado real del proyector y reciba, si existe,
	 * una acción pendiente a realizar.
	 * 
	 * @param requestDto DTO con la MAC del proyector y su estado actual
	 * @return ResponseEntity 200 (OK) con una única acción pendiente o vacía si no existe
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION_ACTUADOR + "')")
	@PostMapping(value = "/actuador/proyector/estado", consumes = "application/json")
	public ResponseEntity<?> actualizarEstadoProyectorYObtenerAccion(@RequestBody ActuadorProyectorEstadoRequestDto requestDto)
	{
		try
		{
			ActuadorAccionesPendientesResponse actuadorAccionesPendientesResponse =
					new ActuadorAccionesPendientesResponse();

			Actuador actuador = this.proyectorEstadoValidarYActualizar(requestDto);

			Accion accion = this.actuadorEstadoValidarUltimasAccionesPendientesYDevolverUna(actuador);

			if (accion != null)
			{
				accion.setEstado(Constants.ESTADO_ACCION_EN_EJECUCION);
				this.accionRepository.saveAndFlush(accion);

				Comando comando = accion.getOrden().getComandos().get(0);
				ComandoActuador comandoActuador = comando.getComandoActuador();

				actuadorAccionesPendientesResponse.setAccionId(accion.getId());
				actuadorAccionesPendientesResponse.setOrden(comandoActuador.getComandos());
				actuadorAccionesPendientesResponse.setKeyword(
						comandoActuador.getComandoActuadorId().getKeyword());
				actuadorAccionesPendientesResponse.setIndiceRele(null);
			}

			return ResponseEntity.ok(actuadorAccionesPendientesResponse);
		}
		catch (AutomationsServerException automationsServerException)
		{
			return ResponseEntity.badRequest().body(automationsServerException.getBodyExceptionMessage());
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsServerException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al actualizar estado del proyector", exception);
			return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}

	/**
	 * Validar y actualizar el estado del actuador para el endpoint /actuador/estado
	 * @param mac MAC del actuador
	 * @return Actuador
	 * @throws AutomationsServerException si la mac es nula o vacía o el actuador no existe
	 */
	private Actuador actuadorEstadoValidarYActualizar(String mac) throws AutomationsServerException
	{
		if (mac == null || mac.isEmpty())
		{
			log.error(Constants.ERR_ACTUADOR_NULO_VACIO);
			throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NULO_VACIO);
		}

		Optional<Actuador> optionalActuador = this.actuadorRepository.findById(mac);
		if (optionalActuador.isEmpty())
		{
			log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
			throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NO_EXISTE);
		}

		Actuador actuador = optionalActuador.get();

		actuador.setEstado(Constants.ESTADO_ACTUADOR_ON);
		actuador.setUltimaActualizacion(new Date());

		this.actuadorRepository.saveAndFlush(actuador);

		return actuador;
	}

	/**
	 * Validar y actualizar el estado del proyector
	 * @param requestDto DTO con la MAC y el estado real del proyector
	 * @return Actuador
	 * @throws AutomationsServerException si la mac o el estado son nulos o el proyector no existe
	 */
	private Actuador proyectorEstadoValidarYActualizar(ActuadorProyectorEstadoRequestDto requestDto) throws AutomationsServerException
	{
		if (requestDto.getMac() == null || requestDto.getMac().isEmpty())
		{
			log.error(Constants.ERR_ACTUADOR_NULO_VACIO);
			throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NULO_VACIO);
		}

		if (requestDto.getEstadoProyector() == null || requestDto.getEstadoProyector().isEmpty())
		{
			log.error("El estado del proyector es nulo o vacío");
			throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, "El estado del proyector es nulo o vacío");
		}

		Optional<ActuadorProyector> optionalActuadorProyector =
				this.actuadorProyectorRepository.findById(requestDto.getMac());

		if (optionalActuadorProyector.isEmpty())
		{
			log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
			throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NO_EXISTE);
		}

		ActuadorProyector actuadorProyector = optionalActuadorProyector.get();

		actuadorProyector.setEstado(Constants.ESTADO_ACTUADOR_ON);
		actuadorProyector.setEstadoProyector(requestDto.getEstadoProyector());
		actuadorProyector.setUltimaActualizacion(new Date());

		this.actuadorProyectorRepository.saveAndFlush(actuadorProyector);

		return actuadorProyector;
	}

	/**
	 * Validar las últimas acciones pendientes y devolver como mucho una a realizar
	 * @param actuador Actuador para el cual se validan las últimas acciones pendientes.
	 * @return Accion a realizar o null si no hay acciones posibles
	 */
	private Accion actuadorEstadoValidarUltimasAccionesPendientesYDevolverUna(Actuador actuador)
	{
		Accion outcome = null;
		List<Accion> accionesPosibles = new ArrayList<Accion>();
		Optional<List<Accion>> optionalAccion = this.accionRepository.buscarUltimasAccionesPendientes(actuador);

		if (!optionalAccion.isEmpty())
		{
			List<Accion> acciones = optionalAccion.get();

			for (Accion accion : acciones)
			{
				Date fechaEnvioOrden = accion.getOrden().getFecha();

				if (fechaEnvioOrden.getTime() < System.currentTimeMillis() - tiempoExpiracion)
				{
					accion.setEstado(Constants.ESTADO_ACCION_EXPIRADA);
					this.accionRepository.saveAndFlush(accion);
				}
				else
				{
					accionesPosibles.add(accion);
				}
			}
		}

		if (!accionesPosibles.isEmpty())
		{
			outcome = accionesPosibles.remove(0);

			if (!accionesPosibles.isEmpty())
			{
				for (Accion accionDuplicada : accionesPosibles)
				{
					accionDuplicada.setEstado(Constants.ESTADO_ACCION_DUPLICADA);
					this.accionRepository.saveAndFlush(accionDuplicada);
				}
			}
		}

		return outcome;
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/accion/estado", consumes = "application/json")
	public ResponseEntity<?> actualizarEstadoAccion(@RequestBody AccionEstadoRequestDto requestDto)
	{
		try
	    {
			this.actualizarEstadoAccionValidarDatosEntrada(requestDto);
			Accion accion = this.actualizarEstadoAccionObtenerAccion(requestDto);

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
	        AutomationsServerException automationsServerException = new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_CODE);

	        log.error("Excepción genérica al actualizar estado de acción", exception);
	        return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
	    }
	}

	/**
	 * Validar los datos de entrada para el endpoint /accion/estado
	 * @param requestDto DTO con los datos de entrada
	 * @throws AutomationsServerException si el identificador de la acción es nulo o vacío o el estado de la acción es nulo o vacío
	 */
	private void actualizarEstadoAccionValidarDatosEntrada(AccionEstadoRequestDto requestDto) throws AutomationsServerException
	{
		if (requestDto.getAccionId() == null)
		{
			log.error(Constants.ERR_ACTUADOR_ACCION_ID_NULO_VACIO);
			throw new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_ACTUADOR_ACCION_ID_NULO_VACIO);
		}

		if (requestDto.getEstado() == null || requestDto.getEstado().isEmpty())
		{
			log.error(Constants.ERR_ACTUADOR_ACCION_ESTADO_NULO_VACIO);
			throw new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_ACTUADOR_ACCION_ESTADO_NULO_VACIO);
		}
	}

	/**
	 * Obtener la acción para el endpoint /accion/estado
	 * @param requestDto DTO con los datos de entrada
	 * @return Accion
	 * @throws AutomationsServerException si la acción no existe
	 */
	private Accion actualizarEstadoAccionObtenerAccion(AccionEstadoRequestDto requestDto) throws AutomationsServerException
	{
		Optional<Accion> optionalAccion = this.accionRepository.findById(requestDto.getAccionId());

		if (optionalAccion.isEmpty())
		{
			log.error(Constants.ERR_ACCION_NO_EXISTE);
			throw new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_ACCION_NO_EXISTE);
		}

		return optionalAccion.get();
	}
}