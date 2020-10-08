package com.example.shoppinglist.utilities;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.shoppinglist.R;
import com.example.shoppinglist.ViewList;
import com.example.shoppinglist.models.Product;
import java.util.List;
import lombok.Data;

@Data
public class UserListAddapter extends BaseAdapter {
    private Context context;
    private List<Product> userProducts;
    private TextView balanceText;
    private ViewList vl;


    public UserListAddapter(Context context, List<Product> userProducts, TextView balanceText,ViewList vl) {
        this.context = context;
        this.userProducts = userProducts;
        this.balanceText = balanceText;
        this.vl = vl;
    }

    @Override
    public int getCount() {
        return userProducts.size();
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
        View view = inflater.inflate(R.layout.rowuser,null);

        ImageView imageView = view.findViewById(R.id.image);
        TextView title = view.findViewById(R.id.productTitleId);
        TextView price = view.findViewById(R.id.productPriceId);
        Button removeProduct  = view.findViewById(R.id.removeToCartButtonId);

        Product currentProduct = userProducts.get(position);

        imageView.setImageResource(currentProduct.getImage());
        title.setText(currentProduct.getProductName());
        price.setText("$"+ currentProduct.getProductPrice());

        removeProduct.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String balanceWithoutSign = balanceText.getText().toString().replace("$","");
                double prevBalance = Double.parseDouble(balanceWithoutSign);
                balanceText.setText("$"+ (prevBalance - currentProduct.getProductPrice()));

                    userProducts.removeIf(p -> p.getProductName().equals(currentProduct.getProductName()));
                    Toast.makeText(context,currentProduct.getProductName()+" removed from the list",Toast.LENGTH_LONG).show();
                     vl.updateListOnRemove();
                }
        });

        return view;
    }


}
