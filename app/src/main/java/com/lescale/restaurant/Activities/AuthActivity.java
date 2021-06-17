package com.lescale.restaurant.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.lescale.restaurant.AuthFragments.Login;
import com.lescale.restaurant.AuthFragments.Registration;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.ModelClasses.UserModel;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AuthActivity extends AppCompatActivity {

    private TextView login,signup,welcome;
    private ImageView cancel_btn;
    public static final String TAG = "AuthActivity";
    private CallbackManager mCallbackManager;
    private ImageView fb, google;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        login = findViewById(R.id.login_txtview);
        signup = findViewById(R.id.signup_txtview);
        welcome = findViewById(R.id.welcome);
        cancel_btn = findViewById(R.id.cancel_img);

        final int blue = ContextCompat.getColor(this,R.color.blue);
        final int black = ContextCompat.getColor(this,R.color.black);

        signup.setOnClickListener(v -> {
            signup.setTextColor(blue);
            login.setTextColor(black);
            welcome.setText(getString(R.string.creteaccount));
            loadFragment(new Registration());
        });

        fb = findViewById(R.id.fb);
        google = findViewById(R.id.google);

        mCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        LoginManager loginManager = LoginManager.getInstance();

        google.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        fb.setOnClickListener(v -> {
            loginManager.logInWithReadPermissions(this, Arrays.asList("email"));
            loginManager.registerCallback(mCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d(TAG, "onSuccess: ");
                            handleFacebookToken(loginResult.getAccessToken());
                        }
                        @Override
                        public void onCancel() {
                            Log.d(TAG, "onCancel: ");

                        }
                        @Override
                        public void onError(FacebookException error) {
                            Log.e(TAG, "onError: ", error);
                        }
                    });
        });


        login.setOnClickListener(v -> {
            signup.setTextColor(black);
            login.setTextColor(blue);
            welcome.setText(getString(R.string.welcome_to_lescale));
            loadFragment(new Login());
        });

        cancel_btn.setOnClickListener(v -> {
            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
            finish();

        });

        findViewById(R.id.login_with_mob).setOnClickListener(v -> {
            startActivity(new Intent(AuthActivity.this,MobileRegistration.class));
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final int blue = ContextCompat.getColor(this,R.color.blue);
        final int black = ContextCompat.getColor(this,R.color.black);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int fragment = getIntent().getExtras().getInt(Utils.LOGIN_TOAST_CHECK);
            switch (fragment) {
                case 1:
                    AppHelper.showToast(AuthActivity.this,getString(R.string.plslogintocontinue));
                    signup.setTextColor(black);
                    login.setTextColor(blue);
                    welcome.setText(getString(R.string.welcome_to_lescale));
                    loadFragment(new Login());
            }
        } else {
            signup.setTextColor(black);
            login.setTextColor(blue);
            welcome.setText(getString(R.string.welcome_to_lescale));
            loadFragment(new Login());
        }

    }

    private void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.auth_frame, fragment);
        fragmentTransaction.commit();
    }

    private void handleFacebookToken(@NotNull AccessToken accessToken) {

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
            Log.d(TAG, "handleFacebookToken: ");
            FirebaseUser user = mAuth.getCurrentUser();
            DatabaseReference reference = MyFirebaseReferences.userInfoReference();
            assert user != null;
            UserModel model = new UserModel(user.getDisplayName(), user.getPhoneNumber(), user.getEmail(), user.getUid());
            reference.setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    assert account != null;
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    Log.e(TAG, "onActivityResult: ", e);
                }
            }

        }
    }

    private void firebaseAuthWithGoogle(@NotNull GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "firebaseAuthWithGoogle: ");
                        FirebaseUser user = mAuth.getCurrentUser();
                        DatabaseReference reference = MyFirebaseReferences.userInfoReference();
                        assert user != null;
                        UserModel model = new UserModel(user.getDisplayName(), user.getPhoneNumber(), user.getEmail(), user.getUid());
                        reference.setValue(model).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Intent intent = new Intent(this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                }).addOnFailureListener( e -> {
            Log.e(TAG, "firebaseAuthWithGoogle: ", e);
        });
    }
}
