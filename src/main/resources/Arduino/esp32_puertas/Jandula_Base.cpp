#include "Jandula_Base.h"
#include "base64.hpp"
#include <ArduinoJson.h>

/*******************************************************/
/****************** Variables globales *****************/
/*******************************************************/

// wifiSSID: sirve para almacenar el SSID de la red WiFi
String wifiSSID;

// wifiPassword: sirve para almacenar la contraseña de la red WiFi
String wifiPassword;

// urlFirebase: sirve para almacenar la URL de Firebase
String urlFirebase;

// clientId: sirve para almacenar el ID del cliente de Firebase
String clientId;

// sistemaArchivosLittleFSInicializado: sirve para indicar si el sistema de archivos LittleFS está inicializado
bool sistemaArchivosLittleFSAccesible = false;

// tarjetaSDAccesible: sirve para indicar si la tarjeta SD es accesible
bool tarjetaSDAccesible = false;

// tokenJWT: sirve para almacenar el token JWT de Firebase
String tokenJWT = "";

// expiracionTokenJWT: sirve para almacenar la fecha de expiración del token JWT
unsigned long expiracionTokenJWT = 0;

// errorGeneralJandulaBase: sirve para almacenar el error general de la librería Jandula Base
String errorGeneralJandulaBase = "";

// macAddress: sirve para almacenar la dirección MAC de la tarjeta WiFi
String macAddress = "";

/***************************************************************************/
/*** Funciones relacionadas con la inicialización de los componentes base **/
/***************************************************************************/

/**
 * Realiza la inicialización de los diferentes componentes de la biblioteca
 * 
 * @return void
 */
void setupJandulaBase()
{
  // Validamos si la conexión a LittleFS es accesible
  validarSiConexionLittleFSEsAccesible();

  // Si no hay ningún error general mientras se validó la conexión a LittleFS
  if (errorGeneralJandulaBase == "")
  {
    // Validamos si la tarjeta SD es accesible
    validarSiSDCardEsAccesible() ;
  }

  // Si no hay ningún error general mientras se validó la tarjeta SD
  if (errorGeneralJandulaBase == "")
  {
    // Si la tarjeta SD es accesible ...
    if (tarjetaSDAccesible)
    {
      // ... procedemos a la comparación y copia de los archivos
      compararYCopiarFicherosSDyLittleFS(RUTA_FICHERO_CONFIGURACION);
    
      // Desmontamos la tarjeta SD después de procesar
      SD.end();
    }
  }
  
  // Si no hay ningún error general mientras se comparó y copió los archivos
  if (errorGeneralJandulaBase == "")
  {
    // Cargamos la configuración desde el archivo de configuración
    parseaFicheroConfiguracionJandulaBase(RUTA_FICHERO_CONFIGURACION);
  }

  // Si no hay ningún error general mientras se cargó la configuración ...
  if (errorGeneralJandulaBase == "")
  {
    // Conectamos a la red WiFi
    conectarWiFi();
  }
      
  // Si no hay ningún error general mientras se conectó a la red WiFi y la conexión a la red WiFi es correcta ...
  if (errorGeneralJandulaBase == "" && WiFi.status() == WL_CONNECTED)
  {
    // ... entonces sincronizamos la hora con el servidor NTP
    sincronizarHoraConServidorNTP();
  }

  // Si no hay ningún error general mientras se sincronizó la hora con el servidor NTP ...
  if (errorGeneralJandulaBase == "")
  {
    // Obtenemos la dirección MAC de la tarjeta WiFi
    macAddress = WiFi.macAddress();
  
    // Mostramos la dirección MAC de la tarjeta WiFi
    Serial.print("MAC guardada: ");
    Serial.println(macAddress);

    // Obtenemos el token de Firebase
    obtenerTokenJWT();
  }
}

/**
 * Valida si la conexión a LittleFS es accesible
 * 
 * @return void
 */
