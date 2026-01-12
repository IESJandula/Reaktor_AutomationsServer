package es.iesjandula.reaktor.automations_server.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase abstracta base que representa un 'Sensor' (dispositivo capaz de medir o enviar datos).
 * Hereda las propiedades comunes de Dispositivo y añade información de actualización.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sensor")
public class Sensor extends Dispositivo
{

    /** Fecha y hora de la última vez que el sensor envió datos o se actualizó su estado.
     */
    @Column
    private Date ultimaActualizacion;
    
    /** Valor mínimo permitido o configurado para la alerta del sensor. */
    @Column
    private Double umbralMinimo;
    
    /** Valor máximo permitido o configurado para la alerta del sensor. */
    @Column
    private Double umbralMaximo;
    
}