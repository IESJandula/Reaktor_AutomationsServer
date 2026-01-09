package es.iesjandula.reaktor.automations_server.rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.UbicacionRequestDto;
import es.iesjandula.reaktor.automations_server.models.Ubicacion;
import es.iesjandula.reaktor.automations_server.repository.IUbicacionRepository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequestMapping("/automations/ubicacion")
@RestController
public class UbicacionRestController
{
    @Autowired
    private IUbicacionRepository ubicacionRepository;
    @PostMapping(value = "/", consumes = "application/json")
    public ResponseEntity<?> crearUbicacion(@RequestBody(required = true) UbicacionRequestDto ubicacionRequestDto) 
    {
        try 
        {
            if (ubicacionRequestDto.getNombreUbicacion() == null || ubicacionRequestDto.getNombreUbicacion().isEmpty()) 
            {
                log.error(Constants.ERR_UBICACION_NULO_VACIO);
                throw new AutomationsServerException(Constants.ERR_UBICACION_NULO_VACIO, Constants.ERR_UBICACION_CODE);
            }
            if (this.ubicacionRepository.existsById(ubicacionRequestDto.getNombreUbicacion())) 
            {
                log.error(Constants.ERR_UBICACION_EXISTE);
                throw new AutomationsServerException(Constants.ERR_UBICACION_EXISTE, Constants.ERR_UBICACION_CODE);
            }
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setNombreUbicacion(ubicacionRequestDto.getNombreUbicacion());
            this.ubicacionRepository.saveAndFlush(ubicacion);
            log.info(Constants.ELEMENTO_AGREGADO);
            return ResponseEntity.ok().build();
        } 
        catch (AutomationsServerException exception) 
        {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().body(exception);
        }
    }
    @GetMapping(value = "/")
    public ResponseEntity<?> obtenerUbicacion() 
    {
        return ResponseEntity.ok(this.ubicacionRepository.buscarUbicaciones());
    }
    @DeleteMapping(value = "/{nombreUbicacion}")
    public ResponseEntity<?> eliminarUbicacion(@PathVariable String nombreUbicacion) 
    {
        try 
        {
            if (!this.ubicacionRepository.existsById(nombreUbicacion)) 
            {
                log.error(Constants.ERR_UBICACION_NO_EXISTE);
                throw new AutomationsServerException(Constants.ERR_UBICACION_CODE, Constants.ERR_UBICACION_NO_EXISTE); 
            }
            this.ubicacionRepository.deleteById(nombreUbicacion);
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