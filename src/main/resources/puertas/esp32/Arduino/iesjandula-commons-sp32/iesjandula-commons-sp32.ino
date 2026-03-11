#include <Controller.h>
#include <ArduinoJson.h>

// Pin del relé de la puerta
#define relePuertaPin 12

// Variables globales
String token;
String miMac;

// Función para accionar el relé
void accionarRelePuerta(int tiempoMs) {
  Serial.println("Activando relé puerta");

  digitalWrite(relePuertaPin, HIGH);   // activar relé
  delay(tiempoMs);

  digitalWrite(relePuertaPin, LOW);    // apagar relé

  Serial.println("Relé desactivado");
}

void setup() {

  Serial.begin(115200);
  delay(200);

  pinMode(offlinePin, OUTPUT);
  pinMode(onlinePin, OUTPUT);
  pinMode(sdFSLed, OUTPUT);
  pinMode(littleFSLed, OUTPUT);

  // CONFIGURAR RELÉ
  pinMode(relePuertaPin, OUTPUT);
  digitalWrite(relePuertaPin, LOW); // relé apagado

  digitalWrite(offlinePin, LOW);
  digitalWrite(onlinePin, LOW);
  digitalWrite(sdFSLed, LOW);
  digitalWrite(littleFSLed, LOW);

  Serial.println("Inicializando SD...");

  if (!initializeSDCard()) {
    Serial.println("❌ No se pudo inicializar la SD. Abortando.");
    digitalWrite(offlinePin, HIGH);
    return;
  }

  Serial.println("✅ SD inicializada.");

  if (!loadConfigFromSD("/configuraciones.txt")) {
    Serial.println("❌ Config inválida/incompleta. Abortando.");
    digitalWrite(offlinePin, HIGH);
    return;
  }

  connectToWifi();

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("❌ No hay WiFi, no se puede continuar.");
    return;
  }

  syncTimeToNtpServer();

  miMac = WiFi.macAddress();

  Serial.print("MAC guardada: ");
  Serial.println(miMac);

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

  // Preguntar al backend si hay acción
  String resp = updateActuatorStateSimple(actuadorEstadoUrl, token, miMac);

  Serial.println("Respuesta servidor:");
  Serial.println(resp);

  // Parsear JSON recibido
  AccionPendiente accion = parsearAccionPendiente(resp);

  if (accion.hayAccion) {

    Serial.println("✅ Acción recibida");

    Serial.print("Accion ID: ");
    Serial.println(accion.accionId);

    Serial.print("Estado: ");
    Serial.println(accion.estado);

    Serial.print("Resultado: ");
    Serial.println(accion.resultado);

    Serial.print("Orden ID: ");
    Serial.println(accion.ordenId);

    bool ok = false;
    String resultadoFinal = "";

    if (accion.estado == "en_ejecucion") {

      Serial.println("Ejecutando acción...");

      // Abrir puerta con relé
      accionarRelePuerta(800);

      ok = true;
      resultadoFinal = "Puerta abierta correctamente";

    } else {

      Serial.println("Estado no válido para ejecutar acción");
      resultadoFinal = "Estado incorrecto para ejecución";
    }

    // Notificar resultado al backend
    if (ok) {

      updateAccionEstado(
        accionEstadoUrl,
        token,
        accion.accionId,
        "finalizado_ok",
        resultadoFinal
      );

    } else {

      updateAccionEstado(
        accionEstadoUrl,
        token,
        accion.accionId,
        "finalizado_error",
        resultadoFinal
      );

    }

  } else {

    Serial.println("ℹ️ No hay acción pendiente.");

  }

  // Espera antes del siguiente heartbeat
  delay(30000);
}