package es.iesjandula.reaktor.automations_server.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivosUbicacionResponseDto
{
    private String nombreUbicacion;
    private List<ActuadorResponseDto> actuadores;
    private List<SensorNumericoResponseDto> sensoresNumericos;
    private List<SensorBooleanoResponseDto> sensoresBooleanos;
}
