#pragma once

// Biblioteca de Arduino
#include <Arduino.h>

// WiFi: sirve para conectar el ESP32 a la red WiFi
#include <WiFi.h>

// SD: sirve para manejar tarjetas SD
#include <SD.h>

// LittleFS: sirve para manejar el sistema de archivos en el ESP32
#include <LittleFS.h>

// FS: sirve para manejar el sistema de archivos
#include <FS.h>

// SPI: sirve para comunicarse con dispositivos externos como tarjetas SD, displays, etc.
#include <SPI.h>

// time.h: sirve para manejar fechas y horas
#include <time.h>

// base64: útil para codificar y decodificar Base64 (JWT)
#include <base64.h>

// HTTPClient: sirve para hacer peticiones HTTP
#include <HTTPClient.h>

// WiFiClientSecure: sirve para hacer peticiones HTTPS
#include <WiFiClientSecure.h>

/************************************************/
/***************** Debug ************************/
/************************************************/

#define DEBUG 1
#if DEBUG == 1
  #define debug(x) Serial.print(x)
  #define debugln(x) Serial.println(x)
#else
  #define debug(x)
  #define debugln(x)
#endif

/************************************************/
/***************** Constantes *******************/
/************************************************/

// Zona horaria: sirve para configurar la zona horaria del ESP32
#define TZ_INFO "CET-1CEST,M3.5.0/02,M10.5.0/03"

// Tiempo de espera para las peticiones HTTP en milisegundos
#define TIME_PETICIONES_HTTP 20000

// Intentos delay: sirve para configurar el delay entre intentos
#define INTENTOS_DELAY 1000

// Máximo de intentos HTTP: sirve para configurar el máximo de intentos para hacer peticiones HTTP
#define MAXIMO_INTENTOS_HTTP 5

// Ruta del archivo de configuración dentro de la tarjeta SD
#define pathFicheroConfiguracion "/configuraciones.txt"

// Ruta del directorio de logs en la tarjeta SD
#define RUTA_DIRECTORIO_LOGS "/logs"

// Ruta del fichero de log actual en la tarjeta SD
#define RUTA_FICHERO_LOG_ACTUAL "/logs/jandula.log"

// Prefijo de los ficheros de log en la tarjeta SD
#define PREFIJO_FICHEROS_LOG "/logs/jandula_"

// Sufijo de los ficheros de log en la tarjeta SD
#define SUFIJO_FICHEROS_LOG ".log"

// Tamaño máximo de los ficheros de log en la tarjeta SD (total: 100 MB)
#define TAMANIO_MAXIMO_FICHERO_LOG (100 * 1024 * 1024)

// Máximo de ficheros de log en la tarjeta SD
#define MAXIMO_FICHEROS_ROLLOVER 10

// Intervalo de tiempo entre intentos de reconexión en milisegundos (10 segundos)
#define INTERVALO_REINTENTO 10000

/************************************/
/**** Pines para la tarjeta SD ******/
/************************************/

// sdCardMOSI: pin para la salida de datos de la tarjeta SD
#define sdCardMOSI 23

// sdCardMISO: pin para la entrada de datos de la tarjeta SD
#define sdCardMISO 19

// sdCardClock: pin para el reloj de la tarjeta SD
#define sdCardClock 18

// sdCardChipSelect: pin para el chip select de la tarjeta SD
#define sdCardChipSelect 5

/*****************************************************/
/**** Propiedades del fichero de configuración *******/
/*****************************************************/

// RUTA_FICHERO_CONFIGURACION: ruta del fichero de configuración
#define RUTA_FICHERO_CONFIGURACION "/configuraciones.txt"

// PROPIEDAD_WIFI_SSID: propiedad del SSID
#define PROPIEDAD_WIFI_SSID "WIFI_SSID"

// PROPIEDAD_WIFI_PASSWORD: propiedad de la contraseña
#define PROPIEDAD_WIFI_PASSWORD "WIFI_PASSWORD"

