package es.iesjandula.reaktor.automations_server.models;

import java.util.List;

import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name = "comando_actuador")
public class ComandoActuador {

    @EmbeddedId
    private ComandoActuadorId comandoActuadorId;

    @Column
    private String comandos;

    @ManyToOne
    @MapsId("mac")
    @JoinColumn(name = "mac")
    private Actuador actuador;
    
    @OneToMany(mappedBy = "comandoActuador")
    private List<Comando> listaComandos;
}
