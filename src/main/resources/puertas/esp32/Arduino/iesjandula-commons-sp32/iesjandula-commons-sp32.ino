#include <Controller.h>
#include <ArduinoJson.h>

String firebaseUrl = "http://192.168.1.207:8083/firebase/token/app";
String xClientId   = "ESP32";

// Endpoint que actualiza estado por MAC
String actuadorEstadoUrl = "http://192.168.1.207:8092/automations/admin/actualizacion/actuador/estado";

// Variables globales que se rellenan en setup y se usan en loop
String token;
String miMac;

void setup() {
  Serial.begin(115200);

  pinMode(offlinePin, OUTPUT);
  pinMode(onlinePin, OUTPUT);

  // Conectar al WiFi
  connectToWifi();

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("❌ No hay WiFi, no se puede continuar.");
    return;
  }

  // Guardar MAC del ESP32
  miMac = WiFi.macAddress();
  Serial.print("MAC guardada: ");
  Serial.println(miMac);

  // Pedir token y guardarlo
  Serial.println("\nPidiendo token a Firebase...\n");
  token = getFirebaseToken(firebaseUrl, xClientId);

  Serial.print("Token guardado: ");
  Serial.println(token);

  if (token.length() == 0) {
    Serial.println("❌ Token vacío. Abortando.");
    return;
  }
}

void loop() {

  Serial.println("\nHeartbeat (ON)...");
  Serial.print("MAC: ");
  Serial.println(miMac);

  // Enviar MAC al servidor cada 10 segundos
  String resp = updateActuatorStateSimple(actuadorEstadoUrl, token, miMac, true);

  Serial.println("Respuesta servidor:");
  Serial.println(resp);

  delay(10000); // 10 segundos
}
