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
 * Entidad JPA que representa el resultado de una 'Validacion' asociada a una orden.
 * Se utiliza para registrar la verificación y el resultado de una solicitud.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="validacion")
public class Validacion
{
    /** Clave primaria (Primary Key) autogenerada. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    /** Puntuación o nivel de verificación de la validación. No puede ser nulo. */
    @Column(nullable = false)
    private Double score; 
    
    /** Resultado final de la validación (e.g., 'Aceptado', 'Rechazado'). No puede ser nulo. */
    @Column(length = 50, nullable = false)
    private String resultado; 

    /** Texto de la respuesta. No puede ser nulo. */
    @Column(length = 255, nullable = false)
    private String textoRespuesta;

    /**
     * Relación Muchos a Uno con la entidad Orden.
     * Clave foránea: 'orden_id'. Muchas validaciones están asociadas a UNA sola orden.
     */
    @ManyToOne
    @JoinColumn(name = "orden_id")
    private Orden orden;
}