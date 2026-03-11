package es.iesjandula.reaktor.automations_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActuadorEstadoResponseDto
{
    private Long accionId;
    private String estado;
    private String resultado;
    private String mac;
    private Long ordenId;
}