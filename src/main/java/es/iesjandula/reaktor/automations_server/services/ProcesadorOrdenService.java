package es.iesjandula.reaktor.automations_server.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import es.iesjandula.reaktor.automations_server.models.Accion;
import es.iesjandula.reaktor.automations_server.models.Comando;
import es.iesjandula.reaktor.automations_server.models.OrdenSimple;
import es.iesjandula.reaktor.automations_server.models.Validacion;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorId;
import es.iesjandula.reaktor.automations_server.models.ids.ComandoId;
import es.iesjandula.reaktor.automations_server.repository.IAccionRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.IComandoRepository;
import es.iesjandula.reaktor.automations_server.repository.IValidacionRepository;
import es.iesjandula.reaktor.automations_server.utils.Constants;

/**
 * Servicio encargado de procesar una orden recibida por el sistema.
 * Aquí se decide:
 * 
 * Qué comando coincide mejor con la frase del usuario  
 * Si la orden es válida o no (según el score)  
 * Se guarda la validación  
 * Se registra el comando ejecutado  
 * Se genera una acción para el actuador
 */
@Service
public class ProcesadorOrdenService
{
	// Repositorio para acceder a los comandos configurados en los actuadores
    @Autowired
    private IComandoActuadorRepository comandoActuadorRepository;

    // Repositorio para guardar el resultado de la validación
    @Autowired
    private IValidacionRepository validacionRepository;

    // Repositorio para registrar el comando ejecutado
    @Autowired
    private IComandoRepository comandoRepository;
    
    // Repositorio para registrar acciones que se enviarán a los actuadores
    @Autowired
    private IAccionRepository accionRepository;

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

        // Obtenemos la primera fila (el mejor resultado)
        Object[] fila = resultado.get(0);

        // Extraemos los datos devueltos por la consulta SQL
        String keyword = (String) fila[0]; // keyword configurada
        String mac = (String) fila[1];     // MAC del actuador
        String textoOk = (String) fila[2]; // texto de respuesta
        Double score = ((Number) fila[4]).doubleValue(); // porcentaje coincidencia

        for (ComandoActuador comando : comandos)
        {
            Set<String> tokensKeyword = limpiar(comando.getComandoActuadorId().getKeyword());

            if (tokensKeyword.isEmpty())
            {
                continue;
            }

            // Creamos la clave compuesta del comando
            ComandoActuadorId comandoActuadorId = new ComandoActuadorId(mac, keyword);

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

            // Creamos el objeto comando
            Comando comando = new Comando();

            comando.setComandoId(comandoId);
            comando.setOrden(orden);

            // Guardamos el comando en la base de datos
            comandoRepository.save(comando);
            
            // ------------------------------------------------
            // CREAR ACCIÓN PARA EL ACTUADOR
            // ------------------------------------------------

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

        // Asociamos la orden
        validacion.setOrden(orden);

        // Guardamos el porcentaje de coincidencia
        validacion.setScore(score);

        // Guardamos si fue aceptada o rechazada
        validacion.setResultado(resultado);

        // Texto que se devolverá al usuario
        validacion.setTextoRespuesta(textoRespuesta);

        // Persistimos en base de datos
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