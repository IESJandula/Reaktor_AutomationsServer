package es.iesjandula.reaktor.automations_server.repository;

import java.util.List;

public interface ComandoActuadorCustomRepository 
{
    List<Object[]> rankPorTokens(List<String> tokens);
}