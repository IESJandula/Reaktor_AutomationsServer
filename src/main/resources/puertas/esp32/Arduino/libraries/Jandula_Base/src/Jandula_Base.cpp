#include "Jandula_Base.h"
#include <ArduinoJson.h>

/*******************************************************/
/****************** Variables globales *****************/
/*******************************************************/

// WifiSSID: sirve para almacenar el SSID de la red WiFi
String WifiSSID;

// WifiPassword: sirve para almacenar la contraseña de la red WiFi
String WifiPassword;

// firebaseUrl: sirve para almacenar la URL de Firebase
String firebaseUrl;

// actuadorEstadoUrl: sirve para almacenar la URL del estado del actuador
String actuadorEstadoUrl;

// accionEstadoUrl: sirve para almacenar la URL de la acción del estado
String accionEstadoUrl;

// xClientId: sirve para almacenar el ID del cliente de Firebase
String xClientId;

// littleFSInitialized: sirve para indicar si el sistema de archivos LittleFS está inicializado
bool littleFSInitialized = false;

// sdCardInitialized: sirve para indicar si la tarjeta SD está inicializada
bool sdCardInitialized = false;

// tokenPropio: sirve para almacenar el token propio
String tokenPropio = "";

// tokenExp: sirve para almacenar la fecha de expiración del token
unsigned long tokenExp = 0;

/*******************************************************/
/****************** Parse de config SD *****************/
/*******************************************************/

/**
 * Parsea una línea de configuración de la tarjeta SD
 * 
 * @param lineRaw: línea de configuración
 * @return void
 */
