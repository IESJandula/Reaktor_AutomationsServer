package es.iesjandula.reaktor.automations_server.rest;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import es.iesjandula.reaktor.base_server.websocket.dtos.WebSocketRequestDto;
import es.iesjandula.reaktor.base_server.websocket.dtos.WebSocketResponseDto;

/**
 * Controlador dummy para pruebas de canales WebSocket
 */
@Controller
public class DummyWebSocketController {

    /**
     * Endpoint completamente independiente del chat
     */
    @MessageMapping("/dummy")
    @SendTo("/topic/dummy")
    public WebSocketResponseDto pruebaDummy(WebSocketRequestDto request) {

        return new WebSocketResponseDto(
            request.getPregunta(),
            "Respuesta desde canal DUMMY",
            "OK"
        );
    }
}