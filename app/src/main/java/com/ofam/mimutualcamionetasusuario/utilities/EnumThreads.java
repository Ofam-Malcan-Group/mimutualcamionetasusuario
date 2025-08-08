package com.ofam.mimutualcamionetasusuario.utilities;

/**
 * **************************************************************************
 * NAME: EnumThreads.java
 * DESCRIPTION:  Clase de Enumeradores de Hilos de la app.
 * MODIFICACIONES:
 * VERSION      FECHA       AUTOR      REQUERIMIENTO              DESCRIPCIÓN DEL CAMBIO
 * -------  ------------  ----------  ---------------  -------------------------------------------------
 * 1.0       14/01/2020   jlondono      Creaciòn                    Creación
 * ***************************************************************************
 */
public enum EnumThreads {

    GET_DRIVERS_AVAILABLE,
    GET_DRIVER_LOCATION,
    GET_SERVICES_DRIVER,
    GET_SERVICES_USER,
    GET_SERVICES_ACTIVE,
    GET_SERVICE,
    GET_CANCEL_SERVICE,
    GET_NOTIFICATION_COLLECT_SERVICE,
    GET_COLLECT_SERVICE,
    GET_LEAVE_SERVICE,
    GET_ACT_DES_DRIVER,
    GET_TYPE_DOCUMENT,
    GET_DEPARTMENTS,
    GET_CITIES,
    GET_PARAMETERS,
    GET_CARS_DRIVER,
    GET_ASSIGN_CAR,
    GET_RESET_PASS,
    GET_EDIT_INFO,
    GET_RATING,
    GET_SAVE_RATING,

    POST_LOGIN_USER,
    POST_CLOSE_SESSION,
    POST_REGISTER,
    POST_REGISTER_GOOGLE,
    POST_CREATE_SERVICE,
    POST_TAKE_SERVICE,
    POST_NEW_CAR,
    POST_NEW_DRIVER,
    SEND_NOTIFICATION
}
