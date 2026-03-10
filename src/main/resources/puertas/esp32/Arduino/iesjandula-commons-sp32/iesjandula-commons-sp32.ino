#include <Controller.h>
#include <ArduinoJson.h>

// Variables globales (se rellenan en setup y se usan en loop)
String token;
String miMac;

void setup() {
  Serial.begin(115200);
  delay(200);

  pinMode(offlinePin, OUTPUT);
  pinMode(onlinePin, OUTPUT);
  pinMode(sdFSLed, OUTPUT);
  pinMode(littleFSLed, OUTPUT);

  digitalWrite(offlinePin, LOW);
  digitalWrite(onlinePin, LOW);
  digitalWrite(sdFSLed, LOW);
  digitalWrite(littleFSLed, LOW);

  // 1) Inicializar SD
  Serial.println("Inicializando SD...");
  if (!initializeSDCard()) {
    Serial.println("❌ No se pudo inicializar la SD. Abortando.");
    digitalWrite(offlinePin, HIGH);
    return;
  }
  Serial.println("✅ SD inicializada.");

  // 2) Leer config desde /configuraciones.txt (raíz SD)
  if (!loadConfigFromSD("/configuraciones.txt")) {
    Serial.println("❌ Config inválida/incompleta. Abortando.");
    digitalWrite(offlinePin, HIGH);
    return;
  }

  // 3) Conectar a WiFi con lo leído
  connectToWifi();
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("❌ No hay WiFi, no se puede continuar.");
    return;
  }

  // 4) Configure device RTC block (needs network)
  syncTimeToNtpServer();

  // 5 Guardar MAC
  miMac = WiFi.macAddress();
  Serial.print("MAC guardada: ");
  Serial.println(miMac);

  // 6) Pedir token
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
  String resp = updateActuatorStateSimple(actuadorEstadoUrl, token, miMac);

  Serial.println("Respuesta servidor:");
  Serial.println(resp);

  delay(30000);
}