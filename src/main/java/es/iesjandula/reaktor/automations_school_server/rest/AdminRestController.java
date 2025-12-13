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
import es.iesjandula.reaktor.automations_school_server.dtos.SensorBooleanoRequestDto;
import es.iesjandula.reaktor.automations_school_server.dtos.SensorBooleanoResponseDto;
import es.iesjandula.reaktor.automations_school_server.dtos.SensorNumericoRequestDto;
import es.iesjandula.reaktor.automations_school_server.dtos.SensorNumericoResponseDto;
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
	private IUbicacionRepository ubicacionRepo;

	@Autowired
	private IActuadorRepository actuadorRepository;
	
	@Autowired
	private IUbicacionRepository ubicacionRepository;
	
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/sensor/booleano", consumes = "application/json")
	public ResponseEntity<?> crearSensorBooleano(@RequestBody SensorBooleanoRequestDto sensorBooleanoDto)
	{
		try
		{
			if (sensorBooleanoDto.getMac() == null || sensorBooleanoDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_SENSOR_NULO_VACIO);
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_NULO_VACIO, Constants.ERR_SENSOR_CODE);
			}

			if (sensorBooleanoRepo.existsById(sensorBooleanoDto.getMac()))
			{
				log.error(Constants.ERR_SENSOR_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_EXISTE, Constants.ERR_SENSOR_CODE);
			}

			if (sensorBooleanoDto.getNombreUbicacion() == null || sensorBooleanoDto.getNombreUbicacion().isEmpty())
			{
				log.error(Constants.ERR_UBICACION_NULO_VACIO);
				throw new AutomationSchoolServerException(Constants.ERR_UBICACION_NULO_VACIO, Constants.ERR_UBICACION_CODE);
			}

			Ubicacion ubicacion = ubicacionRepo.findById(sensorBooleanoDto.getNombreUbicacion())
					.orElseThrow(() -> new AutomationSchoolServerException(Constants.ERR_UBICACION_NO_EXISTE, Constants.ERR_UBICACION_CODE));

			SensorBooleano sensor = new SensorBooleano();
			sensor.setMac(sensorBooleanoDto.getMac());
			sensor.setEstado(sensorBooleanoDto.getEstado());
			sensor.setValorActual(sensorBooleanoDto.getValorActual());
			sensor.setUbicacion(ubicacion);

			sensorBooleanoRepo.saveAndFlush(sensor);

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
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
		
	}
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping("/sensor/booleano")
	public ResponseEntity<?> obtenerSensoresBooleanos()
	{
		
		try
		{
			List<SensorBooleanoResponseDto> lista = sensorBooleanoRepo
					.findAll().stream().map(s -> new SensorBooleanoResponseDto(s.getMac(), 
							s.getEstado(),
							s.getValorActual(), 
							s.getUltimaActualizacion().getTime(), 
							s.getUbicacion().getNombreUbicacion())).toList();
			
			return ResponseEntity.ok(lista);
		}	
		catch (Exception exception) 
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_CODE);
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
	}
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping("/sensor/booleano/{mac}")
	public ResponseEntity<?> eliminarSensorBooleano(@PathVariable String mac)
	{
		try
		{
			if (!sensorBooleanoRepo.existsById(mac))
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

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
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
	}
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/sensor/numerico", consumes = "application/json")
	public ResponseEntity<?> crearSensorNumerico(@RequestBody SensorNumericoRequestDto sensorNumericoDto)
	{
		try
		{
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
				throw new AutomationSchoolServerException(Constants.ERR_UBICACION_NULO_VACIO, Constants.ERR_UBICACION_CODE);
			}

			Ubicacion ubicacion = ubicacionRepo.findById(sensorNumericoDto.getNombreUbicacion())
					.orElseThrow(() -> new AutomationSchoolServerException(Constants.ERR_UBICACION_NO_EXISTE, Constants.ERR_UBICACION_CODE));

			SensorNumerico sensor = new SensorNumerico();
			sensor.setMac(sensorNumericoDto.getMac());
			sensor.setEstado(sensorNumericoDto.getEstado());
			sensor.setValorActual(sensorNumericoDto.getValorActual());
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
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
	}
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping("/sensor/numerico")
	public ResponseEntity<?> obtenerSensoresNumericos()
	{
		List<SensorNumericoResponseDto> lista = sensorNumericoRepo.findAll().stream()
				.map(s -> new SensorNumericoResponseDto(
						s.getMac(), 
						s.getEstado(), 
						s.getValorActual(),
						s.getUmbralMinimo(), 
						s.getUmbralMaximo(), 
						s.getUltimaActualizacion().getTime(),
						s.getUbicacion().getNombreUbicacion())).toList();
		
		return ResponseEntity.ok(lista);
	}
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping("/sensor/numerico/{mac}")
	public ResponseEntity<?> eliminarSensorNumerico(@PathVariable String mac)
	{
		try
		{
			if (!sensorNumericoRepo.existsById(mac))
			{
				log.error(Constants.ERR_SENSOR_NO_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_SENSOR_CODE, Constants.ERR_SENSOR_NO_EXISTE);
			}

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
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
	}
	
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
    @PostMapping(value = "/actuador", consumes = "application/json")
    public ResponseEntity<?> crearActuador(@RequestBody(required = true) ActuadorRequestDto actuadorRequestDto) 
    {
        try 
        {
            if (actuadorRequestDto.getMac() == null || actuadorRequestDto.getMac().isEmpty()) 
            {
                log.error(Constants.ERR_ACTUADOR_NULO_VACIO);
                throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_NULO_VACIO, Constants.ERR_ACTUADOR_CODE);
            }
            if (this.actuadorRepository.existsById(actuadorRequestDto.getMac())) 
            {
                log.error(Constants.ERR_ACTUADOR_EXISTE);
                throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_EXISTE, Constants.ERR_ACTUADOR_CODE);
            }
            
            Ubicacion ubicacion = ubicacionRepo.findById(actuadorRequestDto.getNombreUbicacion())
					.orElseThrow(() -> new AutomationSchoolServerException(Constants.ERR_UBICACION_NO_EXISTE, Constants.ERR_UBICACION_CODE));
            
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
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
    }
    
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
    @GetMapping(value = "/actuador")
    public ResponseEntity<?> obtenerActuador() 
    {
    	try
		{
    		return ResponseEntity.ok(this.actuadorRepository.buscarActuadores());
			
		} 
		catch (Exception exception) 
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
    }
    
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
    @DeleteMapping(value = "/actuador/{mac}")
    public ResponseEntity<?> eliminarActuador(@PathVariable String mac) 
    {
        try 
        {
            if (!this.actuadorRepository.existsById(mac)) 
            {
                log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
                throw new AutomationSchoolServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NO_EXISTE); 
            }
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
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
    }
    
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
    @GetMapping(value = "/ubicacion")
    public ResponseEntity<?> obtenerUbicacion() 
    {
    	try
		{
    		return ResponseEntity.ok(this.ubicacionRepository.buscarUbicaciones());
			
		} 
		catch (Exception exception) 
		{
			AutomationSchoolServerException automationSchoolServerException = new AutomationSchoolServerException(Constants.ERR_UBICACION_CODE, Constants.ERR_CODE);
			return ResponseEntity.status(500).body(automationSchoolServerException); 
		}
    }
}
