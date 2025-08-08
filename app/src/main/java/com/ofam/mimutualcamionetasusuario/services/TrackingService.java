package com.ofam.mimutualcamionetasusuario.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.ofam.mimutualcamionetasusuario.R;
import com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp;
import com.ofam.mimutualcamionetasusuario.utilities.Message;
import com.ofam.mimutualcamionetasusuario.utilities.Utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class TrackingService extends Service {

    //region variables
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    //endregion

    //region Metodos
    private void initData() {
        locationRequest = LocationRequest.create();
        //noinspection deprecation
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(ConstantsApp.TMS_TRACKING);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                updateDriverLocation(locationResult);
                super.onLocationResult(locationResult);
            }
        };
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    protected void updateDriverLocation(LocationResult locationResult) {
        try {
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    Location currentLocation = locationResult.getLastLocation();
                    JSONObject paramService = new JSONObject();
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.share_preference_mimutual), MODE_PRIVATE);
                    paramService.put("idUser", prefs.getString(getString(R.string.share_id_user), ConstantsApp.STRING_ZERO));
                    assert currentLocation != null;
                    paramService.put("latitud", currentLocation.getLatitude());
                    paramService.put("longitud", currentLocation.getLongitude());
                    new CallServiceRest().setLocation(getString(R.string.app_server), paramService);
                    Utilities.appendLog("LLama back pos conductor: " + paramService, this);
                }
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(this.locationRequest, this.locationCallback, Looper.myLooper());
    }

    private void buildNotification() {
        getNotification();
    }

    private void getNotification() {
        String channelName = "Tracking Service MiMutual";
        NotificationChannel chan = new NotificationChannel(getString(R.string.channel_tracking_mm), channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_tracking_mm));
        Notification notification = notificationBuilder
                .setOngoing(true)
                .setCustomContentView(views)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setChannelId(getString(R.string.channel_tracking_mm))
                .setColor(getColor(R.color.colorPrimary))
                .setColorized(true)
                .setSmallIcon(R.mipmap.ic_launcher_foreground).build();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(101, notification);
        } else {
            startForeground(101, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        }

    }
    //endregion

    //region Override
    //onCreate
    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildNotification();
        startLocationUpdates();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    //endregion
}