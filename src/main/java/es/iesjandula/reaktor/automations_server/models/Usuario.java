package es.iesjandula.reaktor.automations_server.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name="usuario")
public class Usuario 
{
	@Id
	@Column
	private String email;
	
	@Column
	private String nombre;
	
	@Column
	private String apellido;
	
    @OneToMany(mappedBy = "usuario")
    private List<Orden> ordenes;
}
