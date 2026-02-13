package es.iesjandula.reaktor.automations_server.rest;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor.automations_server.models.OrdenSimple;
import es.iesjandula.reaktor.automations_server.repository.IOrdenSimpleRepository;
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
    private SpeechService speechService ;
    
    @PostMapping(value = "/texto", consumes = "application/json")
    public ResponseEntity<?> crearOrdenSimpleTexto(@AuthenticationPrincipal DtoUsuarioExtended usuario,
    											   @RequestHeader String frase) 
    {
        try 
        {
            if (frase == null || frase.isBlank()) 
            {
            	// Añade errorString y log
                throw new AutomationsServerException(Constants.ERR_SIMPLE_NULO_VACIO, Constants.ERR_SIMPLE_CODE);
            }

            OrdenSimple ordenSimple = new OrdenSimple();

            ordenSimple.setFecha(new Date());
            ordenSimple.setFrase(frase);
            ordenSimple.setEmail(usuario.getEmail()) ;
            ordenSimple.setNombre(usuario.getNombre());
            ordenSimple.setApellidos(usuario.getApellidos());

            OrdenSimple nuevaOrden = this.ordenSimpleRepository.saveAndFlush(ordenSimple);

            return ResponseEntity.ok(nuevaOrden);
        } 
        catch (AutomationsServerException exception) 
        {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
    
    @PostMapping(value = "/audio", consumes = "multipart/form-data")
    public ResponseEntity<?> crearOrdenSimpleAudio(
            @AuthenticationPrincipal DtoUsuarioExtended usuario,
            @RequestParam("file") MultipartFile file)
    {
        try 
        {
            if (file == null || file.isEmpty())
            {
                throw new AutomationsServerException("Audio vacío", "AUDIO_EMPTY");
            }

            log.info("Archivo recibido: " + file.getOriginalFilename());
            log.info("Tamaño: " + file.getSize());

            String frase = this.speechService.transcribe(file.getInputStream());

            log.info("Texto reconocido: " + frase);

            OrdenSimple ordenSimple = new OrdenSimple();
            ordenSimple.setFecha(new Date());
            ordenSimple.setFrase(frase);

            // 👇 SOLO si hay usuario autenticado
            if (usuario != null)
            {
                ordenSimple.setEmail(usuario.getEmail());
                ordenSimple.setNombre(usuario.getNombre());
                ordenSimple.setApellidos(usuario.getApellidos());
            }
            else
            {
                // Para pruebas sin JWT
                ordenSimple.setEmail("anonimo@local");
                ordenSimple.setNombre("Anonimo");
                ordenSimple.setApellidos("SinLogin");
            }

            OrdenSimple nuevaOrden = this.ordenSimpleRepository.saveAndFlush(ordenSimple);

            return ResponseEntity.ok(nuevaOrden);
        }
        catch (Exception e)
        {
            log.error("Error procesando audio", e);
            return ResponseEntity.badRequest().body("Error procesando audio");
        }
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> obtenerOrdenesSimples() 
    {
        return ResponseEntity.ok(this.ordenSimpleRepository.buscarOrdenesSimples());
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> eliminarOrdenSimple(@PathVariable Long id) 
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
        catch (AutomationsServerException exception) 
        {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().body(exception);
        }
    }
}