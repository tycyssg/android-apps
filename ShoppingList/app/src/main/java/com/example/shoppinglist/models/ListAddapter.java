package com.example.shoppinglist.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppinglist.R;
import com.example.shoppinglist.ShoppingListPanel;

import java.util.List;


public class ListAddapter extends BaseAdapter {

    Context context;
    List<Product> products;
    List<Product> userShoppingList;
    TextView itemsCount;
    TextView balance;

    public ListAddapter(Context context, List<Product> products, TextView itemsCount, TextView balance) {
        this.context = context;
        this.products = products;
        this.itemsCount = itemsCount;
        this.balance = balance;
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
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row,null);

        ImageView imageView = view.findViewById(R.id.image);
        TextView title = view.findViewById(R.id.productTitleId);
        TextView price = view.findViewById(R.id.productPriceId);
        Button addProduct  = view.findViewById(R.id.addToCartButtonId);

        Product currentProduct = products.get(position);

        imageView.setImageResource(currentProduct.getImage());
        title.setText(currentProduct.getProductName());
        price.setText("$"+Double.toString(currentProduct.getProductPrice()));

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,currentProduct.getProductName(),Toast.LENGTH_SHORT).show();
               // int prevItemCount = Integer.parseInt(itemsCount.getText().toString().substring(0,1));
                //itemsCount.setText("Items in the list " + prevItemCount+1);

                String balanceWithoutSign = balance.getText().toString().replace("$","");
                double prevBalance = Double.parseDouble(balanceWithoutSign);
                balance.setText(currentProduct.getProductPrice()+prevBalance + "$");

            }
        });

        return view;
    }



}
