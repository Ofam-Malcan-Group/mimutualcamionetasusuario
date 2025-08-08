package com.ofam.mimutualcamionetasusuario.services;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ofam.mimutualcamionetasusuario.R;
import com.ofam.mimutualcamionetasusuario.utilities.EnumThreads;
import com.ofam.mimutualcamionetasusuario.utilities.Message;
import com.ofam.mimutualcamionetasusuario.utilities.Utilities;

import org.json.JSONObject;

import java.util.Locale;

/**
 * **************************************************************************
 * NAME: CallServiceRest.java
 * DESCRIPTION:  Clase que contiene el llamado a los servicios (hilos) de la aplicación REST.
 * ****************************************************************************************************
 */
public class CallServiceRest {

    private static final String FORMAT_GET_SERVICE_NO_PARAMETER = "%s/%s";
    private static final String FORMAT_GET_SERVICE_PARAMETER_1 = "%s/%s/%s";
    private static final String FORMAT_GET_SERVICE_PARAMETER_2 = "%s/%s/%s/%s";
    private static final String FORMAT_GET_SERVICE_PARAMETER_3 = "%s/%s/%s/%s/%s";

    private static @NonNull String getServer(Context context) {
        return Locale.getDefault().getLanguage().toLowerCase().contains("es") ? context.getString(R.string.app_server) : context.getString(R.string.app_server_EN);
    }


