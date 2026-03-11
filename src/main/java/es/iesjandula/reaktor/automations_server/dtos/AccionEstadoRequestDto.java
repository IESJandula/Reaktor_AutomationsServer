package es.iesjandula.reaktor.automations_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccionEstadoRequestDto
{
    /** Identificador de la acción */
    private Long accionId;

    /** Estado de la acción */
    private String estado;
    
    /** Resultado de la acción */
    private String resultado;
}