package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shoppinglist.models.ListAddapter;
import com.example.shoppinglist.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListPanel extends AppCompatActivity {
    TextView itemsCount;
    TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_panel);
        run();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.shopNowid:
                break;
            case R.id.goCartid:
                break;
            case R.id.logOutId:logOut();
                break;
        }

        return true;
    }

    public void run(){
        itemsCount = findViewById(R.id.itemsAddedId);
        balance = findViewById(R.id.balanceId);
        //String loggedUser = getIntent().getExtras().getString("loggedUser");
        //displayWelcome.setText("Welcome "+ loggedUser);
        listSettings();
    }

    public void logOut(){
        Intent intent = new Intent(ShoppingListPanel.this,MainActivity.class);
        startActivity(intent);
    }

    public List<Product> generateShopList(){
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("Apples",1.0,R.drawable.apple));
        productList.add(new Product("Asparagus",.5,R.drawable.asparagus));
        productList.add(new Product("Avocado",3.0,R.drawable.avocado));
        productList.add(new Product("Baby Bottle",7.0,R.drawable.babybottle));
        productList.add(new Product("Bacon",5.0,R.drawable.bacon));
        productList.add(new Product("Banana",1.5,R.drawable.banana));
        productList.add(new Product("Banana Split",9.0,R.drawable.bananasplit));
        productList.add(new Product("Bar",4.0,R.drawable.bar));

        return productList;
    }

    public void listSettings(){
        ListView productList = findViewById(R.id.listView);
        List<Product> products = generateShopList();

        ListAddapter adapter = new ListAddapter(this,products,itemsCount,balance);
        productList.setAdapter(adapter);
    }
}