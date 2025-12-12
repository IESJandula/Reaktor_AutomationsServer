package es.iesjandula.reaktor.automations_school_server.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sensor")
public abstract class Sensor extends Dispositivo
{

    @Column(nullable = false)
    private Date ultimaActualizacion;
    
}
