package es.iesjandula.reaktor.automations_school_server.rest;

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

import es.iesjandula.reaktor.automations_school_server.dtos.ActuadorRequestDto;
import es.iesjandula.reaktor.automations_school_server.dtos.ActuadorResponseDto;
import es.iesjandula.reaktor.automations_school_server.dtos.SensorBooleanoRequestDto;
import es.iesjandula.reaktor.automations_school_server.dtos.SensorBooleanoResponseDto;
import es.iesjandula.reaktor.automations_school_server.dtos.SensorNumericoRequestDto;
import es.iesjandula.reaktor.automations_school_server.dtos.SensorNumericoResponseDto;
import es.iesjandula.reaktor.automations_school_server.dtos.UbicacionResponseDto;
import es.iesjandula.reaktor.automations_school_server.models.Actuador;
import es.iesjandula.reaktor.automations_school_server.models.SensorBooleano;
import es.iesjandula.reaktor.automations_school_server.models.SensorNumerico;
import es.iesjandula.reaktor.automations_school_server.models.Ubicacion;
import es.iesjandula.reaktor.automations_school_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_school_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_school_server.repository.ISensorNumericoRpository;
import es.iesjandula.reaktor.automations_school_server.repository.IUbicacionRepository;
import es.iesjandula.reaktor.automations_school_server.utils.AutomationSchoolServerException;
import es.iesjandula.reaktor.automations_school_server.utils.Constants;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para operaciones de administración (CRUD) de dispositivos
 * (Sensores y Actuadores) y Ubicaciones. Requiere el rol de ADMINISTRADOR para
 * todos sus endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/automations_school/admin")
public class AdminRestController
{

	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;

	@Autowired
	private IUbicacionRepository ubicacionRepository;

