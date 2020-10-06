package com.example.shoppinglist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shoppinglist.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String userList= "USER_LIST";
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
                final EditText username =  findViewById(R.id.loginUsernameInput);
                final EditText password =  findViewById(R.id.loginPasswordInput);
                User user = getUserByUsername(username.getText().toString());

                if(user == null){
                    errorText.setText("Invalid details");
                }else if(!password.getText().toString().equals(user.getPassword())){
                    errorText.setText("Invalid details");
                }else{
                    errorText.setText("Login successfully!");
                    errorText.setTextColor(Color.parseColor("#FF1E8A00"));
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

        final LinearLayout loginLayout,registerLayout;

        loginLayout = findViewById(R.id.loginLayout);
        registerLayout = findViewById(R.id.registerLayout);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields(username,password,repeatPassword);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                if(user.length() < 6){
                    errorText.setText("Username has to be at least 6 characters long");
                }else if(exitUser(user)){
                    errorText.setText("Username already exist!");
                }else{
                    String pass = password.getText().toString();
                    String passRepeat = repeatPassword.getText().toString();

                    if (pass.length() < 6) {
                        errorText.setText("Password has to be at least 6 characters long");
                    } else if(pass.contains("\\s")){
                        errorText.setText("Password cannot contain space");
                    }else if(!pass.equals(passRepeat)){
                        errorText.setText("Password are not match!");
                    }else{

                        List<User> users = loadData();
                        int beforeInsert = users.size();
                        users.add(new User(user,pass));
                        save(users);
                        int after = users.size();
                        if(after > beforeInsert){
                            errorText.setText("Account successfully created!");
                            errorText.setTextColor(Color.parseColor("#FF1E8A00"));
                            clearFields(username,password,repeatPassword);
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
                clearFields(username,password,repeatPassword);
            }
        });
    }

    public void clearFields(EditText username,EditText password,EditText repeatPassword){
        username.setText("");
        password.setText("");
        repeatPassword.setText("");
    }

    public void save(List<User> users){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(users);
        editor.putString(userList,json);
        editor.apply();
    }

    public List<User> loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(userList,null);
        Type type = new TypeToken<List<User>>(){}.getType();
        List<User> users = gson.fromJson(json,type);
        if(users == null){
            return  new ArrayList<>();
        }
        return users;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean exitUser(String user){
        List<User> users = loadData();
        if(users.isEmpty())
            return false;

        return users.stream().anyMatch(s -> s.getUsername().equals(user));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public User getUserByUsername(String user){
        List<User> users = loadData();
        if(users.isEmpty())
            return null;

        return users.stream().filter(s -> s.getUsername().equals(user)).findAny().orElse(null);
    }
}