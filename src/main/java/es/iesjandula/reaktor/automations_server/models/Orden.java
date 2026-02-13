package es.iesjandula.reaktor.automations_server.models;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase abstracta base que representa una 'Orden' o solicitud de acción.
 * Es la raíz de la jerarquía de herencia para diferentes tipos de órdenes.
 */
@Getter
@Setter
@Entity
@Table(name="orden")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Orden
{
    /** Clave primaria (Primary Key) autogenerada para la orden. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Fecha de creación o ejecución de la orden. No puede ser nula. */
    @Column(nullable = false)
    private Date fecha;
    
    /** Descripción textual o frase asociada a la orden. */
    @Column(length = 255)
    private String frase;
    
    /** Email del usuario que da la orden*/
	@Column
	private String email;
    
    /** Nombre del usuario que da la orden*/
	@Column
	private String nombre;
	
	/** Apellido del usuario que da la orden*/
	@Column
	private String apellidos;
	
    /**
     * Relación Uno a Muchos con la entidad Accion.
     * Mapeado por el campo 'orden' en la clase Accion.
     * Una Orden genera MUCHAS acciones.
     */
    @OneToMany(mappedBy = "orden")
    private List<Accion> acciones;

    /**
     * Relación Uno a Muchos con la entidad Validacion.
     * Mapeado por el campo 'orden' en la clase Validacion.
     * Una Orden puede requerir MUCHAS validaciones.
     */
    @OneToMany(mappedBy = "orden")
    private List<Validacion> validaciones;
    
    /**
     * Relación Uno a Muchos con la entidad Comandos.
     * Mapeado por el campo 'orden' en la clase Comandos.
     */
    @OneToMany(mappedBy = "orden")
    private List<Comando> comandos;
}