void validarSiConexionLittleFSEsAccesible()
{
  // Mostramos el mensaje de inicio de validación del sistema de archivos LittleFS
  Serial.println("INFO: Comenzando la validación del sistema de archivos LittleFS");

  // Validamos si el sistema de archivos LittleFS es accesible
  sistemaArchivosLittleFSAccesible = LittleFS.begin();

  // Si no es accesible el sistema de archivos LittleFS ...
  if (!sistemaArchivosLittleFSAccesible)
  {
    // Intentamos formatear y montar el sistema de archivos LittleFS
    sistemaArchivosLittleFSAccesible = LittleFS.begin(true);

    // Si no es accesible el sistema de archivos LittleFS ...
    if (!sistemaArchivosLittleFSAccesible)
    {
      // Almacenamos el error en la variable global errorGeneralJandulaBase
      errorGeneralJandulaBase = "ERROR: No se ha podido formatear y montar el sistema de archivos LittleFS";

      // Mostramos un mensaje de error
      Serial.println(errorGeneralJandulaBase);
    }
    else
    {
      // Mostramos un mensaje de información
      Serial.println("INFO: El sistema de archivos LittleFS se ha formateado y montado correctamente");
    }
  }
}
 
 /**
  * Valida si la tarjeta SD es accesible
  * 
  * @return void
  */
 void validarSiSDCardEsAccesible()
 {
   // Mostramos un mensaje de inicio de validación de la tarjeta SD
   Serial.println("INFO: Comenzando la validación de la tarjeta SD");
   
   // Inicializamos la SPI que es la interfaz de comunicación con la tarjeta SD para validar si es accesible
   // sdCardClock: pin para el reloj de la tarjeta SD
   // sdCardMISO: pin para la entrada de datos de la tarjeta SD
   // sdCardMOSI: pin para la salida de datos de la tarjeta SD
   // sdCardChipSelect: pin para el chip select de la tarjeta SD
   SPI.begin(sdCardClock, sdCardMISO, sdCardMOSI, sdCardChipSelect);
 
   // Validamos si la tarjeta SD es accesible
   tarjetaSDAccesible = SD.begin(sdCardChipSelect);
 
   // Si la tarjeta SD no es accesible ...
   if (!tarjetaSDAccesible)
   {
     // Mostramos un mensaje de advertencia
     Serial.println("WARNING: No se ha detectado ninguna tarjeta SD");
   }
   else
   {
     // Mostramos un mensaje de información
     Serial.println("INFO: La tarjeta SD es accesible");
 
     // Validamos si la tarjeta SD es accesible abriendo el directorio raíz
     File testFile = SD.open("/");
 
     // Validamos si la tarjeta SD es accesible
     tarjetaSDAccesible = testFile != NULL;
 
     // Si el fichero de prueba no es accesible ...
     if (!tarjetaSDAccesible)
     {
       // Almacenamos el error en la variable global errorGeneralJandulaBase
       errorGeneralJandulaBase = "ERROR: El directorio raíz de la tarjeta SD no es accesible";

       // Mostramos un mensaje de error
       Serial.println(errorGeneralJandulaBase);
     
       // Desmontamos la tarjeta SD
       SD.end();
     }
     else
     {
       // Mostramos un mensaje de información
       Serial.println("INFO: La tarjeta SD es accesible");
 
       // Cerramos el fichero de prueba
       testFile.close();
     }
   }
 }
 
/**
 * Compara y copia un fichero del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @param rutaFichero: ruta del fichero a copiar
 * @return void
 */
void compararYCopiarFicherosSDyLittleFS(const String& rutaFichero)
{
  // Validamos si el fichero existe en el sistema de archivos LittleFS
  bool ficheroLocalLitleFSEncontrado = validarExistenciaFicheroLittleFS(rutaFichero);
  
  // Validamos si el fichero existe en la tarjeta SD
  bool ficheroSDEncontrado = validarExistenciaFicheroSD(rutaFichero);

  // Si el fichero local existe y el fichero en la tarjeta SD existe ...
  if (ficheroLocalLitleFSEncontrado && ficheroSDEncontrado)
  {
    // Abrimos ambos ficheros de la tarjeta SD para comparar los timestamps de modificación
    File ficheroSD = SD.open(rutaFichero, "r");

    // Si no se puede abrir el archivo de la tarjeta SD ...
    if (!ficheroSD)
    {
      // Almacenamos el error en la variable global errorGeneralJandulaBase
      errorGeneralJandulaBase = "ERROR: No se pudo abrir el fichero de la tarjeta SD cuando se ha ido a copiar a LittleFS";

      // Mostramos un mensaje de error
      Serial.println(errorGeneralJandulaBase);
    }
    else
    {
      // Obtenemos los timestamps de modificación para ambos archivos
      unsigned long estampaTiempoLocalFicheroLittleFS = obtenerTimestampDesdeFichero(rutaFichero);
      unsigned long estampaTiempoFicheroSD            = ficheroSD.getLastWrite();

      // Cerramos el fichero de la tarjeta SD
      ficheroSD.close();

      // Si los timestamps de modificación son diferentes, comparamos y copiamos el fichero de la tarjeta SD al sistema de archivos LittleFS
      compararTimeStampsYcopiarSiFicherosSDyLittleFSSonDiferentes(rutaFichero, estampaTiempoLocalFicheroLittleFS, estampaTiempoFicheroSD);
    }
  }
  else if (ficheroSDEncontrado)
  {
    // Si el fichero en la tarjeta SD existe, ...
    // ... copiamos el fichero de la tarjeta SD al sistema de archivos LittleFS
    copiarFicheroDeSDaLittleFS(rutaFichero);
    
    // Mostramos el timestamp de modificación del fichero en la tarjeta SD
    Serial.println("INFO: Nueva estampa de tiempo del fichero en la tarjeta SD");
  }
  else
  {
    // Si el fichero local no existe y el fichero en la tarjeta SD no existe, almacenamos el error en la variable global errorGeneralJandulaBase y mostramos un mensaje de error
    errorGeneralJandulaBase = "ERROR: No se encontró el fichero en el sistema de archivos LittleFS ni en la tarjeta SD";

    // Mostramos un mensaje de error
    Serial.println(errorGeneralJandulaBase);
  }
}

