package es.iesjandula.reaktor.automations_school_server.models;

import es.iesjandula.reaktor.automations_school_server.models.ids.ComandoActuadoresId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Table(name="comando_actuador")
public class ComandoActuador 
{
	@EmbeddedId
	private ComandoActuadoresId comandoActuadoresId;
	
	@ManyToOne
	@MapsId("mac")
	@JoinColumn(name = "mac")
	private Actuador actuador;
	
	@ManyToOne
	@MapsId("keyword")
	@JoinColumn(name = "keyword")
	private Comando comando;
	
}