	@Autowired
	private IActuadorRepository actuadorRepository;

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
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_NULO_VACIO, Constants.ERR_SENSOR_CODE);
			}

			// Validación: El sensor no debe existir previamente
			if (sensorBooleanoRepo.existsById(sensorBooleanoDto.getMac()))
			{
				log.error(Constants.ERR_SENSOR_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_EXISTE, Constants.ERR_SENSOR_CODE);
			}

			// Validación: Nombre de Ubicación no nulo o vacío
			if (sensorBooleanoDto.getNombreUbicacion() == null || sensorBooleanoDto.getNombreUbicacion().isEmpty())
			{
				log.error(Constants.ERR_UBICACION_NULO_VACIO);
				throw new AutomationSchoolServerException(Constants.ERR_UBICACION_NULO_VACIO,
						Constants.ERR_UBICACION_CODE);
			}

			// Busca la Ubicación por nombre. Si no existe, lanza excepción.
			Ubicacion ubicacion = ubicacionRepository.findById(sensorBooleanoDto.getNombreUbicacion())
					.orElseThrow(() -> new AutomationSchoolServerException(Constants.ERR_UBICACION_NO_EXISTE,
							Constants.ERR_UBICACION_CODE));

			// Mapeo del DTO a la entidad SensorBooleano
			SensorBooleano sensor = new SensorBooleano();
			sensor.setMac(sensorBooleanoDto.getMac());
			sensor.setEstado(sensorBooleanoDto.getEstado());
			sensor.setUmbralMaximo(sensorBooleanoDto.getUmbralMaximo());
			sensor.setUmbralMinimo(sensorBooleanoDto.getUmbralMinimo());
			sensor.setUbicacion(ubicacion);

			// Guardar el nuevo sensor en la base de datos
			sensorBooleanoRepo.saveAndFlush(sensor);

			log.info(Constants.ELEMENTO_AGREGADO);

			return ResponseEntity.ok().build(); // Devuelve 200 OK
		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			// Manejo de errores controlados
			return ResponseEntity.badRequest().body(automationSchoolServerException); // Devuelve 400 Bad Request
		} 
		catch (Exception exception)
		{
			// Manejo de errores inesperados del sistema
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationSchoolServerException );
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
																						
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
			List<SensorBooleanoResponseDto> sensores =
					this.sensorBooleanoRepo.buscarSensoresBooleanos();

			if (sensores.isEmpty())
			{
				throw new AutomationSchoolServerException(
						Constants.ERR_SENSOR_CODE,
						"No se encontraron sensores booleanos"
				);
			}

			return ResponseEntity.ok(sensores);
		}
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		}
		catch (Exception exception)
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica", automationSchoolServerException);
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
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
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			// Eliminación del sensor
			sensorBooleanoRepo.deleteById(mac);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);

		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		} 
		catch (Exception exception)
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationSchoolServerException );
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
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
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_NULO_VACIO, Constants.ERR_SENSOR_CODE);
			}

			if (sensorNumericoRepo.existsById(sensorNumericoDto.getMac()))
			{
				log.error(Constants.ERR_SENSOR_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_EXISTE, Constants.ERR_SENSOR_CODE);
			}

			if (sensorNumericoDto.getNombreUbicacion() == null || sensorNumericoDto.getNombreUbicacion().isEmpty())
			{
				log.error(Constants.ERR_UBICACION_NULO_VACIO);
				throw new AutomationSchoolServerException(Constants.ERR_UBICACION_NULO_VACIO,
						Constants.ERR_UBICACION_CODE);
			}

			// Búsqueda de Ubicación
			Ubicacion ubicacion = ubicacionRepository.findById(sensorNumericoDto.getNombreUbicacion())
					.orElseThrow(() -> new AutomationSchoolServerException(Constants.ERR_UBICACION_NO_EXISTE,
							Constants.ERR_UBICACION_CODE));

			// Mapeo del DTO a la entidad SensorNumerico
			SensorNumerico sensor = new SensorNumerico();
			sensor.setMac(sensorNumericoDto.getMac());
			sensor.setEstado(sensorNumericoDto.getEstado());
			sensor.setUmbralMinimo(sensorNumericoDto.getUmbralMinimo());
			sensor.setUmbralMaximo(sensorNumericoDto.getUmbralMaximo());
			sensor.setUbicacion(ubicacion);

			sensorNumericoRepo.saveAndFlush(sensor);
			log.info(Constants.ELEMENTO_AGREGADO);

			return ResponseEntity.ok().build();
		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		} 
		catch (Exception exception)
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationSchoolServerException );
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
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
			List<SensorNumericoResponseDto> sensores =
					this.sensorNumericoRepo.buscarSensoresNumericos();

			if (sensores.isEmpty())
			{
				throw new AutomationSchoolServerException(
						Constants.ERR_SENSOR_CODE,
						"No se encontraron sensores numéricos"
				);
			}

			return ResponseEntity.ok(sensores);
		}
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		}
		catch (Exception exception)
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica", automationSchoolServerException);
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
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
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

			// Eliminación del sensor
			sensorNumericoRepo.deleteById(mac);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);

		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		} 
		catch (Exception exception)
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationSchoolServerException );
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
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
				throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_NULO_VACIO,
						Constants.ERR_ACTUADOR_CODE);
			}
			if (this.actuadorRepository.existsById(actuadorRequestDto.getMac()))
			{
				log.error(Constants.ERR_ACTUADOR_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_EXISTE, Constants.ERR_ACTUADOR_CODE);
			}

			// Búsqueda de Ubicación
			Ubicacion ubicacion = ubicacionRepository.findById(actuadorRequestDto.getNombreUbicacion())
					.orElseThrow(() -> new AutomationSchoolServerException(Constants.ERR_UBICACION_NO_EXISTE,
							Constants.ERR_UBICACION_CODE));

			// Mapeo y guardado
			Actuador actuador = new Actuador();
			actuador.setMac(actuadorRequestDto.getMac());
			actuador.setEstado(actuadorRequestDto.getEstado());
			actuador.setUbicacion(ubicacion);
			this.actuadorRepository.saveAndFlush(actuador);

			log.info(Constants.ELEMENTO_AGREGADO);
			return ResponseEntity.ok().build();
		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		} 
		catch (Exception exception)
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationSchoolServerException );
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
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

			// Validación: si la lista está vacía, lanza excepción personalizada
			if (actuadores.isEmpty())
			{
				throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_CODE,
						"No se encontraron actuadores registrados");
			}

			// Validación de campos críticos de cada actuador
			for (ActuadorResponseDto a : actuadores)
			{
				// MAC no puede ser nula o vacía
				if (a.getMac() == null || a.getMac().isEmpty())
				{
					throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_CODE,
							"Existe un actuador sin dirección MAC");
				}
				// Ubicación no puede ser nula o vacía
				if (a.getUbicacionNombre() == null || a.getUbicacionNombre().isEmpty())
				{
					throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_CODE,
							"Existe un actuador sin ubicación asignada");
				}
			}

			// Devuelve la lista de actuadores en la respuesta HTTP 200 OK
			return ResponseEntity.ok(actuadores);

		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			// Captura las excepciones controladas y devuelve HTTP 400 con el detalle
			return ResponseEntity.badRequest().body(automationSchoolServerException.getBodyExceptionMessage());
		} 
		catch (Exception exception)
		{
			// Captura excepciones inesperadas y devuelve HTTP 500 con detalle
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationSchoolServerException );
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
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
				throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_CODE,
						Constants.ERR_ACTUADOR_NO_EXISTE);
			}
			// Eliminación
			this.actuadorRepository.deleteById(mac);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok().body(Constants.ELEMENTO_ELIMINADO);
		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		} 
		catch (Exception exception)
		{
			log.error("Internal Server Error");
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationSchoolServerException );
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
		}
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA UBICACION ---
	// ----------------------------------------------------------------------------------

	/**
	 * Endpoint para obtener la lista de todas las Ubicaciones.
	 * 
	 * @return ResponseEntity con la lista de Ubicaciones.
	 */
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/ubicacion")
	public ResponseEntity<?> obtenerUbicacion()
	{
		try
		{
			// Valida si la lista de Ubicacion no este vacia
			List<UbicacionResponseDto> ubicaciones = this.ubicacionRepository.buscarUbicaciones();
			if (ubicaciones.isEmpty())
			{
				throw new AutomationSchoolServerException(Constants.ERR_UBICACION_CODE,
						"No se encontraron ubicaciones");
			}

			// Asume que el repositorio tiene un método customizado 'buscarUbicaciones' que
			// devuelve los DTOs correctos.
			return ResponseEntity.ok(ubicaciones);
		} 
		catch (AutomationSchoolServerException automationSchoolServerException)
		{
			return ResponseEntity.badRequest().body(automationSchoolServerException);
		} 
		catch (Exception exception)
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationSchoolServerException );
			return ResponseEntity.status(500).body(automationSchoolServerException.getBodyExceptionMessage());
		}
	}
}