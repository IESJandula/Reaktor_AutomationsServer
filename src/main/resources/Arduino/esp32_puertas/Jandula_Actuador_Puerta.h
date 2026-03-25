#pragma once

#include "Jandula_Base.h"
#include <stdlib.h>

/************************************************/
/************ Estructuras de datos **************/
/************************************************/

/**
 * Estructura de datos para la respuesta de la API de actualización de estado de un actuador
 * 
 * @param accionId: ID de la acción pendiente
 * @param orden: Orden de la acción pendiente
 */
struct AccionPendiente
{
  long accionId;
  String orden;
  bool hayAccion;
};

/*****************************************************/
/**** Propiedades del fichero de configuración *******/
/*****************************************************/

// PROPIEDAD_URL_VALIDAR_ACCION_PENDIENTE: propiedad de la URL de validación de acción pendiente
#define PROPIEDAD_URL_VALIDAR_ACCION_PENDIENTE "URL_VALIDAR_ACCION_PENDIENTE"

// PROPIEDAD_URL_VALIDAR_ACCION_PENDIENTE_LENGTH: longitud de la propiedad de la URL de validación de acción pendiente
#define PROPIEDAD_URL_VALIDAR_ACCION_PENDIENTE_LENGTH strlen(PROPIEDAD_URL_VALIDAR_ACCION_PENDIENTE)

// PROPIEDAD_URL_AVISO_SERVIDOR_ACCION_ESTADO: propiedad de la URL de aviso al servidor de la acción establecida
#define PROPIEDAD_URL_AVISO_SERVIDOR_ACCION_ESTADO "URL_AVISO_SERVIDOR_ACCION_ESTADO"

// PROPIEDAD_URL_AVISO_SERVIDOR_ACCION_ESTADO_LENGTH: longitud de la propiedad de la URL de aviso al servidor de la acción establecida
#define PROPIEDAD_URL_AVISO_SERVIDOR_ACCION_ESTADO_LENGTH strlen(PROPIEDAD_URL_AVISO_SERVIDOR_ACCION_ESTADO)

/************************************************/
/***************** Constantes *******************/
/************************************************/

// Tiempo de apertura de la puerta en milisegundos
#define TIEMPO_APERTURA_PUERTA_MS 4000

/************************************************/
/************ Pines para la puerta **************/
/************************************************/

// Pin del relé de la puerta
#define RELE_PUERTA_PIN 13

/************************************************/
/************ Variables globales ****************/
/************************************************/

// URL de validación de acción pendiente
extern String urlValidacionAccionPendiente;

// URL de aviso al servidor de la apertura de la puerta
extern String urlAvisoServidorAccionEstado;

/************************************************/
/**************** Funciones *********************/
/************************************************/

/**
 * Realiza la inicialización de los diferentes componentes del actuador de la puerta
 * 
 * @return void
 */
 void setupJandulaActuadorPuerta();

/**
 * Carga la configuración desde el fichero de configuración de la biblioteca Jandula Actuador Puerta y la valida
 * 
 * @return void
 */
void parseaFicheroConfiguracionJandulaActuadorPuerta();

/**
 * Valida si todos los campos del fichero de configuración de la biblioteca Jandula Actuador Puerta están rellenos
 * 
 * @return void
 */
void parseaFicheroConfiguracionJandulaActuadorPuertaValidarCamposRellenos();

/**
 * Valida si hay una acción pendiente
 * 
 * @return AccionPendiente: Estructura de datos AccionPendiente
 */
AccionPendiente validarSiHayAccionPendiente();

/**
 * Parsear la respuesta de la API de validación de acción pendiente
 * 
 * @param bodyResponse: Cuerpo de la respuesta de la API de validación de acción pendiente
 * @return AccionPendiente: Estructura de datos AccionPendiente
 */
AccionPendiente validarSiHayAccionPendienteParsearRespuesta(const String& bodyResponse);

/**
 * Gestiona la apertura de la puerta en base a la estructura de datos AccionPendiente
 * 
 * @param accionPendiente: Estructura de datos AccionPendiente
 * @return void
 */
void gestionarAperturaPuerta(AccionPendiente accionPendiente);

/**
 * Gestiona el relé de la puerta
 * 
 * @return void
 */
void gestionarAperturaPuertaRele();

/**
 * Gestiona la acción estado realizada y avisa al servidor
 * 
 * @param accionId: ID de la acción pendiente
 * @return void
 */
void gestionarAccionEstadoRealizadaAvisoServidor(const long accionId);