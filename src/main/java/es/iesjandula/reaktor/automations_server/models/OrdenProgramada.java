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
 * Entidad JPA que representa una 'Orden Programada'.
 * Hereda las propiedades base de la clase abstracta Orden.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="orden_programada")
public class OrdenProgramada extends Orden
{
    /** Fecha en la que la orden está programada para ejecutarse. Puede ser nula. */
    @Column(nullable = true)
    private Date fechaProgramada; 
    
    /** * Frecuencia o patrón de repetición de la orden (e.g., 'Diario', 'Semanal').
     * Columna limitada a 50 caracteres y puede ser nula. 
     */
    @Column(length = 50, nullable = true)
    private String repeticion;
}