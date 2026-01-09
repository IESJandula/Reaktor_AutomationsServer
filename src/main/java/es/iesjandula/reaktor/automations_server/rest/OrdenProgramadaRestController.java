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

import es.iesjandula.reaktor.automations_server.dtos.OrdenProgramadaRequestDto;
import es.iesjandula.reaktor.automations_server.models.OrdenProgramada;
import es.iesjandula.reaktor.automations_server.repository.IOrdenProgramadaRepository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequestMapping("/automations/ordenprogramada")
@RestController
public class OrdenProgramadaRestController
{
    @Autowired
    private IOrdenProgramadaRepository ordenProgramadaRepository;
    @PostMapping(value = "/", consumes = "application/json")
    public ResponseEntity<?> crearOrdenProgramada(@RequestBody(required = true) OrdenProgramadaRequestDto ordenProgramadaRequestDto) 
    {
        try 
        {
            OrdenProgramada ordenProgramada = new OrdenProgramada();
            ordenProgramada.setFecha(ordenProgramadaRequestDto.getFecha());
            ordenProgramada.setFechaProgramada(ordenProgramadaRequestDto.getFechaProgramada());
            ordenProgramada.setRepeticion(ordenProgramadaRequestDto.getRepeticion());
            ordenProgramada.setFrase(ordenProgramadaRequestDto.getFrase());
            if (ordenProgramada.getFecha() == null)
            {
                log.error(Constants.ERR_PROGRAMADA_FECHA_NULA);
                throw new AutomationsServerException(Constants.ERR_PROGRAMADA_FECHA_NULA, Constants.ERR_PROGRAMADA_CODE);
            }
            OrdenProgramada nuevaOrden = this.ordenProgramadaRepository.saveAndFlush(ordenProgramada);
            log.info(Constants.ELEMENTO_AGREGADO);
            return ResponseEntity.ok().body(nuevaOrden);
        } 
        catch (AutomationsServerException exception)
        {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().body(exception);
        }
    }
    @GetMapping(value = "/")
    public ResponseEntity<?> obtenerOrdenesProgramadas() 
    {
        return ResponseEntity.ok(this.ordenProgramadaRepository.buscarOrdenesProgramadas());
    }   
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> eliminarOrdenProgramada(@PathVariable Long id) 
    {
        try 
        {
            if (!this.ordenProgramadaRepository.existsById(id)) 
            {
                log.error(Constants.ERR_PROGRAMADA_NO_EXISTE);
                throw new AutomationsServerException(Constants.ERR_PROGRAMADA_CODE, Constants.ERR_PROGRAMADA_NO_EXISTE); 
            }
            this.ordenProgramadaRepository.deleteById(id);
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