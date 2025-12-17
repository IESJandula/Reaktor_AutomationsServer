package es.iesjandula.reaktor.automations_school_server.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="comando")
public class Comando 
{
	@Id
	@Column
	private String keyword;
	
	@Column
	private String comando;
	
	@ManyToOne
	@JoinColumn(name = "orden_id")
	private Orden orden;
	
    @OneToMany(mappedBy = "comando")
    private List<ComandoActuador> listaComandos;
	
	
}