// PROPIEDAD_URL_FIREBASE: propiedad de la URL de Firebase
#define PROPIEDAD_URL_FIREBASE "URL_FIREBASE"

// PROPIEDAD_CLIENT_ID: propiedad del ID del cliente de Firebase
#define PROPIEDAD_CLIENT_ID "CLIENT_ID"

// PROPIEDAD_WIFI_SSID_LENGTH: longitud de la propiedad del SSID
#define PROPIEDAD_WIFI_SSID_LENGTH strlen(PROPIEDAD_WIFI_SSID)

// PROPIEDAD_WIFI_PASSWORD_LENGTH: longitud de la propiedad de la contraseña
#define PROPIEDAD_WIFI_PASSWORD_LENGTH strlen(PROPIEDAD_WIFI_PASSWORD)

// PROPIEDAD_URL_FIREBASE_LENGTH: longitud de la propiedad de la URL de Firebase
#define PROPIEDAD_URL_FIREBASE_LENGTH strlen(PROPIEDAD_URL_FIREBASE)

// PROPIEDAD_CLIENT_ID_LENGTH: longitud de la propiedad del ID del cliente de Firebase
#define PROPIEDAD_CLIENT_ID_LENGTH strlen(PROPIEDAD_CLIENT_ID)

/************************************************/
/***************** Variables ********************/
/************************************************/

// wifiSSID: sirve para almacenar el SSID de la red WiFi
extern String wifiSSID;

// wifiPassword: sirve para almacenar la contraseña de la red WiFi
extern String wifiPassword;

// urlFirebase: sirve para almacenar la URL de Firebase
extern String urlFirebase;

// clientId: sirve para almacenar el ID del cliente de Firebase
extern String clientId;

// sistemaArchivosLittleFSAccesible: sirve para indicar si el sistema de archivos LittleFS es accesible
extern bool sistemaArchivosLittleFSAccesible;

// tarjetaSDAccesible: sirve para indicar si la tarjeta SD es accesible
extern bool tarjetaSDAccesible;

// tokenJWT: sirve para almacenar el token JWT de Firebase
extern String tokenJWT;

// expiracionTokenJWT: sirve para almacenar la fecha de expiración del token JWT
extern unsigned long expiracionTokenJWT;

// macAddress: sirve para almacenar la dirección MAC del ESP32
extern String macAddress;

// ficheroLog: sirve para almacenar el fichero de log
extern File ficheroLog;

// tamanhoFicheroLog: sirve para almacenar el tamaño del fichero de log
extern size_t tamanhoFicheroLog;

// errorGeneral: sirve para almacenar el error general
extern String errorGeneral;

// ultimoIntento: sirve para almacenar el tiempo del último intento de reconexión
extern unsigned long ultimoIntento;

/************************************************/
/**************** Funciones *********************/
/************************************************/

/*******************************************************/
/********* Funciones relacionadas con los logs *********/
/*******************************************************/

/**
 * Registra una línea de log en Serial y en SD con rollover
 *
 * @param nivel: nivel del log (INFO, WARNING, ERROR)
 * @param mensaje: mensaje del log
 * @return void
 */
void registrarLog(const String& nivel, const String& mensaje);
 
 /**
  * Obtiene la marca temporal del log
  * 
  * @return String: marca temporal del log
  */
String obtenerMarcaTemporalLog();
 
 /**
  * Escribe una línea de log en la tarjeta SD
  * 
  * @param lineaLog: línea de log a escribir
  * @return void
  */
void escribirLogEnSD(const String& lineaLog);
 
 /**
  * Asegura el directorio de logs
  * 
  * @return void
  */
void asegurarDirectorioLogs();
 
 /**
  * Aplica el rollover de logs
  * 
  * @return void
  */
void aplicarRolloverLogs();

/***************************************************************************/
/*** Funciones relacionadas con la inicialización de los componentes base **/
/***************************************************************************/