/**
 * Valida si el fichero existe en el sistema de archivos LittleFS
 * 
 * @param rutaFichero: ruta del fichero
 * @return true si el fichero existe en el sistema de archivos LittleFS, false en caso contrario
 */
bool validarExistenciaFicheroLittleFS(const String& rutaFichero)
{
  // Validamos si el fichero existe en el sistema de archivos LittleFS
  bool ficheroLocalLitleFSEncontrado = LittleFS.exists(rutaFichero);

  // Mostramos si el fichero existe en el sistema de archivos LittleFS
  Serial.print("INFO: Fichero local en LittleFS: ");
  Serial.println(String(ficheroLocalLitleFSEncontrado));

  // Devolvemos el resultado de la validación
  return ficheroLocalLitleFSEncontrado;
}

/**
 * Valida si el archivo existe en la tarjeta SD
 * 
 * @param filePath: ruta del archivo
 * @return true si el archivo existe en la tarjeta SD, false en caso contrario
 */
bool validarExistenciaFicheroSD(const String& filePath)
{
  // Validamos si el archivo existe en la tarjeta SD
  bool ficheroSDEncontrado = SD.exists(filePath);

  // Mostramos si el archivo existe en la tarjeta SD
  Serial.print("INFO: Fichero en SD: ");
  Serial.println(String(ficheroSDEncontrado));

  // Devolvemos el resultado de la validación
  return ficheroSDEncontrado;
}

/**
 * Obtiene el timestamp de modificación de un archivo en el sistema de archivos LittleFS
 * 
 * @param rutaFichero: ruta del archivo
 * @return timestamp: timestamp del archivo
 */
 unsigned long obtenerTimestampDesdeFichero(const String& rutaFichero)
 {
   // Valor por defecto para el timestamp (0 indica que no se encontró)
   unsigned long timestamp = 0;
 
   // Abrimos el archivo en modo lectura
   File ficheroLittleFS = LittleFS.open(rutaFichero, "r");
 
   // Si no se puede abrir el archivo ...
   if (!ficheroLittleFS)
   {
     // Almacenamos el error en la variable global errorGeneralJandulaBase
     errorGeneralJandulaBase = "ERROR: No se pudo abrir el fichero en el sistema de archivos LittleFS: " + rutaFichero;

     // Mostramos un mensaje de error
     Serial.println(errorGeneralJandulaBase);
   }
   // Si el archivo tiene contenido ...
   else if (ficheroLittleFS.available())
   {
     // ... leemos el timestamp de modificación del archivo
     timestamp = ficheroLittleFS.parseInt();
 
     // Cerramos el archivo
     ficheroLittleFS.close();
   }
   else
   {
     // ... mostramos un mensaje de advertencia
     Serial.print("WARNING: No se encontró timestamp en el archivo: ");
     Serial.println(rutaFichero);
   }
 
   // Devolvemos el timestamp
   return timestamp;
}

/**
 * Compara y copia un fichero del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @param rutaFichero: ruta del fichero
 * @param estampaTiempoLocalFicheroLittleFS: timestamp de modificación del fichero en el sistema de archivos LittleFS
 * @param estampaTiempoFicheroSD: timestamp de modificación del fichero en la tarjeta SD
 * @return void
 */
