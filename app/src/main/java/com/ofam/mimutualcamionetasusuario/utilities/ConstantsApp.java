package com.ofam.mimutualcamionetasusuario.utilities;

/**
 * **************************************************************************
 * NAME: ConstantsApp.java
 * DESCRIPTION:  Class Constants app.
 * MODIFICACIONES:
 * VERSION      FECHA       AUTOR      REQUERIMIENTO              DESCRIPCIÓN DEL CAMBIO
 * -------  ------------  ----------  ---------------  -------------------------------------------------
 * 1.0       9/03/2021   jlondono      Creaciòn                    Creación
 * ***************************************************************************
 */
public class ConstantsApp {

    //Tiempos Aplicacion

    // Para producción
    public static final int TMS_SERVICES = 30000; //Time Max response generic Services

    //Constructor
    private ConstantsApp() {
        throw new IllegalAccessError("Constants App Class");
    }

    static final String LOG_MI_MUTUAL = "LOG_MI_MUTUAL";
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String RESPONSE_NO_SERVICE = "No Service";
    static final String RESPONSE_GENERIC_CODE = "codigoRespuesta";
    public static final String RESPONSE_GENERIC_MESSAGE = "mensajeRespuesta";
    public static final String STRING_ZERO = "0";
    public static final String STRING_CONTENT = "application/json";
    public static final String AIRPORT = "AEROPUERTO";
    public static final String AIRPORT_ENG = "AIRPORT";
    public static final String TERMINAL = "TERMINAL";
    public static final int DAYS_SESSION_OPEN = 100;
    public static final long TMS_TRACKING = 15000;
    public static final long TMS_LOCATION = 15000;
    public static final long TMS_CANCEL_AUTOMATIC = 120000;
    public static final long TMS_REFRESH_SERVICE = 20000;
    public static final long TMS_REFRESH_SERVICE_ACTIVE = 10000;
    public static final long TMS_REFRESH_GET_SERVICE = 20000;
    public static final long TMS_VIEW_SERVICE = 90000;
    public static final int PERMISSIONS_REQUEST_ACCESS_READ_WRITE = 104;
    public static final int PERMISSIONS_REQUEST_ACCESS_NOTIFICATION = 103;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102;
    public static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 101;
    public static final int PERMISSIONS_REQUEST_ACCESS_BACKGROUND = 100;
    static final int DAYS_PURGE_LOG = 5; //Dais purge Log
    public static final String COUNTRY_COLOMBIA = "CO";
    public static final String COUNTRY_USA = "US";


}
