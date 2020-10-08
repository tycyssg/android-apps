package com.example.shoppinglist.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.shoppinglist.MainActivity;
import com.example.shoppinglist.R;
import com.example.shoppinglist.ShoppingListPanel;
import com.example.shoppinglist.models.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ListAddapter extends BaseAdapter {

    private Context context;
    private List<Product> products;
    private List<Product> userShoppingList;
    private TextView itemsCount;
    private TextView balance;

    public ListAddapter(Context context, List<Product> products, TextView itemsCount, TextView balance,List<Product> userShoppingList) {
        this.context = context;
        this.products = products;
        this.itemsCount = itemsCount;
        this.balance = balance;
        this.userShoppingList = userShoppingList;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row,null);

        ImageView imageView = view.findViewById(R.id.image);
        TextView title = view.findViewById(R.id.productTitleId);
        TextView price = view.findViewById(R.id.productPriceId);
        Button addProduct  = view.findViewById(R.id.addToCartButtonId);
        Button removeProduct  = view.findViewById(R.id.removeFromCartButtonId);

        Product currentProduct = products.get(position);

        imageView.setImageResource(currentProduct.getImage());
        title.setText(currentProduct.getProductName());
        price.setText("$"+ currentProduct.getProductPrice());

        addProduct.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                if(productAlreadyInTheList(currentProduct.getProductName())){
                    Toast.makeText(context,"Product already in the list",Toast.LENGTH_SHORT).show();
                }else{
                    String[] splitedText = itemsCount.getText().toString().split("\\s");

                     int prevItemCount = Integer.parseInt(splitedText[splitedText.length-1]);
                    itemsCount.setText("Items in the list " + (prevItemCount+1));

                    String balanceWithoutSign = balance.getText().toString().replace("$","");
                    double prevBalance = Double.parseDouble(balanceWithoutSign);
                    balance.setText("$" + (currentProduct.getProductPrice()+prevBalance));

                    userShoppingList.add(currentProduct);
                    Toast.makeText(context,currentProduct.getProductName()+" added to the list",Toast.LENGTH_LONG).show();

                }
            }
        });

        removeProduct.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
               boolean productRemoved =  userShoppingList.removeIf(p -> p.getProductName().equals(currentProduct.getProductName()));

               if(productRemoved){
                   String balanceWithoutSign = balance.getText().toString().replace("$","");
                   double prevBalance = Double.parseDouble(balanceWithoutSign);
                   balance.setText("$"+ (prevBalance - currentProduct.getProductPrice()));

                   String[] splitedText = itemsCount.getText().toString().split("\\s");
                   int prevItemCount = Integer.parseInt(splitedText[splitedText.length-1]);
                   itemsCount.setText("Items in the list " + (prevItemCount-1));

                   userShoppingList.remove(currentProduct);
                   Toast.makeText(context,currentProduct.getProductName()+" removed from the list",Toast.LENGTH_LONG).show();
               }else{
                   Toast.makeText(context,currentProduct.getProductName()+" is not the list",Toast.LENGTH_LONG).show();
               }

            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean productAlreadyInTheList(String productName){
        return userShoppingList.stream().anyMatch(s -> s.getProductName().equals(productName));
    }

}
