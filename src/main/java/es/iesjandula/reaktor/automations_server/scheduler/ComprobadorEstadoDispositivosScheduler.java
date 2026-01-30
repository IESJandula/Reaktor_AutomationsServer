package es.iesjandula.reaktor.automations_server.scheduler;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.iesjandula.reaktor.automations_server.models.Actuador;
import es.iesjandula.reaktor.automations_server.repository.IActuadorRepository;

@Component
public class ComprobadorEstadoDispositivosScheduler {

	private final IActuadorRepository actuadorRepository;

	@Value("${reaktor.comprobadorEstadoDispositivosTiempoMaximo}")
	private int comprobadorEstadoDispositivosTiempoMaximo;

	public ComprobadorEstadoDispositivosScheduler(IActuadorRepository actuadorRepository) {
		this.actuadorRepository = actuadorRepository;
	}

	@Transactional
	@Scheduled(fixedDelayString = "${reaktor.comprobadorEstadoDispositivosTiempoMaximo}")
	public void comprobarEstadoDispositivos() {

		Date limite = new Date(System.currentTimeMillis() - this.comprobadorEstadoDispositivosTiempoMaximo);

		List<Actuador> actuadoresCaducados = actuadorRepository.findByEstadoAndUltimaActualizacionBefore("on", limite);

		if (!actuadoresCaducados.isEmpty()) {
			System.out.println("Actuadores sin respuesta: " + actuadoresCaducados.size());

			for (Actuador a : actuadoresCaducados) {
				System.out.println("Poniendo OFF a: " + a.getMac());
				a.setEstado("off");
			}

			actuadorRepository.saveAll(actuadoresCaducados);
		}
	}
}
