package com.example.stories;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stories.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView errorText, successText;
    Button signUpButton, loginButton, registerButton, cancelLoginButton, cancelRegisterButton, loginSignUpPageId;
    LinearLayout loginLayout, registerLayout;
    EditText loginEmail, loginPassword, registerName, registerPhone, registerEmail, registerPassword;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        run();
    }

    private void run() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        checkIfUserExistsOrIsLoggedIn();
        initializeComponents();
        switchLoginToRegister();
        switchRegisterToLogin();
        onRegister();
        onLogin();
        hideErrorMessage();
        hideSuccessMessage();
        onClearRegisterForm();
        onClearLoginForm();
    }

    private void initializeComponents() {
        signUpButton = findViewById(R.id.signUpButtonId);
        loginSignUpPageId = findViewById(R.id.loginSignUpPageId);
        loginLayout = findViewById(R.id.loginLayoutId);
        registerLayout = findViewById(R.id.registerLayoutId);
        loginEmail = findViewById(R.id.emailLoginId);
        loginPassword = findViewById(R.id.loginPasswordId);
        registerName = findViewById(R.id.fullNameRegisterId);
        registerPhone = findViewById(R.id.phoneRegisterId);
        registerEmail = findViewById(R.id.emailRegisterId);
        registerPassword = findViewById(R.id.passwordRegisterId);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        cancelLoginButton = findViewById(R.id.cancelLoginButton);
        cancelRegisterButton = findViewById(R.id.cancelRegisterButton);
        errorText = findViewById(R.id.errorTextViewId);
        successText = findViewById(R.id.successTextViewId);
        progressBar = findViewById(R.id.progressBarId);

    }

    private void checkIfUserExistsOrIsLoggedIn() {
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }
    }

    private void switchLoginToRegister() {
        loginSignUpPageId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToRegister(registerLayout, loginLayout);
            }
        });
    }

    private void switchRegisterToLogin() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerToLogin(registerLayout, loginLayout);
            }
        });
    }

    private void onRegister() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = registerEmail.getText().toString().trim();
                final String phone = registerPhone.getText().toString().trim();
                final String fullName = registerName.getText().toString();
                String password = registerPassword.getText().toString();
                hideErrorMessage();
                hideSuccessMessage();

                if (TextUtils.isEmpty(email)) {
                    showErrorMessage("Email Required!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    showErrorMessage("Password Required!");
                    return;
                }

                if (password.length() < 6) {
                    showErrorMessage("Password with minimum 6 characters is required!");
                    return;
                }
                showProgressBar();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showSuccessMessage("User successfully created!");
                            String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            addUserDetails(userId, new User(fullName, email, phone, "https://robohash.org/" + fullName.trim() + "?set=set3", userId));
                            loginToRegister(registerLayout, loginLayout);
                        } else {
                            showErrorMessage("Error " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                        hideProgressBar();
                    }
                });


            }
        });
    }

    private void onLogin() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                hideErrorMessage();
                hideSuccessMessage();

                if (TextUtils.isEmpty(email)) {
                    showErrorMessage("Email Required!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    showErrorMessage("Password Required!");
                    return;
                }

                showProgressBar();

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "You successfully logged in!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showErrorMessage("Error " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                        hideProgressBar();
                    }
                });
            }
        });
    }

    private void showErrorMessage(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        errorText.setVisibility(View.GONE);
    }

    private void showSuccessMessage(String message) {
        successText.setText(message);
        successText.setVisibility(View.VISIBLE);
    }

    private void hideSuccessMessage() {
        successText.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void registerToLogin(LinearLayout registerLayout, LinearLayout loginLayout) {
        registerLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);
    }

    private void loginToRegister(LinearLayout registerLayout, LinearLayout loginLayout) {
        registerLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    private void onClearRegisterForm() {
        cancelRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerEmail.setText("");
                registerPhone.setText("");
                registerName.setText("");
                registerPassword.setText("");
            }
        });
    }

    private void onClearLoginForm() {
        cancelLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmail.setText("");
                loginPassword.setText("");
            }
        });
    }

    private void addUserDetails(String userId, User user) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Profile successfully created!", Toast.LENGTH_LONG).show();
            }
        });
    }
}