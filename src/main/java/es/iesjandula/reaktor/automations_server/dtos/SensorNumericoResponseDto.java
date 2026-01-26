package es.iesjandula.reaktor.automations_server.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO para enviar información de un sensor numérico.
 */
public class SensorNumericoResponseDto {
	// Dirección MAC del sensor
	private String mac;

	// Estado del sensor (ej. activo/inactivo)
	private String estado;

	// Valor actual del sensor
	private Double valorActual;

	// Umbral mínimo permitido
	private Double umbralMinimo;

	// Umbral máximo permitido
	private Double umbralMaximo;

	// Timestamp de la última actualización
	private Long ultimaActualizacion;

	// Nombre de la ubicación asociada
	private String nombreUbicacion;
	// aplicabilidad del dispositivo
	private String aplicabilidad;

	/**
	 * Constructor para inicializar un objeto DTO (Data Transfer Object) de respuesta para un sensor numérico.
	 */
	public SensorNumericoResponseDto(String mac, String estado, Double valorActual, Double umbralMinimo, Double umbralMaximo, Date ultimaActualizacion, String nombreUbicacion,	String aplicabilidad)
	{
	    // Asigna la dirección MAC única del sensor.
	    this.mac = mac;
	    // Asigna el estado operativo actual del sensor.
	    this.estado = estado;
	    // Asigna el valor numérico actual medido por el sensor.
	    this.valorActual = valorActual;
	    // Asigna el umbral mínimo configurado para este sensor.
	    this.umbralMinimo = umbralMinimo;
	    // Asigna el umbral máximo configurado para este sensor.
	    this.umbralMaximo = umbralMaximo;
	    // Convierte la fecha de última actualización a milisegundos (tipo long) para serialización.
	    // Si la fecha es nula, asigna null al campo.
	    this.ultimaActualizacion = ultimaActualizacion != null ? ultimaActualizacion.getTime() : null;
	    // Asigna el nombre de la ubicación física o lógica del sensor.
	    this.nombreUbicacion = nombreUbicacion;
		// aplicabilidad del dispositivo
		this.aplicabilidad = aplicabilidad;
	}
}
