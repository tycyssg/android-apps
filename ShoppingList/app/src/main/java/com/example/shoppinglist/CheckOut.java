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
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppinglist.models.User;
import com.example.shoppinglist.utilities.ListAddapter;
import com.example.shoppinglist.utilities.SharedPrefActions;

import java.util.ArrayList;
import java.util.List;

public class CheckOut extends AppCompatActivity {

    private TextView balance;
    private TextView items;
    private SharedPrefActions spa = new SharedPrefActions();
    private String loggedUser;
    private EditText cardNumber, expireDate, ccv, cardName;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        run();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run(){
        Button checkOut = findViewById(R.id.makePaymentId);
        checkOut.setBackgroundColor(Color.parseColor("#ff669900"));
        balance = findViewById(R.id.cartBalanceId);
        items = findViewById(R.id.cartItemsId);
        loggedUser = getIntent().getExtras().getString("loggedUser");

        cardName = findViewById(R.id.cardNameId);
        cardNumber = findViewById(R.id.cardNumberId);
        expireDate = findViewById(R.id.expireDateId);
        ccv = findViewById(R.id.ccvId);

        User u = spa.getUserByUsername(loggedUser,getApplicationContext());
        String calculatedBalance = spa.calculateTheBalanceOfUserList(u.getUserShoppingList());

        updateBalanceAndItems(calculatedBalance,u.getUserShoppingList().size()+"");

        double balanceNum = Double.parseDouble(calculatedBalance.replace("$",""));

        if(balanceNum == 0){
            checkOut.setEnabled(false);
            checkOut.setBackgroundColor(Color.parseColor("#ededed"));
        }

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNum = cardNumber.getText().toString();
                String cName = cardName.getText().toString();
                String expDate = expireDate.getText().toString();
                String ccvNum = ccv.getText().toString();



                if(cardNum.length() != 16){
                    Toast.makeText(getApplicationContext(),"Invalid card number",Toast.LENGTH_LONG).show();
                    return;
                }

                if(cName.length() == 0){
                    Toast.makeText(getApplicationContext(),"The name from the card is required",Toast.LENGTH_LONG).show();
                    return;
                }

                if(!expDate.matches("\\d{2}\\/\\d{2}")){
                    Toast.makeText(getApplicationContext(),"Invalid date!",Toast.LENGTH_LONG).show();
                    return;
                }

                if(ccvNum.length() != 3){
                    Toast.makeText(getApplicationContext(),"Invalid CCV code!",Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getApplicationContext(),"SUCCESS! Your payment is complete!",Toast.LENGTH_LONG).show();
                balance.setText("");
                items.setText("");
                u.setUserShoppingList(new ArrayList<>());

                List<User> userList = spa.loadData(getApplicationContext());
                for (int i = 0; i < userList.size(); i++) {
                    if(userList.get(i).getUsername().equals(loggedUser)){
                        userList.set(i,u);
                    }
                }

                spa.save(userList,getApplicationContext());
                try {
                    Thread.sleep(3000);
                    redirectToShoppingList();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateBalanceAndItems(String bl,String it){
        balance.setText(bl);
        items.setText(it);
    }

    public void redirectToShoppingList(){
        Intent intent = new Intent(CheckOut.this,ShoppingListPanel.class);
        intent.putExtra("loggedUser", loggedUser);
        startActivity(intent);
    }
}