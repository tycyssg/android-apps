package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shoppinglist.utilities.ListAddapter;
import com.example.shoppinglist.models.Product;
import com.example.shoppinglist.models.User;
import com.example.shoppinglist.utilities.SharedPrefActions;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListPanel extends AppCompatActivity {
    private TextView itemsCount;
    private TextView balance;
    private ListAddapter adapter;
    private SharedPrefActions spa = new SharedPrefActions();
    private String loggedUser;

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.viewListId: viewShopList();
                break;
            case R.id.goCartid: goCart();
                break;
            case R.id.logOutId:logOut();
                break;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run(){
        itemsCount = findViewById(R.id.itemsAddedId);
        balance = findViewById(R.id.balanceId);
        TextView displayWelcome = findViewById(R.id.welcomeTextId);
        loggedUser = getIntent().getExtras().getString("loggedUser");
        displayWelcome.setText("Welcome "+ loggedUser);
        listSettings();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void viewShopList(){
        assignListToTheUser();
        Intent intent = new Intent(ShoppingListPanel.this,ViewList.class);
        intent.putExtra("loggedUser", loggedUser);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void logOut(){
        assignListToTheUser();
        Intent intent = new Intent(ShoppingListPanel.this,MainActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void goCart(){
        assignListToTheUser();
        Intent intent = new Intent(ShoppingListPanel.this,CheckOut.class);
        intent.putExtra("loggedUser", loggedUser);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listSettings(){
        ListView productList = findViewById(R.id.listView);
        List<Product> products = generateShopList();

        User u = spa.getUserByUsername(loggedUser,getApplicationContext());
        List<Product> userProducts = u.getUserShoppingList();

        if(userProducts.isEmpty()){
            userProducts = new ArrayList<>();
            balance.setText("$0");
        }else{
            balance.setText(spa.calculateTheBalanceOfUserList(userProducts));
        }

        itemsCount.setText("Items in the list " + userProducts.size());

        adapter = new ListAddapter(this, products, itemsCount, balance,userProducts);
        productList.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void assignListToTheUser(){
        User user = spa.getUserByUsername(loggedUser,getApplicationContext());
        user.setUserShoppingList(adapter.getUserShoppingList());

        List<User> users = spa.loadData(getApplicationContext());
        for (int i = 0; i < users.size() ; i++) {
            if(users.get(i).getUsername().equals(loggedUser)){
                users.set(i,user);
            }
        }

        spa.save(users,getApplicationContext());
    }

}