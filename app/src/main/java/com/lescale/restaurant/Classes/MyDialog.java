package com.lescale.restaurant.Classes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lescale.restaurant.R;

public class MyDialog extends Dialog {

    String message;
    Context context;

    public MyDialog(@NonNull Context context, String message) {
        super(context);
        this.message = message;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog);
        TextView meg = findViewById(R.id.msg);
        meg.setText(message);
    }
}