    //region Get
    public void getDriversAvailable(ICallBackServiceRest iCallBackServiceRest, String lat, String lon, Context context) {
        try {
            Utilities.appendLog("LLama servicio automatico: " + String.format(FORMAT_GET_SERVICE_PARAMETER_2, getServer(context), context.getString(R.string.service_drivers_available), lat, lon), context);
            new MainCallService.ServiceGetBack(iCallBackServiceRest, EnumThreads.GET_DRIVERS_AVAILABLE).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_2, getServer(context), context.getString(R.string.service_drivers_available), lat, lon));
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    public void getDriverLocation(ICallBackServiceRest iCallBackServiceRest, String data, @NonNull Context context) {
        Utilities.appendLog("LLama servicio automatico: " + String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_pos_driver), data), context);
        new MainCallService.ServiceGetBack(iCallBackServiceRest, EnumThreads.GET_DRIVER_LOCATION).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_pos_driver), data));
    }
    public void getServicesDriver(ICallBackServiceRest iCallBackServiceRest, String data, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_SERVICES_DRIVER, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_driver_services), data));
    }
    public void getServicesUser(ICallBackServiceRest iCallBackServiceRest, String data, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_SERVICES_USER, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_user_services), data));
    }
    public void getServicesActive(ICallBackServiceRest iCallBackServiceRest, String data, Context context) {
        Utilities.appendLog("LLama servicio automatico: " + String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_active_services), data), context);
        new MainCallService.ServiceGetBack(iCallBackServiceRest, EnumThreads.GET_SERVICES_ACTIVE).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_active_services), data));
    }
    public void getService(ICallBackServiceRest iCallBackServiceRest, String data, @NonNull Context context) {
        Utilities.appendLog("LLama servicio automatico: " + String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_get_service), data), context);
        new MainCallService.ServiceGetBack(iCallBackServiceRest, EnumThreads.GET_SERVICE).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_get_service), data));
    }
    public void getChangeStateDriver(ICallBackServiceRest iCallBackServiceRest, String idUser, String state, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_ACT_DES_DRIVER, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_2, getServer(context), context.getString(R.string.service_change_driver_state), idUser, state));
    }
    public void getNotificationCollectService(ICallBackServiceRest iCallBackServiceRest, String idService, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_NOTIFICATION_COLLECT_SERVICE, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_notification_collect_service), idService));
    }
    public void getCancelService(ICallBackServiceRest iCallBackServiceRest, String idUser, String idService, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_CANCEL_SERVICE, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_2, getServer(context), context.getString(R.string.service_cancel_service), idUser, idService));
    }
    public void getCancelServiceAutomatic(ICallBackServiceRest iCallBackServiceRest, String idUser, String idService, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_CANCEL_SERVICE, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_2, getServer(context), context.getString(R.string.service_cancel_service_aut), idUser, idService));
    }
    public void getParameters(ICallBackServiceRest iCallBackServiceRest, String data, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_PARAMETERS, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_get_parameters), data));
    }
    public void getCollectService(ICallBackServiceRest iCallBackServiceRest, String idService, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_COLLECT_SERVICE, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_start_service), idService));
    }
    public void getLeaveService(ICallBackServiceRest iCallBackServiceRest, String idService, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_LEAVE_SERVICE, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_end_service), idService));
    }
    public void getTypeDocs(ICallBackServiceRest iCallBackServiceRest, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_TYPE_DOCUMENT, context).execute(String.format(FORMAT_GET_SERVICE_NO_PARAMETER, getServer(context), context.getString(R.string.service_domain)));
    }
    public void getDepartments(ICallBackServiceRest iCallBackServiceRest, Context context) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_DEPARTMENTS, context).execute(String.format(FORMAT_GET_SERVICE_NO_PARAMETER, getServer(context), context.getString(R.string.service_departments)));
    }
    public void getCities(ICallBackServiceRest iCallBackServiceRest, Context context, String pDep) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_CITIES, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_cities), pDep));
    }
    public void getCarsDriver(ICallBackServiceRest iCallBackServiceRest, Context context, String idUser) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_CARS_DRIVER, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_cars_driver), idUser));
    }
    public void getAssignCar(ICallBackServiceRest iCallBackServiceRest, Context context, String idUser, String idCar) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_ASSIGN_CAR, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_2, getServer(context), context.getString(R.string.service_assign_car), idUser, idCar));
    }
    public void getResetPass(ICallBackServiceRest iCallBackServiceRest, Context context, String idUsu, String idTypeDoc) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_RESET_PASS, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_3, getServer(context), context.getString(R.string.service_reset_pass), idUsu, idTypeDoc, "2"));
    }
    public void getEditInfo(ICallBackServiceRest iCallBackServiceRest, Context context, String idUsu, String idTypeDoc) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_EDIT_INFO, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_3, getServer(context), context.getString(R.string.service_edit_data), idUsu, idTypeDoc, "2"));
    }
    public void getRating(ICallBackServiceRest iCallBackServiceRest, Context context, String idUsu) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_RATING, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_1, getServer(context), context.getString(R.string.service_rating_data), idUsu));
    }
    public void getSaveRating(ICallBackServiceRest iCallBackServiceRest, Context context, String idService, String rating) {
        new MainCallService.ServiceGet(iCallBackServiceRest, EnumThreads.GET_SAVE_RATING, context).execute(String.format(FORMAT_GET_SERVICE_PARAMETER_2, getServer(context), context.getString(R.string.service_save_rating_data), idService ,rating));
    }
    //endregion

    //region Post
    public void postLoginUser(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePost(iCallBackServiceRestPost, EnumThreads.POST_LOGIN_USER, parameters, context).execute(getServer(context), context.getString(R.string.service_login));
    }
    public void postCloseSession(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePost(iCallBackServiceRestPost, EnumThreads.POST_CLOSE_SESSION, parameters, context).execute(getServer(context), context.getString(R.string.service_close_session));
    }
    public void postRegister(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePost(iCallBackServiceRestPost, EnumThreads.POST_REGISTER, parameters, context).execute(getServer(context), context.getString(R.string.service_register));
    }
    public void postRegisterGoogle(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePost(iCallBackServiceRestPost, EnumThreads.POST_REGISTER_GOOGLE, parameters, context).execute(getServer(context), context.getString(R.string.service_register_google));
    }
    public void postCreateService(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePost(iCallBackServiceRestPost, EnumThreads.POST_CREATE_SERVICE, parameters, context).execute(getServer(context), context.getString(R.string.service_create_service));
    }
    public void postTakeService(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePost(iCallBackServiceRestPost, EnumThreads.POST_TAKE_SERVICE, parameters, context).execute(getServer(context), context.getString(R.string.service_take_service));
    }
    public void postTakeServiceNotify(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePostBack(iCallBackServiceRestPost, EnumThreads.POST_TAKE_SERVICE, parameters, context).execute(getServer(context), context.getString(R.string.service_take_service));
    }
    public void postNewCar(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePost(iCallBackServiceRestPost, EnumThreads.POST_NEW_CAR, parameters, context).execute(getServer(context), context.getString(R.string.service_new_car));
    }
    public void postNewDriver(ICallBackServiceRestPost iCallBackServiceRestPost, JSONObject parameters, Context context) {
        new MainCallService.ServicePost(iCallBackServiceRestPost, EnumThreads.POST_NEW_DRIVER, parameters, context).execute(getServer(context), context.getString(R.string.service_new_driver));
    }
    public void setLocation(String server, JSONObject params) {
        new MainCallService.SetLocationRest(params).execute(server);
    }
    //endregion

}
