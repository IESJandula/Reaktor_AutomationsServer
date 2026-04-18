package es.iesjandula.reaktor.automations_server.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	/**
	 * Recibe mensajes desde el frontend en /app/automations y responde en
	 * /topic/respuestas
	 */
	@MessageMapping("/automations")
	public void procesar(WebSocketRequestDto request, Principal principal)
	{
		log.info("Usuario autenticado: " + principal.getName());

		WebSocketResponseDto response = webSocketRepository.procesar(request);

		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/automations/respuestas", response);
	}
}