void compararTimeStampsYcopiarSiFicherosSDyLittleFSSonDiferentes(const String& rutaFichero, unsigned long estampaTiempoLocalFicheroLittleFS, unsigned long estampaTiempoFicheroSD)
{
  // Mostramos el timestamp de modificación del fichero en el sistema de archivos LittleFS
  Serial.print("INFO: Última modificación del fichero local: ");
  Serial.println(obtenerFechaYHoraDesdeTimestamp(estampaTiempoLocalFicheroLittleFS));

  // Mostramos el timestamp de modificación del fichero en la tarjeta SD
  Serial.print("INFO: Última modificación del fichero en la tarjeta SD: ");
  Serial.println(obtenerFechaYHoraDesdeTimestamp(estampaTiempoFicheroSD));

  // Si la tarjeta SD tiene un timestamp de modificación más reciente que 
  // el fichero en el sistema de archivos LittleFS, entonces ...
  if (estampaTiempoFicheroSD > estampaTiempoLocalFicheroLittleFS)
  {
    // ... copiamos el archivo de la tarjeta SD al sistema de archivos LittleFS
    copiarFicheroDeSDaLittleFS(rutaFichero);

    // Mostramos un mensaje de resultado
    Serial.print("INFO: Copiado el archivo de la tarjeta SD al sistema de archivos LittleFS");
  }
  else
  {
    // Si el archivo en el sistema de archivos LittleFS tiene u
    // un timestamp de modificación más reciente que el archivo en la tarjeta SD ó ...
    // ... si tiene ambos el mismo timestamp de modificación, entonces no hay que hacer nada
    Serial.print("INFO: Los timestamps de modificación son iguales");
  }
}

/**
 * Copia un archivo de la tarjeta SD al sistema de archivos LittleFS
 * 
 * @param rutaFichero: ruta del archivo
 * @return void
 */
void copiarFicheroDeSDaLittleFS(const String& rutaFichero)
{
  // Abrimos el archivo de la tarjeta SD para lectura
  File ficheroSD = SD.open(rutaFichero, "r");

  // Si no se puede abrir el archivo de la tarjeta SD ...
  if (!ficheroSD)
  {
    // Almacenamos el error en la variable global errorGeneralJandulaBase
    errorGeneralJandulaBase = "ERROR: No se pudo abrir el fichero en la tarjeta SD: " + rutaFichero;

    // Mostramos un mensaje de error
    Serial.println(errorGeneralJandulaBase);
  }
  else
  {
    // Abrimos el archivo en el sistema de archivos LittleFS para escritura
    File ficheroLittleFS = LittleFS.open(rutaFichero, "w");

    // Si no se puede abrir el fichero en el sistema de archivos LittleFS ...
    if (!ficheroLittleFS)
    {
      // ... cerramos el fichero de la tarjeta SD
      ficheroSD.close();

      // Almacenamos el error en la variable global errorGeneralJandulaBase
      errorGeneralJandulaBase = "ERROR: No se pudo abrir el fichero en el sistema de archivos LittleFS: " + rutaFichero;

      // Mostramos un mensaje de error
      Serial.println(errorGeneralJandulaBase);
    }
    else
    {
      // Copiamos el fichero de la tarjeta SD al sistema de archivos LittleFS
      while (ficheroSD.available())
      {
        // Copiamos el contenido del fichero de la tarjeta SD al sistema de archivos LittleFS
        ficheroLittleFS.write(ficheroSD.read());
      }

      // Obtenemos la estampa de tiempo del fichero en la tarjeta SD
      unsigned long estampaTiempoFicheroSD = ficheroSD.getLastWrite();

      // Sobrescribimos la estampa de tiempo en el fichero en el sistema de archivos LittleFS
      sobreescribirEstampaTiempoEnFichero(estampaTiempoFicheroSD, rutaFichero);

      // Cerramos el fichero de la tarjeta SD
      ficheroSD.close();

      // Cerramos el fichero en el sistema de archivos LittleFS
      ficheroLittleFS.close();
    }
  }
}

/**
 * Escribe un timestamp en un archivo en el sistema de archivos LittleFS
 * 
 * @param timestamp: timestamp a escribir
 * @param rutaFichero: ruta del archivo
 * @return void
 */
void sobreescribirEstampaTiempoEnFichero(unsigned long timestamp, const String& rutaFichero)
{
  // Abrimos el archivo en modo escritura, que sobrescribirá cualquier contenido existente
  File ficheroLittleFS = LittleFS.open(rutaFichero, "w");

  // Si no se puede abrir el archivo ...
  if (!ficheroLittleFS)
  {
    // Almacenamos el error en la variable global errorGeneralJandulaBase
    errorGeneralJandulaBase = "ERROR: No se pudo abrir el fichero en el sistema de archivos LittleFS para sobreescribir la estampa de tiempo: " + rutaFichero;

    // Mostramos un mensaje de error
    Serial.println(errorGeneralJandulaBase);
  }
  else
  {
    // Escribimos el timestamp en el archivo
    ficheroLittleFS.println(timestamp);

    // Cerramos el archivo
    ficheroLittleFS.close();

    // Mostramos un mensaje de resultado
    Serial.print("INFO: Estampa de tiempo escrita correctamente en el fichero en el sistema de archivos LittleFS: ");
    Serial.println(rutaFichero);
  }
}

