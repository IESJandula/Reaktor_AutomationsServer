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
 * DTO para enviar información de una acción.
 */
public class AccionResponseDto
{
    // ID de la acción
    private Long id;
    
    // Resultado de la acción
    private String resultado;
    
    // Nombre del actuador asociado
    private String actuadorNombre;
    
    // ID de la orden asociada
    private Long ordenId;
}

