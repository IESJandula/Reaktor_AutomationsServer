#include "Controller.h"

String WifiSSID;
String WifiPassword;
String wifiConfigFilePath = "/wifiConfig.txt";
String wifiConfigMetadataFilePath = "/wifiConfigMetadata.txt";

bool littleFSInitialized = false;
bool sdCardInitialized = false;

bool initializeLittleFS() {
  if (!LittleFS.begin()) {
    LittleFS.begin(true);
  }
  digitalWrite(littleFSLed, HIGH);
  return true;
}

bool initializeSDCard() {
  SPI.begin();
  if (!SD.begin()) return false;
  digitalWrite(sdFSLed, HIGH);
  return true;
}

void compareAndCopy(String filePath, String metadataFilePath, String fileName) {
  if (SD.exists(filePath)) {
    File sdFile = SD.open(filePath, "r");
    File localFile = LittleFS.open(filePath, "w");
    while (sdFile.available()) {
      localFile.write(sdFile.read());
    }
    sdFile.close();
    localFile.close();
  }
}

void loadConfigFromFile(String configFilePath) {
  File configFile = LittleFS.open(configFilePath, "r");
  while (configFile.available()) {
    String line = configFile.readStringUntil('\n');
    line.trim();
    if (line.startsWith("SSID=")) {
      WifiSSID = line.substring(5);
    } else if (line.startsWith("PASSWORD=")) {
      WifiPassword = line.substring(9);
    }
  }
  configFile.close();
}

void connectToWifi() {

  const char* ssid = "Buscando...";
  const char* password = "Obi-WANKenobi1113";

  Serial.print("Conectando a WiFi: ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

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

// pedir token al FirebaseServer
String getFirebaseToken(const String& urlFirebase, const String& xClientId, bool insecureTLS) {

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("❌ getFirebaseToken: WiFi no conectado");
    return "";
  }

  if (urlFirebase.length() == 0) {
    Serial.println("❌ getFirebaseToken: urlFirebase vacía");
    return "";
  }

  if (xClientId.length() == 0) {
    Serial.println("❌ getFirebaseToken: xClientId vacío");
    return "";
  }

  HTTPClient http;
  http.setTimeout(20000);

  int code = -1;
  String token = "";

  // HTTPS
  if (urlFirebase.startsWith("https://")) {
    WiFiClientSecure client;
    if (insecureTLS) client.setInsecure();

    if (!http.begin(client, urlFirebase)) {
      Serial.println("❌ getFirebaseToken: http.begin(HTTPS) falló");
      return "";
    }
  }
  // HTTP
  else {
    if (!http.begin(urlFirebase)) {
      Serial.println("❌ getFirebaseToken: http.begin(HTTP) falló");
      return "";
    }
  }

  http.addHeader("X-CLIENT-ID", xClientId);

  code = http.POST("");

  if (code >= 200 && code < 300) {
    token = http.getString();
    token.trim();

    Serial.print("✅ Token recibido. HTTP ");
    Serial.println(code);
  } else {
    Serial.print("❌ Error pidiendo token. HTTP ");
    Serial.println(code);

    String body = http.getString();
    if (body.length()) {
      Serial.println("Respuesta:");
      Serial.println(body);
    }
  }

  http.end();
  return token;
}

// ✅ NUEVA: enviar SOLO MAC
String updateActuatorStateSimple(
  const String& url,
  const String& token,
  const String& targetMac,
  bool insecureTLS
) {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("❌ updateActuatorStateSimple: WiFi no conectado");
    return "";
  }
  if (url.length() == 0) {
    Serial.println("❌ updateActuatorStateSimple: URL vacía");
    return "";
  }
  if (token.length() == 0) {
    Serial.println("❌ updateActuatorStateSimple: token vacío");
    return "";
  }
  if (targetMac.length() == 0) {
    Serial.println("❌ updateActuatorStateSimple: mac vacía");
    return "";
  }

  HTTPClient http;
  http.setTimeout(20000);

  bool okBegin = false;

  if (url.startsWith("https://")) {
    WiFiClientSecure client;
    if (insecureTLS) client.setInsecure();
    okBegin = http.begin(client, url);
  } else {
    okBegin = http.begin(url);
  }

  if (!okBegin) {
    Serial.println("❌ updateActuatorStateSimple: http.begin falló");
    return "";
  }

  http.addHeader("Authorization", "Bearer " + token);
  http.addHeader("mac", targetMac);

  Serial.println("== POST /actuador/estado (simple) ==");
  Serial.print("URL: "); Serial.println(url);
  Serial.print("mac: "); Serial.println(targetMac);

  int code = http.POST("");
  String response = http.getString();

  Serial.print("HTTP: ");
  Serial.println(code);

  if (response.length()) {
    Serial.println("Body:");
    Serial.println(response);
  }

  http.end();
  return response;
}