/**
 * Convierte un timestamp UNIX a una cadena de fecha y hora formateada
 * 
 * @param timestamp: timestamp UNIX
 * @return fechaYHoraFormateada: cadena de fecha y hora formateada
 */
String obtenerFechaYHoraDesdeTimestamp(unsigned long timestamp)
{
  // Convertimos el timestamp UNIX a un tiempo_t
  time_t timeStamp = (time_t)timestamp;

  // Convertimos el tiempo_t a una estructura tm
  struct tm* timeinfo = localtime(&timeStamp);

  // Formateamos la fecha y hora
  char fechaYHoraFormateada[30];
  strftime(fechaYHoraFormateada, sizeof(fechaYHoraFormateada), "%Y-%m-%d %H:%M:%S", timeinfo);

  // Devolvemos la fecha y hora formateada
  return fechaYHoraFormateada;
}

/*******************************************************/
/********* Parseo de fichero de configuración **********/
/*******************************************************/
 
/**
 * Carga la configuración desde el archivo de configuración
 * 
 * @param configFilePath: ruta del archivo de configuración
 * @return void
 */
void parseaFicheroConfiguracionJandulaBase(String rutaFicheroConfiguracion) 
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
      // Leemos la línea del fichero de configuración
      String linea = ficheroConfiguracion.readStringUntil('\n');

      // Eliminamos los espacios en blanco al principio y al final de la línea
      linea.trim();
 
      if (linea.startsWith(PROPIEDAD_WIFI_SSID))
      {
        wifiSSID = linea.substring(PROPIEDAD_WIFI_SSID_LENGTH + 1);
        wifiSSID.trim();
      }
      else if (linea.startsWith(PROPIEDAD_WIFI_PASSWORD))
      {
        wifiPassword = linea.substring(PROPIEDAD_WIFI_PASSWORD_LENGTH + 1);
        wifiPassword.trim();
      }
      else if (linea.startsWith(PROPIEDAD_URL_FIREBASE))
      {
        urlFirebase = linea.substring(PROPIEDAD_URL_FIREBASE_LENGTH + 1);
        urlFirebase.trim();
      }
      else if (linea.startsWith(PROPIEDAD_CLIENT_ID))
      {
        clientId = linea.substring(PROPIEDAD_CLIENT_ID_LENGTH + 1);
        clientId.trim();
      }
    }

    // Cerramos el fichero de configuración
    ficheroConfiguracion.close();
  }

  // Si no hay un error general, validamos si todos los campos están rellenos
  if (errorGeneralJandulaBase == "")
  {
    parseaFicheroConfiguracionJandulaBaseValidarCamposRellenos();
  }
}

/**
 * Valida si todos los campos del fichero de configuración están rellenos
 * 
 * @return void
 */
void parseaFicheroConfiguracionJandulaBaseValidarCamposRellenos()
{
  // Validamos si el SSID está relleno
  if (wifiSSID.length() == 0)
  {
    // Almacenamos el error en la variable global errorGeneralJandulaBase
    errorGeneralJandulaBase = "ERROR: SSID vacío (no se cargó la configuración de la red WiFi)";
  }
  else if (wifiPassword.length() == 0)
  {
    // Almacenamos el error en la variable global errorGeneralJandulaBase
    errorGeneralJandulaBase = "ERROR: PASSWORD vacío (no se cargó la configuración de la red WiFi)";
  }
  else if (urlFirebase.length() == 0)
  {
    // Almacenamos el error en la variable global errorGeneralJandulaBase
    errorGeneralJandulaBase = "ERROR: URL de Firebase vacía (no se cargó la configuración de la red WiFi)";
  }
  else if (clientId.length() == 0)
  {
    // Almacenamos el error en la variable global errorGeneralJandulaBase
    errorGeneralJandulaBase = "ERROR: ID del cliente vacío (no se cargó la configuración de la red WiFi)";
  }

  if (errorGeneralJandulaBase != "")
  {
    // Mostramos un mensaje de error
    Serial.println(errorGeneralJandulaBase);
  }
  else
  {
    // Mostramos un mensaje de información
    Serial.println("INFO: Todos los campos del fichero de configuración están rellenos");
  }
}

