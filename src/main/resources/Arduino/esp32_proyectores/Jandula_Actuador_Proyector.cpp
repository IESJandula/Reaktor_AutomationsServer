#include "Jandula_Actuador_Proyector.h"
#include <ArduinoJson.h>

/*******************************************************/
/****************** Variables globales *****************/
/*******************************************************/

// urlValidacionAccionPendiente: URL de validación de acción pendiente
String urlValidacionAccionPendiente = "";

// urlAvisoServidorAccionEstado: URL de aviso al servidor de la acción establecida
String urlAvisoServidorAccionEstado = "";

HardwareSerial MySerial(2);

/***************************************************************************/
/****** Inicialización de los componentes del actuador de la puerta ********/
/***************************************************************************/

/**
 * Realiza la inicialización de los diferentes componentes del actuador de la puerta
 * 
 * @return void
 */
void setupJandulaActuadorPuerta()
{
    // Cargamos la configuración desde el fichero de configuración del actuador de la puerta
    parseaFicheroConfiguracionJandulaActuadorPuerta(RUTA_FICHERO_CONFIGURACION);
}

/*******************************************************/
/********* Parseo de fichero de configuración **********/
/*******************************************************/
 
/**
 * Carga la configuración desde el archivo de configuración de la biblioteca Jandula Actuador Puerta
 * 
 * @param configFilePath: ruta del archivo de configuración
 * @return void
 */
 void parseaFicheroConfiguracionJandulaActuadorPuerta(String rutaFicheroConfiguracion) 
 {
    // Abrimos el fichero de configuración en modo lectura de LittleFS
    File ficheroConfiguracion = LittleFS.open(rutaFicheroConfiguracion, "r");
  
    // Si no se puede abrir el fichero de configuración ...
    if (!ficheroConfiguracion)
    {
      // Almacenamos el error en la variable global errorGeneralJandulaBase
      errorGeneralJandulaBase = "ERROR: No se pudo abrir el fichero de configuración: " + rutaFicheroConfiguracion;
 
      // Mostramos un mensaje de error
      Serial.println(errorGeneralJandulaBase);
    }  
    else
    {
        // Leemos el fichero de configuración línea por línea
        while (ficheroConfiguracion.available())
        {
            // Leemos la línea del fichero de configuración y la procesamos
            String linea = ficheroConfiguracion.readStringUntil('\n');

            // Eliminamos los espacios en blanco al principio y al final de la línea
            linea.trim();

            // Si la línea comienza con la propiedad de la URL de actualización de estado de un actuador, se procesa
            if (linea.startsWith(PROPIEDAD_URL_VALIDAR_ACCION_PENDIENTE))
            {
                // Se asigna a la variable urlValidacionAccionPendiente
                urlValidacionAccionPendiente = linea.substring(PROPIEDAD_URL_VALIDAR_ACCION_PENDIENTE_LENGTH + 1);
                urlValidacionAccionPendiente.trim();
            }
            // Si la línea comienza con la propiedad de la URL de aviso al servidor de la acción establecida, se procesa
            else if (linea.startsWith(PROPIEDAD_URL_AVISO_SERVIDOR_ACCION_ESTADO))
            {
                // Se asigna a la variable urlAvisoServidorAccionEstado
                urlAvisoServidorAccionEstado = linea.substring(PROPIEDAD_URL_AVISO_SERVIDOR_ACCION_ESTADO_LENGTH + 1);
                urlAvisoServidorAccionEstado.trim();
            }
        }

        // Cerramos el fichero de configuración
        ficheroConfiguracion.close();
    }
 
    // Si no hay un error general, validamos si todos los campos están rellenos
    if (errorGeneralJandulaBase == "")
    {
        parseaFicheroConfiguracionJandulaActuadorPuertaValidarCamposRellenos();
    }
}
 
 /**
  * Valida si todos los campos del fichero de configuración de la biblioteca Jandula Actuador Puerta están rellenos
  * 
  * @return void
  */
void parseaFicheroConfiguracionJandulaActuadorPuertaValidarCamposRellenos()
{
   // Validamos si la URL de validación de acción pendiente está rellena
   if (urlValidacionAccionPendiente.length() == 0)
   {
     // Almacenamos el error en la variable global errorGeneralJandulaBase
     errorGeneralJandulaBase = "ERROR: URL de validación de acción pendiente vacía";
   }
   // Validamos si la URL de aviso al servidor de la acción establecida está rellena
   else if (urlAvisoServidorAccionEstado.length() == 0)
   {
     // Almacenamos el error en la variable global errorGeneralJandulaBase
     errorGeneralJandulaBase = "ERROR: URL de aviso al servidor del estado de la acción vacía";
   }
 
   if (errorGeneralJandulaBase != "")
   {
     // Mostramos un mensaje de error
     Serial.println(errorGeneralJandulaBase);
   }
   else
   {
     // Mostramos un mensaje de información
     Serial.println("INFO: Todos los campos del fichero de configuración del actuador de la puerta están rellenos");
   }
}

