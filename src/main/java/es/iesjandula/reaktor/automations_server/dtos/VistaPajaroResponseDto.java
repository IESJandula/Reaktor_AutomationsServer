package es.iesjandula.reaktor.automations_server.dtos;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VistaPajaroResponseDto
{
	private Map<String, List<ActuadorResponseDto>> mapaActuadores ;
	private Map<String, List<SensorBooleanoResponseDto>> mapaSensoresBooleanos ;
	private Map<String, List<SensorNumericoResponseDto>> mapaSensoresNumericos ;
}
