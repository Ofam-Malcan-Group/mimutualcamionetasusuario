package com.ofam.mimutualcamionetasusuario.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Restarted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startForegroundService(new Intent(context, TrackingService.class));
    }
}