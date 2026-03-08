package es.iesjandula.reaktor.automations_server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.iesjandula.reaktor.automations_server.models.Accion;
import es.iesjandula.reaktor.automations_server.models.Actuador;
import es.iesjandula.reaktor.automations_server.models.Comando;
import es.iesjandula.reaktor.automations_server.models.OrdenSimple;
import es.iesjandula.reaktor.automations_server.models.Validacion;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorId;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoId;
import es.iesjandula.reaktor.automations_server.repository.IAccionRepository;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoRepository;
import es.iesjandula.reaktor.automations_server.repository.IValidacionRepository;

@Service
public class ProcesadorOrdenService {

    @Autowired
    private IComandoActuadorRepository comandoActuadorRepository;

    @Autowired
    private IValidacionRepository validacionRepository;

    @Autowired
    private IComandoRepository comandoRepository;
    
    @Autowired
    private IAccionRepository accionRepository;

    @Autowired
    private IActuadorRepository actuadorRepository;

    public void procesarOrden(OrdenSimple orden) {

        List<Object[]> resultado = comandoActuadorRepository.buscarMejorComando(orden.getFrase().toLowerCase());

        if(resultado.isEmpty())
        {
            guardarValidacion(orden, 0.0, "Rechazado", "No se ha entendido la orden");
            return;
        }

        Object[] fila = resultado.get(0);

        String keyword = (String) fila[0];
        String mac = (String) fila[1];
        String textoOk = (String) fila[2];
        Double score = ((Number) fila[4]).doubleValue();

        // guardar validación siempre
        if(score >= 80) 
        {
            guardarValidacion(orden, score, "Aceptado", textoOk);

            // guardar comando
            ComandoActuadorId comandoActuadorId = new ComandoActuadorId(mac, keyword);
            ComandoId comandoId = new ComandoId(comandoActuadorId, orden.getId());

            Comando comando = new Comando();
            comando.setComandoId(comandoId);
            comando.setOrden(orden);

            comandoRepository.save(comando);
            
            // CREAR ACCION
            Actuador actuador = actuadorRepository.findById(mac).orElse(null);

            if(actuador != null)
            {
                Accion accion = new Accion();

                accion.setResultado("PENDIENTE");
                accion.setActuador(actuador);
                accion.setOrden(orden);

                accionRepository.save(accion);
            }

        } 
        else 
        {
            guardarValidacion(orden,score,"Rechazado",textoOk);
        }
    }

    private void guardarValidacion(
            OrdenSimple orden,
            Double score,
            String resultado,
            String textoRespuesta) {

        Validacion validacion = new Validacion();

        validacion.setOrden(orden);
        validacion.setScore(score);
        validacion.setResultado(resultado);
        validacion.setTextoRespuesta(textoRespuesta);

        validacionRepository.save(validacion);
    }
}