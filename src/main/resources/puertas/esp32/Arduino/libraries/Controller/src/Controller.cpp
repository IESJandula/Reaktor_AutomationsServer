#include "Controller.h"
#include <ArduinoJson.h>

// -------------------- Variables globales --------------------
String WifiSSID;
String WifiPassword;

String firebaseUrl;
String actuadorEstadoUrl;
String accionEstadoUrl;
String xClientId;

bool littleFSInitialized = false;
bool sdCardInitialized = false;

// -------------------- LittleFS --------------------
bool initializeLittleFS() {
  if (!LittleFS.begin()) {
    LittleFS.begin(true);
  }
  littleFSInitialized = true;
  digitalWrite(littleFSLed, HIGH);
  return true;
}

// -------------------- SD --------------------
bool initializeSDCard() {
  SPI.begin();
  if (!SD.begin()) {
    sdCardInitialized = false;
    digitalWrite(sdFSLed, LOW);
    return false;
  }
  sdCardInitialized = true;
  digitalWrite(sdFSLed, HIGH);
  return true;
}

// Copiar SD -> LittleFS
void compareAndCopy(String filePath, String metadataFilePath, String fileName) {
  (void)metadataFilePath;
  (void)fileName;

  if (SD.exists(filePath)) {
    File sdFile = SD.open(filePath, "r");
    if (!sdFile) return;

    File localFile = LittleFS.open(filePath, "w");
    if (!localFile) {
      sdFile.close();
      return;
    }

    while (sdFile.available()) {
      localFile.write(sdFile.read());
    }

    sdFile.close();
    localFile.close();
  }
}

// -------------------- Parse de config SD --------------------
static void parseConfigLineSD(const String& lineRaw) {
  String line = lineRaw;
  line.trim();
  if (line.length() == 0) return;
  if (line.startsWith("#")) return;

  if (line.startsWith("SSID=")) {
    WifiSSID = line.substring(5);
    WifiSSID.trim();
    return;
  }

  if (line.startsWith("PASSWORD=")) {
    WifiPassword = line.substring(9);
    WifiPassword.trim();
    return;
  }

  if (line.startsWith("FIREBASE_URL=")) {
    firebaseUrl = line.substring(String("FIREBASE_URL=").length());
    firebaseUrl.trim();
    return;
  }

  if (line.startsWith("ACTUADOR_ESTADO_URL=")) {
    actuadorEstadoUrl = line.substring(String("ACTUADOR_ESTADO_URL=").length());
    actuadorEstadoUrl.trim();
    return;
  }

  if (line.startsWith("ACCION_ESTADO_URL=")) {
    accionEstadoUrl = line.substring(String("ACCION_ESTADO_URL=").length());
    accionEstadoUrl.trim();
    return;
  }

  if (line.startsWith("X_CLIENT_ID=")) {
    xClientId = line.substring(12);
    xClientId.trim();
    return;
  }
}

bool loadConfigFromSD(const String& configPath) {
  if (!sdCardInitialized) {
    Serial.println("❌ loadConfigFromSD: SD no inicializada");
    return false;
  }

  if (!SD.exists(configPath)) {
    Serial.print("❌ No existe el archivo en SD: ");
    Serial.println(configPath);
    return false;
  }

  File f = SD.open(configPath, FILE_READ);
  if (!f) {
    Serial.print("❌ No se pudo abrir: ");
    Serial.println(configPath);
    return false;
  }

  Serial.print("📄 Leyendo config desde SD: ");
  Serial.println(configPath);

  WifiSSID = "";
  WifiPassword = "";
  firebaseUrl = "";
  actuadorEstadoUrl = "";
  accionEstadoUrl = "";

  while (f.available()) {
    String line = f.readStringUntil('\n');
    parseConfigLineSD(line);
  }
  f.close();

  bool ok = true;

  if (WifiSSID.length() == 0) {
    Serial.println("❌ Falta SSID");
    ok = false;
  }
  if (WifiPassword.length() == 0) {
    Serial.println("❌ Falta PASSWORD");
    ok = false;
  }
  if (firebaseUrl.length() == 0) {
    Serial.println("❌ Falta FIREBASE_URL...");
    ok = false;
  }
  if (actuadorEstadoUrl.length() == 0) {
    Serial.println("❌ Falta ACTUADOR_ESTADO_URL...");
    ok = false;
  }
  if (accionEstadoUrl.length() == 0) {
    Serial.println("❌ Falta ACCION_ESTADO_URL...");
    ok = false;
  }

  Serial.println("---- CONFIG CARGADA ----");
  Serial.print("SSID: "); Serial.println(WifiSSID);
  Serial.print("FIREBASE: "); Serial.println(firebaseUrl);
  Serial.print("ACTUADOR_ESTADO: "); Serial.println(actuadorEstadoUrl);
  Serial.print("ACCION_ESTADO: "); Serial.println(accionEstadoUrl);
  Serial.print("CLIENT_ID: "); Serial.println(xClientId);
  Serial.println("------------------------");

  return ok;
}

