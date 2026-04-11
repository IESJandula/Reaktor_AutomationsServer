package es.iesjandula.reaktor.automations_server.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
@Table(name="actuador_puerta")
public class ActuadorPuerta extends Actuador
{
    @Column
    private Integer numeroReles;
    
    @OneToMany(mappedBy = "actuadorPuerta")
    private List<ComandoActuadorPuerta> listaComandosPuerta;
}