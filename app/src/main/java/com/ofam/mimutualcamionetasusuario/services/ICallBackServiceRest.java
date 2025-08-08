package com.ofam.mimutualcamionetasusuario.services;

import com.ofam.mimutualcamionetasusuario.utilities.EnumThreads;

/**
 * **************************************************************************
 * NAME: ICallBackServiceRest.java
 * DESCRIPTION:  Interfaz que representa respuesta de los llamados a los servicios REST .
 * ***************************************************************************
 */
public interface ICallBackServiceRest {
    void serviceResultRest(String data, EnumThreads method, boolean cancel);
}
