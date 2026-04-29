package es.iesjandula.reaktor.automations_server.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.ValidacionRequestDto;
import es.iesjandula.reaktor.automations_server.models.Orden;
import es.iesjandula.reaktor.automations_server.models.Validacion;
import es.iesjandula.reaktor.automations_server.repository.IOrdenRepository;
import es.iesjandula.reaktor.automations_server.repository.IValidacionRepository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/automations/validacion")
@RestController
public class ValidacionRestController
{
	@Autowired
	private IValidacionRepository validacionRepository;
	@Autowired
	private IOrdenRepository ordenRepository;
	
	@PostMapping(value = "/", consumes = "application/json")
	public ResponseEntity<?> crearValidacion(@RequestBody(required = true) ValidacionRequestDto validacionRequestDto)
	{
		try
		{
			if (validacionRequestDto.getResultado() == null || validacionRequestDto.getResultado().isEmpty())
			{
				log.error(Constants.ERR_VALIDACION_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_VALIDACION_CODE, Constants.ERR_VALIDACION_NULO_VACIO);
			}
			Long ordenId = validacionRequestDto.getOrdenId();
			Optional<Orden> ordenOpt = ordenRepository.findById(ordenId);
			if (ordenOpt.isEmpty())
			{
				log.error(Constants.ERR_ORDEN_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_ORDEN_CODE, Constants.ERR_ORDEN_NO_EXISTE);
			}
			Validacion validacion = new Validacion();
			validacion.setScore(validacionRequestDto.getScore());
			validacion.setResultado(validacionRequestDto.getResultado());
			validacion.setTextoRespuesta(validacionRequestDto.getTextoRespuesta());
			validacion.setOrden(ordenOpt.get());
			Validacion nuevaValidacion = this.validacionRepository.saveAndFlush(validacion);
			log.info(Constants.ELEMENTO_AGREGADO);
			return ResponseEntity.ok().body(nuevaValidacion);
		} 
		catch (AutomationsServerException automationsServerException)
		{
		    log.error(automationsServerException.getMessage());
		    return ResponseEntity.badRequest().body(automationsServerException.getBodyExceptionMessage());
		}
		catch (Exception exception)
		{
		    AutomationsServerException automationsServerException = new AutomationsServerException(Constants.ERR_SIMPLE_CODE, Constants.ERR_CODE);

		    log.error("Error inesperado", automationsServerException);
		    return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}
	@GetMapping(value = "/")
	public ResponseEntity<?> obtenerValidaciones()
	{
		return ResponseEntity.ok(this.validacionRepository.buscarValidaciones());
	}
	@DeleteMapping(value = "/")
	public ResponseEntity<?> eliminarValidacion(@RequestHeader("id") Long id)
	{
		try
		{
			if (!this.validacionRepository.existsById(id))
			{
				log.error(Constants.ERR_VALIDACON_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_VALIDACION_CODE, Constants.ERR_VALIDACON_NO_EXISTE);
			}
			this.validacionRepository.deleteById(id);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok().body(Constants.ELEMENTO_ELIMINADO);
		} 
		catch (AutomationsServerException automationsServerException)
		{
		    log.error(automationsServerException.getMessage());
		    return ResponseEntity.badRequest().body(automationsServerException.getBodyExceptionMessage());
		}
		catch (Exception exception)
		{
		    AutomationsServerException automationsServerException = new AutomationsServerException(Constants.ERR_SIMPLE_CODE, Constants.ERR_CODE);

		    log.error("Error inesperado", automationsServerException);
		    return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}
}