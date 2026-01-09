package es.iesjandula.reaktor.automations_server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa una 'Accion' (registro de ejecución) en la base de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="accion")
public class Accion
{
    /** Clave primaria (Primary Key) autogenerada. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Resultado de la acción ejecutada. Columna limitada a 50 caracteres. */
    @Column(length = 50)
    private String resultado;

    /**
     * Relación Muchos a Uno con la entidad Actuador.
     * Clave foránea: 'mac'. (Muchas acciones por UN actuador).
     */
    @ManyToOne
    @JoinColumn(name = "mac")
    private Actuador actuador;

    /**
     * Relación Muchos a Uno con la entidad Orden.
     * Clave foránea: 'orden_id'. (Muchas acciones por UNA orden).
     */
    @ManyToOne
    @JoinColumn(name = "orden_id")
    private Orden orden;

}