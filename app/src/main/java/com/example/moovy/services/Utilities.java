package com.example.moovy.services;

import android.app.ProgressDialog;
import android.content.Context;

public class Utilities {

    private static ProgressDialog spinner;

    public static void makeSpinner(Context context) {
        spinner = new ProgressDialog(context);
        spinner.setMessage("Loading...");
        spinner.setTitle("Please wait");
        spinner.setIndeterminate(false);
        spinner.setCancelable(false);
        spinner.show();
    }

    public static void removeSpinner() {
        spinner.dismiss();
    }
}
