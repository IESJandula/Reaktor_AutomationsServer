package es.iesjandula.reaktor.automations_server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.iesjandula.reaktor.automations_server.models.Comando;
import es.iesjandula.reaktor.automations_server.models.OrdenSimple;
import es.iesjandula.reaktor.automations_server.models.Validacion;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorId;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoId;
import es.iesjandula.reaktor.automations_server.repository.IComandoActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoRepository;
import es.iesjandula.reaktor.automations_server.repository.IValidacionRepository;

@Service
public class ProcesadorOrdenService
{

    @Autowired
    private IComandoActuadorRepository comandoActuadorRepository;

    @Autowired
    private IValidacionRepository validacionRepository;

    @Autowired
    private IComandoRepository comandoRepository;

    public void procesarOrden(OrdenSimple orden)
    {

        List<Object[]> resultado =
                comandoActuadorRepository.buscarMejorComando(
                        orden.getFrase()
                );

        if(resultado.isEmpty())
        {
            guardarValidacion(
                    orden,
                    0.0,
                    "Rechazado",
                    "No se ha entendido la orden"
            );
            return;
        }

        Object[] fila = resultado.get(0);

        String keyword = (String) fila[0];
        String mac = (String) fila[1];
        Double porcentaje = ((Number) fila[2]).doubleValue();

        if(porcentaje >= 80)
        {

            guardarValidacion(
                    orden,
                    porcentaje,
                    "Aceptado",
                    "La puerta se abrió correctamente"
            );

            ComandoActuadorId comandoActuadorId =
                    new ComandoActuadorId(mac, keyword);

            ComandoId comandoId =
                    new ComandoId(comandoActuadorId, orden.getId());

            Comando comando = new Comando();
            comando.setComandoId(comandoId);
            comando.setOrden(orden);

            comandoRepository.save(comando);

        }
        else
        {

            guardarValidacion(
                    orden,
                    porcentaje,
                    "Rechazado",
                    "No se ha entendido la orden"
            );

        }

    }

    private void guardarValidacion(OrdenSimple orden,
                                   Double score,
                                   String resultado,
                                   String textoRespuesta)
    {

        Validacion validacion = new Validacion();

        validacion.setOrden(orden);
        validacion.setScore(score);
        validacion.setResultado(resultado);
        validacion.setTextoRespuesta(textoRespuesta);

        validacionRepository.save(validacion);

    }
}