package es.iesjandula.reaktor.automations_server.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer 
{
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) 
    {

        // canal por donde el servidor enviará mensajes
        config.enableSimpleBroker("/topic");

        // prefijo de mensajes enviados desde el cliente
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) 
    {
        // endpoint websocket
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }
}