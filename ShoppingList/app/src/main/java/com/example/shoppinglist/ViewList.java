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

import com.example.shoppinglist.models.Product;
import com.example.shoppinglist.models.User;
import com.example.shoppinglist.utilities.UserListAddapter;
import com.example.shoppinglist.utilities.SharedPrefActions;

import java.util.List;

public class ViewList extends AppCompatActivity {

    private SharedPrefActions spa = new SharedPrefActions();
    private String loggedUser;
    private UserListAddapter adapter;
    private ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        run();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run(){
        TextView balanceText = findViewById(R.id.userListBalanceTextId);
        TextView userListText = findViewById(R.id.userListTextId);
        loggedUser = getIntent().getExtras().getString("loggedUser");
        userListText.setText(loggedUser + " list");
        User u = spa.getUserByUsername(loggedUser,getApplicationContext());
        String currentBalance = spa.calculateTheBalanceOfUserList(u.getUserShoppingList());
        balanceText.setText(currentBalance);
        listSettings(u.getUserShoppingList(), balanceText);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listSettings(List<Product> products, TextView balanceTextView){
        listView = findViewById(R.id.userListView);
        adapter = new UserListAddapter(this, products,balanceTextView,this);
        listView.setAdapter(adapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping_list,menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.buyMoreId: buyMore();
                break;
            case R.id.logOutId:logOut();
                break;
        }

        return true;
    }

    public void logOut(){
        Intent intent = new Intent(ViewList.this,MainActivity.class);
        startActivity(intent);
    }

    public void buyMore(){
        Intent intent = new Intent(ViewList.this,ShoppingListPanel.class);
        intent.putExtra("loggedUser",loggedUser);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateListOnRemove(){
        User user = spa.getUserByUsername(loggedUser,getApplicationContext());
        user.setUserShoppingList(adapter.getUserProducts());

        List<User> users = spa.loadData(getApplicationContext());
        for (int i = 0; i < users.size() ; i++) {
            if(users.get(i).getUsername().equals(loggedUser)){
                users.set(i,user);
            }
        }

        spa.save(users,getApplicationContext());
        listView.invalidateViews();
    }
}