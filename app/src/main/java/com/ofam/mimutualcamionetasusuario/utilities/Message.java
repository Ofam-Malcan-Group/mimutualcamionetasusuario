package com.ofam.mimutualcamionetasusuario.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ofam.mimutualcamionetasusuario.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * **************************************************************************
 * NAME: Message.java
 * DESCRIPTION:  Class Messages app.
 * MODIFICACIONES:
 * VERSION      FECHA       AUTOR      REQUERIMIENTO              DESCRIPCIÓN DEL CAMBIO
 * -------  ------------  ----------  ---------------  -------------------------------------------------
 * 1.0       9/03/2021   jlondono      Creaciòn                    Creación
 * ***************************************************************************
 */
public class Message {

    //Constructor
    private Message() {
        throw new IllegalAccessError("Message class");
    }

    //Show Message
    public static void showToast(String message, Context context) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            logMessageException(context.getClass(), e);
        }
    }

    //Show Message
    public static void showMessage(String message, Context context, EnumMessage type) {
        try {
            getDialog(message, context, type).show();
        } catch (Exception e) {
            logMessageException(context.getClass(), e);
        }
    }

    //Show Message finish
    public static void showMessageFinish(String message, final Context context, EnumMessage type) {
        try {
            final Dialog dialog = getDialog(message, context, type);
            dialog.findViewById(R.id.btnCloseMessage).setOnClickListener(v -> {
                dialog.dismiss();
                ((Activity) context).finish();
            });
            dialog.findViewById(R.id.btnOkMessage).setOnClickListener(v -> {
                dialog.dismiss();
                ((Activity) context).finish();
            });
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            logMessageException(context.getClass(), e);
        }
    }

    //region Exceptions
    //Capture exception and sample log
    public static void logMessageException(Class<?> method, Exception exception) {
        try {
            if (method != null && method.getEnclosingMethod() != null)
                Log.d(ConstantsApp.LOG_MI_MUTUAL, method.getEnclosingMethod().getName() + ConstantsApp.SPACE + exception.getMessage());
            else
                Log.d(ConstantsApp.LOG_MI_MUTUAL, Objects.requireNonNull(exception.getMessage()));
        } catch (Exception e) {
            Log.d(ConstantsApp.LOG_MI_MUTUAL, e.getMessage(), e);
        }
    }

    //endregion

    //region Private Methods

    //Get custom dialog generic
    public static @NotNull Dialog getDialog(String message, Context context, EnumMessage type) {
        final Dialog dialog = new Dialog(context);
        try {
            setGenericMessageProperties(dialog, context);
            dialog.setContentView(R.layout.custom_message);
            getGenericMessage(message, dialog, type);
        } catch (Exception e) {
            logMessageException(context.getClass(), e);
        }
        return dialog;
    }

    private static void setGenericMessageProperties(Dialog dialog, Context context) {
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        } catch (Exception e) {
            logMessageException(context.getClass(), e);
        }
    }

    //control generic dialog
    private static void getGenericMessage(String message, @NotNull final Dialog dialog, EnumMessage type) {
        switch (type) {
            case MESSAGE_FINE:
                ((ImageView) dialog.findViewById(R.id.ivImage)).setImageResource(R.drawable.img_message_ok);
                break;
            case MESSAGE_ALERT:
                ((ImageView) dialog.findViewById(R.id.ivImage)).setImageResource(R.drawable.img_message_important);
                break;
            case MESSAGE_ERROR:
                ((ImageView) dialog.findViewById(R.id.ivImage)).setImageResource(R.drawable.img_message_error);
                break;
            default:
                break;
        }
        ((TextView) dialog.findViewById(R.id.txtMessage)).setText(fromHtml(message.replace("\\n", System.lineSeparator())));
        dialog.findViewById(R.id.btnCloseMessage).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnOkMessage).setOnClickListener(v -> dialog.dismiss());
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        return result;
    }


    //Show Message
    @NonNull
    public static Dialog getMessageFinding(Context context) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        try {
            setGenericMessageProperties(dialog, context);
            dialog.setContentView(R.layout.custom_message_finding);
        } catch (Exception e) {
            logMessageException(context.getClass(), e);
        }
        return dialog;
    }

    //Get custom dialog normal
    @NonNull
    public static Dialog getDialogFingerPrint(Context context) {
        final Dialog dialog = new Dialog(context);
        try {
            setGenericMessageProperties(dialog, context);
            dialog.setContentView(R.layout.custom_message_fingerprint);
        } catch (Exception e) {
            logMessageException(context.getClass(), e);
        }
        return dialog;
    }

    //Get custom dialog question
    @NonNull
    public static Dialog getDialogQuestion(String message, Context context) {
        final Dialog dialog = new Dialog(context);
        try {
            setGenericMessageProperties(dialog, context);
            dialog.setContentView(R.layout.custom_message_question);
            ((TextView) dialog.findViewById(R.id.txtMessage)).setText(Html.fromHtml(message));
            dialog.findViewById(R.id.btnCloseMessage).setOnClickListener(v -> dialog.dismiss());
        } catch (Exception e) {
            logMessageException(context.getClass(), e);
        }
        return dialog;
    }