static void parseConfigLineSD(const String& lineRaw) {
  // Convertimos la línea de configuración a una cadena de caracteres
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

/***************************************************************************/
/*** Funciones relacionadas con la inicialización de los compoentes base ***/
/***************************************************************************/

/**
 * Realiza la inicialización de los diferentes componentes de la biblioteca
 * 
 * @return void
 */
 void setupJandulaBase()
 {
   // Validamos si la tarjeta SD se inicializa correctamente
   tarjetaSDInicializada = inicializarSDCard() ;
 
   // Si la tarjeta SD se inicializa correctamente, procedemos a la comparación y copia de los archivos
   if (tarjetaSDInicializada)
   {
     // Comparamos y copiamos el archivo de configuración más reciente
     compararYCopiar(rutaFicheroConfiguracion);
   
     // Desmontamos la tarjeta SD después de procesar
     SD.end();
 
     // Apagamos el LED de la tarjeta SD
     tarjetaSDDesconectada();
   }
 
   // Conectamos a la red WiFi
   conectarWiFi();
 
   // Si la conexión a la red WiFi es correcta ...
   if (WiFi.status() == WL_CONNECTED)
   {
     // ... entonces sincronizamos la hora con el servidor NTP
     sincronizarHoraConServidorNTP();
 
     // Obtenemos la dirección MAC de la tarjeta WiFi
     miMac = WiFi.macAddress();
   
     // Mostramos la dirección MAC de la tarjeta WiFi
     Serial.print("MAC guardada: ");
     Serial.println(miMac);
 
     // Obtenemos el token de Firebase
     token = getFirebaseToken(firebaseUrl, xClientId);

     // Mostramos el token de Firebase
     Serial.print("Token guardado: ");
     Serial.println(token);
   }
 }
 
 /**
  * Inicializa la tarjeta SD
  * 
  * @return true si la tarjeta SD se inicializa correctamente, false en caso contrario
  */
 bool inicializarSDCard()
 {
   bool inicializacionCorrecta = false;
 
   // Mostramos un mensaje de inicio de inicialización
   Serial.println("INFO: Comenzando la inicialización de la tarjeta SD");
   
   // Inicializamos la SPI que es la interfaz de comunicación con la tarjeta SD
   // sdCardClock: pin para el reloj de la tarjeta SD
   // sdCardMISO: pin para la entrada de datos de la tarjeta SD
   // sdCardMOSI: pin para la salida de datos de la tarjeta SD
   // sdCardChipSelect: pin para el chip select de la tarjeta SD
   SPI.begin(sdCardClock, sdCardMISO, sdCardMOSI, sdCardChipSelect);
 
   // Validamos si la tarjeta SD se inicializa correctamente
   inicializacionCorrecta = !SD.begin(sdCardChipSelect);
 
   // Si la tarjeta SD no se inicializa correctamente ...
   if (!iniciacionCorrecta)
   {
     // Mostramos un mensaje de advertencia
     Serial.println("WARNING: No se ha detectado ninguna tarjeta SD");
   }
   else
   {
     // Mostramos un mensaje de información
     Serial.println("INFO: La tarjeta SD se ha montado correctamente");
 
     // Verificamos la accesibilidad de la tarjeta SD abriendo el directorio raíz
     File testFile = SD.open("/");
 
     // Validamos si la tarjeta SD es accesible
     iniciacionCorrecta = testFile != NULL;
 
     // Si la tarjeta SD no es accesible ...
     if (!iniciacionCorrecta)
     {
       // Mostramos un mensaje de error
       Serial.println("ERROR: No se ha podido acceder a la tarjeta SD");
     
       // Cerramos la tarjeta SD
       SD.end();
     }
     else
     {
       // Mostramos un mensaje de información
       Serial.println("INFO: Se ha podido acceder a la tarjeta SD");
 
       // Cerramos el archivo de prueba
       testFile.close();
 
       // Indica que la tarjeta SD está conectada
       tarjetaSDConectada();
     }
   }
 
   return inicializacionCorrecta;
 }
 
/**
 * Compara y copia un fichero del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @param rutaFichero: ruta del fichero a copiar
 * @return void
 */
void compararYCopiar(const String& rutaFichero)
{
  // Validamos si el fichero existe en el sistema de archivos LittleFS
  bool ficheroLocalLitleFSEncontrado = validarExistenciaFicheroLittleFS(rutaFichero);
  
  // Validamos si el fichero existe en la tarjeta SD
  bool ficheroSDEncontrado = validarExistenciaFicheroSD(rutaFichero);

  // Si el fichero local existe y el fichero en la tarjeta SD existe ...
  if (ficheroLocalLitleFSEncontrado && ficheroSDEncontrado)
  {
    // Abrimos ambos ficheros de la tarjeta SD para comparar los timestamps de modificación
    File ficheroSD = SD.open(rutaFichero, "r");

    // Si no se puede abrir el archivo de la tarjeta SD ...
    if (!ficheroSD)
    {
      // ... mostramos un mensaje de error
      Serial.print("ERROR: No se pudo abrir el fichero de la tarjeta SD");
    }
    else
    {
      // Obtenemos los timestamps de modificación para ambos archivos
      unsigned long estampaTiempoLocalFicheroLittleFS = obtenerTimestampDesdeArchivo(rutaFichero);
      unsigned long estampaTiempoFicheroSD            = ficheroSD.getLastWrite();

      // Cerramos el fichero de la tarjeta SD
      ficheroSD.close();

      // Si los timestamps de modificación son diferentes, comparamos y copiamos el fichero de la tarjeta SD al sistema de archivos LittleFS
      compararYCopiarAmbosFicherosExisten(rutaFichero, estampaTiempoLocalFicheroLittleFS, estampaTiempoFicheroSD);
    }
  }
  else if (ficheroSDEncontrado)
  {
    // Si el fichero en la tarjeta SD existe, ...
    // ... copiamos el fichero de la tarjeta SD al sistema de archivos LittleFS
    copiarArchivoDeSDALittleFS(rutaFichero);
    
    // Mostramos el timestamp de modificación del fichero en la tarjeta SD
    Serial.print("INFO: Nueva estampa de tiempo del fichero en la tarjeta SD: ");
    Serial.println(obtenerFechaYHoraDesdeTimestamp(estampaTiempoFicheroSD));
  }
  else
  {
    // Si el fichero local no existe y el fichero en la tarjeta SD no existe, mostramos un mensaje de error y terminamos la función
    Serial.print("ERROR: No se encontró el fichero en el sistema de archivos LittleFS ni en la tarjeta SD");
  }
}

/**
 * Valida si el fichero existe en el sistema de archivos LittleFS
 * 
 * @param rutaFichero: ruta del fichero
 * @return true si el fichero existe en el sistema de archivos LittleFS, false en caso contrario
 */
bool validarExistenciaFicheroLittleFS(const String& rutaFichero)
{
  // Validamos si el fichero existe en el sistema de archivos LittleFS
  bool ficheroLocalLitleFSEncontrado = LittleFS.exists(rutaFichero);

  // Mostramos si el fichero existe en el sistema de archivos LittleFS
  Serial.print("INFO: Fichero local en LittleFS: ");
  Serial.println(String(ficheroLocalLitleFSEncontrado));

  // Devolvemos el resultado de la validación
  return ficheroLocalLitleFSEncontrado;
}

/**
 * Valida si el archivo existe en la tarjeta SD
 * 
 * @param filePath: ruta del archivo
 * @return true si el archivo existe en la tarjeta SD, false en caso contrario
 */
bool validarExistenciaFicheroSD(const String& filePath)
{
  // Validamos si el archivo existe en la tarjeta SD
  bool ficheroSDEncontrado = SD.exists(filePath);

  // Mostramos si el archivo existe en la tarjeta SD
  Serial.print("INFO: Fichero en SD: ");
  Serial.println(String(ficheroSDEncontrado));

  // Devolvemos el resultado de la validación
  return ficheroSDEncontrado;
}

/**
 * Obtiene el timestamp de modificación de un archivo en el sistema de archivos LittleFS
 * 
 * @param filePath: ruta del archivo
 * @return timestamp: timestamp del archivo
 */
 unsigned long obtenerTimestampDesdeArchivo(String filePath)
 {
   // Valor por defecto para el timestamp (0 indica que no se encontró)
   unsigned long timestamp = 0;
 
   // Abrimos el archivo en modo lectura
   File fileLittleFS = LittleFS.open(filePath, "r");
 
   // Si no se puede abrir el archivo ...
   if (!fileLittleFS)
   {
     // ... mostramos un mensaje de error
     Serial.print("ERROR: No se pudo abrir el archivo: ");
     Serial.println(filePath);
   }
   // Si el archivo tiene contenido ...
   else if (fileLittleFS.available())
   {
     // ... leemos el timestamp de modificación del archivo
     timestamp = fileLittleFS.parseInt();
 
     // Cerramos el archivo
     fileLittleFS.close();
   }
   else
   {
     // ... mostramos un mensaje de advertencia
     Serial.print("WARNING: No se encontró timestamp en el archivo: ");
     Serial.println(filePath);
   }
 
   // Devolvemos el timestamp
   return timestamp;
}

/**
 * Compara y copia un fichero del sistema de archivos LittleFS a la tarjeta SD
 * 
 * @param rutaFichero: ruta del fichero
 * @param estampaTiempoLocalFicheroLittleFS: timestamp de modificación del fichero en el sistema de archivos LittleFS
 * @param estampaTiempoFicheroSD: timestamp de modificación del fichero en la tarjeta SD
 * @return void
 */
void compararYCopiarAmbosFicherosExisten(const String& rutaFichero, unsigned long estampaTiempoLocalFicheroLittleFS, unsigned long estampaTiempoFicheroSD)
{
  // Mostramos el timestamp de modificación del fichero en el sistema de archivos LittleFS
  Serial.print("INFO: Última modificación del fichero local: ");
  Serial.println(obtenerFechaYHoraDesdeTimestamp(estampaTiempoLocalFicheroLittleFS));

  // Mostramos el timestamp de modificación del fichero en la tarjeta SD
  Serial.print("INFO: Última modificación del fichero en la tarjeta SD: ");
  Serial.println(obtenerFechaYHoraDesdeTimestamp(estampaTiempoFicheroSD));

  // Si la tarjeta SD tiene un timestamp de modificación más reciente que 
  // el fichero en el sistema de archivos LittleFS, entonces ...
  if (estampaTiempoFicheroSD > estampaTiempoLocalFicheroLittleFS)
  {
    // ... copiamos el archivo de la tarjeta SD al sistema de archivos LittleFS
    copiarArchivoDeSDALittleFS(rutaFichero);

    // Mostramos un mensaje de resultado
    Serial.print("INFO: Copiado el archivo de la tarjeta SD al sistema de archivos LittleFS");
  }
  else
  {
    // Si el archivo en el sistema de archivos LittleFS tiene u
    // un timestamp de modificación más reciente que el archivo en la tarjeta SD ó ...
    // ... si tiene ambos el mismo timestamp de modificación, entonces no hay que hacer nada
    Serial.print("INFO: Los timestamps de modificación son iguales");
  }
}

/**
 * Copia un archivo de la tarjeta SD al sistema de archivos LittleFS
 * 
 * @param filePath: ruta del archivo
 * @return void
 */
void copiarArchivoDeSDALittleFS(const String& filePath)
{
  // Abrimos el archivo de la tarjeta SD para lectura
  File fileSD = SD.open(filePath, "r");

  // Si no se puede abrir el archivo de la tarjeta SD ...
  if (!fileSD)
  {
    // ... mostramos un mensaje de error
    Serial.print("ERROR: No se pudo abrir el archivo de la tarjeta SD");
  }
  else
  {
    // Abrimos el archivo en el sistema de archivos LittleFS para escritura
    File localFile = LittleFS.open(filePath, "w");

    // Si no se puede abrir el fichero en el sistema de archivos LittleFS ...
    if (!localFile)
    {
      // ... cerramos el fichero de la tarjeta SD
      fileSD.close();

      // Mostramos un mensaje de error
      Serial.print("ERROR: No se pudo abrir el fichero en el sistema de archivos LittleFS");
    }
    else
    {
      // Copiamos el fichero de la tarjeta SD al sistema de archivos LittleFS
      while (fileSD.available())
      {
        // Copiamos el contenido del fichero de la tarjeta SD al sistema de archivos LittleFS
        localFile.write(fileSD.read());
      }

      // Cerramos el fichero de la tarjeta SD
      fileSD.close();

      // Cerramos el fichero en el sistema de archivos LittleFS
      localFile.close();
    }
  }
}

// ---------------------------------------------
// Writes a given timestamp to a file on LittleFS, overwriting any existing content.
// Logs success or error depending on the file operation result.
// ---------------------------------------------
void writeTimestampToFile(unsigned long timestamp, String filePath) {
  // Open file in write mode, which will overwrite existing data
  File file = LittleFS.open(filePath, "w");

  if (!file) {
    debugln("ERROR: Failed to open file '" + filePath + "' for writing.");
    return;
  }

  // Write the timestamp to the file
  file.println(timestamp);

  // Close the file
  file.close();

  printInterfaceSentences("INFO: Timestamp successfully written to file '", filePath);
}

/**
 * Convierte un timestamp UNIX a una cadena de fecha y hora formateada
 * 
 * @param timestamp: timestamp UNIX
 * @return fechaYHoraFormateada: cadena de fecha y hora formateada
 */
String obtenerFechaYHoraDesdeTimestamp(unsigned long timestamp)
{
  // Convertimos el timestamp UNIX a un tiempo_t
  time_t timeStamp = (time_t)timestamp;

  // Convertimos el tiempo_t a una estructura tm
  struct tm* timeinfo = localtime(&timeStamp);

  // Formateamos la fecha y hora
  char fechaYHoraFormateada[30];
  strftime(fechaYHoraFormateada, sizeof(fechaYHoraFormateada), "%Y-%m-%d %H:%M:%S", timeinfo);

  // Devolvemos la fecha y hora formateada
  return fechaYHoraFormateada;
}
 
/**
 * Carga la configuración desde el archivo de configuración
 * 
 * @param configFilePath: ruta del archivo de configuración
 * @return void
 */
void loadConfigFromFile(String configFilePath) {
 
   File configFile = LittleFS.open(configFilePath, "r");
 
   if (!configFile) {
     printInterfaceSentence("ERROR: Failed to open Configuration file for reading.");
     return;  // return here to avoid further code execution
   }
 
   while (configFile.available()) {
     String line = configFile.readStringUntil('\n');
     line.trim();
 
     if (line.startsWith("SSID=")) {
       WifiSSID = line.substring(5);
     } else if (line.startsWith("PASSWORD=")) {
       WifiPassword = line.substring(9);
     } else if (line.startsWith("URL_PROJECTORS=")) {
       urlProjectors = line.substring(15);
     } else if (line.startsWith("URL_FIREBASE=")) {
       urlFirebase = line.substring(13);
     }
   }
   configFile.close();
}

/*********************************************************************/
/******* Funciones relacionadas con la conexión a la red WiFi ********/
/*********************************************************************/

/**
  * Conecta el ESP32 a la red WiFi.
  * 
  * @return void
  */
void conectarWiFi()
{
  // Si el SSID o la contraseña no están configurados, se muestra un mensaje de error y se apagan los LEDs de online y offline
  if (WifiSSID.length() == 0 || WifiPassword.length() == 0)
  {
    // Mostramos un mensaje de error
    Serial.println("ERROR: SSID/PASSWORD vacíos (no se cargó configuración de la red WiFi)");

    // Mostramos un mensaje de conexión incorrecta a tráves de los leds
    conexionIncorrecta();
  }
  else
  {
    // Mostramos un mensaje de inicio de conexión
    Serial.println("INFO: Iniciando conexión a la red WiFi");

    // Conectamos a la red WiFi
    WiFi.mode(WIFI_STA);
    WiFi.begin(WifiSSID.c_str(), WifiPassword.c_str());

    // Inicializamos el contador de intentos
    int intentos = 0;

    // Intentamos conectar a la red WiFi
    while (WiFi.status() != WL_CONNECTED && intentos < 30)
    {
      // Mostramos un punto de espera
      Serial.print(".");

      // Incrementamos el contador de intentos
      intentos++;

      // Esperamos el tiempo de delay antes de reintentar
      delay(INTENTOS_DELAY);
    }

    // Si no se conecta a la red WiFi, se muestra un mensaje de error y se apagan los LEDs de online y offline
    if (WiFi.status() != WL_CONNECTED)
    {
      // Mostramos un mensaje de error
      Serial.println("ERROR: No se pudo conectar a la red WiFi");

      // Mostramos un mensaje de conexión incorrecta a tráves de los leds
      conexionIncorrecta();
    }
    else
    {
      // Mostramos un mensaje de conexión correcta
      Serial.println("INFO: Conectado a la red WiFi");

      // Mostramos la IP de la red WiFi
      Serial.print("IP: ");
      Serial.println(WiFi.localIP());

      // Mostramos un mensaje de conexión correcta a tráves de los leds
      conexionCorrecta();
    }
  }
}

/*********************************************************************/
/***** Funciones relacionadas con la sincronización de la hora *******/
/*********************************************************************/

/**
 * Sincroniza la hora con el servidor NTP.
 * 
 * @return void
 * 
 * @note Si la sincronización se realiza correctamente, se muestra un mensaje de sincronización correcta.
 * @note Si la sincronización no se realiza correctamente, se muestra un mensaje de error y se apagan los LEDs de online y offline.
 */
void sincronizarHoraConServidorNTP()
{
  bool sincronizacionRealizada = false;

  // Mostramos un mensaje de inicio de sincronización
  Serial.println("INFO: Iniciando sincronización de la hora con el servidor NTP");

  // Iniciamos la sincronización de la hora con el servidor NTP
  configTzTime(TZ_INFO, "pool.ntp.org", "time.nist.gov", "time.google.com");

  // Inicializamos la variable de sincronización realizada
  struct tm timeinfo;

  // Inicializamos el contador de intentos
  int intentos = 0;

  // Intentamos sincronizar la hora con el servidor NTP
  while (!sincronizacionRealizada && intentos < MAXIMO_INTENTOS_HTTP)
  {
    // Si se obtiene la fecha y hora correctamente, se sale del bucle
    sincronizacionRealizada = getLocalTime(&timeinfo);

    // Si no se obtiene la fecha y hora correctamente, se reintenta la sincronización
    if (!sincronizacionRealizada)
    {
      // Mostramos un mensaje de error y se incrementa el contador de intentos
      Serial.print(".");

      // Incrementamos el contador de intentos
      intentos++;

      // Esperamos el tiempo de delay antes de reintentar
      delay(INTENTOS_DELAY);
    }
    else
    {
      // Inicializamos la variable de fecha y hora formateada
      char formattedDate[30];

      // Formateamos la fecha y hora
      // %Y: Año (4 dígitos)
      // %m: Mes (2 dígitos)
      // %d: Día (2 dígitos)
      // %H: Hora (2 dígitos)
      // %M: Minuto (2 dígitos)
      // %S: Segundo (2 dígitos)
      strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", &timeinfo);

      // Mostramos el mensaje de sincronización correcta
      Serial.println("INFO: Fecha y hora sincronizada correctamente: " + String(formattedDate));
    }
  }

  // Si finalmente no se ha podido obtener la fecha y hora del servidor NTP,
  // se muestra un mensaje de error y se apagan los LEDs de online y offline
  if (!sincronizacionRealizada)
  {
    // Mostramos un mensaje de error
    Serial.println("ERROR: No se ha podido obtener la fecha y hora del servidor NTP después de varios intentos.");

    // Mostramos un mensaje de sincronización incorrecta a tráves de los leds
    conexionIncorrecta();
  }
}

/*********************************************************************/
/******** Funciones relacionadas con el Token Firebase JWT ***********/
/*********************************************************************/

/**
 * Obtiene el token JWT válido de Firebase.
 * 
 * @param urlFirebase URL de Firebase.
 * @param xClientId ID del cliente.
 * 
 * @return Token JWT válido.
 * 
 * @note Si el token no existe o ha expirado, se solicita un nuevo token.
 * @note Si el token se obtiene correctamente, se actualiza el tiempo de expiración.
 * @note Si el token no se obtiene correctamente, se retorna un token vacío.
 * @note Si el token se obtiene correctamente, se retorna el token.
 * @note Si el token no se obtiene correctamente, se retorna un token vacío.
*/
String obtenerTokenJWTValido(const String& urlFirebase, const String& xClientId)
{
  if (tokenPropio == "" || tokenExpirado())
  {
    // Mostramos un mensaje de advertencia
    Serial.println("WARNING: JWT inexistente o expirado. Solicitando nuevo token...");

    // Mostramos un mensaje de sincronización incorrecta a tráves de los leds
    conexionIncorrecta();

    // Obtenemos el token de Firebase
    tokenPropio = getFirebaseToken(urlFirebase, xClientId);

    // Si el token se obtiene correctamente, se actualiza el tiempo de expiración
    if (tokenPropio != "")
    {
      // Obtenemos la expiración del token
      tokenExp = obtenerExpiracionJWT(tokenPropio);
    }
  }

  return tokenPropio;
}

/**
 * Verifica si el token JWT ha expirado.
 * 
 * @return true si el token ha expirado, false en caso contrario.
 * 
 * @note Si el token no tiene tiempo de expiración, se retorna true.
 * @note Si el token tiene tiempo de expiración y ha expirado, se retorna true.
 * @note Si el token tiene tiempo de expiración y no ha expirado, se retorna false.
*/
bool tokenExpirado()
{
  bool expirado = false;

  // Se obtiene el tiempo actual en segundos
  unsigned long now = millis() / 1000;

  // Se valida si el token ha expirado
  expirado = (tokenExp == 0) || (now >= tokenExp);

  // Si el token ha expirado, se muestra un mensaje de advertencia
  if (expirado)
  {
    Serial.println("WARNING: Token JWT expirado");
  }

  // Devolvemos true si el token ha expirado, false en caso contrario
  return expirado;
}

/**
 * Obtiene el token JWT de Firebase.
 * 
 * @param urlFirebase URL de Firebase.
 * @param xClientId ID del cliente.
 * @return Token JWT de Firebase.
 * 
 * @note Si el token no se obtiene correctamente, se retorna un token vacío.
 * @note Si el token se obtiene correctamente, se retorna el token.
 * @note Si el token no se obtiene correctamente, se retorna un token vacío.
*/
String getFirebaseToken(const String& urlFirebase, const String& xClientId)
{
  String tokenJWT = "";

  // Mostramos un mensaje de inicio de obtención del token de Firebase
  Serial.println("INFO: Iniciando obtención del token de Firebase...");

  // Iniciamos la conexión HTTP
  HTTPClient httpJwt;
  httpJwt.setTimeout(20000);

  // Inicializamos el dato de respuesta HTTP
  String httpResponseData = "";

  // Mostramos los detalles de la petición
  Serial.println("INFO: Detalles de la petición: ");
  Serial.print("-- Dirección del servidor: ");
  Serial.println(urlFirebase);
  Serial.print("-- X-CLIENT-ID: ");
  Serial.println(xClientId);

  // Si se puede iniciar la conexión HTTP, se obtiene el token
  if (iniciarConexionHTTPConReintentos(httpJwt, urlFirebase))
  {
    // Añadimos el encabezado de cliente
    httpJwt.addHeader("X-CLIENT-ID", xClientId);

    // Realizamos la petición POST
    int httpResponseCode = httpJwt.POST("");

    // Si la respuesta no está en el rango del 200 al 299, es incorrecta
    if (httpResponseCode < 200 || httpResponseCode >= 300)
    {
      // Mostramos los detalles de la respuesta
      Serial.println("ERROR: La petición HTTP ha fallado. Código: ");
      Serial.println(String(httpResponseCode));
    }
    else
    {
      // Obtenemos el token de la respuesta
      httpResponseData = httpJwt.getString();

      // Si el token es vacío, se muestra un mensaje de advertencia
      if (httpResponseData.length() == 0)
      {
        // Mostramos un mensaje de advertencia
        Serial.println("WARNING: Se ha recibido una respuesta vacía del servidor");
      }
      else
      {
        // Mostramos los detalles de la respuesta
        Serial.println("INFO: Respuesta del servidor:");
        Serial.println(String(httpResponseData));

        // Asignamos el token a la variable
        tokenJWT = httpResponseData;
      }
    }
  }

  // Cerramos la conexión HTTP
  httpJwt.end();

  // Devolvemos el token de autenticación
  return tokenJWT;
}

/**
 * Inicia una conexión HTTP con reintentos.
 * 
 * @param http Cliente HTTP.
 * @param url URL de la petición.
 * @return true si la conexión se establece correctamente, false en caso contrario.
 */
bool iniciarConexionHTTPConReintentos(HTTPClient& http, const String& url)
{
  // Inicializamos la variable de conexión establecida
  bool conexionEstablecida = false;

  // Inicializamos el contador de intentos
  int intentos = 0;

  // Intentamos establecer la conexión HTTP con reintentos
  while (!conexionEstablecida && intentos < MAXIMO_INTENTOS_HTTP)
  {
    // Intentamos establecer la conexión HTTP
    conexionEstablecida = http.begin(url);

    // Si la conexión se establece correctamente, se sale del bucle
    if (!conexionEstablecida)
    {
      // Si la conexión no se establece correctamente, se muestra un punto de espera que indica un intento fallido
      Serial.print(".");
      
      // Incrementamos el contador de intentos
      intentos++;

      // Esperamos el tiempo de delay antes de intentar nuevamente
      delay(INTENTOS_DELAY);
    }
  }

  if (!conexionEstablecida)
  {
    // Mostramos un mensaje de error
    Serial.println("ERROR: No se pudo establecer conexión HTTP después de varios intentos");
  }

  // Devolvemos true si la conexión se establece correctamente
  return conexionEstablecida;
}

/**
 * Obtiene la expiración del token JWT.
 * 
 * @param token Token JWT.
 * @return Expiración del token JWT.
 * 
 * @note Si el token no es válido, se retorna 0.
 * @note Si el token es válido, se retorna la expiración del token.
*/
unsigned long obtenerExpiracionJWT(String token)
{
  // Inicializamos la variable de expiración del token
  unsigned long exp = 0;

  // Mostramos un mensaje de inicio de obtención de la expiración del token
  Serial.println("INFO: Iniciando obtención de la expiración del token...");

  // Obtenemos el payload del token
  int firstDot = token.indexOf('.');
  int secondDot = token.indexOf('.', firstDot + 1);

  // Si el token no es válido, se devuelve 0
  if (firstDot < 0 || secondDot < 0)
  {
    // Mostramos un mensaje de error
    Serial.println("ERROR: JWT mal formado");

    // Mostramos un mensaje de sincronización incorrecta a tráves de los leds
    conexionIncorrecta();
  }
  else
  {
    // Obtenemos el payload del token
    String payload = token.substring(firstDot + 1, secondDot);

    // Decodificamos el payload
    String decodedPayload = base64::decode(payload);

    // Deserializamos el payload
    DynamicJsonDocument doc(512);
    deserializeJson(doc, decodedPayload);

    // Obtenemos la expiración del token
    exp = doc["exp"];

    // Mostramos la expiración del token
    Serial.println("INFO: JWT expira en:");
    Serial.println(String(exp));

    // Mostramos un mensaje de sincronización correcta a tráves de los leds
    conexionCorrecta();
  }

  return exp;
}

/*********************************************************************/
/**************** Funciones relacionadas con los LEDs ****************/
/*********************************************************************/

/**
 * Enciende el LED de online.
 * 
 * @return void
 */
void conexionOnlineCorrecta()
{
  // Encendemos el LED de online
  digitalWrite(onlinePin, HIGH);

  // Apagamos el LED de offline
  digitalWrite(offlinePin, LOW);
}

/**
 * Apaga el LED de online.
 * 
 * @return void
 */
void conexionOnlineIncorrecta()
{
  // Apagamos el LED de online
  digitalWrite(onlinePin, LOW);

  // Encendemos el LED de offline
  digitalWrite(offlinePin, HIGH);
}

/*********************************************************************/
/******** Funciones relacionadas con la conexión a la SD *************/
/*********************************************************************/

/**
 * Indica que la tarjeta SD está conectada.
 * 
 * @return void
 */
void tarjetaSDConectada()
{
  // Encendemos el LED de la tarjeta SD
  digitalWrite(tarjetaSDLed, HIGH);
}

/**
 * Indica que la tarjeta SD está desconectada.
 * 
 * @return void
 */
void tarjetaSDDesconectada()
{
  // Apagamos el LED de la tarjeta SD
  digitalWrite(tarjetaSDLed, LOW);
}