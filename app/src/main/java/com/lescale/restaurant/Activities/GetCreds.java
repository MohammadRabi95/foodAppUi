package com.lescale.restaurant.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.ModelClasses.UserModel;
import com.lescale.restaurant.R;

public class GetCreds extends AppCompatActivity {

    private static final String TAG = "GetCreds";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_creds);
        TextInputEditText nameEdit = findViewById(R.id.name_getcred);
        TextInputEditText emailEdit = findViewById(R.id.email_getcred);

        findViewById(R.id.login_getcred).setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            String email = emailEdit.getText().toString().trim();

            if (name.isEmpty()) {
                nameEdit.setError(getString(R.string.name_req));
                nameEdit.requestFocus();
            } else if (email.isEmpty()){
                emailEdit.setError(getString(R.string.email_req));
                emailEdit.requestFocus();
            } else {
                insertIntoFirebase(name, email);
            }
        });
    }

    private void insertIntoFirebase(String name, String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        MyDialog myDialog = AppHelper.loadDialog(GetCreds.this,getString(R.string.signingin));
        myDialog.show();
        assert user != null;
        UserModel model = new UserModel(name, user.getPhoneNumber(), email, user.getUid());
        MyFirebaseReferences.userInfoReference().setValue(model).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(GetCreds.this,HomeActivity.class));
                finish();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "insertIntoFirebase: ");
            AppHelper.showToast(GetCreds.this,e.getMessage());
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
