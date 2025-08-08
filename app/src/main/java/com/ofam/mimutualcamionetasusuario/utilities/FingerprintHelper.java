package com.ofam.mimutualcamionetasusuario.utilities;

import android.content.Context;
import android.os.CancellationSignal;
import android.widget.Toast;

import com.ofam.mimutualcamionetasusuario.LoginActivity;

import org.jetbrains.annotations.NotNull;


public class FingerprintHelper extends android.hardware.fingerprint.FingerprintManager.AuthenticationCallback {

    private final Context mContext;
    private CancellationSignal cancellationSignal;

    @SuppressWarnings("deprecation")
    public FingerprintHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void startAuth(@NotNull android.hardware.fingerprint.FingerprintManager manager, android.hardware.fingerprint.FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void stopAuth() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.showInformationMessage("MIMUTUAL: " + errString);
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        //Not Used
    }


    @Override
    public void onAuthenticationFailed() {
        this.showInformationMessage("MIMUTUAL: Inicio de sesion fallido.");
    }


    @Override
    public void onAuthenticationSucceeded(android.hardware.fingerprint.FingerprintManager.AuthenticationResult result) {
        ((LoginActivity) mContext).attemptLoginFingerprint();
    }

    private void showInformationMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}
