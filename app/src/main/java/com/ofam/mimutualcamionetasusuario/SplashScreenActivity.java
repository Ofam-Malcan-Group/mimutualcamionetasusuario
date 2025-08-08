package com.ofam.mimutualcamionetasusuario;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import static com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp.EMPTY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ofam.mimutualcamionetasusuario.entities.KeyValueGeneric2;
import com.ofam.mimutualcamionetasusuario.entities.User;
import com.ofam.mimutualcamionetasusuario.services.CallServiceRest;
import com.ofam.mimutualcamionetasusuario.services.ICallBackServiceRest;
import com.ofam.mimutualcamionetasusuario.services.ICallBackServiceRestPost;
import com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp;
import com.ofam.mimutualcamionetasusuario.utilities.DataBase;
import com.ofam.mimutualcamionetasusuario.utilities.EnumMessage;
import com.ofam.mimutualcamionetasusuario.utilities.EnumThreads;
import com.ofam.mimutualcamionetasusuario.utilities.Message;
import com.ofam.mimutualcamionetasusuario.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity implements ICallBackServiceRest, ICallBackServiceRestPost {

    private String cityParametersData;
    private String token;
    private User user;
    private FusedLocationProviderClient fusedLocationClient;

    //region Methods
    private void startApp() {
        try {
            if (Utilities.isNetworkAvailableFinish(this)) {
                DataBase db = new DataBase(this);
                SharedPreferences prefs = this.getSharedPreferences(getString(R.string.share_preference_mimutual), Context.MODE_PRIVATE);
                user = db.getUser();
                if (user != null && prefs.getInt(getString(R.string.share_last_session_user), 0) != 0 && prefs.getInt(getString(R.string.share_last_session_user), 0) + ConstantsApp.DAYS_SESSION_OPEN > Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                    verifyPermissionsNotification();
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    private void verifyPermissionsNotification() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
                    verifyPermissions();
                else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))
                    Message.showMessageFinish(getString(R.string.msg_no_permission_notification), this, EnumMessage.MESSAGE_ERROR);
                else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_NOTIFICATION);
            } else
                verifyPermissions();
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //get TokenPush
    protected void getTokenPush() {
        try {
            if (Utilities.isNetworkAvailableFinish(this)) {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Message.showMessageFinish("Error token", this, EnumMessage.MESSAGE_ERROR);
                        return;
                    }
                    token = task.getResult().replace("\n", ConstantsApp.EMPTY);
                    try {
                        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.share_preference_mimutual), Context.MODE_PRIVATE);
                        String userString = prefs.getString(getString(R.string.extra_user_mi_mutual), EMPTY);
                        if (user != null && !userString.isEmpty() && prefs.getInt(getString(R.string.share_last_session_user), 0) != 0 && prefs.getInt(getString(R.string.share_last_session_user), 0) + ConstantsApp.DAYS_SESSION_OPEN > Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                            if (getIntent().getExtras() != null && getIntent().hasExtra(getString(R.string.extra_type_notification)) && Objects.requireNonNull(getIntent().getStringExtra(getString(R.string.extra_type_notification))).equals("new")) {
                                Utilities.loadMain(null, cityParametersData, token, getIntent().getStringExtra(getString(R.string.extra_service_id)), this);
                            } else {
                                NotificationManagerCompat.from(this).cancelAll();
                                Utilities.loadMain(null, cityParametersData, token, this);
                            }
                            finish();
                        } else {
                            if (Utilities.isNetworkAvailableFinish(this)) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("parId", user.getUserID());
                                jsonObject.put("parPass", Utilities.encrypted(user.getPassID()));
                                jsonObject.put("parToken", token);
                                jsonObject.put("idPerEmp", 373);
                                new CallServiceRest().postLoginUser(SplashScreenActivity.this, jsonObject, this);
                            }
                        }
                    } catch (Exception e) {
                        Message.logMessageException(getClass(), e);
                    }
                });
            }
        } catch (Exception e) {
            Message.showMessageFinish("Error token", this, EnumMessage.MESSAGE_ERROR);
            Message.logMessageException(getClass(), e);
        }
    }

    //Load main Activity
    protected void loadMainActivity(String data) throws JSONException {
        JSONObject dataJSON = new JSONObject(data);
        if (Utilities.validateResponseJSONService(dataJSON)) {
            if (getIntent().getExtras() != null && getIntent().hasExtra(getString(R.string.extra_type_notification)) && Objects.requireNonNull(getIntent().getStringExtra(getString(R.string.extra_type_notification))).equals("new")) {
                Utilities.loadMain(data, cityParametersData, token, getIntent().getStringExtra(getString(R.string.extra_service_id)), this);
            } else {
                NotificationManagerCompat.from(this).cancelAll();
                Utilities.loadMain(data, cityParametersData, token, this);
            }
            finish();
        } else
            Message.showMessage(dataJSON.getString(ConstantsApp.RESPONSE_GENERIC_MESSAGE), this, EnumMessage.MESSAGE_ERROR);
    }

    //Verify Permissions Location
    public void verifyPermissions() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                getLocation();
            else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
                Message.showMessageFinish(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
            else {
                final Dialog dialog = Message.getDialogQuestion(getString(R.string.msg_permission_location_user), this);
                dialog.findViewById(R.id.btnOk).setOnClickListener(v -> {
                    try {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    } catch (Exception e) {
                        Message.logMessageException(getClass(), e);
                    }
                });
                dialog.show();
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    public void getLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null)
                        getAddress(location);
                    else
                        //noinspection deprecation
                        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                            @Override
                            public boolean isCancellationRequested() {
                                return false;
                            }

                            @NonNull
                            @Override
                            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                                Message.showMessageFinish(getString(R.string.msg_no_permission), SplashScreenActivity.this, EnumMessage.MESSAGE_ERROR);
                                return null;
                            }
                        }).addOnSuccessListener(this, location1 -> {
                            if (location1 != null)
                                getAddress(location1);
                            else
                                Message.showMessageFinish(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
                        });
                });
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
            Message.showMessageFinish(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
        }
    }

    public void getAddress(Location location) {
        try {
            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
            DecimalFormat df = new DecimalFormat("0.000");
            double lat = Double.parseDouble(df.format(location.getLatitude()).replace(",", "."));
            double lon = Double.parseDouble(df.format(location.getLongitude()).replace(",", "."));
            List<Address> addresses = geo.getFromLocation(lat, lon, 1);
            if (addresses == null || addresses.isEmpty())
                getLocation();
            else {
                String locality = addresses.get(0).getLocality();
                if (locality == null)
                    locality = addresses.get(0).getSubAdminArea();
                new CallServiceRest().getParameters(this, locality == null ? ConstantsApp.EMPTY : locality, this);
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
            Message.showMessageFinish(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
        }
    }
    //endregion

    //region Override
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.setLocale(this);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            ((TextView) findViewById(R.id.tvVersion)).setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            Message.logMessageException(getClass(), e);
        }
        new Handler(Looper.getMainLooper()).postDelayed(this::startApp, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == ConstantsApp.PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLocation();
            else if (requestCode == ConstantsApp.PERMISSIONS_REQUEST_ACCESS_NOTIFICATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                verifyPermissions();
            else if (requestCode == ConstantsApp.PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
                Message.showMessageFinish(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
            else if (requestCode == ConstantsApp.PERMISSIONS_REQUEST_ACCESS_NOTIFICATION)
                Message.showMessageFinish(getString(R.string.msg_no_permission_notification), this, EnumMessage.MESSAGE_ERROR);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    @Override
    public void serviceResultRest(String data, EnumThreads method, boolean cancel) {
        try {
            if (!cancel) {
                if (data == null || data.equals(ConstantsApp.RESPONSE_NO_SERVICE))
                    Message.showToast(String.format(getString(R.string.concat_error), getString(R.string.msg_error_no_service), method), this);
                else if (method == EnumThreads.GET_PARAMETERS) {
                    List<KeyValueGeneric2> cityParameters = new Gson().fromJson(data, new TypeToken<ArrayList<KeyValueGeneric2>>() {
                    }.getType());
                    if (!cityParameters.isEmpty()) {
                        cityParametersData = data;
                        if (Utilities.isNetworkAvailableFinish(this))
                            getTokenPush();
                    } else
                        Message.showMessageFinish(getString(R.string.msg_error_parameters), this, EnumMessage.MESSAGE_ERROR);
                }
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    @Override
    public void serviceResultRestPost(String data, EnumThreads method, boolean cancel) {
        try {
            if (data == null || data.equals(ConstantsApp.RESPONSE_NO_SERVICE))
                Message.showToast(String.format(getString(R.string.concat_error), getString(R.string.msg_error_no_service), method), this);
            else if (!cancel && method.equals(EnumThreads.POST_LOGIN_USER))
                loadMainActivity(data);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }
}
