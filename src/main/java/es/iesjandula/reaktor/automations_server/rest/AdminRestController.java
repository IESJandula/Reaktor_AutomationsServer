package es.iesjandula.reaktor.automations_server.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.ActuadorRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.ComandoActuadorRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.ComandoRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.ComandoResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorNumericoRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorNumericoResponseDto;
import es.iesjandula.reaktor.automations_server.models.Actuador;
import es.iesjandula.reaktor.automations_server.models.Comando;
import es.iesjandula.reaktor.automations_server.models.ComandoActuador;
import es.iesjandula.reaktor.automations_server.models.Orden;
import es.iesjandula.reaktor.automations_server.models.SensorBooleano;
import es.iesjandula.reaktor.automations_server.models.SensorNumerico;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorId;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoId;
import es.iesjandula.reaktor.automations_server.repository.IAccionRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoRepository;
import es.iesjandula.reaktor.automations_server.repository.IOrdenRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorNumericoRpository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para operaciones de administración (CRUD) de dispositivos
 * (Sensores y Actuadores) y Ubicaciones. Requiere el rol de ADMINISTRADOR para
 * todos sus endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/automations/admin")
public class AdminRestController
{

	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;

	@Autowired
	private IActuadorRepository actuadorRepository;

	@Autowired
	private IComandoRepository comandoRepository;

	@Autowired
	private IOrdenRepository ordenRepository;

	@Autowired
	private IComandoActuadorRepository comandoActuadorRepository;
	
	@Autowired
	private IAccionRepository accionRepository;

	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA SENSOR BOOLEANO ---
	// ----------------------------------------------------------------------------------

