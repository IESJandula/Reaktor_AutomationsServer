package es.iesjandula.reaktor.automations_server.models.ids;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ComandoId implements Serializable 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column
	private ComandoActuadorId comandoActuadorId ;
	
	@Column(name="orden_id")
	private Long ordenId;
	
}
