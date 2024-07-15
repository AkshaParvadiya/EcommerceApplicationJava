package com.example.aksha_parvadiya_project2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aksha_parvadiya_project2.OtherCalss.CartItem;
import com.example.aksha_parvadiya_project2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewHolder> {

    Context context;
    List<CartItem> cartItems;

    public interface onUpdateListener{
        void onItemUpdated(int position);
    }
    onUpdateListener onUpdateListener;
    public CartAdapter(Context context, List<CartItem> cartItems,onUpdateListener onUpdateListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.onUpdateListener=onUpdateListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_view, parent, false);
        return new viewHolder(view);
    }

    int quantity;
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.itemName.setText(cartItems.get(position).getName());
        Double price = Double.valueOf(cartItems.get(position).getQuantity())* Double.valueOf(cartItems.get(position).getPrice());
        holder.itemPrice.setText(String.format("$%s",cartItems.get(position).getPrice()));
        Glide.with(context).load(cartItems.get(position).getImage()).into(holder.itemImage);
        holder.txtqty.setText(""+cartItems.get(position).getQuantity());
         quantity=cartItems.get(position).getQuantity();

        holder.buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = cartItems.get(position).getQuantity() - 1;
                if (newQuantity<=0){
                    newQuantity=1;
                }
                cartItems.get(position).setQuantity(newQuantity);
                notifyItemChanged(position);
                onUpdateListener.onItemUpdated(position);
            }
        });

        holder.buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = cartItems.get(position).getQuantity() + 1;
                cartItems.get(position).setQuantity(newQuantity);
                notifyItemChanged(position);
                onUpdateListener.onItemUpdated(position);
            }
        });

        holder.imgdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    String userId = user.getUid();
                    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(userId)
                            .child("Cart")
                            .child(cartItems.get(position).getProductId());

                    cartRef.removeValue()
                            .addOnSuccessListener(aVoid -> {

                                cartItems.remove(position);
                                notifyDataSetChanged();
                                onUpdateListener.onItemUpdated(position);

                            })
                            .addOnFailureListener(e -> {

                            });
                } else {
                    Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public double calculateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            double itemPrice = Double.parseDouble(item.getPrice());
            int quantity = item.getQuantity();
            total += itemPrice * quantity;
        }
        return total;
    }

    private static final double TAX_RATE = 0.13;

    public double calculateTax() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            double itemPrice = Double.parseDouble(item.getPrice());
            int quantity = item.getQuantity();
            subtotal += itemPrice * quantity;
        }
        double taxAmount = subtotal * TAX_RATE;
        return taxAmount;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        public TextView itemName, itemPrice,txtqty;
        public ImageView itemImage,imgdelete;
        Button buttonDecrease,buttonIncrease;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.cart_item_image);
            itemName = itemView.findViewById(R.id.cart_item_name);
            itemPrice = itemView.findViewById(R.id.cart_item_price);
            txtqty=itemView.findViewById(R.id.txtqty);
            buttonDecrease=itemView.findViewById(R.id.buttonDecrease);
            buttonIncrease=itemView.findViewById(R.id.buttonIncrease);
            imgdelete=itemView.findViewById(R.id.imgdelete);

        }
    }
}