/*******************************************************/
/* Funciones propias para la comunicación con servidor */
/*******************************************************/

/**
 * Valida si hay una acción pendiente
 * 
 * @return AccionPendiente: Estructura de datos AccionPendiente
 */
 AccionPendiente validarSiHayAccionPendiente()
 {
     // Inicializamos la estructura de datos AccionPendiente
     AccionPendiente accionPendiente;
     accionPendiente.accionId  = 0;
     accionPendiente.orden     = "";
     accionPendiente.hayAccion = false;
 
     // Antes de actualizar el estado de un actuador, obtenemos el token JWT
     obtenerTokenJWT();
 
     // Inicializamos el cliente HTTP
     HTTPClient httpValidacionAccionPendiente;
     httpValidacionAccionPendiente.setTimeout(TIME_PETICIONES_HTTP);
 
     // Inicializamos el dato de respuesta HTTP
     String httpResponseData = "";
 
     // Mostramos los detalles de la petición
     Serial.println("INFO: Detalles de la petición para validar si hay una acción pendiente: ");
     Serial.print("-- Dirección del servidor: ");
     Serial.println(urlValidacionAccionPendiente);
     Serial.print("-- MAC: ");
     Serial.println(macAddress);
 
     // Si se puede iniciar la conexión HTTP, se actualiza el estado del actuador
     if (iniciarConexionHTTPConReintentos(httpValidacionAccionPendiente, urlValidacionAccionPendiente))
     {
         // Añadimos el header de autorización
         httpValidacionAccionPendiente.addHeader("Authorization", "Bearer " + tokenJWT);
 
         // Añadimos el header de MAC
         httpValidacionAccionPendiente.addHeader("mac", macAddress);
 
         // Enviamos la petición
         int httpResponseCode = httpValidacionAccionPendiente.POST("");
 
         // Si la respuesta no está en el rango del 200 al 299, es incorrecta
         if (httpResponseCode < 200 || httpResponseCode >= 300)
         {
             // Almacenamos el error en la variable local errorString
             String errorString = "ERROR: La petición HTTP para validar si hay una acción pendiente ha fallado. Código: " + String(httpResponseCode) ;
 
             // Mostramos un mensaje de error
             Serial.println(errorString);
         }
         else
         {
             // Obtenemos el dato de respuesta HTTP
             String bodyResponse = httpValidacionAccionPendiente.getString();
 
             // Si el dato de respuesta HTTP es válido, se muestra el dato de respuesta HTTP
             if (bodyResponse.length() == 0)
             {
                 // Almacenamos el error en la variable local errorString
                 String errorString = "ERROR: La respuesta de la API de validación de acción pendiente es vacía";
 
                 // Mostramos un mensaje de error
                 Serial.println(errorString);
             }
             else
             {
                 // Mostramos el dato de respuesta HTTP
                 Serial.println("INFO: Respuesta recibida de la API de validación de acción pendiente:");
                 Serial.println(bodyResponse);
 
                 // Ahora parsearemos el JSON de la respuesta
                 accionPendiente = validarSiHayAccionPendienteParsearRespuesta(bodyResponse);
             }
         }
     }
 
     // Liberamos la memoria del cliente HTTP
     httpValidacionAccionPendiente.end();
 
     // Devolvemos la estructura de datos AccionPendiente
     return accionPendiente;
 }
 
 /**
  * Parsear la respuesta de la API de validación de acción pendiente
  * 
  * @param bodyResponse: Respuesta de la API de validación de acción pendiente
  * @return AccionPendiente: AccionPendiente
  */
 AccionPendiente validarSiHayAccionPendienteParsearRespuesta(const String& bodyResponse)
 {
     // Inicializamos la estructura de datos AccionPendiente
     AccionPendiente accionPendiente;
     accionPendiente.accionId  = 0;
     accionPendiente.orden     = "";
     accionPendiente.hayAccion = false;
   
     // Parseamos el JSON de la respuesta
     DynamicJsonDocument doc(1024);
     DeserializationError error = deserializeJson(doc, bodyResponse);
   
     // Si hay un error al parsear el JSON, se muestra el error y se devuelve la estructura de datos AccionPendiente
     if (error) 
     {
       // Almacenamos el error en la variable local errorString
       String errorString = "ERROR: Error parseando JSON: " + String(error.c_str());
 
       // Mostramos un mensaje de error
       Serial.println(errorString);
     }
     else
     {
         // Obtenemos el ID de la acción pendiente
         if (!doc["accionId"].isNull())
         {
             accionPendiente.accionId = doc["accionId"].as<long>();
         }
 
         // Obtenemos la orden de la acción pendiente
         if (!doc["orden"].isNull())
         {
             accionPendiente.orden = doc["orden"].as<String>();
         }
 
         // Si hay una acción pendiente, se asigna true a la variable hayAccion
         if (accionPendiente.accionId > 0)
         {
             accionPendiente.hayAccion = true;
         }
 
         // Mostramos la estructura de datos AccionPendiente
         Serial.println("INFO: Estructura de datos AccionPendiente:");
         Serial.print("-- Accion ID: ");
         Serial.println(accionPendiente.accionId);
         Serial.print("-- Orden: ");
         Serial.println(accionPendiente.orden);
         Serial.print("-- Hay acción: ");
         Serial.println(accionPendiente.hayAccion);
     }
 
     // Devolvemos la estructura de datos AccionPendiente
     return accionPendiente;
 }
 
 /**
  * Gestiona la acción sobre el proyector en base a la estructura de datos AccionPendiente
  * 
  * @param accionPendiente: Estructura de datos AccionPendiente
  * @return void
  */
 void gestionarAccionProyector(AccionPendiente accionPendiente)
 {
     // Si hay una acción pendiente, se gestiona la acción sobre el proyector y 
     // se avisa al servidor del estado de la acción establecida
     if (accionPendiente.hayAccion)
     {
         // Gestionamos la acción sobre el proyector
         String resultado = gestionarAccionProyectorOrden(accionPendiente.orden);
 
         // Avisa al servidor del estado de la acción
         gestionarAccionEstadoRealizadaAvisoServidor(accionPendiente.accionId, resultado);
     }
 }
 
 /**
  * Gestiona la acción sobre el proyector en base a la orden de la acción pendiente
  * 
  * @param orden: Orden de la acción pendiente
  * @return String: Resultado de la acción pendiente
  */
 String gestionarAccionProyectorOrden(String orden)
 {
    // Enviamos la orden escribiendo sobre el puerto serie
    escribirEnPuertoSerie(orden);

    // Leemos la respuesta del proyector
    String resultado = leerDesdePuertoSerie();

    // Mostramos la respuesta del proyector
    Serial.println("INFO: Respuesta del proyector: " + resultado);

    return resultado;
 }
 
 /**
  * Avisa al servidor de la apertura de la puerta
  * 
  * @param accionId: ID de la acción pendiente
  * @param resultado: Resultado de la acción pendiente
  * @return void
  */
 void gestionarAccionEstadoRealizadaAvisoServidor(const long accionId, const String& resultado)
 {
    // Mostramos un mensaje de información
    Serial.println("INFO: Iniciando envío de la petición para avisar al servidor de la apertura de la puerta");

    // Antes de avisar al servidor de la apertura de la puerta, obtenemos el token JWT
    obtenerTokenJWT();

    // Inicializamos el cliente HTTP
    HTTPClient httpAvisoServidorAccionEstado;
    httpAvisoServidorAccionEstado.setTimeout(TIME_PETICIONES_HTTP);

    // Mostramos los detalles de la petición
    Serial.println("INFO: Detalles de la petición para avisar al servidor de la apertura de la puerta realizada: ");
    Serial.print("-- Dirección del servidor: ");
    Serial.println(urlAvisoServidorAccionEstado);
    Serial.print("-- Accion ID: ");
    Serial.println(accionId);
 
    // Si se puede iniciar la conexión HTTP, se obtiene el token
    if (iniciarConexionHTTPConReintentos(httpAvisoServidorAccionEstado, urlAvisoServidorAccionEstado))
    {
        // Añadimos el header de autorización
        httpAvisoServidorAccionEstado.addHeader("Authorization", "Bearer " + tokenJWT);

        // Creamos un objeto JSON para el cuerpo de la petición
        DynamicJsonDocument doc(1024);
        doc["accionId"] = accionId;
        doc["estado"] = "finalizado_ok";
        doc["resultado"] = resultado ;

        String body;
        serializeJson(doc, body);

        // Mostramos el cuerpo de la petición
        Serial.println("INFO: Cuerpo de la petición para avisar al servidor de la apertura de la puerta realizada: ");
        Serial.println(body);

        // Añadimos el header de contenido
        httpAvisoServidorAccionEstado.addHeader("Content-Type", "application/json");

        // Realizamos la petición POST
        int httpResponseCode = httpAvisoServidorAccionEstado.POST(body);

        // Si la respuesta no está en el rango del 200 al 299, es incorrecta
        if (httpResponseCode < 200 || httpResponseCode >= 300)
        {
            // Almacenamos el error en la variable local errorString
            String errorString = "ERROR: La petición HTTP para avisar al servidor de la apertura de la puerta realizada ha fallado. Código: " + String(httpResponseCode) ;

            // Mostramos un mensaje de error
            Serial.println(errorString);
        }
    }
 
    // Liberamos la memoria del cliente HTTP
    httpAvisoServidorAccionEstado.end();

    // Mostramos un mensaje de información
    Serial.println("INFO: Petición para avisar al servidor de la apertura de la puerta realizada enviada correctamente");
}

