#include "Jandula_Base.h"
#include "Jandula_Actuador_Puerta.h"
#include <stdlib.h>

// tiempo del último reintento de setup en milisegundos
static unsigned long ultimoReintentoSetup = 0;

// tiempo del último check de acciones en milisegundos
static unsigned long ultimoCheckAcciones = 0;

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
  if (errorGeneral.length() != 0 && (millis() - ultimoReintentoSetup >= 60000))
  {
    // Gestionamos el mensaje de error
    registrarLog("ERROR", "Error general: " + errorGeneral + " . Esperamos 60 segundos antes de volver a hacer setup");

    // ... actualizamos el tiempo del último reintento de setup
    ultimoReintentoSetup = millis();

    // ... reintentamos el setup
    setupSistema();

    // Realizamos el mantenimiento de la tarjeta SD en caso de que se quite y ponga
    mantenimientoSD();
  }
  else if (errorGeneral.length() == 0 && millis() - ultimoCheckAcciones >= 10000)
  {
    // Actualizamos el tiempo del último check de acciones
    ultimoCheckAcciones = millis();
    
    // Esta función se encarga de realizar el mantenimiento de la tarjeta SD en caso de que se quite y ponga
    mantenimientoSD();

    // Validamos si hay acción pendiente
    AccionPendiente accionPendiente = validarSiHayAccionPendiente();

    // Gestionamos la apertura de la puerta si hay una acción pendiente
    gestionarAperturaPuerta(accionPendiente);
  }
}

/**
 * Función que realiza el setup del sistema
 */
void setupSistema()
{
  // Inicializamos la biblioteca Jandula Base
  setupJandulaBase();

  // Si no hay ningún error general ...
  if (errorGeneral.length() == 0)
  {
    // Inicializamos la biblioteca Jandula Actuador Puerta
    setupJandulaActuadorPuerta();
  }
}