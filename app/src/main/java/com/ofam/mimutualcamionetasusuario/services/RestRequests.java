package com.ofam.mimutualcamionetasusuario.services;

import androidx.annotation.Nullable;

import com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp;
import com.ofam.mimutualcamionetasusuario.utilities.Message;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * **************************************************************************
 * NAME: RestRequests.java
 * DESCRIPTION:  Clase que contiene el llamado a un servicios.
 * ***************************************************************************
 */
class RestRequests {

    //CONSTRUCTOR
    RestRequests() {
    }

    String makeServiceCallGet(final String mainRequestURL) {
        return callServiceGet(mainRequestURL);
    }

    String makeServiceCallPost(final String mainRequestURL, final String methodName, final JSONObject params) {
        return callServicePost(mainRequestURL, methodName, params);
    }

    String postToFCM(String bodyString) throws IOException {
        OkHttpClient mClient = new OkHttpClient();
        //noinspection deprecation
        RequestBody body = RequestBody.create(MediaType.get(ConstantsApp.STRING_CONTENT), bodyString);
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=AAAAk40i0vY:APA91bFKKzN_qnhUxCSyC_NOp6KvJZhVqsecIcGfDKWEyS3r04wFEfkC2kV8YM91gqfZY4NtbQjT1dqGd3F4HqBVH4QSHRGINtX81yFF6hgtaRdw-28j6g69x7AlTHSBXNkuGeRXrCez")
                .build();
        Response response = mClient.newCall(request).execute();
        return Objects.requireNonNull(response.body()).toString();
    }

    @Nullable
    private String callServiceGet(String mainRequestURL) {
        try {
            String request;
            HttpURLConnection connection = (HttpURLConnection) new URL(mainRequestURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(ConstantsApp.TMS_SERVICES);
            connection.setReadTimeout(ConstantsApp.TMS_SERVICES);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = bufferedReader.readLine()) != null)
                    response.append(inputLine);
                bufferedReader.close();
                request = response.toString();
                disconnectConnection(connection);
                return request;
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                disconnectConnection(connection);
                return null;
            } else {
                disconnectConnection(connection);
                return ConstantsApp.RESPONSE_NO_SERVICE;
            }
        } catch (SocketTimeoutException e) {
            Message.logMessageException(getClass(), e);
            return ConstantsApp.RESPONSE_NO_SERVICE;
        } catch (IOException e) {
            Message.logMessageException(getClass(), e);
            return null;
        }
    }

    @Nullable
    private String callServicePost(String mainRequestURL, String methodName, JSONObject params) {
        try {
            HttpURLConnection connection;
            String request;
            URL url = new URL(String.format("%s/%s", mainRequestURL, methodName));
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", ConstantsApp.STRING_CONTENT);
            connection.setRequestProperty("Accept", ConstantsApp.STRING_CONTENT);
            connection.setConnectTimeout(ConstantsApp.TMS_SERVICES);
            connection.setReadTimeout(ConstantsApp.TMS_SERVICES);
            connection.connect();
            if (loadParameters(params, connection))
                return ConstantsApp.RESPONSE_NO_SERVICE;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = bufferedReader.readLine()) != null)
                    response.append(inputLine);
                bufferedReader.close();
                request = response.toString();
                disconnectConnection(connection);
                return request;
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                disconnectConnection(connection);
                return null;
            } else {
                disconnectConnection(connection);
                return ConstantsApp.RESPONSE_NO_SERVICE;
            }
        } catch (SocketTimeoutException e) {
            Message.logMessageException(getClass(), e);
            return ConstantsApp.RESPONSE_NO_SERVICE;
        } catch (IOException e) {
            Message.logMessageException(getClass(), e);
            return null;
        }
    }


    //Carga los parametros de los servicios y prueba la conexion
    private boolean loadParameters(JSONObject params, HttpURLConnection connection) {
        try {
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(params.toString());
            writer.close();
            outputStream.close();
        } catch (IOException e) {
            Message.logMessageException(getClass(), e);
            return true;
        }
        return false;
    }

    //Disconnect connection
    private void disconnectConnection(HttpURLConnection connection) {
        try {
            if (connection != null)
                connection.disconnect();
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }
}