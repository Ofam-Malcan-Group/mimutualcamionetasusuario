package com.ofam.mimutualcamionetasusuario.services;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ofam.mimutualcamionetasusuario.R;
import com.ofam.mimutualcamionetasusuario.utilities.EnumThreads;
import com.ofam.mimutualcamionetasusuario.utilities.Message;
import com.ofam.mimutualcamionetasusuario.utilities.Utilities;

import org.json.JSONObject;

/**
 * **************************************************************************
 * NAME: MainCallService.java
 * DESCRIPTION:  Clase que contiene el llamado a los servicios.
 * *****************************************************************************************************
 */
class MainCallService {


    //Constructor
    private MainCallService() {
    }

    @SuppressWarnings("deprecation")
    static class SendNotification extends AsyncTask<String, Void, String> {

        private final ICallBackServiceRest iCallBackServiceRest;
        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private ProgressDialog dialog;

        SendNotification(ICallBackServiceRest iCallBackServiceRest, Context context) {
            this.iCallBackServiceRest = iCallBackServiceRest;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            if (context != null) {
                dialog = new ProgressDialog(context);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage(context.getString(R.string.msg_loading));
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject root = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("body", params[0]);
                notification.put("title", context.getString(R.string.app_name));
                notification.put("sound", "tuquery");
                JSONObject data = new JSONObject();
                data.put(context.getString(R.string.extra_service_id), params[2]);
                data.put(context.getString(R.string.extra_service_id), params[3]);
                root.put("notification", notification);
                root.put("data", data);
                root.put("to", params[1]);
                return new RestRequests().postToFCM(root.toString());
            } catch (Exception ex) {
                Message.logMessageException(getClass(), ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog != null) dialog.dismiss();
            iCallBackServiceRest.serviceResultRest(result, EnumThreads.SEND_NOTIFICATION, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            iCallBackServiceRest.serviceResultRest(null, EnumThreads.SEND_NOTIFICATION, true);
        }
    }


    @SuppressWarnings("deprecation")
    static class ServiceGet extends AsyncTask<String, Void, String> {
        private final ICallBackServiceRest iCallBackServiceRest;
        private final EnumThreads service;
        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private ProgressDialog dialog;

        ServiceGet(ICallBackServiceRest iCallBackServiceRest, EnumThreads service, Context context) {
            this.iCallBackServiceRest = iCallBackServiceRest;
            this.service = service;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            if (context != null) {
                dialog = new ProgressDialog(context);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage(context.getString(R.string.msg_loading));
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Utilities.appendLog("LLama get pedido: " + params[0], context);
                return new RestRequests().makeServiceCallGet(params[0]);
            } catch (Exception e) {
                Message.logMessageException(getClass(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog != null) dialog.dismiss();
            Utilities.appendLog("Responde get pedido: " + result, context);
            iCallBackServiceRest.serviceResultRest(result, service, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            iCallBackServiceRest.serviceResultRest(null, service, true);
        }
    }

    @SuppressWarnings("deprecation")
    static class ServiceGetBack extends AsyncTask<String, Void, String> {
        private final ICallBackServiceRest iCallBackServiceRest;
        private final EnumThreads service;

        ServiceGetBack(ICallBackServiceRest iCallBackServiceRest, EnumThreads service) {
            this.iCallBackServiceRest = iCallBackServiceRest;
            this.service = service;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return new RestRequests().makeServiceCallGet(params[0]);
            } catch (Exception e) {
                Message.logMessageException(getClass(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            iCallBackServiceRest.serviceResultRest(result, service, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            iCallBackServiceRest.serviceResultRest(null, service, true);
        }
    }

    @SuppressWarnings("deprecation")
    static class ServicePost extends AsyncTask<String, Void, String> {
        private final ICallBackServiceRestPost iCallBackServiceRestPost;
        private final EnumThreads service;
        private final JSONObject parameters;
        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private final Boolean isBack;
        private ProgressDialog dialog;

        ServicePost(ICallBackServiceRestPost iCallBackServiceRestPost, EnumThreads service, JSONObject parameters, Context context) {
            this.iCallBackServiceRestPost = iCallBackServiceRestPost;
            this.service = service;
            this.parameters = parameters;
            this.context = context;
            this.isBack = false;
        }

        @Override
        protected void onPreExecute() {
            if (context != null && !isBack) {
                dialog = new ProgressDialog(context);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage(context.getString(R.string.msg_loading));
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Utilities.appendLog("llama post pedido: " + params[0] + " !!!! " + params[1] + " !!!! " + parameters, context);
                return new RestRequests().makeServiceCallPost(params[0], params[1], parameters);
            } catch (Exception e) {
                Message.logMessageException(getClass(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog != null) dialog.dismiss();
            Utilities.appendLog("Responde post pedido: " + result, context);
            iCallBackServiceRestPost.serviceResultRestPost(result, service, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (dialog != null) dialog.dismiss();
            iCallBackServiceRestPost.serviceResultRestPost(null, service, true);
        }
    }

    @SuppressWarnings("deprecation")
    static class ServicePostBack extends AsyncTask<String, Void, String> {
        private final ICallBackServiceRestPost iCallBackServiceRestPost;
        private final EnumThreads service;
        private final JSONObject parameters;
        @SuppressLint("StaticFieldLeak")
        private final Context context;

        ServicePostBack(ICallBackServiceRestPost iCallBackServiceRestPost, EnumThreads service, JSONObject parameters, Context context) {
            this.iCallBackServiceRestPost = iCallBackServiceRestPost;
            this.service = service;
            this.parameters = parameters;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Utilities.appendLog("llama post pedido: " + params[0] + " !!!! " + params[1] + " !!!! " + parameters, context);
                return new RestRequests().makeServiceCallPost(params[0], params[1], parameters);
            } catch (Exception e) {
                Message.logMessageException(getClass(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Utilities.appendLog("Responde post pedido: " + result, context);
            iCallBackServiceRestPost.serviceResultRestPost(result, service, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            iCallBackServiceRestPost.serviceResultRestPost(null, service, true);
        }
    }

    @SuppressWarnings("deprecation")
    static class SetLocationRest extends AsyncTask<String, Void, String> {
        private final JSONObject parameters;

        SetLocationRest(JSONObject parameters) {
            this.parameters = parameters;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return new RestRequests().makeServiceCallPost(params[0], "SetGuardarPosConductor", parameters);
            } catch (Exception e) {
                Message.logMessageException(getClass(), e);
            }
            return null;
        }
    }
}
