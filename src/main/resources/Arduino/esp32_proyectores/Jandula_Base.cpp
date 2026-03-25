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

// sdYFicheroConfiguracionAccesible: sirve para indicar si la tarjeta SD es accesible junto con el acceso al fichero de configuración
bool sdYFicheroConfiguracionAccesible = false;

// tokenJWT: sirve para almacenar el token JWT de Firebase
String tokenJWT = "";

// expiracionTokenJWT: sirve para almacenar la fecha de expiración del token JWT
unsigned long expiracionTokenJWT = 0;

// errorGeneral: sirve para almacenar el error general
String errorGeneral = "";

// macAddress: sirve para almacenar la dirección MAC de la tarjeta WiFi
String macAddress = "";

/*****************************************************/
/******* Funciones relacionadas con el logging *******/
/*****************************************************/

/**
 * Registra una línea de log en Serial y en SD con rollover
 *
 * @param nivel: nivel del log (INFO, WARNING, ERROR)
 * @param mensaje: mensaje del log
 * @return void
 */
void registrarLog(const String& nivel, const String& mensaje)
{
  // Si el nivel es FATAL ...
  if (nivel == "FATAL")
  {
    // ... entonces almacenamos el mensaje en la variable global errorGeneral
    errorGeneral = mensaje ;
  }

  // Creamos la línea de log
  String lineaLog = obtenerMarcaTemporalLog() + " [" + nivel + "] " + mensaje;
  
  // Mostramos la línea de log en Serial
  Serial.println(lineaLog);
  
  // Escribimos la línea de log en la tarjeta SD
  escribirLogEnSD(lineaLog);
}

/**
 * Obtiene la marca temporal del log
 * 
 * @return String: marca temporal del log
 */
String obtenerMarcaTemporalLog()
{
  // Obtenemos la marca temporal del log
  time_t ahora = time(nullptr);
 
  // Si la marca temporal es menor que 100000, devolvemos "1970-01-01 00:00:00"
  if (ahora < 100000)
  {
    return "1970-01-01 00:00:00";
  }
 
  // Obtenemos la hora local
  struct tm tiempoLocal;
  localtime_r(&ahora, &tiempoLocal);

  // Creamos el buffer para la marca temporal
  char buffer[24];

  // Formateamos la marca temporal
  strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", &tiempoLocal);

  // Devolvemos la marca temporal
  return String(buffer);
}

/**
 * Escribe una línea de log en la tarjeta SD
 * 
 * @param lineaLog: línea de log a escribir
 * @return void
 */
void escribirLogEnSD(const String& lineaLog)
 {
  // Validamos en caliente por si la SD se ha retirado tras el arranque
  validarSiSDCardYFicheroConfiguracionAccesible();

  if (sdYFicheroConfiguracionAccesible)
  {
    // Aseguramos el directorio de logs
    asegurarDirectorioLogs();

    // Aplicamos el rollover de logs si procede
    aplicarRolloverLogsSiProcede();
  
    // Abrimos el fichero de log actual en modo append
    File ficheroLog = SD.open(RUTA_FICHERO_LOG_ACTUAL, FILE_APPEND);

    // Si el fichero de log es accesible ...
    if (ficheroLog)
    {
      // ... entonces escribimos la línea de log en el fichero de log
      ficheroLog.println(lineaLog);

      // ... y cerramos el fichero de log
      ficheroLog.close();
    }
  }
}

/**
 * Asegura el directorio de logs
 * 
 * @return void
 */
void asegurarDirectorioLogs()
{
  // Validamos si el directorio de logs existe
  if (!SD.exists(RUTA_DIRECTORIO_LOGS))
  {
    // Creamos el directorio de logs
    SD.mkdir(RUTA_DIRECTORIO_LOGS);

    // Crear mensaje de información
    registrarLog("INFO", "Carpeta de logs creada");
  }
}

/**
 * Aplica el rollover de logs si procede
 * 
 * @return void
 */
