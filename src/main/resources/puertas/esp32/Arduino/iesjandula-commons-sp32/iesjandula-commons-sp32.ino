#include "Jandula_Base.h"
//#include <Jandula_Actuadores.h>
#include <ArduinoJson.h>
#include <stdlib.h>

// Pin del relé de la puerta
#define relePuertaPin 12



// Función para accionar el relé
void accionarRelePuerta(int tiempoMs)
{
  Serial.println("Activando relé puerta");

  digitalWrite(relePuertaPin, HIGH);   // activar relé
  delay(tiempoMs);

  digitalWrite(relePuertaPin, LOW);    // apagar relé

  Serial.println("Relé desactivado");
}

/**
 * Función para inicializar el ESP32
 */
void setup()
{
  // Inicializamos la comunicación serial
  Serial.begin(115200);

  // Esperamos a que se estabilice la comunicación serial
  delay(200);

  // Configuramos los pines de los LEDs
  pinMode(offlinePin, OUTPUT);
  pinMode(onlinePin, OUTPUT);
  pinMode(sdCardLed, OUTPUT);
  pinMode(littleFSLed, OUTPUT);

  // Inicializamos la biblioteca Jandula Base
  setupJandulaBase();
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
  {/*
    Serial.println("\nHeartbeat (ON)...");
    Serial.print("MAC: ");
    Serial.println(miMac);

    // Preguntar al backend si hay acción
    String accionId = updateActuatorStateSimple(actuadorEstadoUrl, token, miMac);

    Serial.println("Respuesta servidor (accionId):");
    Serial.println(accionId);

    if (accionId != "" && accionId != "null")
    {
      Serial.print("Ejecutando acción...");
      Serial.println(accionId);

      // Abrir puerta con relé
      accionarRelePuerta(4000);

      // Convierto el string accionId a long
      long accionIdLong = strtol(accionId.c_str(), NULL, 10);

      // Notificar resultado al backend
      updateAccionEstado(accionEstadoUrl, token, accionIdLong, "finalizado_ok", "Puerta abierta correctamente");
    }
*/
    // Espera antes del siguiente heartbeat
    delay(30000);
  }
}