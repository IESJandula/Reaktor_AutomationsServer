package es.iesjandula.reaktor.automations_server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase abstracta base que representa cualquier 'Dispositivo' en el sistema.
 * Es la raíz de la jerarquía de herencia.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="dispositivo")
@Inheritance(strategy = InheritanceType.JOINED)
public class Dispositivo
{
    /** Clave primaria (Primary Key) que utiliza la dirección MAC del dispositivo. */
    @Id
    @Column
    private String mac;

    /** Estado actual del dispositivo (e.g., 'Encendido', 'Apagado'). */
    @Column(length = 25)
    private String estado;
    
    /**
     * Relación Muchos a Uno con la entidad Ubicacion.
     * Un dispositivo está asociado a UNA ubicación.
     * Clave foránea: 'ubicacion_nombre'.
     */
    @ManyToOne
    @JoinColumn(name = "ubicacion_nombre")
    private Ubicacion ubicacion;
}