void aplicarRolloverLogsSiProcede()
{
  // Abrimos el fichero de log actual en modo lectura
  File ficheroActual = SD.open(RUTA_FICHERO_LOG_ACTUAL, FILE_READ);

  // Si el fichero de log actual es accesible ...
  if (ficheroActual)
  {
    // ... obtenemos su tamaño
    size_t tamanioActual = ficheroActual.size();

    // ... y cerramos el fichero
    ficheroActual.close();

    // Si el tamaño del fichero de log actual es mayor o igual que el tamaño máximo permitido,
    // procedemos a aplicar el rollover ...
    if (tamanioActual >= TAMANIO_MAXIMO_FICHERO_LOG)
    {
      // ... creamos la ruta del fichero de log más antiguo
      String rutaMasAntigua = String(PREFIJO_FICHEROS_LOG) + String(MAXIMO_FICHEROS_ROLLOVER) + SUFIJO_FICHEROS_LOG;

      // Validamos si existe un fichero de log más antiguo
      if (SD.exists(rutaMasAntigua))
      {
        // Si existe uno más antiguo, entonces lo eliminamos
        SD.remove(rutaMasAntigua);
      }

      // Recorremos los ficheros de log históricos
      for (int indice = MAXIMO_FICHEROS_ROLLOVER - 1; indice >= 1; --indice)
      {
        // ... creamos la ruta del fichero de log origen  
        String rutaOrigen = String(PREFIJO_FICHEROS_LOG) + String(indice) + SUFIJO_FICHEROS_LOG;

        // ... creamos la ruta del fichero de log destino
        String rutaDestino = String(PREFIJO_FICHEROS_LOG) + String(indice + 1) + SUFIJO_FICHEROS_LOG;

        // ... validamos si existe un fichero de log origen
        if (SD.exists(rutaOrigen))
        {
          // Si existe, entonces lo renombramos al fichero de log destino
          SD.rename(rutaOrigen, rutaDestino);
        }
      }
    }

    // Creamos la ruta del fichero de log más antiguo
    String primerHistorico = String(PREFIJO_FICHEROS_LOG) + "1" + SUFIJO_FICHEROS_LOG;

    // Si existe el fichero de log actual
    if (SD.exists(RUTA_FICHERO_LOG_ACTUAL))
    {
      // ... entonces lo renombramos al fichero de log más antiguo
      SD.rename(RUTA_FICHERO_LOG_ACTUAL, primerHistorico);
    }
  }

  // ... y cerramos el fichero
  ficheroActual.close();
}

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
  if (errorGeneral.length() == 0)
  {
    // Validamos si la tarjeta SD es accesible junto con un fichero de configuración accesible
    validarSiSDCardYFicheroConfiguracionAccesible() ;
  }

  // Si no hay ningún error general mientras se validó la tarjeta SD
  if (errorGeneral.length() == 0)
  {
    // Si la tarjeta SD y su fichero de configuración son accesibles ...
    if (sdYFicheroConfiguracionAccesible)
    {
      // ... procedemos a la comparación y copia de los archivos
      compararYCopiarFicherosSDyLittleFS();
    }
  }
  
  // Si no hay ningún error general mientras se comparó y copió los archivos
  if (errorGeneral.length() == 0)
  {
    // Cargamos la configuración desde el archivo de configuración
    parseaFicheroConfiguracionJandulaBase();
  }

  // Si no hay ningún error general mientras se cargó la configuración ...
  if (errorGeneral.length() == 0)
  {
    // Conectamos a la red WiFi
    conectarWiFi();
  }
      
  // Si no hay ningún error general mientras se conectó a la red WiFi y la conexión a la red WiFi es correcta ...
  if (errorGeneral.length() == 0 && WiFi.status() == WL_CONNECTED)
  {
    // ... entonces sincronizamos la hora con el servidor NTP
    sincronizarHoraConServidorNTP();
  }

  // Si no hay ningún error general mientras se sincronizó la hora con el servidor NTP ...
  if (errorGeneral.length() == 0)
  {
    // Obtenemos la dirección MAC de la tarjeta WiFi
    macAddress = WiFi.macAddress();
  
    // Mostramos la dirección MAC de la tarjeta WiFi
    registrarLog("INFO", "MAC guardada: " + macAddress);

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
      // Almacenamos el error en la variable global errorGeneral
      errorGeneral = "FATAL: No se ha podido formatear y montar el sistema de archivos LittleFS";

      // Mostramos un mensaje de error
      Serial.println(errorGeneral);
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
 void validarSiSDCardYFicheroConfiguracionAccesible()
 {  
  // Inicializamos la SPI que es la interfaz de comunicación con la tarjeta SD para validar si es accesible
  // sdCardClock: pin para el reloj de la tarjeta SD
  // sdCardMISO: pin para la entrada de datos de la tarjeta SD
  // sdCardMOSI: pin para la salida de datos de la tarjeta SD
  // sdCardChipSelect: pin para el chip select de la tarjeta SD
  SPI.begin(sdCardClock, sdCardMISO, sdCardMOSI, sdCardChipSelect);

  // Validamos si la tarjeta SD es accesible
  sdYFicheroConfiguracionAccesible = SD.begin(sdCardChipSelect);

  // Si la tarjeta SD no es accesible ...
  if (sdYFicheroConfiguracionAccesible)
  {
    // Validamos si el fichero de configuración existe
    sdYFicheroConfiguracionAccesible = SD.exists(RUTA_FICHERO_CONFIGURACION);

    // Si el fichero de configuración no es accesible ...
    if (!sdYFicheroConfiguracionAccesible)
    {
      // Almacenamos el error en la variable global errorGeneral
      errorGeneral = "FATAL: El fichero de configuración de la tarjeta SD no es accesible";

      // Mostramos un mensaje de error
      Serial.println(errorGeneral);
    }
  }
}

/**
 * Compara y copia un fichero del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @return void
 */
void compararYCopiarFicherosSDyLittleFS()
{
  // Validamos si el fichero existe en el sistema de archivos LittleFS
  bool ficheroLocalLitleFSEncontrado = LittleFS.exists(RUTA_FICHERO_CONFIGURACION);
  
  // Mostramos si el fichero de configuración existe en el sistema de archivos LittleFS y en la tarjeta SD
  String ficheroLittleYsdEncontrados = "fichero de configuración en LittleFS encontrado?: " + String(ficheroLocalLitleFSEncontrado) + ", fichero de configuración en la tarjeta SD encontrado?: " + String(sdYFicheroConfiguracionAccesible) ;
  registrarLog("INFO", ficheroLittleYsdEncontrados);

  // Si el fichero local existe y el fichero en la tarjeta SD existe ...
  if (ficheroLocalLitleFSEncontrado && sdYFicheroConfiguracionAccesible)
  {
    // Abrimos ambos ficheros de la tarjeta SD para comparar los timestamps de modificación
    File ficheroSD = SD.open(RUTA_FICHERO_CONFIGURACION, "r");

    // Obtenemos los timestamps de modificación para ambos ficheros
    unsigned long estampaTiempoLocalFicheroLittleFS = obtenerTimestampFicheroConfiguracionLittleFS();
    unsigned long estampaTiempoFicheroSD            = ficheroSD.getLastWrite();

    // Cerramos el fichero de configuración de la tarjeta SD
    ficheroSD.close();

    // Si los timestamps de modificación son diferentes, comparamos y copiamos el fichero de configuración de la tarjeta SD al sistema de archivos LittleFS
    compararTimeStampsYcopiarSiFicherosSDyLittleFSSonDiferentes(estampaTiempoLocalFicheroLittleFS, estampaTiempoFicheroSD);
  }
  else if (sdYFicheroConfiguracionAccesible)
  {
    // Si el fichero de configuración en la tarjeta SD existe, ...
    // ... copiamos el fichero de configuración de la tarjeta SD al sistema de archivos LittleFS
    copiarFicheroDeSDaLittleFS();
    
    // Mostramos el timestamp de modificación del fichero de configuración en la tarjeta SD
    registrarLog("INFO", "Nueva estampa de tiempo del fichero de configuración en la tarjeta SD guardada");
  }
}

/**
 * Obtiene el timestamp de modificación de un fichero de configuración en el sistema de archivos LittleFS
 * 
 * @return timestamp: timestamp de modificación del fichero de configuración
 */
 unsigned long obtenerTimestampFicheroConfiguracionLittleFS()
 {
   // Valor por defecto para el timestamp (0 indica que no se encontró)
   unsigned long timestamp = 0;
 
   // Abrimos el fichero de configuración en modo lectura
   File ficheroLittleFS = LittleFS.open(RUTA_FICHERO_CONFIGURACION, "r");
 
   // Si el archivo tiene contenido ...
   if (ficheroLittleFS.available())
   {
     // ... leemos el timestamp de modificación del fichero de configuración
     timestamp = ficheroLittleFS.parseInt();
 
     // Cerramos el fichero de configuración en el sistema de archivos LittleFS
     ficheroLittleFS.close();
   }
   else
   {
     // ... mostramos un mensaje de advertencia
     registrarLog("WARNING", "No se encontró timestamp en el fichero de configuración en el sistema de archivos LittleFS");
   }
 
   // Devolvemos el timestamp
   return timestamp;
}

/**
 * Compara y copia un fichero del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @param estampaTiempoLocalFicheroLittleFS: timestamp de modificación del fichero en el sistema de archivos LittleFS
 * @param estampaTiempoFicheroSD: timestamp de modificación del fichero en la tarjeta SD
 * @return void
 */
void compararTimeStampsYcopiarSiFicherosSDyLittleFSSonDiferentes(unsigned long estampaTiempoLocalFicheroLittleFS, unsigned long estampaTiempoFicheroSD)
{
  // Mostramos el timestamp de modificación del fichero en el sistema de archivos LittleFS
  registrarLog("INFO", "Última modificación del fichero de configuración local: " + obtenerFechaYHoraDesdeTimestamp(estampaTiempoLocalFicheroLittleFS));

  // Mostramos el timestamp de modificación del fichero en la tarjeta SD
  registrarLog("INFO", "Última modificación del fichero de configuración en la tarjeta SD: " + obtenerFechaYHoraDesdeTimestamp(estampaTiempoFicheroSD));

  // Si la tarjeta SD tiene un timestamp de modificación más reciente que 
  // el fichero de configuración en el sistema de archivos LittleFS, entonces ...
  if (estampaTiempoFicheroSD > estampaTiempoLocalFicheroLittleFS)
  {
    // ... copiamos el fichero de configuración de la tarjeta SD al sistema de archivos LittleFS
    copiarFicheroDeSDaLittleFS();

    // Mostramos un mensaje de resultado
    registrarLog("INFO", "Copiado el fichero de configuración de la tarjeta SD al sistema de archivos LittleFS guardado");
  }
  else
  {
    // Si el fichero de configuración en el sistema de archivos LittleFS tiene
    // un timestamp de modificación más reciente que el fichero de configuración en la tarjeta SD ó ...
    // ... si tiene ambos el mismo timestamp de modificación, entonces no hay que hacer nada
    registrarLog("INFO", "Los timestamps de modificación del fichero de configuración son iguales");
  }
}

/**
 * Copia un fichero de configuración de la tarjeta SD al sistema de archivos LittleFS
 * 
 * @return void
 */
void copiarFicheroDeSDaLittleFS()
{
  // Abrimos el fichero de configuración de la tarjeta SD para lectura
  File ficheroSD = SD.open(RUTA_FICHERO_CONFIGURACION, "r");

  // Abrimos el fichero de configuración en el sistema de archivos LittleFS para escritura
  File ficheroLittleFS = LittleFS.open(RUTA_FICHERO_CONFIGURACION, "w");

  // Copiamos el fichero de configuración de la tarjeta SD al sistema de archivos LittleFS
  while (ficheroSD.available())
  {
    // Copiamos el contenido del fichero de configuración de la tarjeta SD al sistema de archivos LittleFS
    ficheroLittleFS.write(ficheroSD.read());
  }

  // Obtenemos la estampa de tiempo del fichero en la tarjeta SD
  unsigned long estampaTiempoFicheroSD = ficheroSD.getLastWrite();

  // Sobrescribimos la estampa de tiempo en el fichero de configuración en el sistema de archivos LittleFS
  sobreescribirEstampaTiempoEnFichero(estampaTiempoFicheroSD);
  
  // Cerramos el fichero de configuración en el sistema de archivos LittleFS
  ficheroLittleFS.close();

  // Cerramos el fichero de configuración de la tarjeta SD
  ficheroSD.close();
}

/**
 * Escribe un timestamp en un fichero de configuración en el sistema de archivos LittleFS
 * 
 * @param timestamp: timestamp a escribir
 * @return void
 */
void sobreescribirEstampaTiempoEnFichero(unsigned long timestamp)
{
  // Abrimos el fichero de configuración en el sistema de archivos LittleFS en modo escritura, que sobrescribirá cualquier contenido existente
  File ficheroLittleFS = LittleFS.open(RUTA_FICHERO_CONFIGURACION, "w");

  // Escribimos el timestamp en el fichero de configuración en el sistema de archivos LittleFS
  ficheroLittleFS.println(timestamp);

  // Cerramos el fichero de configuración en el sistema de archivos LittleFS
  ficheroLittleFS.close();

  // Mostramos un mensaje de resultado
  registrarLog("INFO", "Estampa de tiempo escrita correctamente en fichero de configuración LittleFS");
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
 * @return void
 */
void parseaFicheroConfiguracionJandulaBase() 
{
  // Abrimos el fichero de configuración en modo lectura de LittleFS
  File ficheroConfiguracionLittleFS = LittleFS.open(RUTA_FICHERO_CONFIGURACION, "r");

  // Leemos el fichero de configuración línea por línea
  while (ficheroConfiguracionLittleFS.available())
  {
  // Leemos la línea del fichero de configuración
  String linea = ficheroConfiguracionLittleFS.readStringUntil('\n');

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

  // Cerramos el fichero de configuración en el sistema de archivos LittleFS
  ficheroConfiguracionLittleFS.close();

  // Si no hay un error general, validamos si todos los campos del fichero de configuración en el sistema de archivos LittleFS están rellenos
  if (errorGeneral.length() == 0)
  {
    parseaFicheroConfiguracionJandulaBaseValidarCamposRellenos();
  }
}

/**
 * Valida si todos los campos del fichero de configuración en el sistema de archivos LittleFS están rellenos
 * 
 * @return void
 */
void parseaFicheroConfiguracionJandulaBaseValidarCamposRellenos()
{
  // Si el SSID no está relleno ...
  if (wifiSSID.length() == 0)
  {
    // ... almacenamos el error en la variable global errorGeneral
    errorGeneral = "SSID vacío";
  }
  else
  {
    // Mostramos la SSID
    registrarLog("INFO", "SSID: " + wifiSSID);
  }
  
  // Si la contraseña no está rellena ...
  if (wifiPassword.length() == 0)
  {
    // ... almacenamos el error en la variable global errorGeneral
    errorGeneral = "Contraseña vacía";
  }
  else
  {
    // Mostramos la contraseña
    registrarLog("INFO", "Contraseña: " + wifiPassword);
  }

  // Si la URL de Firebase no está rellena ...
  if (urlFirebase.length() == 0)
  {
    // ... almacenamos el error en la variable global errorGeneral
    errorGeneral = "URL de Firebase vacía";
  }
  else
  {
    // Mostramos la URL de Firebase
    registrarLog("INFO", "URL de Firebase: " + urlFirebase);
  }

  // Si el ID del cliente no está relleno ...
  if (clientId.length() == 0)
  {
    // ... almacenamos el error en la variable global errorGeneral
    errorGeneral = "ID del cliente vacío";
  }
  else
  {
    // Mostramos el ID del cliente
    registrarLog("INFO", "ID del cliente: " + clientId);
  }

  // Si hay un error general ...
  if (errorGeneral.length() != 0)
  {
    // Mostramos un mensaje de error
    registrarLog("FATAL", errorGeneral);
  }
  else
  {
    // Mostramos un mensaje de información
    registrarLog("INFO", "Todos los campos del fichero de configuración (BASE) están rellenos");
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
  // Mostramos un mensaje de inicio de conexión
  registrarLog("INFO", "Iniciando conexión a la red WiFi SSID: " + wifiSSID);
  registrarLog("INFO", "Contraseña: " + wifiPassword);

  // Conectamos a la red WiFi
  WiFi.mode(WIFI_STA);
  WiFi.begin(wifiSSID.c_str(), wifiPassword.c_str());

  // Inicializamos el contador de intentos
  int intentos = 0;

  // Intentamos conectar a la red WiFi
  while (WiFi.status() != WL_CONNECTED && intentos < 30)
  {
    // Mostramos un punto de espera
    registrarLog("INFO", ".");

    // Incrementamos el contador de intentos
    intentos++;

    // Esperamos el tiempo de delay antes de reintentar
    delay(INTENTOS_DELAY);
  }

  // Si no se conecta a la red WiFi, se muestra un mensaje de error
  if (WiFi.status() != WL_CONNECTED)
  {
    // Mostramos un mensaje de error
    registrarLog("FATAL", "No se pudo conectar a la red WiFi");
  }
  else
  {
    // Mostramos un mensaje de conexión correcta
    registrarLog("INFO", "Conectado a la red WiFi");

    // Mostramos la IP de la red WiFi
    registrarLog("INFO", "IP de la red WiFi: " + WiFi.localIP().toString());
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
  registrarLog("INFO", "Iniciando sincronización de la hora con el servidor NTP");

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
      registrarLog("INFO", ".");

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
      registrarLog("INFO", "Fecha y hora sincronizada correctamente: " + String(formattedDate));
    }
  }

  // Si finalmente no se ha podido obtener la fecha y hora del servidor NTP,
  // se muestra un mensaje de error
  if (!sincronizacionRealizada)
  {
    // Gestionamos el mensaje de error
    registrarLog("FATAL", "No se ha podido obtener la fecha y hora del servidor NTP después de varios intentos.");
  }
}

/*********************************************************************/
/******** Funciones relacionadas con el Token Firebase JWT ***********/
/*********************************************************************/

/**
 * Obtiene el token JWT válido de Firebase.
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
    registrarLog("WARNING", "JWT inexistente o expirado. Solicitando nuevo token...");

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

  // Mostramos el valor de la fecha y hora actual y la expiración del token
  String valor = "NOW: " + String(now) + " expiración: " + String(expiracionTokenJWT);
  registrarLog("INFO", valor);

  // Se valida si el token ha expirado
  expirado = (expiracionTokenJWT == 0) || (now >= expiracionTokenJWT);

  // Si el token ha expirado, se muestra un mensaje de advertencia
  if (expirado)
  {
    registrarLog("WARNING", "El token JWT ha expirado");
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
  registrarLog("INFO", "Iniciando obtención del token de Firebase...");

  // Iniciamos la conexión HTTP
  HTTPClient httpJwt;
  httpJwt.setTimeout(TIME_PETICIONES_HTTP);

  // Inicializamos el dato de respuesta HTTP
  String httpResponseData = "";

  // Mostramos los detalles de la petición
  registrarLog("INFO", "Detalles de la petición para obtener el token JWT: ");
  registrarLog("INFO", "-- Dirección del servidor: " + urlFirebase);
  registrarLog("INFO", "-- X-CLIENT-ID: " + clientId);

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
      // Gestionamos el mensaje de error
      registrarLog("ERROR", "La petición HTTP para obtener el token JWT de Firebase ha fallado. Código: " + String(httpResponseCode));
    }
    else
    {
      // Obtenemos el token de la respuesta
      httpResponseData = httpJwt.getString();

      // Si el token es vacío, se muestra un mensaje de advertencia
      if (httpResponseData.length() == 0)
      {
        // Gestionamos el mensaje de error
        registrarLog("ERROR", "Se ha recibido una respuesta vacía del servidor para obtener el token JWT de Firebase");
      }
      else
      {
        // Asignamos el token a la variable
        tokenJWT = httpResponseData;

        // Mostramos el token JWT obtenido
        registrarLog("INFO", "El token JWT de Firebase obtenido es: " + tokenJWT);
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
      registrarLog("INFO", ".");
      
      // Incrementamos el contador de intentos
      intentos++;

      // Esperamos el tiempo de delay antes de intentar nuevamente
      delay(INTENTOS_DELAY);
    }
  }

  if (!conexionEstablecida)
  {
    // Gestionamos el mensaje de error
    registrarLog("ERROR", "No se pudo establecer conexión HTTP para acceder a la web: " + url);
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
  registrarLog("INFO", "Iniciando obtención de la expiración del token...");

  // Obtenemos el payload del token por lo que tenemos que obtener el primer y segundo punto
  int firstDot = tokenJWT.indexOf('.');
  int secondDot = tokenJWT.indexOf('.', firstDot + 1);

  // Si el token no es válido, se devuelve 0
  if (firstDot < 0 || secondDot < 0)
  {
    // Gestionamos el mensaje de error
    registrarLog("ERROR", "En la obtención de la expiración del token JWT, el token no es válido");
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
    registrarLog("INFO", "El token JWT expira en: " + obtenerFechaYHoraDesdeTimestamp(expiracionTokenJWT));
  }
}