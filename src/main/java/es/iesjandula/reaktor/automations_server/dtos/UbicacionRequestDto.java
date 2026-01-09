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
 * DTO para recibir datos al crear o actualizar una ubicación.
 */
public class UbicacionRequestDto
{
    // Nombre de la ubicación
	private String nombreUbicacion;
}
