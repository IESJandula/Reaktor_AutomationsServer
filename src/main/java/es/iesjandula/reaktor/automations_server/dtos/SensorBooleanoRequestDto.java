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
 * DTO para recibir datos al crear o actualizar un sensor booleano.
 */
public class SensorBooleanoRequestDto
{
    // Dirección MAC del sensor
    private String mac;
    
    // Estado del sensor (ej. activado/desactivado)
    private String estado;
    
    // Nombre de la ubicación asociada
    private String nombreUbicacion;
    
    // Valor mínimo permitido o configurado para la alerta del sensor
    private Double umbralMinimo;
    
    // Valor máximo permitido o configurado para la alerta del sensor
    private Double umbralMaximo;
}
