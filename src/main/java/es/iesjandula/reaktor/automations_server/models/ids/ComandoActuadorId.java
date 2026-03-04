package es.iesjandula.reaktor.automations_server.models.ids;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ComandoActuadorId implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @Column(name = "mac")
    private String mac;

    @Column(name = "keyword")
    private String keyword;
}
