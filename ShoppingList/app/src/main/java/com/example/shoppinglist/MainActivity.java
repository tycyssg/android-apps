package com.example.shoppinglist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shoppinglist.models.User;
import com.example.shoppinglist.utilities.SharedPrefActions;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPrefActions spa = new SharedPrefActions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        run();
    }

    public void run(){
        final LinearLayout loginLayout,registerLayout;
        Button registerButton , loginButton;

        TextView errorText = findViewById(R.id.errorText);
        loginLayout = findViewById(R.id.loginLayout);
        registerLayout = findViewById(R.id.registerLayout);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);
                register();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                errorText.setText("");//Reset the logs
                final EditText username =  findViewById(R.id.loginUsernameInput);
                final EditText password =  findViewById(R.id.loginPasswordInput);
                User user = spa.getUserByUsername(username.getText().toString(),getApplicationContext());

                if(user == null){
                    errorText.setText("Invalid details");
                }else if(!password.getText().toString().equals(user.getPassword())){
                    errorText.setText("Invalid details");
                }else{
                    Intent intent = new Intent(MainActivity.this,ShoppingListPanel.class);
                    intent.putExtra("loggedUser",user.getUsername());
                    startActivity(intent);
                }

            }
        });
    }

    public void register(){
        Button registerButton, clearButton, switchToLogin;
        registerButton = findViewById(R.id.submitRegisterButton);
        switchToLogin = findViewById(R.id.switchToLogin);

        clearButton = findViewById(R.id.clearButton);
        final TextView errorText = findViewById(R.id.errorTextRegister);
        final EditText username =  findViewById(R.id.registerUsernameInput);
        final EditText password =  findViewById(R.id.registerPasswordInput);
        final EditText repeatPassword =  findViewById(R.id.registerRepeatPasswordInput);
        errorText.setText("");//Reset the logs
        final LinearLayout loginLayout,registerLayout;

        loginLayout = findViewById(R.id.loginLayout);
        registerLayout = findViewById(R.id.registerLayout);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields(username,password,repeatPassword,errorText);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                if(user.length() < 6){
                    errorText.setText("Username has to be at least 6 characters long");
                }else if(spa.exitUser(user,getApplicationContext())){
                    errorText.setText("Username already exist!");
                }else{
                    String pass = password.getText().toString();
                    String passRepeat = repeatPassword.getText().toString();

                    if (pass.length() < 6) {
                        errorText.setText("Password has to be at least 6 characters long");
                    } else if(pass.matches(".*\\s+.*")){
                        errorText.setText("Password cannot contain space");
                    }else if(!pass.equals(passRepeat)){
                        errorText.setText("Password are not match!");
                    }else{

                        List<User> users = spa.loadData(getApplicationContext());
                        int beforeInsert = users.size();
                        users.add(new User(user,pass));
                        spa.save(users,getApplicationContext());
                        int after = users.size();
                        if(after > beforeInsert){
                            errorText.setText("Account successfully created!");
                            errorText.setTextColor(Color.parseColor("#FF1E8A00"));
                            clearFields(username,password,repeatPassword,errorText);
                        }else{
                            errorText.setText("ERROR! Account not created!");
                        }
                    }
                }
            }
        });

        switchToLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                registerLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                clearFields(username,password,repeatPassword,errorText);
            }
        });
    }

    public void clearFields(EditText username,EditText password,EditText repeatPassword,TextView errorText){
        username.setText("");
        password.setText("");
        repeatPassword.setText("");
        errorText.setText("");
    }


}