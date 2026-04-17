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

			// Validamos las validaciones previas
			Actuador actuador = this.actuadorEstadoValidarYActualizar(mac);

			// Validar últimas acciones pendientes y devolver como mucho una a realizar
			Accion accion = this.actuadorEstadoValidarUltimasAccionesPendientesYDevolverUna(actuador);

			// Si la acción no es nula ...
			if (accion != null)
			{
				// ... seteo el estado de la acción a en ejecución
				accion.setEstado(Constants.ESTADO_ACCION_EN_EJECUCION);

				// ... guardo la acción actualizada
				this.accionRepository.saveAndFlush(accion);

				// Recorremos todos los comandos asociados a la orden
				for (Comando comando : accion.getOrden().getComandos())
				{
					ComandoActuador comandoActuador = comando.getComandoActuador();

					// Si no tiene relés asociados, añadimos un único elemento a la lista
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
						// Si tiene varios relés asociados, añadimos un elemento por cada relé
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
	 * Validar y actualizar el estado del actuador para el endpoint /actuador/estado
	 * @param mac MAC del actuador
	 * @return Actuador
	 * @throws AutomationsServerException si la mac es nula o vacía o el actuador no existe
	 */
	private Actuador actuadorEstadoValidarYActualizar(String mac) throws AutomationsServerException
	{
		// Validamos si la mac viene vacía o nula
		if (mac == null || mac.isEmpty())
		{
			log.error(Constants.ERR_ACTUADOR_NULO_VACIO);
			throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NULO_VACIO);
		}

		// Validamos si el actuador existe
		Optional<Actuador> optionalActuador = this.actuadorRepository.findById(mac);
		if (optionalActuador.isEmpty())
		{
			log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
			throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NO_EXISTE);
		}

		// Obtenemos el actuador
		Actuador actuador = optionalActuador.get();

		// Actualizamos el estado del actuador
		actuador.setEstado(Constants.ESTADO_ACTUADOR_ON);
		actuador.setUltimaActualizacion(new Date());

		// Guardamos el actuador actualizado
		this.actuadorRepository.saveAndFlush(actuador);

		return actuador;
	}

	/**
	 * Validar las últimas acciones pendientes y devolver como mucho una a realizar
	 * @param actuador Actuador para el cual se validan las últimas acciones pendientes.
	 * @return Accion a realizar o null si no hay acciones posibles
	 */
	private Accion actuadorEstadoValidarUltimasAccionesPendientesYDevolverUna(Actuador actuador)
	{
		// Inicializo la acción a devolver
		Accion outcome = null;

		// Inicializo la lista de acciones posibles a realizar
		List<Accion> accionesPosibles = new ArrayList<Accion>();

		// Buscamos las últimas acciones pendientes a realizar
		Optional<List<Accion>> optionalAccion = this.accionRepository.buscarUltimasAccionesPendientes(actuador);

		// Si hay acciones pendientes ...
		if (!optionalAccion.isEmpty())
		{
			// ... Obtenemos las acciones pendientes ...
			List<Accion> acciones = optionalAccion.get();

			// ... itero sobre cada una de ellas para quedarme con la última
			// siempre que no haya pasado mucho tiempo
			for (Accion accion : acciones)
			{
				// Obtengo la fecha cuando envió la orden
				Date fechaEnvioOrden = accion.getOrden().getFecha();

				// Si ha pasado más del tiempo de expiración ...
				if (fechaEnvioOrden.getTime() < System.currentTimeMillis() - tiempoExpiracion)
				{
					// ... cambio el estado de la acción a en ejecución
					accion.setEstado(Constants.ESTADO_ACCION_EXPIRADA);

					// ... guardo la acción actualizada
					this.accionRepository.saveAndFlush(accion);
				}
				else
				{
					// ... si llego hasta aquí, la acción no ha expirado
					// ... la añado a la lista de acciones posibles
					accionesPosibles.add(accion);
				}
			}
		}

		// Si hay acciones posibles ...
		if (!accionesPosibles.isEmpty())
		{
			// ... me quedo con la primera eliminándola de la lista
			outcome = accionesPosibles.remove(0);

			// Si sigue habiendo acciones posibles ...
			if (!accionesPosibles.isEmpty())
			{
				// ... itero sobre ellas y las pongo como duplicadas
				for (Accion accionDuplicada : accionesPosibles)
				{
					// Seteo el estado de la acción duplicada a duplicada
					accionDuplicada.setEstado(Constants.ESTADO_ACCION_DUPLICADA);

					// Guardo la acción actualizada
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
			// Validamos los datos de entrada
			this.actualizarEstadoAccionValidarDatosEntrada(requestDto);

			// Obtenemos la acción
			Accion accion = this.actualizarEstadoAccionObtenerAccion(requestDto);

			// Seteamos el estado de la acción
	        accion.setEstado(requestDto.getEstado());

			// Si el resultado de la acción no es nulo, seteamos el resultado de la acción
	        if (requestDto.getResultado() != null)
	        {
	            accion.setResultado(requestDto.getResultado());
	        }

			// Guardamos la acción actualizada
	        this.accionRepository.saveAndFlush(accion);

			// Devolvemos una respuesta correcta
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
		// Validamos si el identificador de la acción es nulo o vacío
		if (requestDto.getAccionId() == null)
		{
			log.error(Constants.ERR_ACTUADOR_ACCION_ID_NULO_VACIO);
			throw new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_ACTUADOR_ACCION_ID_NULO_VACIO);
		}

		// Validamos si el estado de la acción es nulo o vacío
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
		// Busco la acción en la base de datos
		Optional<Accion> optionalAccion = this.accionRepository.findById(requestDto.getAccionId());

		// Si la acción no existe ...
		if (optionalAccion.isEmpty())
		{
			// ... logueamos y lanzamos una excepción
			log.error(Constants.ERR_ACCION_NO_EXISTE);
			throw new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_ACCION_NO_EXISTE);
		}

		// Devolvemos la acción
		return optionalAccion.get();
	}
	
}