package es.iesjandula.reaktor.automations_server.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.automations_server.dtos.ActuadorResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorBooleanoResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.SensorNumericoResponseDto;
import es.iesjandula.reaktor.automations_server.dtos.VistaPajaroResponseDto;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorBooleanoRepository;
import es.iesjandula.reaktor.automations_server.repository.ISensorNumericoRpository;
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
	private IActuadorRepository actuadorRepository;

	@Autowired
	private ISensorNumericoRpository sensorNumericoRepo;

	@Autowired
	private ISensorBooleanoRepository sensorBooleanoRepo;

	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@GetMapping(value = "/ubicacion/")
	public ResponseEntity<?> obtenerDispositivosUbicacion()
	{
		try
		{
			List<ActuadorResponseDto> listaActuadores = this.actuadorRepository.buscarActuadoresPorUbicacion();
			
			Map<String, List<ActuadorResponseDto>> mapaActuadores = new HashMap<String, List<ActuadorResponseDto>>() ;
			
			if (listaActuadores != null && !listaActuadores.isEmpty())
			{
				for (ActuadorResponseDto actuadorDto : listaActuadores)
				{
					String nombreUbicacion = actuadorDto.getNombreUbicacion() ;
					
					List<ActuadorResponseDto> listaMapaActuadores = mapaActuadores.get(nombreUbicacion) ;
					
					if (listaMapaActuadores == null)
					{
						listaMapaActuadores = new ArrayList<ActuadorResponseDto>() ;
						mapaActuadores.put(nombreUbicacion, listaMapaActuadores) ;
					}
					
					listaMapaActuadores.add(actuadorDto) ;
				}
				
			}
				List<SensorNumericoResponseDto> listaSensoresNumericos = this.sensorNumericoRepo.buscarSensoresNumericosPorUbicacion();
				
				Map<String, List<SensorNumericoResponseDto>> mapaSensorNumerico = new HashMap<String, List<SensorNumericoResponseDto>>() ;
				
			if (listaSensoresNumericos != null && !listaSensoresNumericos.isEmpty())
			{
				for (SensorNumericoResponseDto sensorNumericoDto : listaSensoresNumericos)
				{
					String nombreUbicacion = sensorNumericoDto.getNombreUbicacion() ;
					
					List<SensorNumericoResponseDto> listaMapaSensorNumerico = mapaSensorNumerico.get(nombreUbicacion) ;
					
					if (listaMapaSensorNumerico == null)
					{
						listaMapaSensorNumerico  = new ArrayList<SensorNumericoResponseDto>() ;
						mapaSensorNumerico.put(nombreUbicacion, listaMapaSensorNumerico) ;
					}
					
					listaMapaSensorNumerico.add(sensorNumericoDto) ;
				}
			
			}
			
			List<SensorBooleanoResponseDto> listaSensoresBooleanos = this.sensorBooleanoRepo.buscarSensoresBooleanosPorUbicacion();
			
			Map<String, List<SensorBooleanoResponseDto>> mapaSensorBooleano = new HashMap<String, List<SensorBooleanoResponseDto>>() ;
			
			if (listaSensoresBooleanos != null && !listaSensoresBooleanos.isEmpty())
			{
				for (SensorBooleanoResponseDto sensorBooleanoDto : listaSensoresBooleanos)
				{
					String nombreUbicacion = sensorBooleanoDto.getNombreUbicacion() ;
					
					List<SensorBooleanoResponseDto> listaMapaSensorBooleano = mapaSensorBooleano.get(nombreUbicacion) ;
					
					if (listaMapaSensorBooleano == null)
					{
						listaMapaSensorBooleano  = new ArrayList<SensorBooleanoResponseDto>() ;
						mapaSensorBooleano.put(nombreUbicacion, listaMapaSensorBooleano) ;
					}
					
					listaMapaSensorBooleano.add(sensorBooleanoDto) ;
				}
			
			}
			
			VistaPajaroResponseDto pajaroResponseDto = new VistaPajaroResponseDto(mapaActuadores, mapaSensorBooleano, mapaSensorNumerico);

			return ResponseEntity.ok(pajaroResponseDto);
		} 
		catch (Exception exception)
		{
			AutomationsServerException automationsServerException = new AutomationsServerException(Constants.ERR_CODE,Constants.ERR_CODE);
			log.error("Excepción genérica al crear la incidencia", exception);
			return ResponseEntity.status(500).body(automationsServerException.getBodyExceptionMessage());
		}
	}
}

