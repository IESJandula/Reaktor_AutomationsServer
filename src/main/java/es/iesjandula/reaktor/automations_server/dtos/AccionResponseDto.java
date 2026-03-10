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

    // Estado de la acción (pendiente, en_ejecucion, etc.)
    private String estado;
    
    // Resultado de la acción
    private String resultado;
    
    // MAC del actuador asociado
    private String actuadorMac;
    
    // ID de la orden asociada
    private Long ordenId;
}