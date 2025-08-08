package com.ofam.mimutualcamionetasusuario.utilities;

import static com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp.COUNTRY_COLOMBIA;
import static com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp.COUNTRY_USA;
import static com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp.EMPTY;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ofam.mimutualcamionetasusuario.LoginActivity;
import com.ofam.mimutualcamionetasusuario.MainActivity;
import com.ofam.mimutualcamionetasusuario.R;
import com.ofam.mimutualcamionetasusuario.SplashScreenActivity;
import com.ofam.mimutualcamionetasusuario.entities.KeyValueGeneric2;
import com.ofam.mimutualcamionetasusuario.entities.UserMiMutual;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Utilities {

    private static final String LANGUAGE = "es_CO";

    private Utilities() {
        //Not Used
    }

    @NotNull
    public static String getNow() {
        return String.valueOf(android.text.format.DateFormat.format("yyMMdd", new Date()));
    }

    //Number format
    @NonNull
    public static NumberFormat getNumberFormat(@NonNull Context context) {
        String country = getCountry(context);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", country));
        if (country.equalsIgnoreCase(COUNTRY_COLOMBIA)) {
            numberFormat.setMinimumFractionDigits(0);
            numberFormat.setMaximumFractionDigits(0);
        } else if (country.equalsIgnoreCase(COUNTRY_USA)) {
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);
        }
        return numberFormat;
    }

    public static String getCountry(@NonNull Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();
    }

    //Date format simple
    @NotNull
    @Contract(" -> new")
    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", new Locale(LANGUAGE));
    }

    //return validation generic JSON service
    public static boolean validateResponseJSONService(@NotNull JSONObject response) {
        try {
            return (response.has(ConstantsApp.RESPONSE_GENERIC_CODE) && (response.getString(ConstantsApp.RESPONSE_GENERIC_CODE).equals("0")));
        } catch (JSONException e) {
            Message.logMessageException(null, e);
            return false;
        }
    }

    //return is network available is connected
    public static boolean isNetworkAvailable(Context context) {
        boolean state = false;
        try {
            if ((context instanceof MainActivity) && (((MainActivity) context).imgWiFi != null))
                ((MainActivity) context).imgWiFi.setVisibility(View.GONE);

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
            state = activeNetwork != null && activeNetwork.isConnected();
            if (!state) {
                Message.showMessage(context.getString(R.string.msg_error_no_internet), context, EnumMessage.MESSAGE_ERROR);
                if (context instanceof MainActivity && ((MainActivity) context).imgWiFi != null)
                    ((MainActivity) context).imgWiFi.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
        return state;
    }

    public static boolean isNetworkAvailableFinish(Context context) {
        boolean state = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
            state = activeNetwork != null && activeNetwork.isConnected();
            if (!state) {
                Message.showMessageFinish(context.getString(R.string.msg_error_no_internet), context, EnumMessage.MESSAGE_ERROR);
            }
        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
        return state;
    }

    //Close keyboard in context
    public static void closeKeyboard(View view, Context context) {
        try {
            if (view != null) {
                view.clearFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
    }

    //Validate Info Valid
    public static boolean isInfoValid(@NotNull String data) {
        return data.length() < 6;
    }

    //Validate Email Valid
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void openCamera(Activity activity, int codeCamera) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            activity.startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), codeCamera);
        else
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 8);
    }

    public static void openStorage(Activity activity, int codeFile, String title) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                String[] mimeTypes = {"image/*"};
                StringBuilder mimeTypesStr = new StringBuilder();
                for (String mimeType : mimeTypes) {
                    mimeTypesStr.append(mimeType).append("|");
                }
                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intent, title), codeFile);
            } else
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
        } else
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
    }

    @SuppressWarnings("deprecation")
    public static void openFrontCamera(Activity activity, int codeCamera) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
            activity.startActivityForResult(intent, codeCamera);
        } else
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 9);
    }

    @NotNull
    public static String bitMapToString(@NotNull Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT).replace("\n", "").replace("\r", "");
    }

    @Nullable
    public static Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            Message.logMessageException(null, e);
            return null;
        }
    }

    public static void loadMain(String data, String cityParameters, String token, @NotNull Context context) {
        try {
            Intent intent = getIntent(data, cityParameters, token, context);
            context.startActivity(intent);
        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
    }

    public static void loadMain(String data, String cityParameters, String token, String idService, @NotNull Context context) {
        try {
            Intent intent = getIntent(data, cityParameters, token, context);
            intent.putExtra(context.getString(R.string.extra_service_id), idService);
            context.startActivity(intent);
        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
    }

    @androidx.annotation.Nullable
    public static String getParameterByName(List<KeyValueGeneric2> data, String nameParameter) {
        try {
            for (KeyValueGeneric2 parameter : data) {
                if (parameter.getCode().equals(nameParameter))
                    return parameter.getDescription();
            }
        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
        return null;
    }

    @NotNull
    protected static Intent getIntent(String data, String cityParameters, String token, @NotNull Context context) {
        if (data != null) {
            UserMiMutual userMiMutual = new Gson().fromJson(data, new TypeToken<UserMiMutual>() {
            }.getType());
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.share_preference_mimutual), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(context.getString(R.string.extra_user_mi_mutual), data);
            editor.putString(context.getString(R.string.share_id_user), userMiMutual.getId());
            editor.putInt(context.getString(R.string.share_last_session_user), Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
            editor.putString(context.getString(R.string.share_token), token);
            editor.apply();
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("parameters", cityParameters);
        return intent;
    }


    public static String encrypted(@NotNull String data) {
        return getData(data);
    }

    protected static String getData(@NotNull String data) {
        return Base64.encodeToString(data.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
    }

    //Verify Permissions Location
    public static boolean verifyPermissions(Context context) {
        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Message.showMessage(context.getString(R.string.msg_error_gps), context, EnumMessage.MESSAGE_ERROR);
                return false;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return true;
                else {
                    final Dialog dialog = Message.getDialogQuestion(context.getString(R.string.msg_permission_location_user), context);
                    dialog.findViewById(R.id.btnOk).setOnClickListener(v -> {
                        try {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(((MainActivity) context), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_BACKGROUND);
                        } catch (Exception e) {
                            Message.logMessageException(context.getClass(), e);
                        }
                    });
                    dialog.show();
                }
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    return true;
                else {
                    final Dialog dialog = Message.getDialogQuestion(context.getString(R.string.msg_permission_location_user), context);
                    dialog.findViewById(R.id.btnOk).setOnClickListener(v -> {
                        try {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(((MainActivity) context), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                        } catch (Exception e) {
                            Message.logMessageException(context.getClass(), e);
                        }
                    });
                    dialog.show();
                }
            }
        } catch (Exception e) {
            Message.logMessageException(context.getClass(), e);
        }
        return false;
    }

    public static void sendNotification(String serviceId, String message, Context context) {
        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(), context.getString(R.string.channel_mm));
            RemoteViews viewSmall = new RemoteViews(context.getPackageName(), R.layout.notification);

            viewSmall.setTextViewText(R.id.notification_body, message);
            createNotificationChannel(context);
            notificationBuilder.setContent(viewSmall)
                    .setCustomContentView(viewSmall)
                    .setColorized(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.mimutual2))
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.parseInt(serviceId), notificationBuilder.build());

        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
    }

    private static void createNotificationChannel(Context context) {
        try {
            String description = "Notification channel MiMutual";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            //Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.mimutual2);
            //AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.channel_mm), context.getString(R.string.app_name), importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            //channel.setSound(alarmSound, audioAttributes);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        } catch (Exception e) {
            Message.logMessageException(null, e);
        }
    }

    public static void appendLog(String text, Context context) {
        if (false) {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) || (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                File directory;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    directory = new File(context.getExternalFilesDir(null) + File.separator + context.getString(R.string.app_folder) + File.separator + context.getString(R.string.app_folder_log));
                } else {
                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getString(R.string.app_folder) + File.separator + context.getString(R.string.app_folder_log));
                }

                if (createDirectory(context, directory)) return;
                File logFile = createFile(context, directory);

                try (BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true))) {
                    String valueLog = String.format("%s : %s", android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date()), text);
                    buf.append(valueLog);
                    buf.newLine();
                } catch (IOException e) {
                    Message.logMessageException(null, e);
                }
            } else {

                if (context instanceof MainActivity) {
                    ActivityCompat.requestPermissions(((MainActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
                } else if (context instanceof LoginActivity) {
                    ActivityCompat.requestPermissions(((LoginActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
                } else if (context instanceof SplashScreenActivity) {
                    ActivityCompat.requestPermissions(((SplashScreenActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
                }
//                } else if (context instanceof ChatActivity) {
//                    ActivityCompat.requestPermissions(((ChatActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
//                } else if (context instanceof CarsDriverActivity) {
//                    ActivityCompat.requestPermissions(((CarsDriverActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
//                } else if (context instanceof NewServiceActivity) {
//                    ActivityCompat.requestPermissions(((NewServiceActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
//                } else if (context instanceof ProfileActivity) {
//                    ActivityCompat.requestPermissions(((ProfileActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
//                } else if (context instanceof RegisterActivity) {
//                    ActivityCompat.requestPermissions(((RegisterActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
//                } else if (context instanceof ServicesDriverActivity) {
//                    ActivityCompat.requestPermissions(((ServicesDriverActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
//                } else if (context instanceof ServicesUserActivity) {
//                    ActivityCompat.requestPermissions(((ServicesUserActivity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsApp.PERMISSIONS_REQUEST_ACCESS_READ_WRITE);
//                }
            }
        }
    }

    //Create File for Log
    private static File createFile(@NotNull Context context, File directory) {

        File logFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            logFile = new File(context.getExternalFilesDir(null) + String.format("/%s/%s/logMM%s.txt", context.getString(R.string.app_folder), context.getString(R.string.app_folder_log), Utilities.getNow()));
        } else {
            logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + String.format("/%s/%s/logMM%s.txt", context.getString(R.string.app_folder), context.getString(R.string.app_folder_log), Utilities.getNow()));
        }

        if (!logFile.exists()) {
            try {
                File[] files = directory.listFiles();
                if (files.length >= ConstantsApp.DAYS_PURGE_LOG)
                    refineLog(context, files);
                if (!logFile.createNewFile())
                    Message.showToast(context.getString(R.string.msg_error_create_log), context);
            } catch (IOException e) {
                Message.logMessageException(context.getClass(), e);
            }
        }
        return logFile;
    }

    //Create Directory for log
    private static boolean createDirectory(Context context, File directory) {
        try {
            if (!directory.exists() && !directory.mkdirs()) {
                Message.showToast(context.getString(R.string.msg_error_create_log), context);
                return true;
            }
        } catch (Exception e) {
            Message.logMessageException(context.getClass(), e);
        }
        return false;
    }

    //Refine files logs
    private static void refineLog(Context context, File[] files) {
        try {
            for (int i = 0; i < files.length - 1; i++) {
                Date lastModified = new Date(files[0].lastModified());
                Calendar time = Calendar.getInstance();
                time.add(Calendar.DAY_OF_YEAR, ConstantsApp.DAYS_PURGE_LOG * -1);
                if (lastModified.before(time.getTime()) && !files[0].delete())
                    Message.showToast(context.getString(R.string.msg_error_refine_log), context);
            }
        } catch (Exception e) {
            Message.logMessageException(context.getClass(), e);
        }
    }

    public static String decodeImage(Context context, String image) {
        String decodeImage = null;
        try {
            if (image != null && !Objects.equals(image, EMPTY)) {
                byte[] data = Base64.decode(image.getBytes(), Base64.DEFAULT);
                decodeImage = URLDecoder.decode(new String(data, StandardCharsets.UTF_8), "UTF-8");
            }
        } catch (Exception e) {
            Message.logMessageException(context.getClass(), e);
        }
        return decodeImage;
    }

    public static void setLocale(@NonNull Activity activity) {
        Locale locale = new Locale(Locale.getDefault().getLanguage());
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
