package com.example.biobolt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 1982;
    private SharedPreferences preferences;
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int RC_SIGN_IN = 2891;

    EditText userName;
    EditText password;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = findViewById(R.id.editTextUserName);
        password = findViewById(R.id.editTextPassword);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id_copy)).requestEmail().build();
        googleSignIn = GoogleSignIn.getClient(this, gso);

        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "firebaseAutWithGoogle: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch  (ApiException e) {
                Log.w(LOG_TAG, "Google sign in failed", e);
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Sikeres belépés Google fiókkal!");
                    startShop();
                } else {
                    Log.d(LOG_TAG, "Sikertelen belépés Google fiókkal!");
                    Toast.makeText(MainActivity.this, "Sikertelen belépés Google fiókkal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void login(View view) {
        String userNameStr = userName.getText().toString();
        String passwordStr = password.getText().toString();

        auth.signInWithEmailAndPassword(userNameStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Sikeres belépés!");
                    startShop();
                } else {
                    Log.d(LOG_TAG, "Sikertelen belépés!");
                    Toast.makeText(MainActivity.this, "Sikertelen belépés: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.i(LOG_TAG, "Bejelentkezett: " + userNameStr + ", jelszó: " + passwordStr);
    }

    public void loginAsGuest(View view) {
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Sikeres belépés vendégként!");
                    startShop();
                } else {
                    Log.d(LOG_TAG, "Sikertelen belépés vendégként!");
                    Toast.makeText(MainActivity.this, "Sikertelen belépés vendégként: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void loginWithGoogle(View view) {
        Intent signInIntent = googleSignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void startShop() {
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", userName.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }
}