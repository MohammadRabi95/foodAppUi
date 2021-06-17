package com.lescale.restaurant.AuthFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.messaging.FirebaseMessaging;

import com.lescale.restaurant.Activities.ForgotPassword;
import com.lescale.restaurant.Activities.HomeActivity;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;

import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {

    public Login() {
    }

    private TextInputEditText email_edit;
    private TextInputEditText password_edit;
    private MyDialog myDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email_edit = view.findViewById(R.id.email_login);
        password_edit = view.findViewById(R.id.password_login);
        Button loginbtn = view.findViewById(R.id.login_btn_loginfrag);

        myDialog = AppHelper.loadDialog(getActivity(), getString(R.string.signingin));

        TextView forgotpassword = view.findViewById(R.id.forgot_pass_txtview);
        forgotpassword.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ForgotPassword.class));
            Objects.requireNonNull(getActivity()).finish();
        });
        loginbtn.setOnClickListener(v -> login(Objects.requireNonNull(email_edit.getText()).toString().trim(),
                Objects.requireNonNull(password_edit.getText()).toString()));
        return view;
    }

    private void login(@NotNull String email, String password) {
        if (email.isEmpty()) {
            email_edit.setError(getString(R.string.email_req));
            email_edit.requestFocus();
        } else if (password.isEmpty()) {
            password_edit.setError(getString(R.string.password_req));
            password_edit.requestFocus();
        } else {
            myDialog.show();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Utils.USERS_DB);
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myDialog.dismiss();
    }

}
