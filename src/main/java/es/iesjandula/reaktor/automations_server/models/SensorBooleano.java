package es.iesjandula.reaktor.automations_server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un 'Sensor Booleano' (solo puede tener dos estados: verdadero o falso).
 * Hereda las propiedades de la clase abstracta Sensor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sensor_booleano")
public class SensorBooleano extends Sensor
{
    /** Valor actual reportado por el sensor (True/False). */
    @Column
    private Boolean valorActual;

}