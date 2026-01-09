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
public class AccionRequestDto
{
    // Nombre del actuador asociado a la acción
    private String actuadorNombre; 
    
    // Resultado de la acción
    private String resultado; 
    
    // ID de la orden asociada a la acción
    private Long ordenId;
}