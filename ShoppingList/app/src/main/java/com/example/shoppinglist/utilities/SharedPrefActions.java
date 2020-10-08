package com.example.shoppinglist.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppinglist.models.Product;
import com.example.shoppinglist.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefActions  {
    private final String userList= "USER_LIST";

    public void save(List<User> users, Context mContext){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(users);
        editor.putString(userList,json);
        editor.apply();
    }

    public List<User> loadData(Context mContext){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
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
    public boolean exitUser(String user,Context mContext){
        List<User> users = loadData(mContext);
        if(users.isEmpty())
            return false;

        return users.stream().anyMatch(s -> s.getUsername().equals(user));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public User getUserByUsername(String user,Context mContext){
        List<User> users = loadData(mContext);
        if(users.isEmpty())
            return null;

        return users.stream().filter(s -> s.getUsername().equals(user)).findAny().orElse(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String calculateTheBalanceOfUserList(List<Product> products){
        double sum = products.stream().mapToDouble(Product::getProductPrice).sum();
        return "$"+sum;
    }
}
