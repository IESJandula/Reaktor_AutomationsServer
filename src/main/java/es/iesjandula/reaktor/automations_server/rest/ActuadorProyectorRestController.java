package es.iesjandula.reaktor.automations_server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.ActuadorProyectorRequestDto;
import es.iesjandula.reaktor.automations_server.models.ActuadorProyector;
import es.iesjandula.reaktor.automations_server.repository.IActuadorProyectorRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorPuertaRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorNumericoRpository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/automations/admin/actuador/proyector")
public class ActuadorProyectorRestController
{

	@Autowired
	private IActuadorPuertaRepository actuadorPuertaRepository; 
	
	@Autowired
	private IActuadorProyectorRepository actuadorProyectorRepository;

	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;

	@Autowired
	private IActuadorRepository actuadorRepository;

	
	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA ACTUADOR PROYECTOR ---
	// ----------------------------------------------------------------------------------

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/", consumes = "application/json")
	public ResponseEntity<?> crearActuadorProyector(@RequestBody(required = true) ActuadorProyectorRequestDto actuadorProyectorRequestDto)
	{
		try
		{
			if (actuadorProyectorRequestDto.getMac() == null || actuadorProyectorRequestDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_ACTUADOR_MAC_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_MAC_NULO_VACIO);
			}

			String mac = actuadorProyectorRequestDto.getMac();
			ActuadorProyector actuadorProyector = null;

			if (this.actuadorProyectorRepository.existsById(mac))
			{
				actuadorProyector = this.actuadorProyectorRepository.findById(mac).get();
			}
			else
			{
				if (sensorBooleanoRepo.existsById(mac))
				{
					sensorBooleanoRepo.deleteById(mac);
				}

				if (sensorNumericoRepo.existsById(mac))
				{
					sensorNumericoRepo.deleteById(mac);
				}

				if (actuadorPuertaRepository.existsById(mac))
				{
					actuadorPuertaRepository.deleteById(mac);
				}

				if (actuadorRepository.existsById(mac))
				{
					actuadorRepository.deleteById(mac);
				}

				actuadorProyector = new ActuadorProyector();
				actuadorProyector.setMac(mac);
			}

			actuadorProyector.setEstado(actuadorProyectorRequestDto.getEstado());
			actuadorProyector.setTipo(actuadorProyectorRequestDto.getTipo());
			actuadorProyector.setNombreUbicacion(actuadorProyectorRequestDto.getNombreUbicacion());
			actuadorProyector.setComandoEstado(actuadorProyectorRequestDto.getComandoEstado());

			this.actuadorProyectorRepository.saveAndFlush(actuadorProyector);

			log.info(Constants.ELEMENTO_AGREGADO);
			return ResponseEntity.ok().build();
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al crear actuador proyector", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/")
	public ResponseEntity<?> obtenerActuadoresProyector()
	{
		try
		{
			return ResponseEntity.ok(this.actuadorProyectorRepository.findAll());
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al obtener actuadores proyector", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/")
	public ResponseEntity<?> eliminarActuadorProyector(@RequestHeader("mac") String mac)
	{
		try
		{
			if (!this.actuadorProyectorRepository.existsById(mac))
			{
				log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NO_EXISTE);
			}

			this.actuadorProyectorRepository.deleteById(mac);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok().body(Constants.ELEMENTO_ELIMINADO);
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al eliminar actuador proyector", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

}
