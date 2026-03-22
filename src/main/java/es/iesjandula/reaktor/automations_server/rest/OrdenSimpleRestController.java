package es.iesjandula.reaktor.automations_server.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor.automations_server.dtos.OrdenTextoRequest;
import es.iesjandula.reaktor.automations_server.models.OrdenSimple;
import es.iesjandula.reaktor.automations_server.repository.IOrdenSimpleRepository;
import es.iesjandula.reaktor.automations_server.services.ProcesadorOrdenService;
import es.iesjandula.reaktor.automations_server.services.SpeechService;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import es.iesjandula.reaktor.base.security.models.DtoUsuarioExtended;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/automations/ordensimple")
@RestController
public class OrdenSimpleRestController
{
	@Autowired
	private IOrdenSimpleRepository ordenSimpleRepository;

	@Autowired
	private SpeechService speechService;

	@Autowired
	private ProcesadorOrdenService procesadorOrdenService;

	@PostMapping(value = "/texto", consumes = "application/json")
	public ResponseEntity<?> crearOrdenSimpleTexto(@AuthenticationPrincipal DtoUsuarioExtended usuario, @RequestBody OrdenTextoRequest request)
	{
		try
		{
			String frase = request.getFrase();

			if (frase == null || frase.isBlank())
			{
				throw new AutomationsServerException(Constants.ERR_SIMPLE_NULO_VACIO, Constants.ERR_SIMPLE_CODE);
			}

			OrdenSimple ordenSimple = new OrdenSimple();
			ordenSimple.setFecha(new Date());
			ordenSimple.setFrase(frase);

			// Soporte con y sin JWT
			if (usuario != null)
			{
				ordenSimple.setEmail(usuario.getEmail());
				ordenSimple.setNombre(usuario.getNombre());
				ordenSimple.setApellidos(usuario.getApellidos());
			} 
			else
			{
				ordenSimple.setEmail("anonimo@local");
				ordenSimple.setNombre("Anonimo");
				ordenSimple.setApellidos("SinLogin");
			}

			OrdenSimple nuevaOrden = this.ordenSimpleRepository.saveAndFlush(ordenSimple);

			procesadorOrdenService.procesarOrden(nuevaOrden);

			// construimos respuesta para el frontend
			Map<String, Object> respuesta = new HashMap<>();
			respuesta.put("frase", nuevaOrden.getFrase());

			return ResponseEntity.ok(respuesta);
			
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

	// Endpoint que recibe un audio desde el frontend en formato multipart/form-data
	// Este endpoint únicamente se encarga de transcribir el audio a texto.
	// No guarda nada en base de datos ni ejecuta ninguna orden.
	@PostMapping(value = "/audio", consumes = "multipart/form-data")
	public ResponseEntity<?> crearOrdenSimpleAudio(@AuthenticationPrincipal DtoUsuarioExtended usuario, @RequestParam("file") MultipartFile file)
	{
	    try
	    {
	        // Comprobación básica para evitar procesar audios vacíos
	        if (file == null || file.isEmpty())
	        {
	            throw new AutomationsServerException("Audio vacío", "AUDIO_EMPTY");
	        }
	        
	        log.info("Archivo recibido: " + file.getOriginalFilename());
	        log.info("Tamaño: " + file.getSize());
	        
	        // Se envía el audio al servicio de reconocimiento de voz (Vosk)
	        // Este servicio transforma el audio en una frase de texto
	        String frase = this.speechService.transcribe(file.getInputStream());
	        log.info("Texto reconocido: " + frase);

	        // Se crea un mapa para construir la respuesta que se enviará al frontend
	        Map<String, Object> respuesta = new HashMap<>();

	        respuesta.put("frase", frase);
	        return ResponseEntity.ok(respuesta);

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
	public ResponseEntity<?> obtenerOrdenesSimples()
	{
		return ResponseEntity.ok(this.ordenSimpleRepository.buscarOrdenesSimples());
	}

	@DeleteMapping(value = "/")
	public ResponseEntity<?> eliminarOrdenSimple(@RequestHeader("id") Long id)
	{
		try
		{
			if (!this.ordenSimpleRepository.existsById(id))
			{
				log.error(Constants.ERR_SIMPLE_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_SIMPLE_CODE, Constants.ERR_SIMPLE_NO_EXISTE);
			}
			this.ordenSimpleRepository.deleteById(id);
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