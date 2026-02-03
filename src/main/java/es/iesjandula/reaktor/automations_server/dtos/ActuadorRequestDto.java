package es.iesjandula.reaktor.automations_server.dtos;

import es.iesjandula.reaktor.automations_server.models.Aplicabilidad;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para recibir datos al crear o actualizar un actuador.
 */
public class ActuadorRequestDto
{
    // Dirección MAC del actuador
	private String mac;	
	
    // Estado del actuador (ej. encendido/apagado)
	private String estado;
	
    // Nombre de la ubicación asociada
	private String nombreUbicacion;
	
	// aplicabilidad del dispositivo
	private Aplicabilidad aplicabilidad;
}
