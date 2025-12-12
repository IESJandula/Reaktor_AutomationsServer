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

import es.iesjandula.reaktor.automations_school_server.dtos.OrdenSimpleRequestDto;
import es.iesjandula.reaktor.automations_school_server.models.OrdenSimple;
import es.iesjandula.reaktor.automations_school_server.repository.IOrdenSimpleRepository;
import es.iesjandula.reaktor.automations_school_server.utils.Constants;
import es.iesjandula.reaktor.automations_school_server.utils.AutomationSchoolServerException;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequestMapping("/api/ordensimple")
@RestController
public class OrdenSimpleRestController
{
    @Autowired
    private IOrdenSimpleRepository ordenSimpleRepository;
    @PostMapping(value = "/", consumes = "application/json")
    public ResponseEntity<?> crearOrdenSimple(@RequestBody(required = true) OrdenSimpleRequestDto ordenSimpleRequestDto) 
    {
        try 
        {
            OrdenSimple ordenSimple = new OrdenSimple();
            ordenSimple.setFecha(ordenSimpleRequestDto.getFecha());
            ordenSimple.setFrase(ordenSimpleRequestDto.getFrase());
            if (ordenSimple.getFecha() == null)
            {
                log.error(Constants.ERR_SIMPLE_NULO_VACIO);
                throw new AutomationSchoolServerException(Constants.ERR_SIMPLE_NULO_VACIO, Constants.ERR_SIMPLE_CODE);
            }
            OrdenSimple nuevaOrden = this.ordenSimpleRepository.saveAndFlush(ordenSimple);
            log.info(Constants.ELEMENTO_AGREGADO); 
            return ResponseEntity.ok().body(nuevaOrden); 
        } 
        catch (AutomationSchoolServerException exception) 
        {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().body(exception);
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
                throw new AutomationSchoolServerException(Constants.ERR_SIMPLE_CODE, Constants.ERR_SIMPLE_NO_EXISTE); 
            }
            this.ordenSimpleRepository.deleteById(id);
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