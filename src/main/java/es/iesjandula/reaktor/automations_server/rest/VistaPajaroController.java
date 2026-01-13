package es.iesjandula.reaktor.automations_server.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class VistaPajaroController
{
	@Autowired
	private IUbicacionRepository ubicacionRepository;

	@Autowired
	private IActuadorRepository actuadorRepository;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;

	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_PROFESOR + "')")
	@GetMapping("/ubicacion/{nombreUbicacion}")
	public ResponseEntity<?> obtenerDispositivosUbicacion(@PathVariable String nombreUbicacion)
	{
		try
		{
			if (nombreUbicacion == null || nombreUbicacion.isEmpty())
			{
				log.error(Constants.ERR_UBICACION_NULO_VACIO);
				throw new AutomationsServerException(Constants.ERR_UBICACION_NULO_VACIO, Constants.ERR_UBICACION_CODE);
			}

			if (!this.ubicacionRepository.existsById(nombreUbicacion))
			{
				log.error(Constants.ERR_UBICACION_NO_EXISTE);
				throw new AutomationsServerException(Constants.ERR_UBICACION_NO_EXISTE, Constants.ERR_UBICACION_CODE);
			}

			List<ActuadorResponseDto> actuadores = this.actuadorRepository
					.buscarActuadoresPorUbicacion(nombreUbicacion);

			List<SensorNumericoResponseDto> sensoresNumericos = this.sensorNumericoRepo
					.buscarSensoresNumericosPorUbicacion(nombreUbicacion);

			List<SensorBooleanoResponseDto> sensoresBooleanos = this.sensorBooleanoRepo
					.buscarSensoresBooleanosPorUbicacion(nombreUbicacion);

			DispositivosUbicacionResponseDto dispositivosUbicacionResponseDto = new DispositivosUbicacionResponseDto(
					actuadores, sensoresNumericos, sensoresBooleanos);

			Map<String, DispositivosUbicacionResponseDto> resultado = new HashMap<>();
			resultado.put(nombreUbicacion, dispositivosUbicacionResponseDto);

			return ResponseEntity.ok(resultado);
		} 
		catch (AutomationsServerException exception)
		{
			return ResponseEntity.badRequest().body(exception);
		} 
		catch (Exception exception)
		{
			AutomationsServerException automationsServerException = new AutomationsServerException(Constants.ERR_CODE,Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", exception);
			return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}
}
