package com.lescale.restaurant.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;

public class ForgotPassword extends AppCompatActivity {

    private TextInputEditText email_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email_edit = findViewById(R.id.email_forgot);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPassword.this,AuthActivity.class));
            finish();
        });

        findViewById(R.id.submit_forgotpass).setOnClickListener(v -> sendEmail(email_edit.getText().toString().trim()));
    }

    private void sendEmail(@NotNull String email) {
        if (email.isEmpty()){
            email_edit.setError(getString(R.string.email_req));
            email_edit.requestFocus();
        }else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    AppHelper.showToast(ForgotPassword.this,getString(R.string.restpasslink));
                    startActivity(new Intent(ForgotPassword.this,HomeActivity.class));
                    finish();
                }
            });
        }
    }
}
