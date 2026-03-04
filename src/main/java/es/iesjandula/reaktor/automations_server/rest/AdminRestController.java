package es.iesjandula.reaktor.automations_server.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.ActuadorRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.ComandoActuadorRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.ComandoActuadorResponseDto;
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
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')") // Restringe el acceso solo a usuarios con rol
																			// ADMINISTRADOR
	@PostMapping(value = "/sensor/booleano", consumes = "application/json")
	public ResponseEntity<?> crearSensorBooleano(@RequestBody SensorBooleanoRequestDto sensorBooleanoDto)
	{
		try
		{
			// Validación: MAC no nula o vacía
			if (sensorBooleanoDto.getMac() == null || sensorBooleanoDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_SENSOR_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_SENSOR_NULO_VACIO, Constants.ERR_SENSOR_CODE);
			}

			// Si el dispositivo existe lo modificamos
			if (sensorBooleanoRepo.existsById(sensorBooleanoDto.getMac()))
			{

				SensorBooleano sensor = new SensorBooleano();
				sensor.setEstado(sensorBooleanoDto.getEstado());
				sensor.setUmbralMaximo(sensorBooleanoDto.getUmbralMaximo());
				sensor.setUmbralMinimo(sensorBooleanoDto.getUmbralMinimo());
				sensor.setTipo(sensorBooleanoDto.getTipo());
				sensor.setNombreUbicacion(sensorBooleanoDto.getNombreUbicacion());
				;
			}

			String mac = sensorBooleanoDto.getMac();

			// Si existe como actuador → borra
			if (actuadorRepository.existsById(mac))
			{
				actuadorRepository.deleteById(mac);
			}
			// Si existe como sensor numérico → borra
			if (sensorNumericoRepo.existsById(mac))
			{
				sensorNumericoRepo.deleteById(mac);
			}

			// Validación: Nombre de Ubicación no nulo o vacío
			if (sensorBooleanoDto.getNombreUbicacion() == null || sensorBooleanoDto.getNombreUbicacion().isEmpty())
			{
				log.error(Constants.ERR_UBICACION_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_UBICACION_NULO_VACIO,
						Constants.ERR_UBICACION_CODE);
			}

			// Mapeo del DTO a la entidad SensorBooleano
			SensorBooleano sensor = new SensorBooleano();
			sensor.setMac(sensorBooleanoDto.getMac());
			sensor.setEstado(sensorBooleanoDto.getEstado());
			sensor.setUmbralMaximo(sensorBooleanoDto.getUmbralMaximo());
			sensor.setUmbralMinimo(sensorBooleanoDto.getUmbralMinimo());
			sensor.setTipo(sensorBooleanoDto.getTipo());
			sensor.setNombreUbicacion(sensorBooleanoDto.getNombreUbicacion());

			// Guardar el nuevo sensor en la base de datos
			sensorBooleanoRepo.saveAndFlush(sensor);

			log.info(Constants.ELEMENTO_AGREGADO);

			return ResponseEntity.ok().build(); // Devuelve 200 OK
		}
		catch (AutomationsServerException AutomationsServerException)
		{
			// Manejo de errores controlados
			return ResponseEntity.badRequest().body(AutomationsServerException); // Devuelve 400 Bad Request
		}
		catch (Exception exception)
		{
			// Manejo de errores inesperados del sistema
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
	@DeleteMapping("/sensor/booleano/{mac}")
	public ResponseEntity<?> eliminarSensorBooleano(@PathVariable String mac)
	{
		try
		{
			// Validación: El sensor debe existir
			if (!sensorBooleanoRepo.existsById(mac))
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			// Eliminación del sensor
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
			// Validaciones (MAC, Existencia, Ubicación)
			if (sensorNumericoDto.getMac() == null || sensorNumericoDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_SENSOR_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_SENSOR_NULO_VACIO, Constants.ERR_SENSOR_CODE);
			}

			// Si el dispositivo existe lo modificamos
			if (sensorNumericoRepo.existsById(sensorNumericoDto.getMac()))
			{

				SensorNumerico sensor = new SensorNumerico();
				sensor.setEstado(sensorNumericoDto.getEstado());
				sensor.setUmbralMaximo(sensorNumericoDto.getUmbralMaximo());
				sensor.setUmbralMinimo(sensorNumericoDto.getUmbralMinimo());
				sensor.setTipo(sensorNumericoDto.getTipo());
				sensor.setNombreUbicacion(sensorNumericoDto.getNombreUbicacion());
				;
			}

			String mac = sensorNumericoDto.getMac();

			// Si existe como actuador → borra
			if (actuadorRepository.existsById(mac))
			{
				actuadorRepository.deleteById(mac);
			}
			// Si existe como sensor booleano → borra
			if (sensorBooleanoRepo.existsById(mac))
			{
				sensorBooleanoRepo.deleteById(mac);
			}

			if (sensorNumericoDto.getNombreUbicacion() == null || sensorNumericoDto.getNombreUbicacion().isEmpty())
			{
				log.error(Constants.ERR_UBICACION_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_UBICACION_NULO_VACIO,
						Constants.ERR_UBICACION_CODE);
			}

			// Mapeo del DTO a la entidad SensorNumerico
			SensorNumerico sensor = new SensorNumerico();
			sensor.setMac(sensorNumericoDto.getMac());
			sensor.setEstado(sensorNumericoDto.getEstado());
			sensor.setUmbralMinimo(sensorNumericoDto.getUmbralMinimo());
			sensor.setUmbralMaximo(sensorNumericoDto.getUmbralMaximo());
			sensor.setTipo(sensorNumericoDto.getTipo());
			sensor.setNombreUbicacion(sensorNumericoDto.getNombreUbicacion());
			;

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
	@DeleteMapping("/sensor/numerico/{mac}")
	public ResponseEntity<?> eliminarSensorNumerico(@PathVariable String mac)
	{
		try
		{
			// Validación: El sensor debe existir
			if (!sensorNumericoRepo.existsById(mac))
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			// Eliminación del sensor
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
			// Validaciones (MAC y Existencia)
			if (actuadorRequestDto.getMac() == null || actuadorRequestDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_ACTUADOR_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_NULO_VACIO,
						Constants.ERR_ACTUADOR_CODE);
			}

			// Si el dispositivo existe lo modificamos
			if (actuadorRepository.existsById(actuadorRequestDto.getMac()))
			{

				Actuador actuador = new Actuador();
				actuador.setEstado(actuadorRequestDto.getEstado());
				actuador.setTipo(actuador.getTipo());
				actuador.setNombreUbicacion(actuadorRequestDto.getNombreUbicacion());
				;
			}

			String mac = actuadorRequestDto.getMac();

			// Si existe como sensor booleano → borra
			if (sensorBooleanoRepo.existsById(mac))
			{
				sensorBooleanoRepo.deleteById(mac);
			}

			// Si existe como sensor numérico → borra
			if (sensorNumericoRepo.existsById(mac))
			{
				sensorNumericoRepo.deleteById(mac);
			}

			// Mapeo y guardado
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
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
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
			// Llama al repositorio para obtener la lista de actuadores (DTOs)
			List<ActuadorResponseDto> actuadores = this.actuadorRepository.buscarActuadores();

			// Validación de campos críticos de cada actuador
			for (ActuadorResponseDto a : actuadores)
			{
				// MAC no puede ser nula o vacía
				if (a.getMac() == null || a.getMac().isEmpty())
				{
					throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE,
							"Existe un actuador sin dirección MAC");
				}
				// Ubicación no puede ser nula o vacía
				if (a.getNombreUbicacion() == null || a.getNombreUbicacion().isEmpty())
				{
					throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE,
							"Existe un actuador sin ubicación asignada");
				}
			}

			// Devuelve la lista de actuadores en la respuesta HTTP 200 OK
			return ResponseEntity.ok(actuadores);

		}
		catch (AutomationsServerException AutomationsServerException)
		{
			// Captura las excepciones controladas y devuelve HTTP 400 con el detalle
			return ResponseEntity.badRequest().body(AutomationsServerException.getBodyExceptionMessage());
		}
		catch (Exception exception)
		{
			// Captura excepciones inesperadas y devuelve HTTP 500 con detalle
			AutomationsServerException AutomationsServerException =
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
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
	@DeleteMapping(value = "/actuador/{mac}")
	public ResponseEntity<?> eliminarActuador(@PathVariable String mac)
	{
		try
		{
			// Validación: El actuador debe existir
			if (!this.actuadorRepository.existsById(mac))
			{
				log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE,
						Constants.ERR_ACTUADOR_NO_EXISTE);
			}
			// Eliminación
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
					new AutomationsServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
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
	@GetMapping(value = "/tipo")
	public ResponseEntity<?> obtenerTipos()
	{
		try
		{
			return ResponseEntity.ok(Constants.APLICABILIDAD);
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
				throw new AutomationsServerException("Keyword vacía o nula", "ERR_COMANDO");
			}
			if (comandoRequestdto.getMac() == null || comandoRequestdto.getMac().isEmpty())
			{
				throw new AutomationsServerException("MAC vacía o nula", "ERR_COMANDO");
			}
			if (comandoRequestdto.getOrdenId() == null)
			{
				throw new AutomationsServerException("OrdenId nulo", "ERR_COMANDO");
			}

			Orden orden = this.ordenRepository.findById(comandoRequestdto.getOrdenId())
					.orElseThrow(() -> new AutomationsServerException("No existe esa orden", "ERR_COMANDO"));

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

			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException ex)
		{
			return ResponseEntity.badRequest().body(ex);
		}
		catch (Exception exception)
		{
			AutomationsServerException ex = new AutomationsServerException("ERR_COMANDO", Constants.ERR_CODE);
			log.error("Excepción genérica al crear comando", ex);
			return ResponseEntity.status(500).body(ex.getBodyExceptionMessage());
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
				throw new AutomationsServerException("ERR_COMANDO", "No se encontraron comandos");
			}

			return ResponseEntity.ok(lista);
		}
		catch (AutomationsServerException ex)
		{
			return ResponseEntity.badRequest().body(ex);
		}
		catch (Exception exception)
		{
			AutomationsServerException ex = new AutomationsServerException("ERR_COMANDO", Constants.ERR_CODE);
			log.error("Excepción genérica al obtener comandos", ex);
			return ResponseEntity.status(500).body(ex.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/comando/orden/{ordenId}")
	public ResponseEntity<?> obtenerComandosPorOrden(@PathVariable Long ordenId)
	{
		try
		{
			if (ordenId == null)
			{
				throw new AutomationsServerException("OrdenId nulo", "ERR_COMANDO");
			}

			List<ComandoResponseDto> lista = this.comandoRepository.buscarComandosPorOrden(ordenId);

			if (lista.isEmpty())
			{
				throw new AutomationsServerException("ERR_COMANDO", "No hay comandos para esa orden");
			}

			return ResponseEntity.ok(lista);
		}
		catch (AutomationsServerException ex)
		{
			return ResponseEntity.badRequest().body(ex);
		}
		catch (Exception exception)
		{
			AutomationsServerException ex = new AutomationsServerException("ERR_COMANDO", Constants.ERR_CODE);
			log.error("Excepción genérica al obtener comandos por orden", ex);
			return ResponseEntity.status(500).body(ex.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/comando/mac/{mac}")
	public ResponseEntity<?> obtenerComandosPorMac(@PathVariable String mac)
	{
		try
		{
			if (mac == null || mac.isEmpty())
			{
				throw new AutomationsServerException("MAC vacía o nula", "ERR_COMANDO");
			}

			List<ComandoResponseDto> lista = this.comandoRepository.buscarComandosPorMac(mac);

			if (lista.isEmpty())
			{
				throw new AutomationsServerException("ERR_COMANDO", "No hay comandos para esa MAC");
			}

			return ResponseEntity.ok(lista);
		}
		catch (AutomationsServerException ex)
		{
			return ResponseEntity.badRequest().body(ex);
		}
		catch (Exception exception)
		{
			AutomationsServerException ex = new AutomationsServerException("ERR_COMANDO", Constants.ERR_CODE);
			log.error("Excepción genérica al obtener comandos por mac", ex);
			return ResponseEntity.status(500).body(ex.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/comando/{ordenId}/{mac}/{keyword}")
	public ResponseEntity<?> eliminarComando(@PathVariable Long ordenId, @PathVariable String mac, @PathVariable String keyword)
	{
		try
		{
			if (ordenId == null || mac == null || mac.isEmpty() || keyword == null || keyword.isEmpty())
			{
				throw new AutomationsServerException("ERR_COMANDO", "Parámetros inválidos");
			}

			ComandoActuadorId comandoActuadorId = new ComandoActuadorId(mac, keyword);
			ComandoId comandoId = new ComandoId(comandoActuadorId, ordenId);

			if (!this.comandoRepository.existsById(comandoId))
			{
				throw new AutomationsServerException("ERR_COMANDO", "No existe comando con esa clave");
			}

			this.comandoRepository.deleteById(comandoId);
			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);
		}
		catch (AutomationsServerException ex)
		{
			return ResponseEntity.badRequest().body(ex);
		}
		catch (Exception exception)
		{
			AutomationsServerException ex = new AutomationsServerException("ERR_COMANDO", Constants.ERR_CODE);
			log.error("Excepción genérica al eliminar comando", ex);
			return ResponseEntity.status(500).body(ex.getBodyExceptionMessage());
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
				// TODO poner logs
				throw new AutomationsServerException("Keyword vacía o nula", "ERR_COMANDO_ACTUADOR");
			}

			if (comandoActuadorRequestDto.getMac() == null || comandoActuadorRequestDto.getMac().isEmpty())
			{
				throw new AutomationsServerException("MAC vacía o nula", "ERR_COMANDO_ACTUADOR");
			}
			
			if (comandoActuadorRequestDto.getTextoOk() == null || comandoActuadorRequestDto.getTextoOk().isEmpty())
			{
				throw new AutomationsServerException("Información de respuesta correcta vacío o nulo", "ERR_COMANDO_ACTUADOR");
			}

			// ✅ CAMBIO NECESARIO: con tu modelo ComandoActuador (@ManyToOne @MapsId("mac"))
			// necesitas setear el actuador para que Hibernate pueda persistir bien el FK/PK.
			if (!this.actuadorRepository.existsById(comandoActuadorRequestDto.getMac()))
			{
				throw new AutomationsServerException("No existe actuador con esa MAC", "ERR_COMANDO_ACTUADOR");
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

			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException ex)
		{
			return ResponseEntity.badRequest().body(ex);
		}
		catch (Exception exception)
		{
			AutomationsServerException ex = new AutomationsServerException("ERR_COMANDO_ACTUADOR", Constants.ERR_CODE);
			log.error("Excepción genérica al crear comando_actuador", ex);
			return ResponseEntity.status(500).body(ex.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/comando/actuador")
	public ResponseEntity<?> obtenerComandosActuador()
	{
	    try
	    {
	        List<ComandoActuadorResponseDto> lista = this.comandoActuadorRepository.buscarComandosActuador();

	        return ResponseEntity.ok(lista);
	   
	    }
	    catch (Exception exception)
	    {
	        AutomationsServerException ex = new AutomationsServerException("ERR_COMANDO_ACTUADOR", Constants.ERR_CODE);
	        log.error("Excepción genérica al obtener comandos_actuador", ex);
	        return ResponseEntity.status(500).body(ex.getBodyExceptionMessage());
	    }
	}
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/comando/actuador/{mac}/{keyword}")
	public ResponseEntity<?> eliminarComandoActuador(@PathVariable String mac, @PathVariable String keyword)
	{
		try
		{
			if (mac == null || mac.isEmpty() || keyword == null || keyword.isEmpty())
			{
				throw new AutomationsServerException("Parámetros inválidos", "ERR_COMANDO_ACTUADOR");
			}

			ComandoActuadorId comandoActuadorId = new ComandoActuadorId(mac, keyword);

			if (!this.comandoActuadorRepository.existsById(comandoActuadorId))
			{
				throw new AutomationsServerException("No existe comando_actuador con esa clave", "ERR_COMANDO_ACTUADOR");
			}

			this.comandoActuadorRepository.deleteById(comandoActuadorId);

			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);
		}
		catch (AutomationsServerException ex)
		{
			return ResponseEntity.badRequest().body(ex);
		}
		catch (Exception exception)
		{
			AutomationsServerException ex = new AutomationsServerException("ERR_COMANDO_ACTUADOR", Constants.ERR_CODE);
			log.error("Excepción genérica al eliminar comando_actuador", ex);
			return ResponseEntity.status(500).body(ex.getBodyExceptionMessage());
		}
	}
}