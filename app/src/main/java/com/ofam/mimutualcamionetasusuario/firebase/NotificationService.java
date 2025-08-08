package com.ofam.mimutualcamionetasusuario.firebase;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ofam.mimutualcamionetasusuario.R;
import com.ofam.mimutualcamionetasusuario.SplashScreenActivity;
import com.ofam.mimutualcamionetasusuario.services.BroadcastReceiverNotification;
import com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp;
import com.ofam.mimutualcamionetasusuario.utilities.EnumMessage;
import com.ofam.mimutualcamionetasusuario.utilities.Message;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private void sendNotificationServiceNew(String serviceId, String type, String dirOri, String dirDes) {
        try {
            Intent intent = new Intent(this, SplashScreenActivity.class);
            intent.putExtra(getString(R.string.extra_service_id), serviceId);
            intent.putExtra(getString(R.string.extra_type_notification), type);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_mm));

            Intent intentAccept = new Intent(this, BroadcastReceiverNotification.class);
            intentAccept.setAction("accept");
            intentAccept.putExtra(getString(R.string.extra_service_id), serviceId);
            intentAccept.putExtra("acceptService", 1);
            PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0, intentAccept, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

            Intent intentDecline = new Intent(this, BroadcastReceiverNotification.class);
            intentDecline.setAction("decline");
            intentDecline.putExtra(getString(R.string.extra_service_id), serviceId);
            intentDecline.putExtra("acceptService", 0);
            PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, 0, intentDecline, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);


            RemoteViews viewSmall = new RemoteViews(getPackageName(), R.layout.notification_service);
            RemoteViews viewBig = new RemoteViews(getPackageName(), R.layout.notification_service_big);

            createNotificationChannel();

            Notification notification = notificationBuilder
                    .setContent(viewSmall)
                    .setCustomContentView(viewSmall)
                    .setCustomBigContentView(viewBig)
                    .setColor(getColor(R.color.white))
                    .setColorized(true)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(
                            getResources(), R.mipmap.ic_launcher))
                    .setChannelId(getString(R.string.channel_mm))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.mimutual2))
                    .build();

            viewSmall.setOnClickPendingIntent(R.id.btnOk, acceptPendingIntent);
            viewSmall.setOnClickPendingIntent(R.id.btnCloseMessage, declinePendingIntent);
            viewSmall.setTextViewText(R.id.tvDirectionOriginService, dirOri);

            viewBig.setOnClickPendingIntent(R.id.btnOk, acceptPendingIntent);
            viewBig.setOnClickPendingIntent(R.id.btnCloseMessage, declinePendingIntent);
            viewBig.setTextViewText(R.id.tvDirectionOriginService, dirOri);
            viewBig.setTextViewText(R.id.tvDirectionDestinationService, dirDes);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.parseInt(serviceId), notification);

        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
    }

    private void sendNotification(String serviceId, String type, String message) {
        try {
            Intent intent = new Intent(this, SplashScreenActivity.class);
            intent.putExtra(getString(R.string.extra_service_id), serviceId);
            intent.putExtra(getString(R.string.extra_type_notification), type);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.channel_mm));
            RemoteViews viewSmall = new RemoteViews(getPackageName(), R.layout.notification);

            viewSmall.setTextViewText(R.id.notification_body, message);

            createNotificationChannel();

            notificationBuilder.setContent(viewSmall)
                    .setCustomContentView(viewSmall)
                    .setColorized(true)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.mimutual2))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.parseInt(serviceId), notificationBuilder.build());

        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
    }

    private void createNotificationChannel() {
        String description = "Notification channel MiMutual";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        //Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + getApplicationContext().getPackageName() + "/" + R.raw.mimutual2);
        //AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
        NotificationChannel channel = new NotificationChannel(getString(R.string.channel_mm), getString(R.string.app_name), importance);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.enableVibration(true);
        //channel.setSound(alarmSound, audioAttributes);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        try {
            remoteMessage.getData();
            Map<String, String> data = remoteMessage.getData();
            String serviceId = data.getOrDefault(getString(R.string.extra_service_id), ConstantsApp.STRING_ZERO);
            String typeNotification = data.getOrDefault(getString(R.string.extra_type_notification), ConstantsApp.EMPTY);
            String message = data.getOrDefault(getString(R.string.extra_message_notification), ConstantsApp.EMPTY);
            String dirOri = data.getOrDefault(getString(R.string.extra_dirOri_notification), ConstantsApp.EMPTY);
            String dirDes = data.getOrDefault(getString(R.string.extra_dirDes_notification), ConstantsApp.EMPTY);
            @SuppressLint("WrongThread") boolean foreground = new ForegroundCheckTask().execute(getApplicationContext()).get();
            if (typeNotification != null && Objects.requireNonNull(typeNotification).equals("new"))
                notifyNew(dirOri, dirDes, message, serviceId, foreground, typeNotification);
            else if (typeNotification != null && Objects.requireNonNull(typeNotification).equals("change"))
                notifyChange(message, serviceId, foreground, typeNotification);
            else if (typeNotification != null && Objects.requireNonNull(typeNotification).equals("notification"))
                notifyNotification(message, serviceId, foreground, typeNotification);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            Message.showMessage("Error llegada 1" + ie.getMessage(), getApplicationContext(), EnumMessage.MESSAGE_ALERT);
        } catch (ExecutionException ee) {
            Message.logMessageException(null, ee);
            Message.showMessage("Error llegada 2" + ee.getMessage(), getApplicationContext(), EnumMessage.MESSAGE_ALERT);
        } catch (Exception e) {
            Message.logMessageException(null, e);
            Message.showMessage("Error llegada 3" + e.getMessage(), getApplicationContext(), EnumMessage.MESSAGE_ALERT);
        }
    }

    protected void notifyNew(String dirOri, String dirDes, String message, String serviceId, boolean foreground, String type) {
        if (serviceId != null) {
            if (foreground) {
//                Intent intent = new Intent(getApplicationContext(), NewServiceActivity.class);
//                intent.putExtra(getString(R.string.extra_service_id), serviceId);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //final MediaPlayer mp = MediaPlayer.create(this, R.raw.mimutual2);
                //mp.start();
//                startActivity(intent);
            } else
                sendNotificationServiceNew(serviceId, type, dirOri, dirDes);
        } else
            Message.showMessage(message, getApplicationContext(), EnumMessage.MESSAGE_ALERT);
    }

    protected void notifyChange(String message, String serviceId, boolean foreground, String type) {
        if (foreground) {
            Intent intent = new Intent("com.ofam.mimutual_FCM-MESSAGE");
            intent.putExtra("message", message);
            intent.putExtra("typeMessage", type);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);
        } else
            sendNotification(serviceId, type, message);
    }

    protected void notifyNotification(String message, String serviceId, boolean foreground, String type) {
        if (foreground) {
            Intent intent = new Intent("com.ofam.mimutual_FCM-MESSAGE");
            intent.putExtra("message", message);
            intent.putExtra("typeMessage", type);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);
        } else
            sendNotification(serviceId, type, message);
    }

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }
}

@SuppressWarnings("deprecation")
class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

    public ForegroundCheckTask() {
        //Not Used
    }

    @Override
    protected Boolean doInBackground(@NotNull Context... params) {
        final Context context = params[0].getApplicationContext();
        return isAppOnForeground(context);
    }

    private boolean isAppOnForeground(@NotNull Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = Objects.requireNonNull(activityManager).getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}