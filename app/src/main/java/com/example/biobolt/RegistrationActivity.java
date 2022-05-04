package com.example.biobolt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final int SECRET_KEY = 1982;
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();

    EditText editTextUserName;
    EditText editTextPassword;
    EditText editTextPasswordAgain;
    EditText editTextEmail;
    EditText editTextAddress;
    EditText editTextPhoneNumber;

    Spinner phoneNumberSpinner;

    private SharedPreferences preferences;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 1982) {
            finish();
        }

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordAgain = findViewById(R.id.editTextPasswordAgain);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        phoneNumberSpinner = findViewById(R.id.phoneNumberSpinner);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        editTextUserName.setText(userName);
        editTextPassword.setText(password);

        phoneNumberSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.phones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneNumberSpinner.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void registration(View view) {
        String userNameStr = editTextUserName.getText().toString();
        String passwordStr = editTextPassword.getText().toString();
        String passwordAgainStr = editTextPasswordAgain.getText().toString();
        String emailStr = editTextEmail.getText().toString();
        String addressStr = editTextAddress.getText().toString();
        String phoneStr = editTextPhoneNumber.getText().toString();

        if (!passwordStr.equals(passwordAgainStr)) {
            Log.e(LOG_TAG, "A jelszó és a megerősítése nem egyezik! ");
            return;
        }

        String phoneNumber = editTextPhoneNumber.getText().toString();
        String phoneType = phoneNumberSpinner.getSelectedItem().toString();
        String address = editTextAddress.getText().toString();

        Log.i(LOG_TAG, "Regisztrált: " + userNameStr + ", email: " + emailStr);

        auth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Felhasználó sikeresen regisztrált.");
                    startShop();
                } else {
                    Log.d(LOG_TAG, "Sikertelen regisztráció!");
                    Toast.makeText(RegistrationActivity.this, "Sikertelen regisztráció: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });

        startShop();
    }

    public void cancel(View view) {
        finish();
    }

    public void startShop() {
        Intent intent = new Intent(this, ShopActivity.class);
        //intent.putExtra("SECRET_KEY", SECRET_KEY);
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
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);
        //TODO
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //TODO
    }
}