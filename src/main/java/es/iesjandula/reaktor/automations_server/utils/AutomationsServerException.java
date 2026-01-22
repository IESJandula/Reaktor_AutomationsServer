package es.iesjandula.reaktor.automations_server.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Excepción personalizada para el servidor Automation School.
 * Permite incluir un código de error y un mensaje detallado, además de la causa.
 */
public class AutomationsServerException extends Exception
{
    // Identificador de versión de la clase Serializable
	private static final long serialVersionUID = -621923489318165563L;
    
    // Código de error personalizado
	private String codigo;
	
    /**
     * Constructor con código y mensaje.
     */
	public AutomationsServerException(String codigo, String mensaje)
	{
		super(mensaje);
		this.codigo = codigo;
	}
	
    /**
     * Constructor con código, mensaje y causa original.
     */
	public AutomationsServerException(String errorId, String message, Throwable excepcion)
	{
		super(message, excepcion);
		this.codigo = errorId;
	}
	
    /**
     * Devuelve un mapa con los detalles de la excepción:
     * código, mensaje y stacktrace de la causa si existe.
     */
	public Object getBodyExceptionMessage()
	{
		Map<String, Object> mapBodyException = new HashMap<>();
		mapBodyException.put("codigo", this.codigo);
		mapBodyException.put("message", this.getMessage());
		
		if (this.getCause() != null)
		{
			String stackTrace = ExceptionUtils.getStackTrace(this.getCause());
			mapBodyException.put("excepcion", stackTrace);
		}
		
		return mapBodyException;
	}

    /**
     * Devuelve el código de la excepción como Integer.
     * 
     */
	public Integer getCodigo() {
	    return this.codigo == null ? null : Integer.valueOf(this.codigo);
	}
}
