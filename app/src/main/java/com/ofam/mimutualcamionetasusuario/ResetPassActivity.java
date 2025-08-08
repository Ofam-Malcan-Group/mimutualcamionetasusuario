package com.ofam.mimutualcamionetasusuario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ofam.mimutualcamionetasusuario.entities.KeyValueGeneric;
import com.ofam.mimutualcamionetasusuario.services.CallServiceRest;
import com.ofam.mimutualcamionetasusuario.services.ICallBackServiceRest;
import com.ofam.mimutualcamionetasusuario.utilities.ConstantsApp;
import com.ofam.mimutualcamionetasusuario.utilities.EnumMessage;
import com.ofam.mimutualcamionetasusuario.utilities.EnumThreads;
import com.ofam.mimutualcamionetasusuario.utilities.Message;
import com.ofam.mimutualcamionetasusuario.utilities.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResetPassActivity extends AppCompatActivity implements ICallBackServiceRest {

    private List<KeyValueGeneric> typoDocument;
    private int indexTypoDocument = -1;
    private EditText edTypeDocument;
    private AlertDialog alertDialogTypoDocument;
    private EditText edNumberId;

    //region Methods
    //Load Controls
    private void loadControls() {
        try {
            edNumberId = findViewById(R.id.edId);
            edTypeDocument = findViewById(R.id.edTypeDocument);
            edTypeDocument.setOnClickListener(v -> alertDialogTypoDocument.show());
            findViewById(R.id.btnBack).setOnClickListener(view -> onBackPressed());
            findViewById(R.id.btnContinue).setOnClickListener(v -> validateData());
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Validate data
    private void validateData() {
        try {
            cleanErrors();
            if (edNumberId.getText().toString().trim().length() < 6) {
                edNumberId.setError(getString(R.string.msg_error_invalid_number_id));
                edNumberId.requestFocus();
                return;
            } else if (indexTypoDocument == -1) {
                edTypeDocument.setError(getString(R.string.msg_select_type_doc));
                Message.showToast(getString(R.string.msg_select_type_doc), ResetPassActivity.this);
                return;
            }
            sendInfo();
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Clean Error
    private void cleanErrors() {
        try {
            edTypeDocument.setError(null);
            edNumberId.setError(null);
            Utilities.closeKeyboard(this.getCurrentFocus(), this);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    //Send Info
    protected void sendInfo() {
        try {
            if (Utilities.isNetworkAvailable(this))
                new CallServiceRest().getResetPass(this, this, edNumberId.getText().toString().trim(), typoDocument.get(indexTypoDocument).getCode().toString());
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    private void messageResetPass(@NonNull JSONObject dataJSON) {
        try {
            final Dialog dialog = Message.getDialog(dataJSON.getString(ConstantsApp.RESPONSE_GENERIC_MESSAGE), this, EnumMessage.MESSAGE_FINE);
            dialog.findViewById(R.id.btnCloseMessage).setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
            dialog.findViewById(R.id.btnOkMessage).setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
            dialog.show();
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
                loadControls();
            } else
                Message.showMessage(getString(R.string.msg_message_type_id), ResetPassActivity.this, EnumMessage.MESSAGE_ERROR);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        callServiceTypeDocument();
    }

    @Override
    public void serviceResultRest(String data, EnumThreads method, boolean cancel) {
        try {
            if (cancel)
                return;
            if (data == null || data.equals(ConstantsApp.RESPONSE_NO_SERVICE))
                Message.showToast(String.format(getString(R.string.concat_error), getString(R.string.msg_error_no_service), method), this);
            else if (method == EnumThreads.GET_TYPE_DOCUMENT)
                loadComboDocuments(data);
            else if (method == EnumThreads.GET_RESET_PASS) {
                JSONObject dataJSON = new JSONObject(data);
                if (Utilities.validateResponseJSONService(dataJSON))
                    messageResetPass(dataJSON);
                else
                    Message.showMessage(dataJSON.getString(ConstantsApp.RESPONSE_GENERIC_MESSAGE), this, EnumMessage.MESSAGE_ERROR);
            }
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        }
    }
}