package es.iesjandula.reaktor.automations_server.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa una 'Ubicacion' (lugar físico) en la base de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ubicacion")
public class Ubicacion
{

    /** Clave primaria (Primary Key) que utiliza el nombre de la ubicación. */
    @Id
    @Column
    private String nombreUbicacion;

    /**
     * Relación Uno a Muchos con la clase base Dispositivo.
     * Mapeado por el campo 'ubicacion' en la clase Dispositivo.
     * Una Ubicación puede contener MUCHOS dispositivos.
     */
    @OneToMany(mappedBy = "ubicacion")
    private List<Dispositivo> dispositivos;
    
}