package com.lescale.restaurant.AuthFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.lescale.restaurant.Activities.AuthActivity;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.ModelClasses.UserModel;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class Registration extends Fragment {


    public Registration() {
        // Required empty public constructor
    }

    private MyDialog myDialog;
    private TextInputEditText email_edit,name_edit,password_edit,confirm_pass_edit,phone_edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        email_edit = view.findViewById(R.id.email_reg);
        name_edit = view.findViewById(R.id.username_reg);
        password_edit = view.findViewById(R.id.password_reg);
        confirm_pass_edit = view.findViewById(R.id.confirm_pass_reg);
        phone_edit = view.findViewById(R.id.phone_reg);

        myDialog = AppHelper.loadDialog(getActivity(),getString(R.string.signingUP));

        view.findViewById(R.id.signup_btn).setOnClickListener(v ->
                register(Objects.requireNonNull(name_edit.getText()).toString().trim()
        , Objects.requireNonNull(email_edit.getText()).toString().trim()
        , Objects.requireNonNull(password_edit.getText()).toString()
        , Objects.requireNonNull(confirm_pass_edit.getText()).toString()
        , Objects.requireNonNull(phone_edit.getText()).toString().trim()));
        return view;
    }

    private void register(@NotNull final String name, final String email, String password, String confirm_password, final String phone) {
        if (name.isEmpty()){
            name_edit.setError(getString(R.string.name_req));
            name_edit.requestFocus();
        }
        else if (email.isEmpty()){
            email_edit.setError(getString(R.string.email_req));
            email_edit.requestFocus();
        }
        else if (password.isEmpty()){
            password_edit.setError(getString(R.string.password_req));
            password_edit.requestFocus();
        }
        else if (confirm_password.isEmpty()){
            confirm_pass_edit.setError(getString(R.string.cnfrm_pass_req));
            confirm_pass_edit.requestFocus();
        }
        else if (!password.equals(confirm_password)){
            confirm_pass_edit.setError(getString(R.string.pass_mismatch));
            confirm_pass_edit.requestFocus();
        }
        else if (password.length() < 7) {
            password_edit.setError(getString(R.string.pass_six_char));
            password_edit.requestFocus();
        } else if (phone.isEmpty()){
            phone_edit.setError(getString(R.string.num_req));
            phone_edit.requestFocus();
        }else {
                myDialog.show();
            final FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DatabaseReference reference = MyFirebaseReferences.userInfoReference();
                    UserModel model = new UserModel(name,phone,email,AppHelper.getUID());
                      reference.setValue(model).addOnCompleteListener(task1 -> {
                          if (task1.isSuccessful()){
                              AppHelper.showCenterToast(getActivity(),getString(R.string.reg_sucess));
                              auth.signOut();
                              Intent intent = new Intent(getContext(), AuthActivity.class);
                              intent.putExtra(Utils.LOGIN_TOAST_CHECK,1);
                              startActivity(intent);
                              Objects.requireNonNull(getActivity()).finish();
                          }
                      });
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
