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
 * DTO para enviar información de una ubicación.
 */
public class UbicacionResponseDto
{
    // Nombre de la ubicación
    private String nombreUbicacion;
    
    // Lista de dispositivos asociados (opcional)
    // private List<String> dispositivos;
}
