package es.iesjandula.reaktor.automations_server.dtos;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class VistaPajaroResponseDto
{
	private Map<String, List<ActuadorResponseDto>> mapaActuadores ;
	private Map<String, List<SensorBooleanoResponseDto>> mapaSensoresBooleanos ;
	private Map<String, List<SensorNumericoResponseDto>> mapaSensoresNumericos ;
}
