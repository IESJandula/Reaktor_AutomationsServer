package es.iesjandula.reaktor.automations_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComandoActuadorPuertaResponseDto
{
    private String comandoActuadorMac;
    private String comandoActuadorKeyword;
    private Integer indiceRele;
}