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

import es.iesjandula.reaktor.automations_server.dtos.ActuadorPuertaRequestDto;
import es.iesjandula.reaktor.automations_server.dtos.ComandoActuadorPuertaRequestDto;
import es.iesjandula.reaktor.automations_server.models.ActuadorPuerta;
import es.iesjandula.reaktor.automations_server.models.ComandoActuador;
import es.iesjandula.reaktor.automations_server.models.ComandoActuadorPuerta;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorId;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorPuertaId;
import es.iesjandula.reaktor.automations_server.repository.IActuadorProyectorRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorPuertaRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoActuadorPuertaRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorNumericoRpository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/automations/admin/actuador/puerta")
public class ActuadorPuertaRestController
{

	@Autowired
	private IActuadorProyectorRepository actuadorProyectorRepository;

	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;

	@Autowired
	private IActuadorRepository actuadorRepository;

	@Autowired
	private IComandoActuadorRepository comandoActuadorRepository;
	
	@Autowired
	private IActuadorPuertaRepository actuadorPuertaRepository;
	
	@Autowired
	private IComandoActuadorPuertaRepository comandoActuadorPuertaRepository;
	
	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA ACTUADOR PUERTA ---
	// ----------------------------------------------------------------------------------

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/", consumes = "application/json")
	public ResponseEntity<?> crearActuadorPuerta(@RequestBody(required = true) ActuadorPuertaRequestDto actuadorPuertaRequestDto)
	{
		try
		{
			if (actuadorPuertaRequestDto.getMac() == null || actuadorPuertaRequestDto.getMac().isEmpty())
			{
				log.error(Constants.ERR_ACTUADOR_MAC_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_MAC_NULO_VACIO);
			}

			String mac = actuadorPuertaRequestDto.getMac();
			ActuadorPuerta actuadorPuerta = null;

			if (this.actuadorPuertaRepository.existsById(mac))
			{
				actuadorPuerta = this.actuadorPuertaRepository.findById(mac).get();
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

				if (actuadorProyectorRepository.existsById(mac))
				{
					actuadorProyectorRepository.deleteById(mac);
				}

				if (actuadorRepository.existsById(mac))
				{
					actuadorRepository.deleteById(mac);
				}

				actuadorPuerta = new ActuadorPuerta();
				actuadorPuerta.setMac(mac);
			}

			actuadorPuerta.setEstado(actuadorPuertaRequestDto.getEstado());
			actuadorPuerta.setTipo(actuadorPuertaRequestDto.getTipo());
			actuadorPuerta.setNombreUbicacion(actuadorPuertaRequestDto.getNombreUbicacion());
			actuadorPuerta.setNumeroReles(actuadorPuertaRequestDto.getNumeroReles());

			this.actuadorPuertaRepository.saveAndFlush(actuadorPuerta);

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
			log.error("Excepción genérica al crear actuador puerta", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/")
	public ResponseEntity<?> obtenerActuadoresPuerta()
	{
		try
		{
			return ResponseEntity.ok(this.actuadorPuertaRepository.findAll());
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException =
					new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_CODE);
			log.error("Excepción genérica al obtener actuadores puerta", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/")
	public ResponseEntity<?> eliminarActuadorPuerta(@RequestHeader("mac") String mac)
	{
		try
		{
			if (!this.actuadorPuertaRepository.existsById(mac))
			{
				log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NO_EXISTE);
			}

			this.actuadorPuertaRepository.deleteById(mac);
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
			log.error("Excepción genérica al eliminar actuador puerta", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	// ----------------------------------------------------------------------------------
	// --- ENDPOINTS PARA COMANDO_ACTUADOR_PUERTA ---
	// ----------------------------------------------------------------------------------

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@PostMapping(value = "/comando", consumes = "application/json")
	public ResponseEntity<?> crearComandoActuadorPuerta(@RequestBody ComandoActuadorPuertaRequestDto comandoActuadorPuertaRequestDto)
	{
		try
		{
			if (comandoActuadorPuertaRequestDto.getMac() == null || comandoActuadorPuertaRequestDto.getMac().isEmpty())
			{
				log.error("MAC vacía o nula");
				throw new AutomationsServerException("MAC vacía o nula", "ERR_COMANDO_ACTUADOR_PUERTA");
			}

			if (comandoActuadorPuertaRequestDto.getKeyword() == null || comandoActuadorPuertaRequestDto.getKeyword().isEmpty())
			{
				log.error("Keyword vacía o nula");
				throw new AutomationsServerException("Keyword vacía o nula", "ERR_COMANDO_ACTUADOR_PUERTA");
			}

			if (comandoActuadorPuertaRequestDto.getIndiceRele() == null)
			{
				log.error("Índice de relé nulo");
				throw new AutomationsServerException("Índice de relé nulo", "ERR_COMANDO_ACTUADOR_PUERTA");
			}

			ComandoActuadorId comandoActuadorId =
					new ComandoActuadorId(comandoActuadorPuertaRequestDto.getMac(), comandoActuadorPuertaRequestDto.getKeyword());

			if (!this.comandoActuadorRepository.existsById(comandoActuadorId))
			{
				log.error("No existe comando_actuador con esa clave");
				throw new AutomationsServerException("No existe comando_actuador con esa clave", "ERR_COMANDO_ACTUADOR_PUERTA");
			}

			if (!this.actuadorPuertaRepository.existsById(comandoActuadorPuertaRequestDto.getMac()))
			{
				log.error("No existe actuador_puerta con esa MAC");
				throw new AutomationsServerException("No existe actuador_puerta con esa MAC", "ERR_COMANDO_ACTUADOR_PUERTA");
			}

			ComandoActuador comandoActuador = this.comandoActuadorRepository.findById(comandoActuadorId).get();
			ActuadorPuerta actuadorPuerta = this.actuadorPuertaRepository.findById(comandoActuadorPuertaRequestDto.getMac()).get();

			ComandoActuadorPuertaId comandoActuadorPuertaId = new ComandoActuadorPuertaId();
			comandoActuadorPuertaId.setComandoActuadorMac(comandoActuadorPuertaRequestDto.getMac());
			comandoActuadorPuertaId.setComandoActuadorKeyword(comandoActuadorPuertaRequestDto.getKeyword());
			comandoActuadorPuertaId.setIndiceRele(comandoActuadorPuertaRequestDto.getIndiceRele());

			ComandoActuadorPuerta comandoActuadorPuerta = new ComandoActuadorPuerta();
			comandoActuadorPuerta.setComandoActuadorPuertaId(comandoActuadorPuertaId);
			comandoActuadorPuerta.setComandoActuador(comandoActuador);
			comandoActuadorPuerta.setActuadorPuerta(actuadorPuerta);

			this.comandoActuadorPuertaRepository.saveAndFlush(comandoActuadorPuerta);
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
					new AutomationsServerException("ERR_COMANDO_ACTUADOR_PUERTA", Constants.ERR_CODE);
			log.error("Excepción genérica al crear comando_actuador_puerta", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/comando")
	public ResponseEntity<?> obtenerComandosActuadorPuerta()
	{
		try
		{
			return ResponseEntity.ok(this.comandoActuadorPuertaRepository.buscarComandosActuadorPuerta());
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException =
					new AutomationsServerException("ERR_COMANDO_ACTUADOR_PUERTA", Constants.ERR_CODE);
			log.error("Excepción genérica al obtener comandos_actuador_puerta", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}
	
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@DeleteMapping(value = "/comando")
	public ResponseEntity<?> eliminarComandoActuadorPuerta(@RequestHeader("mac") String mac,
			@RequestHeader("keyword") String keyword,
			@RequestHeader("indiceRele") Integer indiceRele)
	{
		try
		{
			if (mac == null || mac.isEmpty() || keyword == null || keyword.isEmpty() || indiceRele == null)
			{
				log.error("Parámetros inválidos");
				throw new AutomationsServerException("Parámetros inválidos", "ERR_COMANDO_ACTUADOR_PUERTA");
			}

			ComandoActuadorPuertaId comandoActuadorPuertaId = new ComandoActuadorPuertaId();
			comandoActuadorPuertaId.setComandoActuadorMac(mac);
			comandoActuadorPuertaId.setComandoActuadorKeyword(keyword);
			comandoActuadorPuertaId.setIndiceRele(indiceRele);

			if (!this.comandoActuadorPuertaRepository.existsById(comandoActuadorPuertaId))
			{
				log.error("No existe comando_actuador_puerta con esa clave");
				throw new AutomationsServerException("No existe comando_actuador_puerta con esa clave", "ERR_COMANDO_ACTUADOR_PUERTA");
			}

			this.comandoActuadorPuertaRepository.deleteById(comandoActuadorPuertaId);
			log.info(Constants.ELEMENTO_ELIMINADO);

			return ResponseEntity.ok(Constants.ELEMENTO_ELIMINADO);
		}
		catch (AutomationsServerException automationsException)
		{
			return ResponseEntity.badRequest().body(automationsException);
		}
		catch (Exception exception)
		{
			AutomationsServerException automationsException =
					new AutomationsServerException("ERR_COMANDO_ACTUADOR_PUERTA", Constants.ERR_CODE);
			log.error("Excepción genérica al eliminar comando_actuador_puerta", automationsException);
			return ResponseEntity.status(500).body(automationsException.getBodyExceptionMessage());
		}
	}
}
