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
 * DTO para recibir datos al crear o actualizar una orden programada.
 */
public class OrdenProgramadaRequestDto
{
    // ID de la orden programada
    private Long id;
    
    // Fecha de creación de la orden
    private Date fecha;
    
    // Fecha en la que se ejecutará la orden
    private Date fechaProgramada;
    
    // Tipo de repetición (ej. diaria, semanal)
    private String repeticion;
    
    // Frase o acción que se ejecutará
    private String frase;
}