/*********************************************************************/
/******* Funciones relacionadas con la conexión a la red WiFi ********/
/*********************************************************************/

/**
  * Conecta el ESP32 a la red WiFi.
  * 
  * @return void
  */
void conectarWiFi()
{
  // Si el SSID o la contraseña no están configurados, se muestra un mensaje de error
  if (wifiSSID.length() == 0 || wifiPassword.length() == 0)
  {
    // Mostramos un mensaje de error
    errorGeneralJandulaBase = "ERROR: SSID/PASSWORD vacíos (no se cargó la configuración de la red WiFi)";

    // Mostramos un mensaje de error
    Serial.println(errorGeneralJandulaBase);
  }
  else
  {
    // Mostramos un mensaje de inicio de conexión
    Serial.print("INFO: Iniciando conexión a la red WiFi SSID: ");
    Serial.print(wifiSSID.c_str());
    Serial.print(" Contraseña: ");
    Serial.print(wifiPassword.c_str());

    // Conectamos a la red WiFi
    WiFi.mode(WIFI_STA);
    WiFi.begin(wifiSSID.c_str(), wifiPassword.c_str());

    // Inicializamos el contador de intentos
    int intentos = 0;

    // Intentamos conectar a la red WiFi
    while (WiFi.status() != WL_CONNECTED && intentos < 30)
    {
      // Mostramos un punto de espera
      Serial.print(".");

      // Incrementamos el contador de intentos
      intentos++;

      // Esperamos el tiempo de delay antes de reintentar
      delay(INTENTOS_DELAY);
    }

    // Si no se conecta a la red WiFi, se muestra un mensaje de error
    if (WiFi.status() != WL_CONNECTED)
    {
      // Mostramos un mensaje de error
      errorGeneralJandulaBase = "ERROR: No se pudo conectar a la red WiFi";

      // Mostramos un mensaje de error
      Serial.println(errorGeneralJandulaBase);
    }
    else
    {
      // Mostramos un mensaje de conexión correcta
      Serial.println("INFO: Conectado a la red WiFi");

      // Mostramos la IP de la red WiFi
      Serial.print("IP: ");
      Serial.println(WiFi.localIP());
    }
  }
}

/*********************************************************************/
/***** Funciones relacionadas con la sincronización de la hora *******/
/*********************************************************************/

/**
 * Sincroniza la hora con el servidor NTP.
 * 
 * @return void
 * 
 * @note Si la sincronización se realiza correctamente, se muestra un mensaje de sincronización correcta.
 * @note Si la sincronización no se realiza correctamente, se muestra un mensaje de error
 */
void sincronizarHoraConServidorNTP()
{
  bool sincronizacionRealizada = false;

  // Mostramos un mensaje de inicio de sincronización
  Serial.println("INFO: Iniciando sincronización de la hora con el servidor NTP");

  // Iniciamos la sincronización de la hora con el servidor NTP
  configTzTime(TZ_INFO, "pool.ntp.org", "time.nist.gov", "time.google.com");

  // Inicializamos la variable de sincronización realizada
  struct tm timeinfo;

  // Inicializamos el contador de intentos
  int intentos = 0;

  // Intentamos sincronizar la hora con el servidor NTP
  while (!sincronizacionRealizada && intentos < MAXIMO_INTENTOS_HTTP)
  {
    // Si se obtiene la fecha y hora correctamente, se sale del bucle
    sincronizacionRealizada = getLocalTime(&timeinfo);

    // Si no se obtiene la fecha y hora correctamente, se reintenta la sincronización
    if (!sincronizacionRealizada)
    {
      // Mostramos un mensaje de error y se incrementa el contador de intentos
      Serial.print(".");

      // Incrementamos el contador de intentos
      intentos++;

      // Esperamos el tiempo de delay antes de reintentar
      delay(INTENTOS_DELAY);
    }
    else
    {
      // Inicializamos la variable de fecha y hora formateada
      char formattedDate[30];

      // Formateamos la fecha y hora
      // %Y: Año (4 dígitos)
      // %m: Mes (2 dígitos)
      // %d: Día (2 dígitos)
      // %H: Hora (2 dígitos)
      // %M: Minuto (2 dígitos)
      // %S: Segundo (2 dígitos)
      strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", &timeinfo);

      // Mostramos el mensaje de sincronización correcta
      Serial.println("INFO: Fecha y hora sincronizada correctamente: " + String(formattedDate));
    }
  }

  // Si finalmente no se ha podido obtener la fecha y hora del servidor NTP,
  // se muestra un mensaje de error
  if (!sincronizacionRealizada)
  {
    // Mostramos un mensaje de error
    errorGeneralJandulaBase = "ERROR: No se ha podido obtener la fecha y hora del servidor NTP después de varios intentos.";

    // Mostramos un mensaje de error
    Serial.println(errorGeneralJandulaBase);
  }
}

