#include "Jandula_Base.h"
#include "Jandula_Actuador_Puerta.h"
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

  // Test de los relés
  testReles();

  // Setup del sistema
  setupSistema();
}

/**
 * Función para testear los relés
 */
void testReles()
{
  // Pines de los relés
  const int reles[] = {12, 13};

  // Inicializamos los pines de los relés como salidas y los apagamos
  for (int i = 0; i < 2; i++)
  {
    pinMode(reles[i], OUTPUT);
    digitalWrite(reles[i], LOW);
  }
}

/**
 * Función para ejecutar el bucle principal
 */
void loop()
{
  // Si hay ningún error general se hace un delay de 60 segundos
  if (errorGeneralJandulaBase != "")
  {
    // Pintamos el error por pantalla
    Serial.print("Error general: ");
    Serial.print(errorGeneralJandulaBase);
    Serial.println(" . Esperamos 60 segundos antes de volver a hacer setup");

    // Esperamos 60 segundos
    delay(60000);
    
    // Volvemos a hacer setup
    setupSistema();
  }
  else
  {
    // Validamos si hay acción pendiente
    AccionPendiente accionPendiente = validarSiHayAccionPendiente();

    // Gestionamos la apertura de la puerta si hay una acción pendiente
    gestionarAperturaPuerta(accionPendiente);

    // Esperamos 10 segundos antes de volver a validar si hay acción pendiente
    delay(10000);
  }
}

/**
 * Función que realiza el setup del sistema
 */
void setupSistema()
{
  // Inicializamos la biblioteca Jandula Base
  setupJandulaBase();

  // Inicializamos la biblioteca Jandula Actuador Puerta
  setupJandulaActuadorPuerta();
}