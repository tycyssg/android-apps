package com.example.calculatetip;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        run();
    }

    public void run(){
        final EditText billInput = findViewById(R.id.billTextId);
        final EditText tipInput = findViewById(R.id.tipTextId);
        final TextView tipInputResult = findViewById(R.id.tipResultId);
        final TextView totalResult = findViewById(R.id.totalId);
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                int tipValue = Integer.parseInt(tipInput.getText().toString());
                double billValue = Double.parseDouble(billInput.getText().toString());

                double calculateTip = (billValue * tipValue) / 100;
                tipInputResult.setText(Double.toString(calculateTip));
                totalResult.setText(Double.toString(calculateTip + billValue));
            }
        });

    }
}
