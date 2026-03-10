package es.iesjandula.reaktor.automations_server.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.iesjandula.reaktor.automations_server.models.Accion;
import es.iesjandula.reaktor.automations_server.models.Comando;
import es.iesjandula.reaktor.automations_server.models.ComandoActuador;
import es.iesjandula.reaktor.automations_server.models.OrdenSimple;
import es.iesjandula.reaktor.automations_server.models.Validacion;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoId;
import es.iesjandula.reaktor.automations_server.repository.IAccionRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoRepository;
import es.iesjandula.reaktor.automations_server.repository.IValidacionRepository;
import es.iesjandula.reaktor.automations_server.utils.Constants;

@Service
public class ProcesadorOrdenService
{
    @Autowired
    private IComandoActuadorRepository comandoActuadorRepository;

    @Autowired
    private IValidacionRepository validacionRepository;

    @Autowired
    private IComandoRepository comandoRepository;

    @Autowired
    private IAccionRepository accionRepository;

    public void procesarOrden(OrdenSimple orden)
    {
        Set<String> tokensUsuario = limpiar(orden.getFrase());

        if (tokensUsuario.isEmpty())
        {
            guardarValidacion(orden, 0.0, "Rechazado", "No se ha entendido la orden");
            return;
        }

        List<ComandoActuador> comandos = comandoActuadorRepository.findAll();

        double mejorScore = 0.0;
        ComandoActuador mejorComando = null;

        for (ComandoActuador comando : comandos)
        {
            Set<String> tokensKeyword = limpiar(comando.getComandoActuadorId().getKeyword());

            if (tokensKeyword.isEmpty())
            {
                continue;
            }

            Set<String> interseccion = new HashSet<>(tokensUsuario);
            interseccion.retainAll(tokensKeyword);

            if (interseccion.isEmpty())
            {
                continue;
            }

            double score = (double) interseccion.size() / tokensKeyword.size();

            if (score > mejorScore)
            {
                mejorScore = score;
                mejorComando = comando;
            }
        }

        if (mejorComando == null)
        {
            guardarValidacion(orden, 0.0, "Rechazado", "No se ha entendido la orden");
            return;
        }

        if (mejorScore >= 0.6)
        {
            guardarValidacion(
                    orden,
                    mejorScore,
                    "Aceptado",
                    mejorComando.getTextoOk()
            );

            ComandoId comandoId =
                    new ComandoId(
                            mejorComando.getComandoActuadorId(),
                            orden.getId()
                    );

            Comando comando = new Comando();
            comando.setComandoId(comandoId);
            comando.setOrden(orden);

            comandoRepository.save(comando);

            // Crear ACCION pendiente para que luego la consuma el ESP
            Accion accion = new Accion();
            accion.setActuador(mejorComando.getActuador());
            accion.setOrden(orden);
            accion.setEstado(Constants.ESTADO_ACCION_PENDIENTE);
            accion.setResultado(mejorComando.getTextoOk());

            accionRepository.save(accion);
        }
        else
        {
            guardarValidacion(
                    orden,
                    mejorScore,
                    "Rechazado",
                    "No se ha entendido la orden"
            );

            // Si quieres registrar también la acción fallida de validación
            Accion accion = new Accion();
            accion.setActuador(mejorComando.getActuador());
            accion.setOrden(orden);
            accion.setEstado(Constants.ESTADO_ACCION_ERROR_VALIDACION);
            accion.setResultado("No se ha entendido la orden");

            accionRepository.save(accion);
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

    private Set<String> limpiar(String frase)
    {
        frase = frase.toLowerCase()
                .replaceAll("[^a-z0-9. ]", "");

        String[] partes = frase.split("\\s+");

        Set<String> resultado = new HashSet<>();

        for (int i = 0; i < partes.length; i++)
        {
            if (i < partes.length - 1 &&
                partes[i].matches("\\d+") &&
                partes[i + 1].matches("\\d+"))
            {
                resultado.add(partes[i] + "." + partes[i + 1]);
                i++;
                continue;
            }

            resultado.add(partes[i]);
        }

        return resultado;
    }
}