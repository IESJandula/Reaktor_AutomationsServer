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
 * DTO para enviar información de un actuador.
 */
public class ActuadorResponseDto
{
    // Dirección MAC del actuador
    private String mac;
    
    // Estado del actuador (ej. encendido/apagado)
    private String estado;
    
    // Nombre de la ubicación asociada
    private String ubicacionNombre;
}

