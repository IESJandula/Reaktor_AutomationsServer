package es.iesjandula.reaktor.automations_school_server.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class AutomationSchoolServerException extends Exception
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -621923489318165563L;
	private String codigo;
	
	public AutomationSchoolServerException(String codigo, String mensaje)
	{
		super(mensaje);
		
		this.codigo=codigo;
	}
	
	public AutomationSchoolServerException(String errorId, String message, Throwable excepcion)
	{
		super(message, excepcion);
		
		this.codigo=errorId;
	}
	
	public Object getBodyExceptionMessage()
	{
		Map<String, Object> mapBodyException = new HashMap<>() ;
		
		mapBodyException.put("codigo", this.codigo) ;
		mapBodyException.put("message", this.getMessage()) ;
		
		if (this.getCause() != null)
		{
			String stackTrace = ExceptionUtils.getStackTrace(this.getCause()) ;
			mapBodyException.put("excepcion", stackTrace) ;
		}
		
		return mapBodyException ;
	}

	public Integer getCodigo()
	{
		return this.getCodigo() ;
	}
}
