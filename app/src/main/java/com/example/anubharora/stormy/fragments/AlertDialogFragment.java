package com.example.anubharora.stormy.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.anubharora.stormy.R;

/**
 * Created by anubharora on 10/18/16.
 */
public class AlertDialogFragment extends DialogFragment {




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.error_title))
                .setMessage(context.getString(R.string.error_message))
                .setPositiveButton(context.getString(R.string.dialog_positive_response), null);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
