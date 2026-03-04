#include "Controller.h"

// -------------------- Variables globales --------------------
String WifiSSID;
String WifiPassword;

String firebaseUrl;
String actuadorEstadoUrl;
String xClientId;

bool littleFSInitialized = false;
bool sdCardInitialized = false;

// -------------------- LittleFS (opcional) --------------------
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
  SPI.begin();          // si tu SD usa pines por defecto
  if (!SD.begin()) {    // si necesitas CS: SD.begin(CS_PIN);
    sdCardInitialized = false;
    digitalWrite(sdFSLed, LOW);
    return false;
  }
  sdCardInitialized = true;
  digitalWrite(sdFSLed, HIGH);
  return true;
}

// Copiar SD -> LittleFS (no lo necesitas para esta versión, pero lo dejo)
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

  // Tu archivo: "FIREBASE_URL_http://...."
  if (line.startsWith("FIREBASE_URL=")) {
    firebaseUrl = line.substring(String("FIREBASE_URL=").length());
    firebaseUrl.trim();
    return;
  }

  // Tu archivo: "ACTUADOR_ESTADO_URL_http://...."
  if (line.startsWith("ACTUADOR_ESTADO_URL=")) {
    actuadorEstadoUrl = line.substring(String("ACTUADOR_ESTADO_URL=").length());
    actuadorEstadoUrl.trim();
    return;
  }

  // Opcional: CLIENT_ID=...
  if (line.startsWith("X_CLIENT_ID=")) {
    xClientId = line.substring(12);
    xClientId.trim();
    return;
  }

  // Ignorar claves desconocidas
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

  // limpiar variables (por si reinicias sin power-cycle)
  WifiSSID = "";
  WifiPassword = "";
  firebaseUrl = "";
  actuadorEstadoUrl = "";
  // xClientId NO lo vacío para mantener default si no viene

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

  Serial.println("---- CONFIG CARGADA ----");
  Serial.print("SSID: "); Serial.println(WifiSSID);
  Serial.print("FIREBASE: "); Serial.println(firebaseUrl);
  Serial.print("ACTUADOR_ESTADO: "); Serial.println(actuadorEstadoUrl);
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

// (si luego sincronizas NTP)
void syncTimeToNtpServer() {
  Serial.println("Network Time Protocol");
  configTzTime(TZ_INFO, "pool.ntp.org", "time.nist.gov", "time.google.com");  // Configurar NTP

  struct tm timeinfo;
  const int maxRetries = 5;
  int attempt = 0;

  while (attempt < maxRetries) {
    if (getLocalTime(&timeinfo)) {
      // Éxito: imprimir hora formateada y salir
      char formattedDate[30];
      strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", &timeinfo);
      Serial.println("- Date and time set successfully: ");
      Serial.println(formattedDate);
      return;
    }

    // Fallo: mostrar intento fallido y esperar un poco
    Serial.println("WARNING: Attempt ");
    Serial.println(String(attempt + 1));
    Serial.println("failed. Retrying...");
    delay(1000);  // Espera 1 segundo antes de intentar otra vez
    attempt++;
  }

  // Si llegamos aquí, fallaron todos los intentos
  Serial.println("ERROR: Failed to obtain date and time from NTP server after multiple attempts.");
}

// -------------------- Token Firebase --------------------
String getFirebaseToken(const String& urlFirebase, const String& xClientId)
{
  // ---------------------------------------------
  // REQUEST SETUP
  // ---------------------------------------------
  HTTPClient httpJwt;         // HTTP client for the request.
  httpJwt.setTimeout(20000);  // configura una ventana de tiempo mas amplia para la respuesta.
  String httpResponseData = "";
  String authToken = "";

  // Log request details.
  Serial.println("Request details: ");
  Serial.println("-- Server Address: ");
  Serial.println(urlFirebase);
  Serial.println("-- X-CLIENT-ID: ");
  Serial.println(xClientId);

  // Llamada a funcion que intenta varias veces la conexión.
  if (!beginWithRetry(httpJwt, urlFirebase)) {
    return "";  // Falló incluso tras varios intentos
  }

  httpJwt.addHeader("X-CLIENT-ID", xClientId);

  int httpResponseCode = httpJwt.POST("");  // Send the request.

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

  httpJwt.end();  // Free memory resources.

  return authToken;
}

// Funcion que permite reintentar una conexión por si falla.
bool beginWithRetry(HTTPClient& http, const String& url, int maxRetries) {
  int attempts = 0;
  while (attempts < maxRetries) {
    if (http.begin(url)) {
      return true;
    }
    Serial.println("Intento fallido al iniciar conexión HTTP. Reintentando... intento ");
    Serial.println(String(attempts + 1));
    attempts++;
    delay(500);  // Espera entre reintentos
  }

  Serial.println("ERROR: No se pudo establecer conexión HTTP después de varios intentos.");
  return false;
}

// -------------------- Update estado SIMPLE (solo MAC) --------------------
String updateActuatorStateSimple(
  const String& url,
  const String& token,
  const String& targetMac) {
  // ---------------------------------------------
  // REQUEST SETUP
  // ---------------------------------------------
  HTTPClient httpActualizarActuador;         // HTTP client for the request.
  httpActualizarActuador.setTimeout(20000);  // configura una ventana de tiempo mas amplia para la respuesta.
  String httpResponseData = "";
  String authToken = "";

  // Llamada a funcion que intenta varias veces la conexión.
  if (!beginWithRetry(httpActualizarActuador, url)) {
    return "";  // Falló incluso tras varios intentos
  }

  httpActualizarActuador.addHeader("Authorization", "Bearer " + token);
  httpActualizarActuador.addHeader("mac", targetMac);

  int httpResponseCode = httpActualizarActuador.POST("");  // Send the request.

  if (httpResponseCode >= 200 && httpResponseCode < 300) {

    Serial.println("ACTUALIZAR ACTUADOR: Response received.");

    Serial.println("- HTTP Response Code:");
    Serial.println(String(httpResponseCode));

    httpResponseData = httpActualizarActuador.getString();
    if (httpResponseData.length() > 0) {

      Serial.println("- Server Response:");
      Serial.println(String(httpResponseData));

      // aQUÍ SERÍA VER SI TENGO UNA ACCIÓN A REALIZAR DE ABRIR PUERTA
    } else {
      Serial.println("WARNING: Received an empty response from the server.");
    }

  } else {
    Serial.println("ERROR: HTTP request failed. Code: ");
    Serial.println(String(httpResponseCode));
  }

  httpActualizarActuador.end();  // Free memory resources.
}

// -------------------- Stubs (si no los usas, puedes borrarlos) --------------------
String updateActuatorState(
  const String& url,
  const String& token,
  const String& targetMac,
  const String& estado) {
  (void)url; (void)token; (void)targetMac; (void)estado;
  return "";
}

String getActuadoresJson(const String& url, const String& token) {
  (void)url; (void)token;
  return "";
}