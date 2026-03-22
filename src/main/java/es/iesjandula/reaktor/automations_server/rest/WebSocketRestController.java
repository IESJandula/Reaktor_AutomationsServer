package es.iesjandula.reaktor.automations_server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import es.iesjandula.reaktor.automations_server.repository.IWebSocketRepository;
import es.iesjandula.reaktor.base_server.websocket.dtos.WebSocketRequestDto;
import es.iesjandula.reaktor.base_server.websocket.dtos.WebSocketResponseDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador WebSocket que recibe mensajes del frontend y devuelve respuestas.
 */
@Slf4j
@Controller
public class WebSocketRestController
{
	@Autowired
	private IWebSocketRepository webSocketRepository;

	/**
	 * Recibe mensajes desde el frontend en /app/automations y responde en
	 * /topic/respuestas
	 */
	@MessageMapping("/automations")
	@SendTo("/topic/respuestas")
	public WebSocketResponseDto procesar(WebSocketRequestDto request)
	{
		// Delega la lógica al repository
		return webSocketRepository.procesar(request);
	}
}