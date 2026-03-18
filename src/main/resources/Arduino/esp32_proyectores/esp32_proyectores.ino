#include "Jandula_Base.h"
#include "Jandula_Actuador_Proyector.h"
#include <stdlib.h>

/**
 * Función para inicializar el ESP32
 */
void setup()
{
  // Inicializamos la comunicación serial
  Serial.begin(115200);

  // Esperamos a que se estabilice la comunicación serial
  delay(200);

  // Inicializamos la biblioteca Jandula Base
  setupJandulaBase();

  // Inicializamos el puerto serie del proyector
  MySerial.begin(RS232BaudRate, SERIAL_8N1, rxRS232Port, txRS232Port);  // baudrate, config, RX pin, TX pin

  // Inicializamos la biblioteca Jandula Actuador Puerta
  setupJandulaActuadorPuerta();
}

/**
 * Función para ejecutar el bucle principal
 */
void loop()
{
  // Si hay ningún error general se hace un delay de 60 segundos
  if (errorGeneralJandulaBase != "")
  {
    Serial.print("Error general: ");
    Serial.println(errorGeneralJandulaBase);
    delay(60000);
  }
  else
  {
    // Validamos si hay acción pendiente
    AccionPendiente accionPendiente = validarSiHayAccionPendiente();

    // Gestionamos la acción sobre el proyector si hay una acción pendiente
    gestionarAccionProyector(accionPendiente);

    // Esperamos 15 segundos antes de volver a validar si hay acción pendiente
    delay(15000);
  }
}