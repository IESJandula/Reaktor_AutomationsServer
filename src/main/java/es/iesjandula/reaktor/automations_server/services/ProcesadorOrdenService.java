package es.iesjandula.reaktor.automations_server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * Servicio encargado de procesar una orden recibida por el sistema. Aquí se
 * decide:
 * 
 * Qué comando coincide mejor con la frase del usuario Si la orden es válida o
 * no (según el score) Se guarda la validación Se registra el comando ejecutado
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

	// Repositorio para obtener información de los actuadores
	@Autowired
	private IActuadorRepository actuadorRepository;

	/**
	 * Variable que define el porcentaje mínimo de coincidencia para aceptar una
	 * orden.
	 * 
	 * Se lee directamente desde el application.yml
	 */
	@Value("${reaktor.score_minimo_validacion}")
	private Double scoreMinimoValidacion;

	/**
	 * Método principal que procesa una orden del usuario.
	 */
	public void procesarOrden(OrdenSimple orden)
	{

		// Se ejecuta la consulta SQL que calcula el mejor comando posible
		// comparando la frase del usuario con las keywords configuradas
		List<Object[]> resultado = comandoActuadorRepository.buscarMejorComando(orden.getFrase().toLowerCase());

		// Si no hay coincidencias posibles
		if (resultado.isEmpty())
		{
			// Guardamos validación rechazada
			guardarValidacion(orden, 0.0, "Rechazado", "No se ha entendido la orden");
			return;
		}

		// Obtenemos la primera fila (el mejor resultado)
		Object[] fila = resultado.get(0);

		// Extraemos los datos devueltos por la consulta SQL
		String keyword = (String) fila[0]; // keyword configurada
		String mac = (String) fila[1]; // MAC del actuador
		String textoOk = (String) fila[2]; // texto de respuesta
		Double score = ((Number) fila[4]).doubleValue(); // porcentaje coincidencia

		// ----------------------------------------------------
		// VALIDACIÓN DE LA ORDEN
		// ----------------------------------------------------

		// Si el porcentaje de coincidencia supera el mínimo configurado
		if (score >= scoreMinimoValidacion)
		{
			Actuador actuador = actuadorRepository.findById(mac).orElse(null);

			if (actuador != null)
			{
				if ("on".equalsIgnoreCase(actuador.getEstado()))
				{
					// OK
					guardarValidacion(orden, score, "Aceptado", textoOk);

					ComandoActuadorId comandoActuadorId = new ComandoActuadorId(mac, keyword);
					ComandoId comandoId = new ComandoId(comandoActuadorId, orden.getId());

					Comando comando = new Comando();
					comando.setComandoId(comandoId);
					comando.setOrden(orden);

					comandoRepository.save(comando);

					Accion accion = new Accion();
					accion.setResultado("PENDIENTE");
					accion.setActuador(actuador);
					accion.setOrden(orden);

					accionRepository.save(accion);
				} 
				else
				{
					// NO DISPONIBLE
					guardarValidacion(orden, score, "Rechazado", "El dispositivo no se encuentra disponible en este momento");
				}
			} 
			else
			{
				// ACTUADOR NO EXISTE
				guardarValidacion(orden, score, "Rechazado", "No se ha encontrado el dispositivo");
			}
		} 
		else
		{
			// SCORE BAJO → NO ENTENDIDA
			guardarValidacion(orden, score, "Rechazado", "No se ha entendido la orden");
		}
	}
	
	/**
     * MÉTODO NUEVO PARA WEBSOCKET (AÑADIDO)
     */
    public String procesarOrdenDesdeWebSocket(String frase)
    {
        List<Object[]> resultado = comandoActuadorRepository.buscarMejorComando(frase.toLowerCase());

        if (resultado.isEmpty())
        {
            return "No se ha entendido la orden";
        }

        Object[] fila = resultado.get(0);

        String keyword = (String) fila[0];
        String mac = (String) fila[1];
        String textoOk = (String) fila[2];
        Double score = ((Number) fila[4]).doubleValue();

        if (score >= scoreMinimoValidacion)
        {
            Actuador actuador = actuadorRepository.findById(mac).orElse(null);

            if (actuador != null)
            {
                if ("on".equalsIgnoreCase(actuador.getEstado()))
                {
                    return textoOk;
                } 
                else
                {
                    return "El dispositivo no se encuentra disponible en este momento";
                }
            } 
            else
            {
                return "No se ha encontrado el dispositivo";
            }
        } 
        else
        {
            return "No se ha entendido la orden";
        }
    }

	/**
	 * Método auxiliar que guarda una validación en la base de datos.
	 */
	private void guardarValidacion(OrdenSimple orden, Double score, String resultado, String textoRespuesta)
	{
		// Creamos objeto validación
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
}