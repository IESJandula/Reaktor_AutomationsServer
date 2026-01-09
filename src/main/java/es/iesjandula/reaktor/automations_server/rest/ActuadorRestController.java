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

import es.iesjandula.reaktor.automations_server.dtos.ActuadorRequestDto;
import es.iesjandula.reaktor.automations_server.models.Actuador;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequestMapping("/automations/actuador")
@RestController
public class ActuadorRestController
{
    @Autowired
    private IActuadorRepository actuadorRepository; 
    @PostMapping(value = "/", consumes = "application/json")
    public ResponseEntity<?> crearActuador(@RequestBody(required = true) ActuadorRequestDto actuadorRequestDto) 
    {
        try 
        {
            if (actuadorRequestDto.getMac() == null || actuadorRequestDto.getMac().isEmpty()) 
            {
                log.error(Constants.ERR_ACTUADOR_NULO_VACIO);
                throw new AutomationsServerException(Constants.ERR_ACTUADOR_NULO_VACIO, Constants.ERR_ACTUADOR_CODE);
            }
            if (this.actuadorRepository.existsById(actuadorRequestDto.getMac())) 
            {
                log.error(Constants.ERR_ACTUADOR_EXISTE);
                throw new AutomationsServerException(Constants.ERR_ACTUADOR_EXISTE, Constants.ERR_ACTUADOR_CODE);
            }
            Actuador actuador = new Actuador();
            actuador.setMac(actuadorRequestDto.getMac());
            actuador.setEstado(actuadorRequestDto.getEstado());
            this.actuadorRepository.saveAndFlush(actuador);
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
    public ResponseEntity<?> obtenerActuador() 
    {
        return ResponseEntity.ok(this.actuadorRepository.buscarActuadores());
    }
    @DeleteMapping(value = "/{mac}")
    public ResponseEntity<?> eliminarActuador(@PathVariable String mac) 
    {
        try 
        {
            if (!this.actuadorRepository.existsById(mac)) 
            {
                log.error(Constants.ERR_ACTUADOR_NO_EXISTE);
                throw new AutomationsServerException(Constants.ERR_ACTUADOR_CODE, Constants.ERR_ACTUADOR_NO_EXISTE); 
            }
            this.actuadorRepository.deleteById(mac);
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