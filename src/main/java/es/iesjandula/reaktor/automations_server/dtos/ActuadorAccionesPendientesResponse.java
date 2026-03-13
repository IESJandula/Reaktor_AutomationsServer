package es.iesjandula.reaktor.automations_server.dtos;

import lombok.Data;

@Data
public class ActuadorAccionesPendientesResponse
{
    /** Atributo - ID de la acción */
    private Long accionId;
  
    /** Atributo - Orden de la acción */
    private String orden;
}
