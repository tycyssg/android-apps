package com.example.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.shoppinglist.utilities.ListAddapter;
import com.example.shoppinglist.utilities.SharedPrefActions;

public class CheckOut extends AppCompatActivity {

    private TextView balance;
    private SharedPrefActions spa = new SharedPrefActions();
    private String loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
    }

    public void run(){

    }
}