// -------------------- WiFi --------------------
void connectToWifi() {
  if (WifiSSID.length() == 0 || WifiPassword.length() == 0) {
    Serial.println("❌ connectToWifi: SSID/PASSWORD vacíos (no se cargó config)");
    digitalWrite(onlinePin, LOW);
    digitalWrite(offlinePin, HIGH);
    return;
  }

  Serial.print("Conectando a WiFi: ");
  Serial.println(WifiSSID);

  WiFi.mode(WIFI_STA);
  WiFi.begin(WifiSSID.c_str(), WifiPassword.c_str());

  int intentos = 0;
  while (WiFi.status() != WL_CONNECTED && intentos < 30) {
    delay(500);
    Serial.print(".");
    intentos++;
  }

  Serial.println();

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("✅ CONECTADO AL WIFI");
    Serial.print("IP: ");
    Serial.println(WiFi.localIP());
    digitalWrite(onlinePin, HIGH);
    digitalWrite(offlinePin, LOW);
  } else {
    Serial.println("❌ NO SE PUDO CONECTAR");
    digitalWrite(onlinePin, LOW);
    digitalWrite(offlinePin, HIGH);
  }
}

// -------------------- NTP --------------------
void syncTimeToNtpServer() {
  Serial.println("Network Time Protocol");
  configTzTime(TZ_INFO, "pool.ntp.org", "time.nist.gov", "time.google.com");

  struct tm timeinfo;
  const int maxRetries = 5;
  int attempt = 0;

  while (attempt < maxRetries) {
    if (getLocalTime(&timeinfo)) {
      char formattedDate[30];
      strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", &timeinfo);
      Serial.println("- Date and time set successfully: ");
      Serial.println(formattedDate);
      return;
    }

    Serial.println("WARNING: Attempt ");
    Serial.println(String(attempt + 1));
    Serial.println("failed. Retrying...");
    delay(1000);
    attempt++;
  }

  Serial.println("ERROR: Failed to obtain date and time from NTP server after multiple attempts.");
}

// -------------------- Token Firebase --------------------
String getFirebaseToken(const String& urlFirebase, const String& xClientId)
{
  HTTPClient httpJwt;
  httpJwt.setTimeout(20000);

  String httpResponseData = "";
  String authToken = "";

  Serial.println("Request details: ");
  Serial.println("-- Server Address: ");
  Serial.println(urlFirebase);
  Serial.println("-- X-CLIENT-ID: ");
  Serial.println(xClientId);

  if (!beginWithRetry(httpJwt, urlFirebase)) {
    return "";
  }

  httpJwt.addHeader("X-CLIENT-ID", xClientId);

  int httpResponseCode = httpJwt.POST("");

  if (httpResponseCode >= 200 && httpResponseCode < 300) {
    Serial.println("FIREBASE: Response received.");
    Serial.println("- HTTP Response Code:");
    Serial.println(String(httpResponseCode));

    httpResponseData = httpJwt.getString();
    if (httpResponseData.length() > 0) {
      Serial.println("- Server Response:");
      Serial.println(String(httpResponseData));
      authToken = httpResponseData;
    } else {
      Serial.println("WARNING: Received an empty response from the server.");
    }
  } else {
    Serial.println("ERROR: HTTP request failed. Code: ");
    Serial.println(String(httpResponseCode));
  }

  httpJwt.end();
  return authToken;
}

// -------------------- Reintentos HTTP --------------------
bool beginWithRetry(HTTPClient& http, const String& url, int maxRetries) {
  int attempts = 0;
  while (attempts < maxRetries) {
    if (http.begin(url)) {
      return true;
    }
    Serial.println("Intento fallido al iniciar conexión HTTP. Reintentando... intento ");
    Serial.println(String(attempts + 1));
    attempts++;
    delay(500);
  }

  Serial.println("ERROR: No se pudo establecer conexión HTTP después de varios intentos.");
  return false;
}