/*********************************************************************/
/******** Funciones relacionadas con el Token Firebase JWT ***********/
/*********************************************************************/

/**
 * Obtiene el token JWT válido de Firebase.
 * 
 * @param urlFirebase URL de Firebase.
 * @param xClientId ID del cliente.
 * 
 * @return void
 * 
 * @note Si el token no existe o ha expirado, se solicita un nuevo token.
 * @note Si el token se obtiene correctamente, se actualiza el tiempo de expiración.
 * @note Si el token no se obtiene correctamente, se retorna un token vacío.
 * @note Si el token se obtiene correctamente, se retorna el token.
 * @note Si el token no se obtiene correctamente, se retorna un token vacío.
*/
void obtenerTokenJWT()
{
  if (tokenJWT == "" || tokenJWTExpirado())
  {
    // Mostramos un mensaje de advertencia
    Serial.println("WARNING: JWT inexistente o expirado. Solicitando nuevo token...");

    // Obtenemos el token de Firebase
    obtenerTokenJWTInternal();

    // Si el token se obtiene correctamente, se actualiza el tiempo de expiración
    if (tokenJWT != "")
    {
      // Obtenemos la expiración del token
      obtenerExpiracionJWT();
    }
  }
}

/**
 * Verifica si el token JWT ha expirado.
 * 
 * @return true si el token ha expirado, false en caso contrario.
 * 
 * @note Si el token no tiene tiempo de expiración, se retorna true.
 * @note Si el token tiene tiempo de expiración y ha expirado, se retorna true.
 * @note Si el token tiene tiempo de expiración y no ha expirado, se retorna false.
*/
bool tokenJWTExpirado()
{
  bool expirado = false;

  time_t now;
  time(&now);

  Serial.print("NOW: ");
  Serial.print(now);
  Serial.print(" expiración: ");
  Serial.println(expiracionTokenJWT);

  // Se valida si el token ha expirado
  expirado = (expiracionTokenJWT == 0) || (now >= expiracionTokenJWT);

  // Si el token ha expirado, se muestra un mensaje de advertencia
  if (expirado)
  {
    Serial.println("WARNING: El token JWT ha expirado");
  }

  // Devolvemos true si el token ha expirado, false en caso contrario
  return expirado;
}

/**
 * Obtiene el token JWT de Firebase.
 * 
 * @return void
 * 
 * @note Si el token no se obtiene correctamente, se muestra un mensaje de error
*/
void obtenerTokenJWTInternal()
{
  // Inicializamos la variable de token JWT
  tokenJWT = "";

  // Mostramos un mensaje de inicio de obtención del token de Firebase
  Serial.println("INFO: Iniciando obtención del token de Firebase...");

  // Iniciamos la conexión HTTP
  HTTPClient httpJwt;
  httpJwt.setTimeout(TIME_PETICIONES_HTTP);

  // Inicializamos el dato de respuesta HTTP
  String httpResponseData = "";

  // Mostramos los detalles de la petición
  Serial.println("INFO: Detalles de la petición para obtener el token JWT: ");
  Serial.print("-- Dirección del servidor: ");
  Serial.println(urlFirebase);
  Serial.print("-- X-CLIENT-ID: ");
  Serial.println(clientId);

  // Si se puede iniciar la conexión HTTP, se obtiene el token
  if (iniciarConexionHTTPConReintentos(httpJwt, urlFirebase))
  {
    // Añadimos el encabezado de cliente
    httpJwt.addHeader("X-CLIENT-ID", clientId);

    // Realizamos la petición POST
    int httpResponseCode = httpJwt.POST("");

    // Si la respuesta no está en el rango del 200 al 299, es incorrecta
    if (httpResponseCode < 200 || httpResponseCode >= 300)
    {
      // Almacenamos el error en la variable global errorGeneralJandulaBase
      errorGeneralJandulaBase = "ERROR: La petición HTTP para obtener el token JWT ha fallado. Código: " + String(httpResponseCode) ;

      // Mostramos un mensaje de error
      Serial.println(errorGeneralJandulaBase);
    }
    else
    {
      // Obtenemos el token de la respuesta
      httpResponseData = httpJwt.getString();

      // Si el token es vacío, se muestra un mensaje de advertencia
      if (httpResponseData.length() == 0)
      {
        // Almacenamos el error en la variable global errorGeneralJandulaBase
        errorGeneralJandulaBase = "ERROR: Se ha recibido una respuesta vacía del servidor para obtener el token JWT";

        // Mostramos un mensaje de error
        Serial.println(errorGeneralJandulaBase);
      }
      else
      {
        // Asignamos el token a la variable
        tokenJWT = httpResponseData;

        // Mostramos el token JWT obtenido
        Serial.println("INFO: El token JWT obtenido es:");
        Serial.println(String(tokenJWT));
      }
    }
  }

  // Cerramos la conexión HTTP
  httpJwt.end();
}

