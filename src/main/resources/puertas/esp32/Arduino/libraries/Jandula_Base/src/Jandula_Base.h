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

// Intentos delay: sirve para configurar el delay entre intentos
#define INTENTOS_DELAY 1000

// Máximo de intentos HTTP: sirve para configurar el máximo de intentos para hacer peticiones HTTP
#define MAXIMO_INTENTOS_HTTP 5

// Ruta del archivo de configuración dentro de la tarjeta SD
#define pathFicheroConfiguracion "/configuraciones.txt"

/************************************************/
/**************** Pines y LEDs ******************/
/************************************************/

/************************************/
/*** Pines para LEDs informativos ***/
/************************************/

// Offline: pin para indicar que el ESP32 está offline
#define offlinePin 4

// Online: pin para indicar que el ESP32 está online
#define onlinePin 0

// littleFSLed: pin para indicar que el ESP32 tiene el sistema de archivos LittleFS
#define littleFSLed 2

// sdFSLed: pin para indicar que el ESP32 tiene la tarjeta SD
#define sdFSLed 15

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

/************************************************/
/***************** Variables ********************/
/************************************************/

// WifiSSID: sirve para almacenar el SSID de la red WiFi
extern String WifiSSID;

// WifiPassword: sirve para almacenar la contraseña de la red WiFi
extern String WifiPassword;

// firebaseUrl: sirve para almacenar la URL de Firebase
extern String firebaseUrl;

// xClientId: sirve para almacenar el ID del cliente de Firebase
extern String xClientId;

// sistemaArchivosLittleFSInicializado: sirve para indicar si el sistema de archivos LittleFS está inicializado
extern bool sistemaArchivosLittleFSInicializado;

// tarjetaSDInicializada: sirve para indicar si la tarjeta SD está inicializada
extern bool tarjetaSDInicializada;

/************************************************/
/**************** Funciones *********************/
/************************************************/

/**
 * Realiza la inicialización de los diferentes componentes de la biblioteca
 * 
 * @return void
 */
void setupJandulaBase();

/**
 * Inicializa la tarjeta SD
 * 
 * @return true si la tarjeta SD está inicializada, false en caso contrario
 */
bool inicializarSDCard();

/**
 * Compara y copia un archivo del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @param filePath: ruta del archivo a copiar
 * @return void
 */
void compararYCopiar(String filePath);

/**
 * Valida si el archivo existe en el sistema de archivos LittleFS
 * 
 * @param filePath: ruta del archivo
 * @return true si el archivo existe en el sistema de archivos LittleFS, false en caso contrario
 */
bool validarExistenciaFicheroLittleFS(const String& rutaFichero);

/**
 * Valida si el archivo existe en la tarjeta SD
 * 
 * @param rutaFichero: ruta del fichero
 * @return true si el fichero existe en la tarjeta SD, false en caso contrario
 */
bool validarExistenciaFicheroSD(const String& rutaFichero);

/**
 * Compara y copia un fichero del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @param rutaFichero: ruta del fichero
 * @param estampaTiempoLocalFicheroLittleFS: timestamp de modificación del fichero en el sistema de archivos LittleFS
 * @param estampaTiempoFicheroSD: timestamp de modificación del fichero en la tarjeta SD
 * @return void
 */
void compararYCopiarAmbosFicherosExisten(const String& rutaFichero, unsigned long estampaTiempoLocalFicheroLittleFS, unsigned long estampaTiempoFicheroSD);

/**
 * Copia un fichero de la tarjeta SD al sistema de archivos LittleFS
 * 
 * @param rutaFichero: ruta del fichero
 * @return void
 */
void copiarArchivoDeSDALittleFS(const String& rutaFichero);

/**
 * Escribe el timestamp de modificación de un fichero en el sistema de archivos LittleFS
 * 
 * @param timestamp: timestamp de modificación del fichero
 * @param rutaFichero: ruta del fichero
 * @return void
 */
void writeTimestampToFile(unsigned long timestamp, const String& rutaFichero);

/**
 * Obtiene el timestamp de modificación de un fichero en el sistema de archivos LittleFS
 * 
 * @param rutaFichero: ruta del fichero
 * @return timestamp: timestamp del fichero
 */
unsigned long obtenerTimestampDesdeArchivo(const String& rutaFichero);

/**
 * Obtiene la fecha y hora formateada desde un timestamp
 * 
 * @param timestamp: timestamp
 * @return fechaYHoraFormateada: fecha y hora formateada
 */
String obtenerFechaYHoraDesdeTimestamp(unsigned long timestamp);

/**
 * Inicializa el sistema de archivos LittleFS
 * 
 * @return true si el sistema de archivos LittleFS está inicializado, false en caso contrario
 */
bool inicializarLittleFS();

/**
 * Carga la configuración desde un fichero en la tarjeta SD
 * 
 * @param rutaFichero: ruta del fichero de configuración
 * @return true si la configuración se ha cargado correctamente, false en caso contrario
 */
bool loadConfigFromSD(const String& rutaFichero);

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
 * @param urlFirebase: URL de Firebase
 * @param xClientId: ID del cliente de Firebase
 * @return token JWT válido de Firebase
 */
String obtenerTokenJWTValido(const String& urlFirebase, const String& xClientId);

/**
 * Verifica si el token JWT de Firebase ha expirado
 * 
 * @param token: token JWT de Firebase
 * @return true si el token ha expirado, false en caso contrario
 */
 bool tokenExpirado(String token);

 /**
  * Obtiene el token JWT de Firebase
  * 
  * @param urlFirebase: URL de Firebase
  * @param xClientId: ID del cliente de Firebase
  * @return token JWT de Firebase
  */
 String getFirebaseToken(const String& urlFirebase, const String& xClientId);
 
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
 * @param token: token JWT de Firebase
 * @return expiración del token JWT de Firebase
 */
unsigned long obtenerExpiracionJWT(String token);

/*********************************************************************/
/******** Funciones relacionadas con la conexión online **************/
/*********************************************************************/

/**
 * Enciende el LED de online.
 * 
 * @return void
 */
void conexionOnlineCorrecta();

/**
 * Apaga el LED de online.
 * 
 * @return void
 */
void conexionOnlineIncorrecta();

/*********************************************************************/
/******** Funciones relacionadas con la conexión a la SD *************/
/*********************************************************************/

/**
 * Indica que la tarjeta SD está conectada.
 * 
 * @return void
 */
void tarjetaSDConectada();

/**
 * Indica que la tarjeta SD está desconectada.
 * 
 * @return void
 */
void tarjetaSDDesconectada();