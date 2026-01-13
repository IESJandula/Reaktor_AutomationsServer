package es.iesjandula.reaktor.automations_server.dtos;

import java.util.List;
import java.util.Map;

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
    private Map<String, List<ActuadorResponseDto>> actuadores;
    private Map<String, List<SensorNumericoResponseDto>> sensoresNumericos;
    private Map<String, List<SensorBooleanoResponseDto>> sensoresBooleanos;
}