/**
 * Realiza la inicialización de los diferentes componentes de la biblioteca
 * 
 * @return void
 */
void setupJandulaBase();

/**
 * Realiza el mantenimiento de la tarjeta SD en caso de que se quite y ponga
 * 
 * @return void
 */
void mantenimientoSD();

/**
 * Valida si la tarjeta SD es accesible junto con un fichero de configuración accesible
 * 
 * @return void
 */
void validarSiSDCardYFicheroConfiguracionAccesible();

/**
 * Valida si la conexión a LittleFS es accesible
 * 
 * @return void
 */
void validarSiConexionLittleFSEsAccesible();

/**
 * Compara y copia un archivo del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @return void
 */
void compararYCopiarFicherosSDyLittleFS();

/**
 * Compara y copia un fichero del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @param estampaTiempoLocalFicheroLittleFS: timestamp de modificación del fichero en el sistema de archivos LittleFS
 * @param estampaTiempoFicheroSD: timestamp de modificación del fichero en la tarjeta SD
 * @return void
 */
void compararTimeStampsYcopiarSiFicherosSDyLittleFSSonDiferentes(unsigned long estampaTiempoLocalFicheroLittleFS, unsigned long estampaTiempoFicheroSD);

/**
 * Copia un fichero de la tarjeta SD al sistema de archivos LittleFS
 * 
 * @return void
 */
void copiarFicheroDeSDaLittleFS();

/**
 * Escribe el timestamp de modificación de un fichero de configuración en el sistema de archivos LittleFS
 * 
 * @param timestamp: timestamp de modificación del fichero de configuración
 * @return void
 */
void sobreescribirEstampaTiempoEnFichero(unsigned long timestamp);

/**
 * Obtiene el timestamp de modificación de un fichero de configuración en el sistema de archivos LittleFS
 * 
 * @return timestamp: timestamp de modificación del fichero de configuración
 */
unsigned long obtenerTimestampFicheroConfiguracionLittleFS();

/**
 * Obtiene la fecha y hora formateada desde un timestamp
 * 
 * @param timestamp: timestamp
 * @return fechaYHoraFormateada: fecha y hora formateada
 */
String obtenerFechaYHoraDesdeTimestamp(unsigned long timestamp);

/**
 * Conecta el ESP32 a la red WiFi
 * 
 * @return void
 */
void conectarWiFi();

/**
 * Sincroniza la hora con el servidor NTP
 * 
 * @return void
 */
void sincronizarHoraConServidorNTP();

/**
 * Obtiene el token JWT válido de Firebase
 * 
 * @return void
 */
void obtenerTokenJWT();

/**
 * Verifica si el token JWT de Firebase ha expirado
 * 
 * @param token: token JWT de Firebase
 * @return true si el token ha expirado, false en caso contrario
 */
 bool tokenJWTExpirado();

 /**
  * Obtiene el token JWT de Firebase
  * 
  * @return void
  */
 void obtenerTokenJWTInternal();
 
 /**
  * Inicia una conexión HTTP con reintentos
  * 
  * @param http: referencia al objeto HTTPClient
  * @param url: URL de la petición
  * @return true si la conexión se ha iniciado correctamente, false en caso contrario
  */
 bool iniciarConexionHTTPConReintentos(HTTPClient& http, const String& url);

/**
 * Obtiene la expiración del token JWT de Firebase
 * 
 * @return void
 */
void obtenerExpiracionJWT();

/*******************************************************/
/********* Parseo de fichero de configuración **********/
/*******************************************************/

/**
 * Carga la configuración de la biblioteca Jandula Base desde el archivo de configuración
 *
 * @return void
 */
void parseaFicheroConfiguracionJandulaBase();

/**
 * Valida si todos los campos del fichero de configuración de la biblioteca Jandula Base están rellenos
 * 
 * @return void
 */
void parseaFicheroConfiguracionJandulaBaseValidarCamposRellenos();