	/**
	 * Endpoint para crear un nuevo Sensor Booleano.
	 * 
	 * @param sensorBooleanoDto DTO que contiene los datos del nuevo sensor.
	 * @return ResponseEntity con código 200 (OK) o 400 (Bad Request) si falla la
	 *         validación.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/sensor/booleano", consumes = "application/json")
	public ResponseEntity<?> crearSensorBooleano(@RequestBody SensorBooleanoRequestDto sensorBooleanoDto)
	{
		try
		{
			if (sensorBooleanoDto.getMac() == null || sensorBooleanoDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_SENSOR_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NULO_VACIO);
			}

			if (sensorBooleanoRepo.existsById(sensorBooleanoDto.getMac()))
			{

				SensorBooleano sensor = new SensorBooleano();
				sensor.setEstado(sensorBooleanoDto.getEstado());
				sensor.setUmbralMaximo(sensorBooleanoDto.getUmbralMaximo());
				sensor.setUmbralMinimo(sensorBooleanoDto.getUmbralMinimo());
				sensor.setTipo(sensorBooleanoDto.getTipo());
				sensor.setNombreUbicacion(sensorBooleanoDto.getNombreUbicacion());
			}

			String mac = sensorBooleanoDto.getMac();

			if (actuadorRepository.existsById(mac))
			{
				actuadorRepository.deleteById(mac);
			}
			if (sensorNumericoRepo.existsById(mac))
			{
				sensorNumericoRepo.deleteById(mac);
			}

			if (sensorBooleanoDto.getNombreUbicacion() == null || sensorBooleanoDto.getNombreUbicacion().isEmpty())
			{
				log.error(Constants.ERR_UBICACION_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_UBICACION_CODE,
						Constants.ERR_UBICACION_NULO_VACIO);
			}

			SensorBooleano sensor = new SensorBooleano();
			sensor.setMac(sensorBooleanoDto.getMac());
			sensor.setEstado(sensorBooleanoDto.getEstado());
			sensor.setUmbralMaximo(sensorBooleanoDto.getUmbralMaximo());
			sensor.setUmbralMinimo(sensorBooleanoDto.getUmbralMinimo());
			sensor.setTipo(sensorBooleanoDto.getTipo());
			sensor.setNombreUbicacion(sensorBooleanoDto.getNombreUbicacion());

			sensorBooleanoRepo.saveAndFlush(sensor);

			log.info(Constants.ELEMENTO_AGREGADO);

			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException);
		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());

		}

	}

	/**
	 * Endpoint para obtener la lista de todos los Sensores Booleanos.
	 * 
	 * @return ResponseEntity con la lista de SensorBooleanoResponseDto o un código
	 *         500 en caso de error.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping("/sensor/booleano")
	public ResponseEntity<?> obtenerSensoresBooleanos()
	{
		try
		{
			List<SensorBooleanoResponseDto> sensores = this.sensorBooleanoRepo.buscarSensoresBooleanos();

			return ResponseEntity.ok(sensores);

		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	/**
	 * Endpoint para eliminar un Sensor Booleano por su MAC.
	 * 
	 * @param mac MAC del sensor a eliminar.
	 * @return ResponseEntity con código 200 (OK) o 400 si el sensor no existe.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping("/sensor/booleano")
	public ResponseEntity<?> eliminarSensorBooleano(@RequestHeader("mac") String mac)
	{
		try
		{
			if (!sensorBooleanoRepo.existsById(mac))
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			sensorBooleanoRepo.deleteById(mac);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);

		}
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException);
		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA SENSOR NUMÉRICO ---
	// ----------------------------------------------------------------------------------

	/**
	 * Endpoint para crear un nuevo Sensor Numérico. El flujo de validación y
	 * creación es similar al Sensor Booleano.
	 * 
	 * @param sensorNumericoDto DTO con los datos del nuevo sensor.
	 * @return ResponseEntity con código 200 (OK) o 400 si falla la validación.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/sensor/numerico", consumes = "application/json")
	public ResponseEntity<?> crearSensorNumerico(@RequestBody SensorNumericoRequestDto sensorNumericoDto)
	{
		try
		{
			if (sensorNumericoDto.getMac() == null || sensorNumericoDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_SENSOR_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NULO_VACIO);
			}

			if (sensorNumericoRepo.existsById(sensorNumericoDto.getMac()))
			{

				SensorNumerico sensor = new SensorNumerico();
				sensor.setEstado(sensorNumericoDto.getEstado());
				sensor.setUmbralMaximo(sensorNumericoDto.getUmbralMaximo());
				sensor.setUmbralMinimo(sensorNumericoDto.getUmbralMinimo());
				sensor.setTipo(sensorNumericoDto.getTipo());
				sensor.setNombreUbicacion(sensorNumericoDto.getNombreUbicacion());
			}

			String mac = sensorNumericoDto.getMac();

			if (actuadorRepository.existsById(mac))
			{
				actuadorRepository.deleteById(mac);
			}
			if (sensorBooleanoRepo.existsById(mac))
			{
				sensorBooleanoRepo.deleteById(mac);
			}

			if (sensorNumericoDto.getNombreUbicacion() == null || sensorNumericoDto.getNombreUbicacion().isEmpty())
			{
				log.error(Constants.ERR_UBICACION_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_UBICACION_CODE,
						Constants.ERR_UBICACION_NULO_VACIO);
			}

			SensorNumerico sensor = new SensorNumerico();
			sensor.setMac(sensorNumericoDto.getMac());
			sensor.setEstado(sensorNumericoDto.getEstado());
			sensor.setUmbralMinimo(sensorNumericoDto.getUmbralMinimo());
			sensor.setUmbralMaximo(sensorNumericoDto.getUmbralMaximo());
			sensor.setTipo(sensorNumericoDto.getTipo());
			sensor.setNombreUbicacion(sensorNumericoDto.getNombreUbicacion());

			sensorNumericoRepo.saveAndFlush(sensor);
			log.info(Constants.ELEMENTO_AGREGADO);

			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException);
		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	/**
	 * Endpoint para obtener la lista de todos los Sensores Numéricos.
	 * 
	 * @return ResponseEntity con la lista de SensorNumericoResponseDto.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping("/sensor/numerico")
	public ResponseEntity<?> obtenerSensoresNumericos()
	{
		try
		{
			List<SensorNumericoResponseDto> sensores = this.sensorNumericoRepo.buscarSensoresNumericos();

			return ResponseEntity.ok(sensores);

		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	/**
	 * Endpoint para eliminar un Sensor Numérico por su MAC.
	 * 
	 * @param mac MAC del sensor a eliminar.
	 * @return ResponseEntity con código 200 (OK) o 400 si el sensor no existe.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping("/sensor/numerico")
	public ResponseEntity<?> eliminarSensorNumerico(@RequestHeader("mac") String mac)
	{
		try
		{
			if (!sensorNumericoRepo.existsById(mac))
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			sensorNumericoRepo.deleteById(mac);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);

		}
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException);
		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA ACTUADOR ---
	// ----------------------------------------------------------------------------------

	/**
	 * Endpoint para crear un nuevo Actuador.
	 * 
	 * @param actuadorRequestDto DTO que contiene los datos del nuevo actuador.
	 * @return ResponseEntity con código 200 (OK) o 400 si falla la validación.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/actuador", consumes = "application/json")
	public ResponseEntity<?> crearActuador(@RequestBody(required = true) ActuadorRequestDto actuadorRequestDto)
	{
		try
		{
			if (actuadorRequestDto.getMac() == null || actuadorRequestDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_ACTUADOR_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE,
						Constants.ERR_ACTUADOR_NULO_VACIO);
			}

			if (actuadorRepository.existsById(actuadorRequestDto.getMac()))
			{

				Actuador actuador = new Actuador();
				actuador.setEstado(actuadorRequestDto.getEstado());
				actuador.setTipo(actuadorRequestDto.getTipo());
				actuador.setNombreUbicacion(actuadorRequestDto.getNombreUbicacion());
			}

			String mac = actuadorRequestDto.getMac();

			if (sensorBooleanoRepo.existsById(mac))
			{
				sensorBooleanoRepo.deleteById(mac);
			}

			if (sensorNumericoRepo.existsById(mac))
			{
				sensorNumericoRepo.deleteById(mac);
			}

			Actuador actuador = new Actuador();
			actuador.setMac(actuadorRequestDto.getMac());
			actuador.setEstado(actuadorRequestDto.getEstado());
			actuador.setTipo(actuadorRequestDto.getTipo());
			actuador.setNombreUbicacion(actuadorRequestDto.getNombreUbicacion());
			this.actuadorRepository.saveAndFlush(actuador);

			log.info(Constants.ELEMENTO_AGREGADO);
			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException);
		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	/**
	 * Endpoint para obtener la lista de todos los Actuadores.
	 * 
	 * @return ResponseEntity con la lista de Actuadores.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/actuador")
	public ResponseEntity<?> obtenerActuador()
	{
		try
		{
			List<ActuadorResponseDto> actuadores = this.actuadorRepository.buscarActuadores();

			for (ActuadorResponseDto a : actuadores)
			{
				if (a.getMac() == null || a.getMac().isEmpty())
				{
					log.error(Constants.ERR_ACTUADOR_SIN_MAC);
					throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE,
							Constants.ERR_ACTUADOR_SIN_MAC);
				}
				if (a.getNombreUbicacion() == null || a.getNombreUbicacion().isEmpty())
				{
					log.error(Constants.ERR_ACTUADOR_SIN_UBICACION);
					throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE,
							Constants.ERR_ACTUADOR_SIN_UBICACION);
				}
			}

			return ResponseEntity.ok(actuadores);

		}
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException.getBodyExceptionMessage());
		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	/**
	 * Endpoint para eliminar un Actuador por su MAC.
	 * 
	 * @param mac MAC del actuador a eliminar.
	 * @return ResponseEntity con código 200 (OK) o 400 si el actuador no existe.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/actuador")
	public ResponseEntity<?> eliminarActuador(@RequestHeader("mac") String mac)
	{
		try
		{
			if (!this.actuadorRepository.existsById(mac))
			{
				log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE,
						Constants.ERR_ACTUADOR_NO_EXISTE);
			}
			this.actuadorRepository.deleteById(mac);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok().body(Constants.ELEMENTO_ELIMINADO);
		}
		catch (AutomationsServerException AutomationsServerException)
		{
			return ResponseEntity.badRequest().body(AutomationsServerException);
		}
		catch (Exception exception)
		{
			log.error("Internal Server Error");
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	/**
	 * Endpoint para obtener el tipo del dispositvo
	 * 
	 * @return ResponseEntity con la lista de aplicabilidades de los dispositivos
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/tipo/actuador")
	public ResponseEntity<?> obtenerTiposActuador()
	{
		try
		{
			return ResponseEntity.ok(Constants.APLICABILIDAD_ACTUADOR);
		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/tipo/sensor")
	public ResponseEntity<?> obtenerTiposSensor()
	{
		try
		{
			return ResponseEntity.ok(Constants.APLICABILIDAD_SENSOR);
		}
		catch (Exception exception)
		{
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", AutomationsServerException);
			return ResponseEntity.status(500).body(AutomationsServerException.getBodyExceptionMessage());
		}
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA COMANDO ---
	// ----------------------------------------------------------------------------------

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/comando", consumes = "application/json")
	public ResponseEntity<?> crearComando(@RequestBody ComandoRequestDto comandoRequestdto)
	{
		try
		{
			if (comandoRequestdto.getKeyword() == null || comandoRequestdto.getKeyword().isEmpty())
			{
				log.error(Constants.ERR_COMANDO_KEYWORD_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_KEYWORD_NULO_VACIO);
			}
			if (comandoRequestdto.getMac() == null || comandoRequestdto.getMac().isEmpty())
			{
				log.error(Constants.ERR_COMANDO_MAC_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_MAC_NULO_VACIO);
			}
			if (comandoRequestdto.getOrdenId() == null)
			{
				log.error(Constants.ERR_COMANDO_ORDEN_ID_NULO);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_ORDEN_ID_NULO);
			}

			Orden orden = this.ordenRepository.findById(comandoRequestdto.getOrdenId())
					.orElseThrow(() -> {
						log.error(Constants.ERR_COMANDO_ORDEN_NO_EXISTE);
						return new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_ORDEN_NO_EXISTE);
					});

			Comando comando = new Comando();

			ComandoActuadorId comandoActuadorId = new ComandoActuadorId();
			comandoActuadorId.setMac(comandoRequestdto.getMac());
			comandoActuadorId.setKeyword(comandoRequestdto.getKeyword());

			ComandoId comandoId = new ComandoId();
			comandoId.setOrdenId(comandoRequestdto.getOrdenId());
			comandoId.setComandoActuadorId(comandoActuadorId);

			comando.setComandoId(comandoId);
			comando.setOrden(orden);

			this.comandoRepository.saveAndFlush(comando);
			log.info(Constants.ELEMENTO_AGREGADO);

			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear comando", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/comando")
	public ResponseEntity<?> obtenerComandos()
	{
		try
		{
			List<ComandoResponseDto> lista = this.comandoRepository.buscarComandos();

			if (lista.isEmpty())
			{
				log.error(Constants.ERR_COMANDO_NO_ENCONTRADOS);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_NO_ENCONTRADOS);
			}

			return ResponseEntity.ok(lista);
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al obtener comandos", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/comando/orden")
	public ResponseEntity<?> obtenerComandosPorOrden(@RequestHeader("ordenId") Long ordenId)
	{
		try
		{
			if (ordenId == null)
			{
				log.error(Constants.ERR_COMANDO_ORDEN_ID_NULO);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_ORDEN_ID_NULO);
			}

			List<ComandoResponseDto> lista = this.comandoRepository.buscarComandosPorOrden(ordenId);

			if (lista.isEmpty())
			{
				log.error(Constants.ERR_COMANDO_NO_HAY_PARA_ORDEN);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_NO_HAY_PARA_ORDEN);
			}

			return ResponseEntity.ok(lista);
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al obtener comandos por orden", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/comando/mac")
	public ResponseEntity<?> obtenerComandosPorMac(@RequestHeader("mac") String mac)
	{
		try
		{
			if (mac == null || mac.isEmpty())
			{
				log.error(Constants.ERR_COMANDO_MAC_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_MAC_NULO_VACIO);
			}

			List<ComandoResponseDto> lista = this.comandoRepository.buscarComandosPorMac(mac);

			if (lista.isEmpty())
			{
				log.error(Constants.ERR_COMANDO_NO_HAY_PARA_MAC);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_NO_HAY_PARA_MAC);
			}

			return ResponseEntity.ok(lista);
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al obtener comandos por mac", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/comando")
	public ResponseEntity<?> eliminarComando(@RequestHeader("ordenId") Long ordenId, @RequestHeader("mac") String mac, @RequestHeader("keyword") String keyword)
	{
		try
		{
			if (ordenId == null || mac == null || mac.isEmpty() || keyword == null || keyword.isEmpty())
			{
				log.error(Constants.ERR_COMANDO_PARAMETROS_INVALIDOS);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_PARAMETROS_INVALIDOS);
			}

			ComandoActuadorId comandoActuadorId = new ComandoActuadorId(mac, keyword);
			ComandoId comandoId = new ComandoId(comandoActuadorId, ordenId);

			if (!this.comandoRepository.existsById(comandoId))
			{
				log.error(Constants.ERR_COMANDO_NO_EXISTE_CLAVE);
				throw new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_COMANDO_NO_EXISTE_CLAVE);
			}

			this.comandoRepository.deleteById(comandoId);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_COMANDO_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al eliminar comando", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA COMANDO_ACTUADOR ---
	// ----------------------------------------------------------------------------------

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/comando/actuador", consumes = "application/json")
	public ResponseEntity<?> crearComandoActuador(@RequestBody ComandoActuadorRequestDto comandoActuadorRequestDto)
	{
		try
		{
			if (comandoActuadorRequestDto.getKeyword() == null || comandoActuadorRequestDto.getKeyword().isEmpty())
			{
				log.error(Constants.ERR_COMANDO_ACTUADOR_KEYWORD_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_COMANDO_ACTUADOR_KEYWORD_NULO_VACIO);
			}

			if (comandoActuadorRequestDto.getMac() == null || comandoActuadorRequestDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_COMANDO_ACTUADOR_MAC_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_COMANDO_ACTUADOR_MAC_NULO_VACIO);
			}
			
			if (comandoActuadorRequestDto.getTextoOk() == null || comandoActuadorRequestDto.getTextoOk().isEmpty())
			{
				log.error(Constants.ERR_COMANDO_ACTUADOR_TEXTO_OK_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_COMANDO_ACTUADOR_TEXTO_OK_NULO_VACIO);
			}

			if (!this.actuadorRepository.existsById(comandoActuadorRequestDto.getMac()))
			{
				log.error(Constants.ERR_COMANDO_ACTUADOR_NO_EXISTE_MAC);
				throw new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_COMANDO_ACTUADOR_NO_EXISTE_MAC);
			}

			Actuador actuador = this.actuadorRepository.findById(comandoActuadorRequestDto.getMac()).get();

			ComandoActuadorId comandoActuadorId = new ComandoActuadorId();
			comandoActuadorId.setMac(comandoActuadorRequestDto.getMac());
			comandoActuadorId.setKeyword(comandoActuadorRequestDto.getKeyword());

			ComandoActuador comandoActuador = new ComandoActuador();
			comandoActuador.setComandoActuadorId(comandoActuadorId);
			comandoActuador.setTextoOk(comandoActuadorRequestDto.getTextoOk());
			comandoActuador.setComandos(comandoActuadorRequestDto.getComandos());

			comandoActuador.setActuador(actuador);

			this.comandoActuadorRepository.saveAndFlush(comandoActuador);
			log.info(Constants.ELEMENTO_AGREGADO);

			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear comando_actuador", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/comando/actuador")
	public ResponseEntity<?> obtenerComandosActuador(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
	{
	    try
	    {
	        Pageable pageable = PageRequest.of(page, size);
	        return ResponseEntity.ok(this.comandoActuadorRepository.buscarComandosActuadorPagina(pageable));
	   
	    }
	    catch (Exception exception)
	    {
	        AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_CODE);
	        log.error("Excepción genérica al obtener comandos_actuador", automationsException);
	        return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
	    }
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/comando/actuador")
	public ResponseEntity<?> eliminarComandoActuador(@RequestHeader("mac") String mac, @RequestHeader("keyword") String keyword)
	{
		try
		{
			if (mac == null || mac.isEmpty() || keyword == null || keyword.isEmpty())
			{
				log.error(Constants.ERR_COMANDO_ACTUADOR_PARAMETROS_INVALIDOS);
				throw new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_COMANDO_ACTUADOR_PARAMETROS_INVALIDOS);
			}

			ComandoActuadorId comandoActuadorId = new ComandoActuadorId(mac, keyword);

			if (!this.comandoActuadorRepository.existsById(comandoActuadorId))
			{
				log.error(Constants.ERR_COMANDO_ACTUADOR_NO_EXISTE_CLAVE);
				throw new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_COMANDO_ACTUADOR_NO_EXISTE_CLAVE);
			}

			this.comandoActuadorRepository.deleteById(comandoActuadorId);
			log.info(Constants.ELEMENTO_ELIMINADO);

			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_COMANDO_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al eliminar comando_actuador", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/accion")
	public ResponseEntity<?> obtenerAcciones(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
	{
		try
		{
			Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		     return ResponseEntity.ok(this.accionRepository.buscarAccionesPagina(pageable));
		}	
	    catch (Exception exception)
	    {
	        AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_CODE);
	        log.error("Excepción genérica al obtener la accion", automationsException);
	        return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
	    }
		
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/accion")
	public ResponseEntity<?> eliminarAccion(@RequestHeader("idAccion") Long idAccion)
	{
		try
		{
			if (!this.accionRepository.existsById(idAccion))
			{
				log.error(Constants.ERR_ACCION_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_ACCION_NO_EXISTE);
			}
			this.accionRepository.deleteById(idAccion);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok().body(Constants.ELEMENTO_ELIMINADO);
		} 
		catch (AutomationsServerException automationsException)
		{
			log.error(automationsException.getMessage());
			return ResponseEntity.badRequest().body(automationsException);
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/actuador/paginacion")
	public ResponseEntity<?> obtenerActuadorPaginacion(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
	{
		try
		{
			 Pageable pageable = PageRequest.of(page, size);
		     return ResponseEntity.ok(this.actuadorRepository.buscarActuadoresPagina(pageable));
		}	
	    catch (Exception exception)
	    {
	        AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
	        log.error("Excepción genérica al obtener actuadores", automationsException);
	        return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
	    }
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/sensor/booleano/paginacion")
	public ResponseEntity<?> obtenerSensoresBooleanosPaginacion(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
	{
		try
		{
			 Pageable pageable = PageRequest.of(page, size);
		     return ResponseEntity.ok(this.sensorBooleanoRepo.buscarSensoresBooleanosPagina(pageable));
		}	
	    catch (Exception exception)
	    {
	        AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
	        log.error("Excepción genérica al obtener sensores booleanos", automationsException);
	        return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
	    }
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/sensor/numerico/paginacion")
	public ResponseEntity<?> obtenerSensoresNumericosPaginacion(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
	{
		try
		{
			 Pageable pageable = PageRequest.of(page, size);
		     return ResponseEntity.ok(this.sensorNumericoRepo.buscarSensoresNumericosPagina(pageable));
		}	
	    catch (Exception exception)
	    {
	        AutomationsServerException automationsException = new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
	        log.error("Excepción genérica al obtener sensores numéricos", automationsException);
	        return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
	    }
	}
	
}