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
 * DTO genérico para recibir datos al crear o actualizar un sensor.
 */
public class SensorRequestDto
{
    // Dirección MAC del sensor
	private String mac;
    
    // Estado del sensor (ej. activo/inactivo)
	private String estado;	
	
    // Valor actual del sensor como cadena
    private String valor;
    
    // Tipo de sensor (ej. booleano, numérico)
    private String tipo;
}

