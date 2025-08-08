package com.ofam.mimutualcamionetasusuario.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.ofam.mimutualcamionetasusuario.R;
import com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp;
import com.ofam.mimutualcamionetasusuario.utilities.EnumMessage;
import com.ofam.mimutualcamionetasusuario.utilities.EnumThreads;
import com.ofam.mimutualcamionetasusuario.utilities.Message;
import com.ofam.mimutualcamionetasusuario.utilities.Utilities;

import org.json.JSONObject;

import java.util.Objects;

public class BroadcastReceiverNotification extends BroadcastReceiver implements ICallBackServiceRestPost {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        try {
            if (Objects.equals(Objects.requireNonNull(intent.getAction()), "accept")) {
                callTakeService(Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).get(context.getString(R.string.extra_service_id))).toString());
            }
            cancelNotification(intent);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    private void cancelNotification(@NonNull Intent intent) {
        try {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).get(context.getString(R.string.extra_service_id))).toString()));
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    protected void callTakeService(String serviceId) {
        try {
            if (Utilities.isNetworkAvailable(context)) {
                JSONObject jsonObject = new JSONObject();
                SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.share_preference_mimutual), Context.MODE_PRIVATE);
                jsonObject.put("idUsuario", prefs.getString(context.getString(R.string.share_id_user), ConstantsApp.STRING_ZERO));
                jsonObject.put("idServicio", serviceId);
                new CallServiceRest().postTakeServiceNotify(BroadcastReceiverNotification.this, jsonObject, context);
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    @Override
    public void serviceResultRestPost(String data, EnumThreads method, boolean cancel) {
        try {
            if (data == null || data.equals(ConstantsApp.RESPONSE_NO_SERVICE))
                Message.showMessage(String.format(context.getString(R.string.concat_error), context.getString(R.string.msg_error_no_service), method), context, EnumMessage.MESSAGE_ERROR);
            if (!cancel && method.equals(EnumThreads.POST_TAKE_SERVICE))
                postAssignService(data);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    protected void postAssignService(String data) {
        try {
            JSONObject dataJSON = new JSONObject(data);
            if (Utilities.validateResponseJSONService(dataJSON)) {
                context.getSharedPreferences(context.getString(R.string.share_preference_mimutual), Context.MODE_PRIVATE).edit().putString(context.getString(R.string.share_id_service), dataJSON.getString("data")).apply();
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.deleteNotificationChannel(context.getString(R.string.channel_mm));
                PackageManager pm = context.getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage("com.ofam.mimutualcamionetasusuario");
                context.startActivity(launchIntent);
            } else
                Message.showToast(dataJSON.getString(ConstantsApp.RESPONSE_GENERIC_MESSAGE), context);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }
}
