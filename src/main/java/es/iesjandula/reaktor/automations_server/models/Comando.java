package es.iesjandula.reaktor.automations_server.models;

import es.iesjandula.reaktor.automations_server.models.ids.ComandoId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name="comando")
public class Comando 
{
	@EmbeddedId
	private ComandoId comandoId;
	
	@MapsId("ordenId")
	@ManyToOne
	@JoinColumn(name = "orden_id")
	private Orden orden;
	
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "mac",      referencedColumnName = "mac",      insertable = false, updatable = false),
        @JoinColumn(name = "keyword",  referencedColumnName = "keyword",  insertable = false, updatable = false)
    })
    private ComandoActuador comandoActuador;
}
