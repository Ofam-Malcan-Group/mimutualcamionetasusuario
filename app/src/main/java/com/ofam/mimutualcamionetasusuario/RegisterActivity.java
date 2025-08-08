package com.ofam.mimutualcamionetasusuario;

import static com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp.EMPTY;
import static com.ofam.mimutualcamionetasusuario.utilities.Message.logMessageException;
import static com.ofam.mimutualcamionetasusuario.utilities.Message.showToast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.ofam.mimutualcamionetasusuario.controls.DatePickerFragment;
import com.ofam.mimutualcamionetasusuario.entities.KeyValueGeneric;
import com.ofam.mimutualcamionetasusuario.services.CallServiceRest;
import com.ofam.mimutualcamionetasusuario.services.ICallBackServiceRest;
import com.ofam.mimutualcamionetasusuario.services.ICallBackServiceRestPost;
import com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp;
import com.ofam.mimutualcamionetasusuario.utilities.EnumMessage;
import com.ofam.mimutualcamionetasusuario.utilities.EnumThreads;
import com.ofam.mimutualcamionetasusuario.utilities.Message;
import com.ofam.mimutualcamionetasusuario.utilities.Utilities;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements ICallBackServiceRest, ICallBackServiceRestPost {

    private List<KeyValueGeneric> typoDocument;
    private int indexTypoDocument = -1;
    private AlertDialog alertDialogTypoDocument;
    private EditText edTypeDocument;
    private Bitmap photoId;
    private Bitmap photoPerfil;
    private String docId;
    private String docPerfil;
    private TextView tvImageScanner;
    private EditText edName;
    private EditText edLastName;
    private EditText edNumberPhone;
    private EditText edNumberId;
    private EditText edEmail;
    private EditText edPassword;
    private EditText edPassword2;
    private EditText edDateExp;
    private EditText edAddress;
    private EditText edNomContact;
    private EditText edNumberPhoneContact;

    //Load Controls
    private void loadControls() {
        try {
            tvImageScanner = findViewById(R.id.tvImageScanner);
            edName = findViewById(R.id.edName);
            edLastName = findViewById(R.id.edLastName);
            edTypeDocument = findViewById(R.id.edTypeDocument);
            edTypeDocument.setOnClickListener(v -> alertDialogTypoDocument.show());
            edNumberId = findViewById(R.id.edId);
            edNumberPhone = findViewById(R.id.edNumberPhone);
            edEmail = findViewById(R.id.edEmail);
            edPassword = findViewById(R.id.edPassword);
            edPassword2 = findViewById(R.id.edPassword2);
            edDateExp = findViewById(R.id.edDateExp);
            edDateExp.setOnClickListener(v -> showDatePickerDialog(edDateExp, false));
            edAddress = findViewById(R.id.edAddress);
            edNomContact = findViewById(R.id.edNomContact);
            edNumberPhoneContact = findViewById(R.id.edNumberPhoneContact);

            findViewById(R.id.ivTakePhotoDocument).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utilities.openCamera(RegisterActivity.this, 1);
                    } catch (Exception e) {
                        Message.logMessageException(getClass(), e);
                    }
                }
            });
            findViewById(R.id.ivTakePhotoProfile).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utilities.openFrontCamera(RegisterActivity.this, 2);
                    } catch (Exception e) {
                        Message.logMessageException(getClass(), e);
                    }
                }
            });
            findViewById(R.id.ivFindPhotoDocument).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utilities.openStorage(RegisterActivity.this, 3, getString(R.string.txt_photo_id));
                    } catch (Exception e) {
                        logMessageException(getClass(), e);
                    }
                }
            });
            findViewById(R.id.ivFindPhotoProfile).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utilities.openStorage(RegisterActivity.this, 4, getString(R.string.txt_profile_photo));
                    } catch (Exception e) {
                        logMessageException(getClass(), e);
                    }
                }
            });
            findViewById(R.id.btnRegister).setOnClickListener(v -> validateData());
            findViewById(R.id.ibCameraScanner).setOnClickListener(v -> createLaunchOptions());
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Show Picker dialog date
    private void showDatePickerDialog(EditText editText, boolean onlyMax) {
        try {
            DatePickerFragment pickerFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month + 1) + "/" + year;
                editText.setText(selectedDate);
            }, onlyMax);
            pickerFragment.show(getSupportFragmentManager(), "datePicker");
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }


    @NotNull
    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    //Load Type Documents
    private void loadComboDocuments(String data) {
        try {
            typoDocument = new Gson().fromJson(data, new TypeToken<ArrayList<KeyValueGeneric>>() {
            }.getType());
            if (!typoDocument.isEmpty()) {
                String[] array = new String[typoDocument.size()];
                int index = 0;
                for (KeyValueGeneric value : typoDocument) {
                    array[index] = value.getDescription();
                    index++;
                }
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle(R.string.txt_select_type_id);
                mBuilder.setSingleChoiceItems(array, indexTypoDocument, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            dialogInterface.dismiss();
                            indexTypoDocument = i;
                            edTypeDocument.setText(typoDocument.get(i).getDescription());
                        } catch (Exception e) {
                            Message.logMessageException(getClass(), e);
                        }
                    }
                });
                alertDialogTypoDocument = mBuilder.create();
            } else
                Message.showMessage(getString(R.string.msg_message_type_id), RegisterActivity.this, EnumMessage.MESSAGE_ERROR);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
        loadControls();
    }

    //Load Image
    private void loadImage(@NotNull Intent data, int p) {
        try {
            InputStream in;
            in = getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
            ((ImageView) findViewById(p)).setImageBitmap(BitmapFactory.decodeStream(in));
        } catch (FileNotFoundException e) {
            ((ImageView) findViewById(p)).setImageResource(R.drawable.img_document);
        }
    }

    //Validate data
    private void validateData() {
        try {
            cleanErrors();
            if (edName.getText().toString().trim().length() <= 1 || !edName.getText().toString().trim().matches(getString(R.string.ex_reg_name))) {
                setError(edName, R.string.msg_error_invalid_name);
                tvImageScanner.setError(getString(R.string.txt_scan_doc_or_name));
                return;
            } else if (edLastName.getText().toString().trim().length() <= 1 || !edLastName.getText().toString().trim().matches(getString(R.string.ex_reg_name))) {
                setError(edLastName, R.string.msg_error_invalid_last_name);
                tvImageScanner.setError(getString(R.string.txt_scan_doc_or_surname));
                return;
            } else if (indexTypoDocument == -1) {
                edTypeDocument.setError(getString(R.string.msg_select_type_doc));
                showToast(getString(R.string.msg_select_type_doc), RegisterActivity.this);
                return;
            } else if (edNumberId.getText().toString().trim().length() < 6) {
                setError(edNumberId, R.string.msg_error_invalid_number_id);
                tvImageScanner.setError(getString(R.string.txt_scan_doc_or_id));
                return;
            } else if (edDateExp.getText().toString().trim().length() < 6) {
                setError(edDateExp, R.string.msg_error_invalid_date);
                return;
            } else if (!edNumberPhone.getText().toString().trim().matches(getString(R.string.ex_reg_tel))) {
                setError(edNumberPhone, R.string.msg_error_invalid_number_phone);
                return;
            } else if (edEmail.getText().toString().trim().length() <= 6 || !Utilities.isValidEmail(edEmail.getText().toString().trim())) {
                setError(edEmail, R.string.msg_error_invalid_email);
                return;
            } else if (edPassword.getText().toString().trim().length() < 4 || edPassword2.getText().toString().trim().length() < 4) {
                setError(edPassword2, R.string.msg_error_pass_min);
                return;
            } else if (!edPassword.getText().toString().trim().equals(edPassword2.getText().toString().trim())) {
                setError(edPassword2, R.string.msg_error_pass_equal);
                return;
            } else if (!edPassword.getText().toString().trim().matches(getString(R.string.ex_reg_pass))) {
                setError(edPassword2, R.string.msg_error_pass_character);
                return;
            } else if (photoPerfil == null && docPerfil == null) {
                showToast(getString(R.string.txt_photo_background_white), this);
                return;
            } else if (photoId == null && docId == null) {
                showToast(getString(R.string.txt_photo_obligatory), this);
                return;
            } else if (edNomContact.getText().toString().trim().length() <= 1 || !edNomContact.getText().toString().trim().matches(getString(R.string.ex_reg_name))) {
                setError(edNomContact, R.string.msg_error_invalid_name);
                return;
            } else if (!edNumberPhoneContact.getText().toString().trim().matches(getString(R.string.ex_reg_tel))) {
                setError(edNumberPhoneContact, R.string.msg_error_invalid_number_phone);
                return;
            }
            sendInfo();
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //set Error
    private void setError(@NotNull EditText edName, int p) {
        edName.setError(getString(p));
        edName.requestFocus();
    }

    //Clean Error
    private void cleanErrors() {
        try {
            tvImageScanner.setError(null);
            edName.setError(null);
            edLastName.setError(null);
            edTypeDocument.setError(null);
            edNumberId.setError(null);
            edNumberPhone.setError(null);
            edEmail.setError(null);
            edPassword.setError(null);
            edPassword2.setError(null);
            edDateExp.setError(null);
            edAddress.setError(null);
            edNomContact.setError(null);
            edNumberPhoneContact.setError(null);
            Utilities.closeKeyboard(this.getCurrentFocus(), this);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Send Info
    protected void sendInfo() {
        try {
            if (Utilities.isNetworkAvailable(this)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nombre", edName.getText().toString().trim());
                jsonObject.put("apellido", edLastName.getText().toString().trim().equals(EMPTY) ? "ApellidoPrueba" : edLastName.getText().toString().trim());
                jsonObject.put("telefono", edNumberPhone.getText().toString().trim());
                jsonObject.put("tipoDoc", indexTypoDocument == -1 ? "1" : typoDocument.get(indexTypoDocument).getCode());
                jsonObject.put("identificacion", edNumberId.getText().toString().trim());
                jsonObject.put("email", edEmail.getText().toString().trim().equals(EMPTY) ? "EmailPrueba" : edEmail.getText().toString().trim());
                jsonObject.put("pass", encrypted(edPassword.getText().toString().trim()));
                jsonObject.put("fechaExp", !edDateExp.getText().toString().trim().equals(EMPTY) ? edDateExp.getText().toString().trim().replace("/", "") : getString(R.string.txt_date_base));
                loadPhotos(jsonObject);
                jsonObject.put("rol", "2");
                jsonObject.put("direccion", edAddress.getText().toString().trim().equals(EMPTY) ? "DirPrueba" : edAddress.getText().toString().trim());
                jsonObject.put("ciudad", "5619");
                jsonObject.put("nombContEmerg", edNomContact.getText().toString().trim().equals(EMPTY) ? "ContEmergPrueba" : edNomContact.getText().toString().trim());
                jsonObject.put("telEmerg", edNumberPhoneContact.getText().toString().trim().equals(EMPTY) ? "3131234567" : edNumberPhoneContact.getText().toString().trim());
                jsonObject.put("idTipoPersona", 1);
                new CallServiceRest().postRegister(this, jsonObject, this);
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    protected void loadPhotos(JSONObject jsonObject) {
        try {
            if (docId != null)
                jsonObject.put("photoId", photoId != null ? Utilities.bitMapToString(photoId) : docId);
            else
                jsonObject.put("photoId", photoId != null ? Utilities.bitMapToString(photoId) : EMPTY);
            if (docPerfil != null)
                jsonObject.put("photoPerfil", photoPerfil != null ? Utilities.bitMapToString(photoPerfil) : docPerfil);
            else
                jsonObject.put("photoPerfil", photoPerfil != null ? Utilities.bitMapToString(photoPerfil) : EMPTY);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //encrypted
    protected static String encrypted(@NotNull String data1) {
        byte[] data = data1.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    //create launch option camera
    private void createLaunchOptions() {
        try {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.PDF_417);
            options.setPrompt(getString(R.string.txt_escaner_id));
            options.setCameraId(0);
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(false);
            barcodeLauncher.launch(options);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null)
            Toast.makeText(RegisterActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
        else
            readData(result.getContents());
    });

    //Read data
    protected void readData(@NonNull String result) {
        try {
            int longResult = result.length();
            if (longResult > 450) {
                char[] characters = result.substring(48, 65).toCharArray();
                StringBuilder idResult = new StringBuilder();
                int posFinalId = 0;
                for (char character : characters) {
                    posFinalId++;
                    if (Character.isDigit(character))
                        idResult.append(character);
                    else
                        break;
                }
                if (!idResult.toString().equals(EMPTY)) {
                    edNumberId.setText(String.valueOf(Integer.parseInt(idResult.toString())));
                    edNumberId.setEnabled(false);
                    getData(result.substring(47 + posFinalId, longResult));
                } else
                    showToast(getString(R.string.msg_error_data_doc), this);
            } else {
                edNumberId.setText(EMPTY);
                showToast(getString(R.string.msg_error_data_doc), this);
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //get data
    private void getData(@NotNull String charactersName) {
        try {
            StringBuilder nameResult = new StringBuilder();
            for (char character : charactersName.toCharArray()) {
                if (!Character.isDigit(character)) {
                    nameResult.append(character);
                } else {
                    String nameComplete = nameResult.toString().trim().replaceAll("[^a-zA-ZñÑáéíóúÁÉÍÓÚ ]", ConstantsApp.SPACE);
                    nameComplete = nameComplete.trim().replaceAll(" +", ConstantsApp.SPACE).replaceAll("[^a-zA-ZñÑáéíóúÁÉÍÓÚ ]", EMPTY);
                    String[] partsName = nameComplete.split(ConstantsApp.SPACE);
                    switch (partsName.length) {
                        case 2:
                            edLastName.setText(partsName[0]);
                            edName.setText(partsName[1]);
                            break;
                        case 3:
                            edLastName.setText(String.format(getString(R.string.format_concat), partsName[0], partsName[1]));
                            edName.setText(partsName[2]);
                            break;
                        case 4:
                            edLastName.setText(String.format(getString(R.string.format_concat), partsName[0], partsName[1]));
                            edName.setText(String.format(getString(R.string.format_concat), partsName[2], partsName[3]));
                            break;
                        default:
                            break;
                    }
                    edLastName.setEnabled(false);
                    edName.setEnabled(false);

                    int i = 0;
                    for (KeyValueGeneric document : typoDocument) {
                        if (document.getDescription().contains("Cédula")) {
                            indexTypoDocument = i;
                            edTypeDocument.setText(typoDocument.get(i).getDescription());
                            edTypeDocument.setEnabled(false);
                            return;
                        }
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    private void callServiceTypeDocument() {
        try {
            if (Utilities.isNetworkAvailable(this))
                new CallServiceRest().getTypeDocs(this, this);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    @NonNull
    private byte[] getBytesFile(@NotNull Intent data) throws IOException {
        InputStream is = getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
        byte[] bytesArray = new byte[Objects.requireNonNull(is).available()];
        if (is.read(bytesArray) > 0)
            return bytesArray;
        return new byte[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        callServiceTypeDocument();
        findViewById(R.id.btnBack).setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void serviceResultRest(String data, EnumThreads method, boolean cancel) {
        try {
            if (cancel)
                return;
            if (data == null || data.equals(ConstantsApp.RESPONSE_NO_SERVICE))
                showToast(String.format(getString(R.string.concat_error), getString(R.string.msg_error_no_service), method), this);
            else if (method == EnumThreads.GET_TYPE_DOCUMENT)
                loadComboDocuments(data);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    @Override
    public void serviceResultRestPost(String data, EnumThreads method, boolean cancel) {
        try {
            if (!cancel) {
                if (data == null || data.equals(ConstantsApp.RESPONSE_NO_SERVICE))
                    showToast(String.format(getString(R.string.concat_error), getString(R.string.msg_error_no_service), method), this);
                else if (method == EnumThreads.POST_REGISTER) {
                    JSONObject object = new JSONObject(data);
                    if (Utilities.validateResponseJSONService(object))
                        Message.showMessageFinish(object.getString(ConstantsApp.RESPONSE_GENERIC_MESSAGE), this, EnumMessage.MESSAGE_FINE);
                    else
                        Message.showMessage(object.getString(ConstantsApp.RESPONSE_GENERIC_MESSAGE), this, EnumMessage.MESSAGE_ERROR);
                }
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == 8) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Utilities.openCamera(RegisterActivity.this, 1);
                else
                    Message.showMessage(getString(R.string.msg_no_permissions), this, EnumMessage.MESSAGE_ERROR);
            } else if (requestCode == 9) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Utilities.openFrontCamera(RegisterActivity.this, 2);
                else
                    Message.showMessage(getString(R.string.msg_no_permissions), this, EnumMessage.MESSAGE_ERROR);
            }
        } catch (Exception e) {
            logMessageException(getClass(), e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK) {
                photoId = (Bitmap) Objects.requireNonNull(data.getExtras()).get(getString(R.string.intent_Data_Camera));
                ((ImageView) findViewById(R.id.ivPhotoDocument)).setImageBitmap(photoId);
                findViewById(R.id.ivPhotoDocument).setVisibility(View.VISIBLE);
                findViewById(R.id.llPhotoDocument).setVisibility(View.GONE);
            } else if (requestCode == 2 && resultCode == RESULT_OK) {
                photoPerfil = (Bitmap) Objects.requireNonNull(data.getExtras()).get(getString(R.string.intent_Data_Camera));
                ((ImageView) findViewById(R.id.ivPhotoProfile)).setImageBitmap(photoPerfil);
                findViewById(R.id.ivPhotoProfile).setVisibility(View.VISIBLE);
                findViewById(R.id.llPhotoProfile).setVisibility(View.GONE);
            } else if (requestCode == 3 && resultCode == RESULT_OK) {
                if (data == null) {
                    showToast(getString(R.string.msg_file_error), this);
                    return;
                }
                findViewById(R.id.ivPhotoDocument).setVisibility(View.VISIBLE);
                findViewById(R.id.llPhotoDocument).setVisibility(View.GONE);
                docId = Base64.encodeToString(getBytesFile(data), Base64.DEFAULT).replace("\n", "").replace("\r", "");
                loadImage(data, R.id.ivPhotoDocument);
            } else if (requestCode == 4 && resultCode == RESULT_OK) {
                if (data == null) {
                    showToast(getString(R.string.msg_file_error), this);
                    return;
                }
                findViewById(R.id.ivPhotoProfile).setVisibility(View.VISIBLE);
                findViewById(R.id.llPhotoProfile).setVisibility(View.GONE);
                docPerfil = Base64.encodeToString(getBytesFile(data), Base64.DEFAULT).replace("\n", "").replace("\r", "");
                loadImage(data, R.id.ivPhotoProfile);
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

}