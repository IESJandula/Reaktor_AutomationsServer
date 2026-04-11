package es.iesjandula.reaktor.automations_server.models;

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
@Table(name="actuador_proyector")
public class ActuadorProyector extends Actuador
{
    @Column
    private String comandoEstado;
}