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
 * DTO genérico para enviar información de un sensor.
 */
public class SensorResponseDto
{
    // Dirección MAC del sensor
    private String mac;
    
    // Estado del sensor (ej. activo/inactivo)
    private String estado;
    
    // Nombre de la ubicación asociada
    private String nombreUbicacion;
    
    // Valor actual del sensor como cadena
    private String valorActual;
    
    // Tipo de medida del sensor (ej. booleano, numérico)
    private String tipoMedia;
}

