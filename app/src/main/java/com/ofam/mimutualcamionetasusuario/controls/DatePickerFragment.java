package com.ofam.mimutualcamionetasusuario.controls;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private boolean onlyMax;

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener, boolean onlyMax) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(listener, onlyMax);
        return fragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener, boolean onlyMax) {
        this.listener = listener;
        this.onlyMax = onlyMax;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), listener, year, month, day);
        if (onlyMax) {
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            c.set(Calendar.YEAR, year + 60);
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        } else {
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            c.set(Calendar.YEAR, year - 60);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        }
        return datePickerDialog;
    }

}
