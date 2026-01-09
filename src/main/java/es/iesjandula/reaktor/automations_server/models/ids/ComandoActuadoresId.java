package es.iesjandula.reaktor.automations_server.models.ids;

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
public class ComandoActuadoresId 
{
	private String mac;
	private String keyword;
}
