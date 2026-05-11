package es.iesjandula.reaktor.automations_server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.iesjandula.reaktor.base_server.websocket.dtos.WebSocketRequestDto;
import es.iesjandula.reaktor.base_server.websocket.dtos.WebSocketResponseDto;
import es.iesjandula.reaktor.automations_server.services.ProcesadorOrdenService;

/**
 * Implementación del WebSocket en Automations
 * 
 * Aquí se procesa la lógica real del mensaje recibido.
 */
@Service
public class WebSocketRepositoryImpl implements IWebSocketRepository
{

	@Autowired
	private ProcesadorOrdenService procesadorOrdenService;

	@Override
	public WebSocketResponseDto procesar(WebSocketRequestDto request)
	{

		// Obtenemos la pregunta del usuario
		String pregunta = request.getPregunta();

		// Procesamos la orden en el servicio
		String respuesta = procesadorOrdenService.procesarOrdenDesdeWebSocket(pregunta);

		// Devolvemos la respuesta al frontend
		return new WebSocketResponseDto(pregunta, respuesta, "OK");
	}
}