/*************************************************/
/** Funciones propias del actuador del proyector */
/*************************************************/

/**
 * Envía un comando al proyector para consultar el estado de la lámpara y espera una respuesta, y almacena el resultado.
 * 
 * @return String: Estado de la lámpara del proyector
 */
String obtenerEstadoLamparaProyector()
{
    // Envía el comando "estado de la lámpara?" al proyector
    escribirEnPuertoSerie("lampStatusInquireCommand"); // TODO: Añadir el comando para obtener el estado de la lámpara

    // Lee la respuesta del proyector
    String lampStatus = leerDesdePuertoSerie();

    // Si no se recibe respuesta dentro del tiempo límite, se muestra un mensaje de error
    if (lampStatus == "")
    {
        // Almacenamos el error en la variable local errorString
        String errorString = "ERROR: No se recibió respuesta del proyector";

        // Mostramos un mensaje de error
        Serial.println(errorString);
    }
    else
    {
        // Mostramos el estado de la lámpara
        Serial.print("INFO: Estado de la lámpara: ");
        Serial.println(lampStatus);
    }

    return lampStatus;
}
 
 /**
  * Escribe una cadena al puerto serie
  * 
  * @param instruction: Instrucción a escribir
  * @return String: Respuesta de la instrucción
  */
String escribirEnPuertoSerie(const String& orden)
{
    // Reemplazamos el salto de línea al final de la instrucción
    String ordenProcesada = reemplazarFinDeLinea(orden);

    // Si el puerto serie no está listo para escribir, se espera
    while (!MySerial.availableForWrite())
    {
        // Mostramos un mensaje de información
        Serial.println("INFO: El puerto serie no está listo para escribir");

        // Esperamos el tiempo indicado
        delay(TIEMPO_ESPERA_RS232);
    }

    // Envía la instrucción recibida en formato cadena..
    MySerial.write(ordenProcesada.c_str());

    return leerDesdePuertoSerie();
}

