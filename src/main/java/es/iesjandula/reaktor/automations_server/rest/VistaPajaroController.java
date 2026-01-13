package es.iesjandula.reaktor.automations_server.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.DispositivosUbicacionResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorNumericoResponseDto;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorNumericoRpository;
import es.iesjandula.reaktor.automations_server.repository.IUbicacionRepository;
import es.iesjandula.reaktor.automations_server.utils.AutomationsServerException;
import es.iesjandula.reaktor.automations_server.utils.Constants;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/automations/map")
public class VistaPajaroController {
	@Autowired
	private IUbicacionRepository ubicacionRepository;

	@Autowired
	private IActuadorRepository actuadorRepository;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;

	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;
	
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@GetMapping("/ubicacion/")
	public ResponseEntity<?> obtenerDispositivosUbicacion() 
	{
		try 
		{
			Map<String, List<ActuadorResponseDto>> actuadores = this.actuadorRepository.buscarActuadoresPorUbicacion();

			Map<String, List<SensorNumericoResponseDto>> sensoresNumericos = this.sensorNumericoRepo.buscarSensoresNumericosPorUbicacion();

			Map<String, List<SensorBooleanoResponseDto>> sensoresBooleanos = this.sensorBooleanoRepo.buscarSensoresBooleanosPorUbicacion();

			DispositivosUbicacionResponseDto dispositivosUbicacionResponseDto = new DispositivosUbicacionResponseDto(actuadores, sensoresNumericos, sensoresBooleanos);

			return ResponseEntity.ok(dispositivosUbicacionResponseDto);
		} 
		catch (Exception exception) 
		{
			AutomationsServerException automationsServerException = new AutomationsServerException(Constants.ERR_CODE,Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", automationsServerException);
			return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}
}
