package es.iesjandula.reaktor.automations_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO para recibir datos al crear o actualizar un dispositivo.
 */
public class DispositivoRequestDto
{
    // Dirección MAC del dispositivo
	private String mac;
	
    // Estado del dispositivo (ej. activo/inactivo)
	private String estado;
}