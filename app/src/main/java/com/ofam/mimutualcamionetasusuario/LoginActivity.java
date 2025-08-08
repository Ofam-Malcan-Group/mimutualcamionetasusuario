package com.ofam.mimutualcamionetasusuario;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.textfield.TextInputEditText;
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
import com.ofam.mimutualcamionetasusuario.utilities.FingerprintHelper;
import com.ofam.mimutualcamionetasusuario.utilities.Message;
import com.ofam.mimutualcamionetasusuario.utilities.Utilities;

import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity implements ICallBackServiceRest, ICallBackServiceRestPost {

    //region variables
    private KeyStore keyStore;
    private static final String KEY_NAME = "FingerPrintPoC";
    private Cipher cipher;
    private EditText edId;
    private TextInputEditText edPassword;
    private CheckBox cbTerms;
    private boolean isFingerprint;
    private Dialog dialogFingerPrint;
    private DataBase db;
    private String token;
    private String cityParametersData;
    private FusedLocationProviderClient fusedLocationClient;
    private JSONObject jsonObjectUser = new JSONObject();
    //endregion

    //region Methods
    //Load Controls
    private void loadControls() {
        try {
            db = new DataBase(this);
            loadFingerprint();
            edId = findViewById(R.id.edId);
            edPassword = findViewById(R.id.edPassword);
            cbTerms = findViewById(R.id.cbTerms);
            cityParametersData = getIntent().getStringExtra("parameters");
            TextView tvTerms = findViewById(R.id.tvTerms);
            tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
            String text = getString(R.string.txt_term_conditions);
            tvTerms.setText(Html.fromHtml(text));

            edPassword.setOnEditorActionListener((textView, id, keyEvent) -> {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            });
            Button btnEnter = findViewById(R.id.btnEnter);
            btnEnter.setOnClickListener(view -> attemptLogin());
            findViewById(R.id.btnRegister).setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
            findViewById(R.id.btnForgotPass).setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ResetPassActivity.class)));

        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Load Fingerprint
    private void loadFingerprint() {
        try {
            ImageButton btnFinger = findViewById(R.id.btnFinger);
            LinearLayout llFinger = findViewById(R.id.llFinger);
            llFinger.setVisibility(View.GONE);
            if (db.getUser() != null) {
                // Keyguard Manager and Fingerprint Manager
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                final android.hardware.fingerprint.FingerprintManager fingerprintManager = (android.hardware.fingerprint.FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

                // Check the fingerprint sensor
                if (fingerprintManager.isHardwareDetected() && ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED &&
                        fingerprintManager.hasEnrolledFingerprints() && keyguardManager.isKeyguardSecure()) {
                    llFinger.setVisibility(View.VISIBLE);
                    btnFinger.setOnClickListener(v -> {
                        generateKey();
                        if (cipherInit()) {
                            dialogFingerPrint = Message.getDialogFingerPrint(LoginActivity.this);
                            final FingerprintHelper helper = new FingerprintHelper(LoginActivity.this);
                            dialogFingerPrint.findViewById(R.id.btn_accept).setOnClickListener(v1 -> {
                                dialogFingerPrint.dismiss();
                                helper.stopAuth();
                            });
                            dialogFingerPrint.findViewById(R.id.dialog_fingerprint_viewer_close).setOnClickListener(v12 -> dialogFingerPrint.dismiss());
                            android.hardware.fingerprint.FingerprintManager.CryptoObject cryptoObject = new android.hardware.fingerprint.FingerprintManager.CryptoObject(cipher);
                            helper.startAuth(fingerprintManager, cryptoObject);
                            dialogFingerPrint.show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Load key
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Validate Generate Key
    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(String.format("%s/%s/%s", KeyProperties.KEY_ALGORITHM_AES, KeyProperties.BLOCK_MODE_CBC, KeyProperties.ENCRYPTION_PADDING_PKCS7));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Message.logMessageException(getClass(), e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException |
                 IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            Message.logMessageException(getClass(), e);
            return false;
        }
    }

    //Verify Login
    private void attemptLogin() {
        try {
            // Reset errors.
            edId.setError(null);
            edPassword.setError(null);
            cbTerms.setError(null);

            Utilities.closeKeyboard(this.getCurrentFocus(), this);
            if (Utilities.isInfoValid(edId.getText().toString().trim())) {
                edId.setError(getString(R.string.msg_error_invalid_number_id));
                edId.requestFocus();
                return;
            } else if (Utilities.isInfoValid(Objects.requireNonNull(edPassword.getText()).toString().trim())) {
                edPassword.setError(getString(R.string.msg_error_invalid_password));
                edPassword.requestFocus();
                return;
            } else if (!cbTerms.isChecked()) {
                cbTerms.setError(getString(R.string.msg_error_invalid_terms));
                Message.showToast(getString(R.string.msg_error_invalid_terms), this);
                return;
            }
            isFingerprint = false;
            sendDataLogin(edId.getText().toString().trim(), edPassword.getText().toString().trim());
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Verify Login FingerPrint
    public void attemptLoginFingerprint() {
        try {
            dialogFingerPrint.dismiss();
            User user = db.getUser();
            isFingerprint = true;
            sendDataLogin(user.getUserID(), user.getPassID());
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    protected void sendDataLogin(String userID, String passID) {
        try {
            jsonObjectUser = new JSONObject();
            jsonObjectUser.put("parId", userID);
            jsonObjectUser.put("parPass", Utilities.encrypted(passID));
            jsonObjectUser.put("parToken", token);
            jsonObjectUser.put("idPerEmp", 373);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
                    sendData();
                else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))
                    Message.showMessage(getString(R.string.msg_no_permission_notification), this, EnumMessage.MESSAGE_ERROR);
                else {
                    final Dialog dialog = Message.getDialogQuestion(getString(R.string.msg_permission_notification_user), this);
                    dialog.findViewById(R.id.btnOk).setOnClickListener(v -> {
                        try {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_NOTIFICATION);
                        } catch (Exception e) {
                            Message.logMessageException(getClass(), e);
                        }
                    });
                    dialog.show();
                }
            } else
                sendData();
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    private void sendData() {
        if (cityParametersData == null)
            verifyPermissions();
        else if (Utilities.isNetworkAvailable(this))
            new CallServiceRest().postLoginUser(this, jsonObjectUser, this);
    }


    //get TokenPush
    private void getTokenPush() {
        try {
            if (Utilities.isNetworkAvailableFinish(this)) {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Message.showMessageFinish("Error token" + task.getException(), this, EnumMessage.MESSAGE_ERROR);
                        return;
                    }
                    token = task.getResult().replace("\n", ConstantsApp.EMPTY);
                    loadControls();
                });
            }
        } catch (Exception e) {
            Message.showMessageFinish("Error token", this, EnumMessage.MESSAGE_ERROR);
            Message.logMessageException(getClass(), e);
        }
    }

    public void verifyPermissions() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                getLocation();
            else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
                Message.showMessage(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
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
                                Message.showMessage(getString(R.string.msg_no_permission), LoginActivity.this, EnumMessage.MESSAGE_ERROR);
                                return null;
                            }
                        }).addOnSuccessListener(this, location1 -> {
                            if (location1 != null)
                                getAddress(location1);
                            else
                                Message.showMessage(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
                        });
                });
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
            Message.showMessage(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
        }
    }

    public void getAddress(Location location) {
        try {
            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
            DecimalFormat df = new DecimalFormat("0.000");
            double lat = Double.parseDouble(df.format(location.getLatitude()).replace(",", "."));
            double lon = Double.parseDouble(df.format(location.getLongitude()).replace(",", "."));
            List<Address> addresses = geo.getFromLocation(lat, lon, 1);
            if (addresses == null || addresses.isEmpty()) {
                getLocation();
            } else {
                String locality = addresses.get(0).getLocality();
                if (locality == null)
                    locality = addresses.get(0).getSubAdminArea();

                new CallServiceRest().getParameters(LoginActivity.this, locality == null ? ConstantsApp.EMPTY : locality, this);
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
            Message.showMessage(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
        }
    }
    //endregion

    //region Override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);
        } catch (Exception e) {
            Message.showMessage(getString(R.string.msg_no_permission), this, EnumMessage.MESSAGE_ERROR);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getTokenPush();
    }

    @Override
    public void serviceResultRest(String data, EnumThreads method, boolean cancel) {
        try {
            if (!cancel) {
                if (data == null || data.equals(ConstantsApp.RESPONSE_NO_SERVICE))
                    Message.showMessageFinish(String.format(getString(R.string.concat_error), getString(R.string.msg_error_no_service), method), this, EnumMessage.MESSAGE_ERROR);
                else if (method == EnumThreads.GET_PARAMETERS) {
                    List<KeyValueGeneric2> cityParameters = new Gson().fromJson(data, new TypeToken<ArrayList<KeyValueGeneric2>>() {
                    }.getType());
                    if (!cityParameters.isEmpty()) {
                        cityParametersData = data;
                        if (Utilities.isNetworkAvailable(this))
                            new CallServiceRest().postLoginUser(this, jsonObjectUser, this);
                    } else
                        Message.showMessageFinish(this.getString(R.string.msg_error_parameters), this, EnumMessage.MESSAGE_ERROR);
                }
            }
        } catch (
                Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    @Override
    public void serviceResultRestPost(String data, EnumThreads method, boolean cancel) {
        try {
            if (data == null || data.equals(ConstantsApp.RESPONSE_NO_SERVICE))
                Message.showToast(String.format(getString(R.string.concat_error), getString(R.string.msg_error_no_service), method), this);
            else if (!cancel && method.equals(EnumThreads.POST_LOGIN_USER)) {
                JSONObject dataJSON = new JSONObject(data);
                if (Utilities.validateResponseJSONService(dataJSON)) {
                    if (!isFingerprint) {
                        db.deleteUser();
                        db.insertUser(new User(edId.getText().toString().trim(), Objects.requireNonNull(edPassword.getText()).toString().trim()));
                    }
                    Utilities.loadMain(data, cityParametersData, token, this);
                    finish();
                } else
                    Message.showMessage(dataJSON.getString(ConstantsApp.RESPONSE_GENERIC_MESSAGE), this, EnumMessage.MESSAGE_ERROR);
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //endregion
}