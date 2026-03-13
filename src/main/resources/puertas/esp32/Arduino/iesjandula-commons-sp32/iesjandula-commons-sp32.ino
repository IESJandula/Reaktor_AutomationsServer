#include "Jandula_Base.h"
#include "Jandula_Actuador_Puerta.h"
#include <ArduinoJson.h>
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
    delay(60000);
  }
  else
  {
    // Validamos si hay acción pendiente
    AccionPendiente accionPendiente = validarSiHayAccionPendiente();

    // Gestionamos la apertura de la puerta si hay una acción pendiente
    gestionarAperturaPuerta(accionPendiente);

    // Esperamos 30 segundos antes de volver a validar si hay acción pendiente
    delay(30000);
  }
}