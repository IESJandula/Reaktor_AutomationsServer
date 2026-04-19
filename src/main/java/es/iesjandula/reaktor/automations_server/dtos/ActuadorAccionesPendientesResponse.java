package es.iesjandula.reaktor.automations_server.dtos;

import lombok.Data;

@Data
public class ActuadorAccionesPendientesResponse
{
    /** ID de la acción */
    private Long accionId;
  
    /** Orden de la acción */
    private String orden;
    
    /** Keyword asociada al comando */
    private String keyword;

    /** Índice de relé, si aplica */
    private Integer indiceRele;
}
