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
 * DTO para enviar información de una validación.
 */
public class ValidacionResponseDto
{
    // ID de la validación
    private Long id;
    
    // Puntuación de la validación
    private Integer score;
    
    // Resultado de la validación (ej. aprobado/rechazado)
    private String resultado;
    
    // Motivo del rechazo, si aplica
    private String motivoRechazo;
    
    // ID de la orden asociada a la validación
    private Long ordenId;
}
