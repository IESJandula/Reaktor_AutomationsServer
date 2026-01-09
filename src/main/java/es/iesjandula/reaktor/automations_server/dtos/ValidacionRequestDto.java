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
 * DTO para recibir datos al crear o actualizar una validación.
 */
public class ValidacionRequestDto
{
    // ID de la orden asociada a la validación
	private Long ordenId;
    
    // Puntuación de la validación
    private Integer score; 
    
    // Resultado de la validación (ej. aprobado/rechazado)
    private String resultado; 
    
    // Motivo del rechazo, si aplica
    private String motivoRechazo;
}

