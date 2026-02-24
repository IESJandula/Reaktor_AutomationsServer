package es.iesjandula.reaktor.automations_server.utils;

import java.util.List;

import io.jsonwebtoken.lang.Arrays;

public class Constants
{
	public static final String ELEMENTO_AGREGADO = "Elemento agregado";
    public static final String ELEMENTO_MODIFICADO = "Elemento modificado correctamente.";
    public static final String ELEMENTO_ELIMINADO = "Elemento eliminado correctamente.";
    public static final String ERR_CODE = "Error de servidor";
	
	public static final String ERR_ACTUADOR_CODE = "ACTUADOR_ERROR";
	public static final String ERR_ACTUADOR_NULO_VACIO = "El nombre del actuador no puede ser nulo ni vacío.";
    public static final String ERR_ACTUADOR_EXISTE = "El actuador ya existe en el sistema.";
    public static final String ERR_ACTUADOR_NO_EXISTE = "El actuador no existe en el sistema.";
    
    public static final String ERR_SENSOR_CODE = "SENSOR_ERROR";
	public static final String ERR_SENSOR_NULO_VACIO = "El nombre del sensor no puede ser nulo ni vacío.";
    public static final String ERR_SENSOR_EXISTE = "El sensor ya existe en el sistema.";
    public static final String ERR_SENSOR_NO_EXISTE = "El sensor no existe en el sistema.";
    
    public static final String ERR_UBICACION_CODE = "UBICACION_ERROR";
	public static final String ERR_UBICACION_NULO_VACIO = "El nombre de la ubicacion no puede ser nulo ni vacío.";
    public static final String ERR_UBICACION_EXISTE = "La ubicacion ya existe en el sistema.";
    public static final String ERR_UBICACION_NO_EXISTE = "La ubicacion no existe en el sistema.";
    
    public static final String ERR_SIMPLE_CODE = "SIMPLE_ERROR";
	public static final String ERR_SIMPLE_NULO_VACIO = "El identificador de orden orden simple no puede ser nulo ni vacío.";
    public static final String ERR_SIMPLE_EXISTE = "La orden simple ya existe en el sistema.";
    public static final String ERR_SIMPLE_NO_EXISTE = "La orden simple no existe en el sistema.";
    
    public static final String ERR_PROGRAMADA_CODE = "PROGRAMADA_ERROR";
    public static final String ERR_PROGRAMADA_FECHA_NULA = "Fecha programada nula";
	public static final String ERR_PROGRAMADA_NULO_VACIO = "El identificador de orden programada no puede ser nulo ni vacío.";
    public static final String ERR_PROGRAMADA_EXISTE = "La orden programada ya existe en el sistema.";
    public static final String ERR_PROGRAMADA_NO_EXISTE = "La orden programada no existe en el sistema.";
    
    public static final String ERR_VALIDACION_CODE = "VALIDACION_ERROR";
	public static final String ERR_VALIDACION_NULO_VACIO = "El identificador validacion no puede ser nulo ni vacío.";
    public static final String ERR_VALIDACION_EXISTE = "La validacion ya existe en el sistema.";
    public static final String ERR_VALIDACON_NO_EXISTE = "La validacion programada no existe en el sistema.";
    
    public static final String ERR_ACCION_CODE = "ACCION_ERROR";
    public static final String ERR_ACCION_DATOS_NULOS = "El nombre o el identificador de la accion no pueden ser nulos o vacíos.";
    public static final String ERR_ACCION_EXISTE = "La relación Accion ya existe.";
    public static final String ERR_ACCION_NO_EXISTE = "La relación Accion no existe.";
    
    public static final String ERR_ORDEN_CODE = "ORDEN_ERROR";
	public static final String ERR_ORDEN_NULO_VACIO = "El identificador de orden no puede ser nulo ni vacío.";
    public static final String ERR_ORDEN_EXISTE = "La orden ya existe en el sistema.";
    public static final String ERR_ORDEN_NO_EXISTE = "La orden no existe en el sistema.";
    
    /** Aplicabilidad para los dispositivos */
    public static final List<String> APLICABILIDAD = Arrays.asList(new String[] {"Puerta", "Proyector"});    
}
