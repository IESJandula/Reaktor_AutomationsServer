package es.iesjandula.reaktor.automations_school_server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_school_server.dtos.ValidacionRequestDto;
import es.iesjandula.reaktor.automations_school_server.models.Orden;
import es.iesjandula.reaktor.automations_school_server.models.Validacion;
import es.iesjandula.reaktor.automations_school_server.repository.IOrdenRepository;
import es.iesjandula.reaktor.automations_school_server.repository.IValidacionRepository;
import es.iesjandula.reaktor.automations_school_server.utils.Constants;
import es.iesjandula.reaktor.automations_school_server.utils.AutomationSchoolServerException;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/validacion")
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
				throw new AutomationSchoolServerException(Constants.ERR_VALIDACION_NULO_VACIO, Constants.ERR_VALIDACION_CODE);
			}
			Long ordenId = validacionRequestDto.getOrdenId();
			Optional<Orden> ordenOpt = ordenRepository.findById(ordenId);
			if (ordenOpt.isEmpty())
			{
				log.error(Constants.ERR_ORDEN_NO_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_ORDEN_NO_EXISTE, Constants.ERR_ORDEN_CODE);
			}
			Validacion validacion = new Validacion();
			validacion.setScore(validacionRequestDto.getScore());
			validacion.setResultado(validacionRequestDto.getResultado());
			validacion.setMotivoRechazo(validacionRequestDto.getMotivoRechazo());
			validacion.setOrden(ordenOpt.get());
			Validacion nuevaValidacion = this.validacionRepository.saveAndFlush(validacion);
			log.info(Constants.ELEMENTO_AGREGADO);
			return ResponseEntity.ok().body(nuevaValidacion);
		} 
		catch (AutomationSchoolServerException exception)
		{
			log.error(exception.getMessage());
			return ResponseEntity.badRequest().body(exception);
		}
	}
	@GetMapping(value = "/")
	public ResponseEntity<?> obtenerValidaciones()
	{
		return ResponseEntity.ok(this.validacionRepository.buscarValidaciones());
	}
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> eliminarValidacion(@PathVariable Long id)
	{
		try
		{
			if (!this.validacionRepository.existsById(id))
			{
				log.error(Constants.ERR_VALIDACON_NO_EXISTE);
				throw new AutomationSchoolServerException(Constants.ERR_VALIDACION_CODE, Constants.ERR_VALIDACON_NO_EXISTE);
			}
			this.validacionRepository.deleteById(id);
			log.info(Constants.ELEMENTO_ELIMINADO);
			return ResponseEntity.ok().body(Constants.ELEMENTO_ELIMINADO);
		} 
		catch (AutomationSchoolServerException exception)
		{
			log.error(exception.getMessage());

			return ResponseEntity.badRequest().body(exception);
		}
	}
}