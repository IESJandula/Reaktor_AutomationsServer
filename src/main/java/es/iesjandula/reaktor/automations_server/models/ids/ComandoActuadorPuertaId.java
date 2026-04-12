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
public class ComandoActuadorPuertaId implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @Column(name = "comando_actuador_mac")
    private String comandoActuadorMac;

    @Column(name = "comando_actuador_keyword")
    private String comandoActuadorKeyword;
    
    @Column(name = "indice_rele")
    private Integer indiceRele;
}