// -------------------- Parsear acción pendiente --------------------
AccionPendiente parsearAccionPendiente(const String& json) {
  AccionPendiente accion;
  accion.accionId = 0;
  accion.estado = "";
  accion.resultado = "";
  accion.mac = "";
  accion.ordenId = 0;
  accion.hayAccion = false;

  if (json.length() == 0) {
    return accion;
  }

  DynamicJsonDocument doc(1024);
  DeserializationError error = deserializeJson(doc, json);

  if (error) {
    Serial.print("❌ Error parseando JSON: ");
    Serial.println(error.c_str());
    return accion;
  }

  if (!doc["accionId"].isNull()) {
    accion.accionId = doc["accionId"].as<long>();
  }

  accion.estado = doc["estado"] | "";
  accion.resultado = doc["resultado"] | "";
  accion.mac = doc["mac"] | "";

  if (!doc["ordenId"].isNull()) {
    accion.ordenId = doc["ordenId"].as<long>();
  }

  if (accion.accionId > 0) {
    accion.hayAccion = true;
  }

  return accion;
}

// -------------------- Relé puerta --------------------
void accionarRelePuerta(unsigned long tiempoMs) {
  Serial.println("🚪 Abriendo puerta...");
  digitalWrite(relePuertaPin, HIGH);   // cambia a LOW si tu relé es activo en bajo
  delay(tiempoMs);
  digitalWrite(relePuertaPin, LOW);    // cambia a HIGH si tu relé es activo en bajo
  Serial.println("✅ Relé desactivado.");
}

// -------------------- Notificar estado final de acción --------------------
String updateAccionEstado(
  const String& url,
  const String& token,
  long accionId,
  const String& estado,
  const String& resultado) {

  HTTPClient http;
  http.setTimeout(20000);
  String httpResponseData = "";

  if (!beginWithRetry(http, url)) {
    return "";
  }

  http.addHeader("Authorization", "Bearer " + token);
  http.addHeader("Content-Type", "application/json");

  DynamicJsonDocument doc(512);
  doc["accionId"] = accionId;
  doc["estado"] = estado;
  doc["resultado"] = resultado;

  String body;
  serializeJson(doc, body);

  int httpResponseCode = http.POST(body);

  if (httpResponseCode >= 200 && httpResponseCode < 300) {
    Serial.println("✅ Estado de acción actualizado correctamente.");
    httpResponseData = http.getString();
  } else {
    Serial.println("❌ Error actualizando estado de acción.");
    Serial.println(String(httpResponseCode));
    httpResponseData = http.getString();
    Serial.println(httpResponseData);
  }

  http.end();
  return httpResponseData;
}

// -------------------- Update estado SIMPLE (solo MAC) --------------------
String updateActuatorStateSimple(
  const String& url,
  const String& token,
  const String& targetMac) {

  HTTPClient httpActualizarActuador;
  httpActualizarActuador.setTimeout(20000);
  String httpResponseData = "";

  if (!beginWithRetry(httpActualizarActuador, url)) {
    return "";
  }

  httpActualizarActuador.addHeader("Authorization", "Bearer " + token);
  httpActualizarActuador.addHeader("mac", targetMac);

  int httpResponseCode = httpActualizarActuador.POST("");

  if (httpResponseCode >= 200 && httpResponseCode < 300) {
    Serial.println("ACTUALIZAR ACTUADOR: Response received.");
    Serial.println("- HTTP Response Code:");
    Serial.println(String(httpResponseCode));

    httpResponseData = httpActualizarActuador.getString();
    if (httpResponseData.length() > 0) {
      Serial.println("- Server Response:");
      Serial.println(String(httpResponseData));
    } else {
      Serial.println("WARNING: Received an empty response from the server.");
    }
  } else {
    Serial.println("ERROR: HTTP request failed. Code: ");
    Serial.println(String(httpResponseCode));
  }

  httpActualizarActuador.end();
  return httpResponseData;
}

// -------------------- Stubs --------------------
String updateActuatorState(
  const String& url,
  const String& token,
  const String& targetMac,
  const String& estado) {
  (void)url;
  (void)token;
  (void)targetMac;
  (void)estado;
  return "";
}

String getActuadoresJson(const String& url, const String& token) {
  (void)url;
  (void)token;
  return "";
}