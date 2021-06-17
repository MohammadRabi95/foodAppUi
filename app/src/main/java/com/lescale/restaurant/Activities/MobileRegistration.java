package com.lescale.restaurant.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.Normalizer2;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MobileRegistration extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private FirebaseAuth mAuth;

    private static final String TAG = "MobileRegistration";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    EditText otpCodeEdit;
    TextInputEditText phoneEdit;
    Button sendBtn, verifyBtn, resendBtn;
    TextInputLayout textInputLayout;
    MyDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_registration);

        otpCodeEdit = findViewById(R.id.otp_edit);
        phoneEdit = findViewById(R.id.phone_otp);
        sendBtn = findViewById(R.id.login_otp);
        verifyBtn = findViewById(R.id.verify_otp);
        resendBtn = findViewById(R.id.resend_otp);
        textInputLayout = findViewById(R.id.a);

        sendBtn.setOnClickListener(this);
        resendBtn.setOnClickListener(this);
        verifyBtn.setOnClickListener(this);

        myDialog = AppHelper.loadDialog(MobileRegistration.this,"");

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);

                mVerificationInProgress = false;

                updateUI(STATE_VERIFY_SUCCESS, credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);

                mVerificationInProgress = false;


                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    AppHelper.showToast(MobileRegistration.this,e.getMessage());

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }

                updateUI(STATE_VERIFY_FAILED);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);

                mVerificationId = verificationId;
                mResendToken = token;

                updateUI(STATE_CODE_SENT);

            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(phoneEdit.getText().toString().trim());
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {

        if (isMobileNumberCorrect(phoneNumber)) {
           phoneNumber = countryCode(phoneNumber);
        } else {
            phoneEdit.setError(getString(R.string.invalidphone));
            return;
        }
            myDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);


        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        myDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        if (isMobileNumberCorrect(phoneNumber)) {
            phoneNumber = countryCode(phoneNumber);
        } else {
            phoneEdit.setError(getString(R.string.invalidphone));
            return;
        }
        myDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();

                        updateUI(STATE_SIGNIN_SUCCESS, user);

                    } else {

                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            otpCodeEdit.setError("Invalid code.");

                        }

                        updateUI(STATE_SIGNIN_FAILED);

                    }
                });
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                break;
            case STATE_CODE_SENT:
                myDialog.dismiss();
                textInputLayout.setVisibility(View.GONE);
                otpCodeEdit.setVisibility(View.VISIBLE);
                verifyBtn.setVisibility(View.VISIBLE);
                phoneEdit.setVisibility(View.GONE);
                sendBtn.setVisibility(View.GONE);
                resendBtn.setVisibility(View.GONE);
                break;
            case STATE_VERIFY_FAILED:
                myDialog.dismiss();
                textInputLayout.setVisibility(View.VISIBLE);
                phoneEdit.setVisibility(View.VISIBLE);
                sendBtn.setVisibility(View.GONE);
                resendBtn.setVisibility(View.VISIBLE);
                otpCodeEdit.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.GONE);
                break;
            case STATE_VERIFY_SUCCESS:
                myDialog.dismiss();
                textInputLayout.setVisibility(View.GONE);
                phoneEdit.setVisibility(View.GONE);
                sendBtn.setVisibility(View.GONE);
                otpCodeEdit.setVisibility(View.VISIBLE);
                resendBtn.setVisibility(View.GONE);
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        otpCodeEdit.setText(cred.getSmsCode());
                    } else {

                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                textInputLayout.setVisibility(View.VISIBLE);
                phoneEdit.setVisibility(View.VISIBLE);
                phoneEdit.setText("");
                otpCodeEdit.setText("");
                sendBtn.setVisibility(View.GONE);
                resendBtn.setVisibility(View.VISIBLE);
                otpCodeEdit.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.GONE);
                myDialog.dismiss();
                AppHelper.showToast(this,"Sign in Failed");
                break;
            case STATE_SIGNIN_SUCCESS:
                myDialog.dismiss();
                check();
                break;
        }

    }

    private boolean validatePhoneNumber() {
        String phoneNumber =phoneEdit.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneEdit.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_otp:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(phoneEdit.getText().toString().trim());
                break;

            case R.id.verify_otp:
                String code = otpCodeEdit.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    otpCodeEdit.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;

            case R.id.resend_otp:
                resendVerificationCode(phoneEdit.getText().toString().trim(), mResendToken);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void check() {

        MyFirebaseReferences.userInfoReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (isUserAlreadyBeenRegistered(dataSnapshot.child("name").getValue(String.class))) {
                        startActivity(new Intent(MobileRegistration.this, HomeActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(MobileRegistration.this, GetCreds.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Contract(pure = true)
    private boolean isUserAlreadyBeenRegistered(@NotNull String name) {
        if (name.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isMobileNumberCorrect(@NotNull String num) {
        if (num.length() == 8) {
            return true;
        }
        return false;
    }

    @NotNull
    @Contract(pure = true)
    private String countryCode(String num) {
        return "+222" + num;
    }
}

