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
    public static final String ERR_ACTUADOR_SIN_MAC = "Existe un actuador sin dirección MAC";
    public static final String ERR_ACTUADOR_SIN_UBICACION = "Existe un actuador sin ubicación asignada";
    
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
    
    public static final String ERR_AUDIO_CODE = "AUDIO_ERROR";
    public static final String ERR_AUDIO_VACIO = "Audio vacío";
    
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
    
    public static final String ERR_COMANDO_CODE = "COMANDO_ERROR";
    public static final String ERR_COMANDO_KEYWORD_NULO_VACIO = "Keyword vacía o nula";
    public static final String ERR_COMANDO_MAC_NULO_VACIO = "MAC vacía o nula";
    public static final String ERR_COMANDO_ORDEN_ID_NULO = "OrdenId nulo";
    public static final String ERR_COMANDO_ORDEN_NO_EXISTE = "No existe esa orden";
    public static final String ERR_COMANDO_NO_ENCONTRADOS = "No se encontraron comandos";
    public static final String ERR_COMANDO_NO_HAY_PARA_ORDEN = "No hay comandos para esa orden";
    public static final String ERR_COMANDO_NO_HAY_PARA_MAC = "No hay comandos para esa MAC";
    public static final String ERR_COMANDO_PARAMETROS_INVALIDOS = "Parámetros inválidos";
    public static final String ERR_COMANDO_NO_EXISTE_CLAVE = "No existe comando con esa clave";
    
    public static final String ERR_COMANDO_ACTUADOR_CODE = "COMANDO_ACTUADOR_ERROR";
    public static final String ERR_COMANDO_ACTUADOR_KEYWORD_NULO_VACIO = "Keyword vacía o nula";
    public static final String ERR_COMANDO_ACTUADOR_MAC_NULO_VACIO = "MAC vacía o nula";
    public static final String ERR_COMANDO_ACTUADOR_TEXTO_OK_NULO_VACIO = "Información de respuesta correcta vacío o nulo";
    public static final String ERR_COMANDO_ACTUADOR_NO_EXISTE_MAC = "No existe actuador con esa MAC";
    public static final String ERR_COMANDO_ACTUADOR_PARAMETROS_INVALIDOS = "Parámetros inválidos";
    public static final String ERR_COMANDO_ACTUADOR_NO_EXISTE_CLAVE = "No existe comando_actuador con esa clave";
    
    public static final String ERR_COMANDO_ACTUADOR_PUERTA_CODE = "COMANDO_ACTUADOR_PUERTA_ERROR";
    public static final String ERR_COMANDO_ACTUADOR_PUERTA_MAC_NULO_VACIO = "MAC vacía o nula";
    public static final String ERR_COMANDO_ACTUADOR_PUERTA_KEYWORD_NULO_VACIO = "Keyword vacía o nula";
    public static final String ERR_COMANDO_ACTUADOR_PUERTA_INDICE_RELE_NULO = "Índice de relé nulo";
    public static final String ERR_COMANDO_ACTUADOR_PUERTA_COMANDO_ACTUADOR_NO_EXISTE = "No existe comando_actuador con esa clave";
    public static final String ERR_COMANDO_ACTUADOR_PUERTA_ACTUADOR_PUERTA_NO_EXISTE = "No existe actuador_puerta con esa MAC";
    public static final String ERR_COMANDO_ACTUADOR_PUERTA_PARAMETROS_INVALIDOS = "Parámetros inválidos";
    public static final String ERR_COMANDO_ACTUADOR_PUERTA_NO_EXISTE_CLAVE = "No existe comando_actuador_puerta con esa clave";
    
    public static final String ERR_PROYECTOR_ESTADO_NULO_VACIO = "El estado del proyector es nulo o vacío";
    
    public static final String ERR_MAP_CODE = "MAP_ERROR";
    
    /** Aplicabilidad para los tipos de dispositivos */
    public static final List<String> APLICABILIDAD_ACTUADOR = Arrays.asList(new String[] {"Puerta", "Proyector"}); 
    public static final List<String> APLICABILIDAD_SENSOR = Arrays.asList(new String[] {"Humedad", "CO2", "Movimiento"}); 
    
    /*************************************************/
    /***************** ESTADOS ACCION  ***************/
    /*************************************************/

    /** Estado de la acción cuando está pendiente */
    public static final String ESTADO_ACCION_PENDIENTE           = "pendiente";
    
    /** Estado de la acción cuando hay error de validación */
    public static final String ESTADO_ACCION_ERROR_VALIDACION    = "error_validacion";
    
    /** Estado de la acción cuando está en ejecución */
    public static final String ESTADO_ACCION_EN_EJECUCION        = "en_ejecucion";
    
    /** Estado de la acción cuando está finalizada correctamente */
    public static final String ESTADO_ACCION_FINALIZADO_OK       = "finalizado_ok";
    
    /** Estado de la acción cuando está finalizada con error */
    public static final String ESTADO_ACCION_FINALIZADO_ERROR    = "finalizado_error";
    
    /** Estado de la acción cuando ha expirado */
    public static final String ESTADO_ACCION_EXPIRADA            = "expirada" ;

    /** Estado de la acción cuando está duplicada */
    public static final String ESTADO_ACCION_DUPLICADA           = "duplicada" ;

    /*************************************************/
    /*************** ESTADOS ACTUADOR  ***************/
    /*************************************************/

    /** Estado del actuador cuando está encendido */
    public static final String ESTADO_ACTUADOR_ON = "on";
    
    /** Estado del actuador cuando está apagado */
    public static final String ESTADO_ACTUADOR_OFF = "off";

    /*************************************************/
    /*************** ESTADOS ACCION  ***************/
    /*************************************************/

    /** Error en los datos pasados por el actuador - el identificador de la acción no puede ser nulo ni vacío */
    public static final String ERR_ACTUADOR_ACCION_ID_NULO_VACIO     = "El identificador de la acción no puede ser nulo ni vacío";
    
    /** Error en los datos pasados por el actuador - el estado de la acción no puede ser nulo ni vacío */
    public static final String ERR_ACTUADOR_ACCION_ESTADO_NULO_VACIO = "El estado de la acción no puede ser nulo ni vacío";
}