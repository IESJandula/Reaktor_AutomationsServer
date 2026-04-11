package es.iesjandula.reaktor.automations_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActuadorPuertaRequestDto
{
    private String mac;
    
    private String estado;
    
    private String tipo;
    
    private String nombreUbicacion;
    
    private Integer numeroReles;
}