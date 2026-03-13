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

  // Inicializamos la biblioteca Jandula Base
  setupJandulaBase();

  // Inicializamos la biblioteca Jandula Actuador Puerta
  setupJandulaActuadorPuerta();
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

  // Realizamos un encendido secuencial de los relés
  for (int i = 0; i < 2; i++)
  {
    digitalWrite(reles[i], HIGH);
    delay(500);
  }

  // Mantenemos todos los relés encendidos durante 1 segundo.
  delay(1000);

  // Realizamos un apagado secuencial de los relés
  for (int i = 0; i < 2; i++)
  {
    digitalWrite(reles[i], LOW);
    delay(500);
  }
 
  // Hacemos una pausa antes de repetir.
  delay(1000);  
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