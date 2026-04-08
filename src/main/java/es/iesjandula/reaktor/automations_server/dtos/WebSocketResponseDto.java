package es.iesjandula.reaktor.automations_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para recibir datos al crear o actualizar una acción.
 */
public class WebSocketResponseDto
{
	// Pregunta enviada
	private String pregunta;

	// Respuesta generada
	private String respuesta;

	// Estado de la respuesta
	private String estado;
}
