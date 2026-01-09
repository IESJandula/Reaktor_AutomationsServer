package es.iesjandula.reaktor.automations_server.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.AccionRequestDto;
import es.iesjandula.reaktor.automations_server.models.Accion;
import es.iesjandula.reaktor.automations_server.models.Actuador;
import es.iesjandula.reaktor.automations_server.models.Orden;
import es.iesjandula.reaktor.automations_server.repository.IAccionRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IOrdenRepository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/automations/accion")
@RestController
public class AccionRestController
{
	@Autowired
	private IAccionRepository accionRepository;
	@Autowired
	private IActuadorRepository actuadorRepository; 
	@Autowired
	private IOrdenRepository ordenRepository;
	@PostMapping(value = "/", consumes = "application/json")
	public ResponseEntity<?> crearAccion(@RequestBody(required = true) AccionRequestDto accionRequestDto)
	{
		try
		{
			String actuadorNombre = accionRequestDto.getActuadorNombre();
			Long ordenId = accionRequestDto.getOrdenId();
			if (actuadorNombre == null || actuadorNombre.isEmpty() || ordenId == null)
			{
				log.error(Constants.ERR_ACCION_DATOS_NULOS);
				throw new AutomationsServerException(Constants.ERR_ACCION_DATOS_NULOS, Constants.ERR_ACCION_CODE);
			}
			Optional<Actuador> actuadorOpt = actuadorRepository.findById(actuadorNombre);
			if (actuadorOpt.isEmpty())
			{
				log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACTUADOR_NO_EXISTE, Constants.ERR_ACTUADOR_CODE);
			}
			Optional<Orden> ordenOpt = ordenRepository.findById(ordenId);
			if (ordenOpt.isEmpty())
			{
				log.error(Constants.ERR_ORDEN_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ORDEN_NO_EXISTE, Constants.ERR_ORDEN_CODE);
			}
			Accion accion = new Accion();
			accion.setResultado(accionRequestDto.getResultado());
			accion.setActuador(actuadorOpt.get());
			accion.setOrden(ordenOpt.get());
			Accion nuevaAccion = this.accionRepository.saveAndFlush(accion);
			log.info(Constants.ELEMENTO_AGREGADO);
			return ResponseEntity.ok().body(nuevaAccion);
		} 
		catch (AutomationsServerException exception)
		{
			log.error(exception.getMessage());
			return ResponseEntity.badRequest().body(exception);
		}
	}
	@GetMapping(value = "/")
	public ResponseEntity<?> obtenerAcciones()
	{
		return ResponseEntity.ok(this.accionRepository.buscarAcciones());
	}
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> eliminarAccion(@PathVariable Long id)
	{
		try
		{
			if (!this.accionRepository.existsById(id))
			{
				log.error(Constants.ERR_ACCION_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ACCION_CODE, Constants.ERR_ACCION_NO_EXISTE);
			}
			this.accionRepository.deleteById(id);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok().body(Constants.ELEMENTO_ELIMINADO);
		} 
		catch (AutomationsServerException exception)
		{
			log.error(exception.getMessage());
			return ResponseEntity.badRequest().body(exception);
		}
	}
}