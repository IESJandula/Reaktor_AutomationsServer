package es.iesjandula.reaktor.automations_server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una 'Orden Simple' (ejecución inmediata y no recurrente).
 * Hereda todas las propiedades y relaciones de la clase abstracta Orden.
 */
@Entity
@Table(name="orden_simple") 
public class OrdenSimple extends Orden
{
    
}