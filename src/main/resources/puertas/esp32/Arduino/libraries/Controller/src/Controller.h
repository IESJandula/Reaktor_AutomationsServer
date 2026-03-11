#pragma once
#include <Arduino.h>
#include <WiFi.h>
#include <SD.h>
#include <LittleFS.h>
#include <FS.h>
#include <SPI.h>
#include <time.h>

// HTTP
#include <HTTPClient.h>
#include <WiFiClientSecure.h>

// ---------------------------------------------
// Timezone Configuration
// ---------------------------------------------
#define TZ_INFO "CET-1CEST,M3.5.0/02,M10.5.0/03"

// LEDs
#define offlinePin 4
#define onlinePin 0
#define littleFSLed 2
#define sdFSLed 15

// RELÉ PUERTA
#define relePuertaPin 16   // cambia este GPIO si usas otro

// Debug
#define DEBUG 1
#if DEBUG == 1
  #define debug(x) Serial.print(x)
  #define debugln(x) Serial.println(x)
#else
  #define debug(x)
  #define debugln(x)
#endif

// WiFi
extern String WifiSSID;
extern String WifiPassword;

// Config desde SD
extern String firebaseUrl;
extern String actuadorEstadoUrl;
extern String accionEstadoUrl;
extern String xClientId;

// Rutas
extern String wifiConfigFilePath;
extern String wifiConfigMetadataFilePath;

extern bool littleFSInitialized;
extern bool sdCardInitialized;

// Estructura para la acción pendiente
struct AccionPendiente {
  long accionId;
  String estado;
  String resultado;
  String mac;
  long ordenId;
  bool hayAccion;
};

// FS
bool initializeLittleFS();
bool initializeSDCard();
void compareAndCopy(String filePath, String metadataFilePath, String fileName);

// Leer config directo desde SD
bool loadConfigFromSD(const String& configPath);

// WiFi
void connectToWifi();
void syncTimeToNtpServer();

void printInterfaceSentence(String sentence);

// HTTP
bool beginWithRetry(HTTPClient& http, const String& url, int maxRetries = 5);

// Token
String getFirebaseToken(const String& urlFirebase, const String& xClientId);

// Poll actuador
String updateActuatorState(
  const String& url,
  const String& token,
  const String& targetMac,
  const String& estado
);

String updateActuatorStateSimple(
  const String& url,
  const String& token,
  const String& targetMac
);

String getActuadoresJson(const String& url, const String& token);

// Acciones
AccionPendiente parsearAccionPendiente(const String& json);

String updateAccionEstado(
  const String& url,
  const String& token,
  long accionId,
  const String& estado,
  const String& resultado
);

void accionarRelePuerta(unsigned long tiempoMs = 1000);