/**
 * Inicia una conexión HTTP con reintentos.
 * 
 * @param http Cliente HTTP.
 * @param url URL de la petición.
 * @return true si la conexión se establece correctamente, false en caso contrario.
 */
bool iniciarConexionHTTPConReintentos(HTTPClient& http, const String& url)
{
  // Inicializamos la variable de conexión establecida
  bool conexionEstablecida = false;

  // Inicializamos el contador de intentos
  int intentos = 0;

  // Intentamos establecer la conexión HTTP con reintentos
  while (!conexionEstablecida && intentos < MAXIMO_INTENTOS_HTTP)
  {
    // Intentamos establecer la conexión HTTP
    conexionEstablecida = http.begin(url);

    // Si la conexión se establece correctamente, se sale del bucle
    if (!conexionEstablecida)
    {
      // Si la conexión no se establece correctamente, se muestra un punto de espera que indica un intento fallido
      Serial.print(".");
      
      // Incrementamos el contador de intentos
      intentos++;

      // Esperamos el tiempo de delay antes de intentar nuevamente
      delay(INTENTOS_DELAY);
    }
  }

  if (!conexionEstablecida)
  {
    // Mostramos un mensaje de error
    errorGeneralJandulaBase = "ERROR: No se pudo establecer conexión HTTP para obtener el token JWT después de varios intentos";

    // Mostramos un mensaje de error
    Serial.println(errorGeneralJandulaBase);
  }

  // Devolvemos true si la conexión se establece correctamente
  return conexionEstablecida;
}

/**
 * Obtiene la expiración del token JWT.
 * 
 * @return void
 * 
 * @note Si el token no es válido, se retorna 0.
 * @note Si el token es válido, se retorna la expiración del token.
*/
void obtenerExpiracionJWT()
{
  // Inicializamos la variable de expiración del token
  expiracionTokenJWT = 0;

  // Mostramos un mensaje de inicio de obtención de la expiración del token
  Serial.println("INFO: Iniciando obtención de la expiración del token...");

  // Obtenemos el payload del token por lo que tenemos que obtener el primer y segundo punto
  int firstDot = tokenJWT.indexOf('.');
  int secondDot = tokenJWT.indexOf('.', firstDot + 1);

  // Si el token no es válido, se devuelve 0
  if (firstDot < 0 || secondDot < 0)
  {
    // Almacenamos el error en la variable global errorGeneralJandulaBase
    errorGeneralJandulaBase = "ERROR: En la obtención de la expiración del token JWT, el token no es válido";

    // Mostramos un mensaje de error
    Serial.println(errorGeneralJandulaBase);
  }
  else
  {
    // Obtenemos el payload del token
    String payload = tokenJWT.substring(firstDot + 1, secondDot);

    // Base64URL nos obliga a reemplazar los caracteres '-' y '_' por '+' y '/'
    payload.replace('-', '+');
    payload.replace('_', '/');
    
    // Añadimos padding
    while (payload.length() % 4 != 0)
    {
      payload += '=';
    }
    
    // Obtenemos el tamaño del payload decodificado
    unsigned int decodedLen = decode_base64_length((const unsigned char*)payload.c_str());
    
    // Inicializamos el array de bytes del payload decodificado
    unsigned char decoded[decodedLen + 1];
    
    // Decodificamos el payload
    decode_base64((const unsigned char*)payload.c_str(), decoded);
    
    // Añadimos el carácter nulo al final del array de bytes del payload decodificado
    decoded[decodedLen] = '\0';
    
    // Obtenemos el payload decodificado
    String decodedPayload = String((char*)decoded);

    // Deserializamos el payload
    DynamicJsonDocument doc(512);
    deserializeJson(doc, decodedPayload);

    // Obtenemos la expiración del token JWT
    expiracionTokenJWT = doc["exp"];

    // Mostramos la expiración del token JWT
    Serial.println("INFO: El token JWT expira en:");
    Serial.println(obtenerFechaYHoraDesdeTimestamp(expiracionTokenJWT));
  }
}