//    @NonNull
//    public static Dialog getDialogPay(String message, Context context) {
//        final Dialog dialog = new Dialog(context);
//        try {
//            setGenericMessageProperties(dialog, context);
//            dialog.setContentView(R.layout.custom_message_pay);
//            ((TextView) dialog.findViewById(R.id.txtMessage)).setText(Html.fromHtml(message));
//            dialog.findViewById(R.id.btnEfectivo).setOnClickListener(v -> dialog.dismiss());
//            dialog.findViewById(R.id.btnTuQuery).setOnClickListener(v -> {
//                try {
//                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.ofam.tuquery");
//                    if (launchIntent != null)
//                        context.startActivity(launchIntent);//null pointer check in case package name was not found
//                    else
//                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://tuquery.com/")));
//                } catch (Exception e) {
//                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://tuquery.com/")));
//                }
//                dialog.dismiss();
//            });
//        } catch (Exception e) {
//            logMessageException(context.getClass(), e);
//        }
//        return dialog;
//    }
//
//    //Get custom dialog new car
//    @NonNull
//    public static Dialog getDialogNewCar(Context context) {
//        final Dialog dialog = new Dialog(context);
//        try {
//            setGenericMessageProperties(dialog, context);
//            dialog.setContentView(R.layout.custom_message_new_car);
//        } catch (Exception e) {
//            logMessageException(context.getClass(), e);
//        }
//        return dialog;
//    }
//
//    @NonNull
//    public static Dialog getDialogNeighbourhood(Context context) {
//        final Dialog dialog = new Dialog(context);
//        try {
//            setGenericMessageProperties(dialog, context);
//            dialog.setContentView(R.layout.custom_message_neighbourhood);
//            dialog.findViewById(R.id.btnCloseMessage).setOnClickListener(view1 -> dialog.dismiss());
//        } catch (Exception e) {
//            logMessageException(context.getClass(), e);
//        }
//        return dialog;
//    }
//
//    //Get custom dialog new driver
//    @NonNull
//    public static Dialog getDialogNewDriver(Context context) {
//        final Dialog dialog = new Dialog(context);
//        try {
//            setGenericMessageProperties(dialog, context);
//            dialog.setContentView(R.layout.custom_message_new_driver);
//            dialog.findViewById(R.id.btnCloseMessage).setOnClickListener(view1 -> dialog.dismiss());
//        } catch (Exception e) {
//            logMessageException(context.getClass(), e);
//        }
//        return dialog;
//    }
//
//    //Get custom dialog new driver
//    @NonNull
//    public static Dialog getDialogRating(Context context) {
//        final Dialog dialog = new Dialog(context);
//        try {
//            setGenericMessageProperties(dialog, context);
//            dialog.setContentView(R.layout.custom_message_rating);
//        } catch (Exception e) {
//            logMessageException(context.getClass(), e);
//        }
//        return dialog;
//    }
    //endregion
}

