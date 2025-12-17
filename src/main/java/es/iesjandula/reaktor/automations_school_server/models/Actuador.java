package es.iesjandula.reaktor.automations_school_server.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un 'Actuador' (dispositivo capaz de ejecutar acciones).
 * Hereda propiedades comunes de Dispositivo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="actuador")
public class Actuador extends Dispositivo
{
    /**
     * Relación Uno a Muchos con la entidad Accion.
     * Mapeado por el campo 'actuador' en la clase Accion.
     * Un Actuador puede realizar MUCHAS acciones.
     */
    @OneToMany(mappedBy = "actuador")
    private List<Accion> acciones;
    
    @OneToMany(mappedBy = "actuador")
    private List<ComandoActuador> listaComandos;



}