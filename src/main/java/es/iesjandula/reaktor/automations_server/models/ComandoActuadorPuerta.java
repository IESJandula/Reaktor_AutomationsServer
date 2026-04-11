package es.iesjandula.reaktor.automations_server.models;

import es.iesjandula.reaktor.automations_server.models.ids.ComandoActuadorPuertaId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
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
@Table(name = "comando_actuador_puerta")
public class ComandoActuadorPuerta
{
    @EmbeddedId
    private ComandoActuadorPuertaId comandoActuadorPuertaId;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "comando_actuador_mac", referencedColumnName = "mac", insertable = false, updatable = false),
        @JoinColumn(name = "comando_actuador_keyword", referencedColumnName = "keyword", insertable = false, updatable = false)
    })
    private ComandoActuador comandoActuador;
    
    @ManyToOne
    @JoinColumn(name = "comando_actuador_mac", referencedColumnName = "mac", insertable = false, updatable = false)
    private ActuadorPuerta actuadorPuerta;
}