package es.iesjandula.reaktor.automations_server.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para recibir datos al crear o actualizar una orden simple.
 */
public class OrdenSimpleRequestDto
{
    // ID de la orden simple
    private Long id;
    
    // Fecha de creación de la orden
    private Date fecha;
    
    // Frase o acción que se ejecutará
    private String frase;
}