/**
 * Lee desde el puerto serie la respuesta del proyector
 * 
 * @return String: Respuesta del proyector
 */
String leerDesdePuertoSerie()
{
    while (!MySerial.available())
    {
        // Mostramos un mensaje de información
        Serial.println("INFO: El puerto serie no está listo para leer");

        // Esperamos el tiempo indicado
        delay(TIEMPO_ESPERA_RS232);
    }

    return MySerial.readStringUntil('\r');
}
 
/**
  * Esta función es una solución temporal para corregir cómo se reciben
  * las cadenas desde el backend, específicamente cuando los caracteres de fin
  * de línea se representan como secuencias escapadas ("\\n" o "\\r").
  * 
  * @param input: Cadena de entrada
  * @return String: Cadena de salida
  */
String reemplazarFinDeLinea(const String& input)
{
    String inputProcesada = input;

    // Si la cadena de entrada termina con "\n", se reemplaza por "\n"
    if (inputProcesada.endsWith("\\n"))
    {
        // Mostramos un mensaje de información
        Serial.println("INFO: Cambia \\n por \n");
        inputProcesada = inputProcesada.substring(0, inputProcesada.length() - 2) + '\n';  // Elimina "\\n" y añade '\r'
    }
    // Si la cadena de entrada termina con "\r", se reemplaza por "\r"
    else if (inputProcesada.endsWith("\\r"))
    {
        // Mostramos un mensaje de información
        Serial.println("INFO: Cambia '\\r' por '\r'");
        inputProcesada = inputProcesada.substring(0, inputProcesada.length() - 2) + '\r';  // Elimina "\\n" y añade '\r'
    }
    // Si la cadena de entrada termina con "\n", se reemplaza por "\n"
    else if (inputProcesada.endsWith("\\\n"))
    {
        // Mostramos un mensaje de información
        Serial.println("INFO: Cambia \\\n por \n");
        inputProcesada = inputProcesada.substring(0, inputProcesada.length() - 3) + '\r';  // Elimina "\\n" y añade '\r'
    }
    // Si la cadena de entrada termina con "\r", se reemplaza por "\r"
    else if (inputProcesada.endsWith("\\\r"))
    {
        // Mostramos un mensaje de información
        Serial.println("INFO: Cambia '\\\r' por '\r'");
        inputProcesada = inputProcesada.substring(0, inputProcesada.length() - 3) + '\r';  // Elimina "\\n" y añade '\r'
    }

    // Devolvemos la cadena de salida
    return inputProcesada;
}