package es.iesjandula.reaktor.automations_server.dtos;

import es.iesjandula.reaktor.automations_server.models.Aplicabilidad;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para recibir datos al crear o actualizar un sensor numérico.
 */
public class SensorNumericoRequestDto
{
    // Dirección MAC del sensor
    private String mac;
    
    // Estado del sensor (ej. activo/inactivo)
    private String estado;
    
    // Umbral mínimo permitido
    private Double umbralMinimo;
    
    // Umbral máximo permitido
    private Double umbralMaximo;
    
    // Nombre de la ubicación asociada
    private String nombreUbicacion;
	// aplicabilidad del dispositivo
	private Aplicabilidad aplicabilidad;
}

