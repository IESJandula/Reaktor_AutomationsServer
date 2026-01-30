#pragma once
#include <Arduino.h>
#include <WiFi.h>
#include <SD.h>
#include <LittleFS.h>
#include <FS.h>
#include <SPI.h>
#include <time.h>

// para pedir token al servidor
#include <HTTPClient.h>
#include <WiFiClientSecure.h>

// LEDs
#define offlinePin 4
#define onlinePin 0
#define littleFSLed 2
#define sdFSLed 15

// Debug
#define DEBUG 1
#if DEBUG == 1
  #define debug(x) Serial.print(x)
  #define debugln(x) Serial.println(x)
#else
  #define debug(x)
  #define debugln(x)
#endif

// Variables
extern String WifiSSID;
extern String WifiPassword;
extern String wifiConfigFilePath;
extern String wifiConfigMetadataFilePath;

extern bool littleFSInitialized;
extern bool sdCardInitialized;

// Funciones
bool initializeLittleFS();
bool initializeSDCard();
void compareAndCopy(String filePath, String metadataFilePath, String fileName);
void loadConfigFromFile(String configFilePath);
void connectToWifi();
void syncTimeToNtpServer();

void printInterfaceSentence(String sentence);

// token desde FirebaseServer
String getFirebaseToken(const String& urlFirebase, const String& xClientId, bool insecureTLS = true);

// 
String updateActuatorState(
  const String& url,
  const String& token,
  const String& targetMac,
  const String& estado,
  bool insecureTLS = true
);

// enviar SOLO MAC 
String updateActuatorStateSimple(
  const String& url,
  const String& token,
  const String& targetMac,
  bool insecureTLS = true
);

String getActuadoresJson(const String& url, const String& token, bool insecureTLS = true);
