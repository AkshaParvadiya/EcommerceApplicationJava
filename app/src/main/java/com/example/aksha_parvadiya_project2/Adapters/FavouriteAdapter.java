package com.example.aksha_parvadiya_project2.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aksha_parvadiya_project2.OtherCalss.CartItem;
import com.example.aksha_parvadiya_project2.OtherCalss.MainClassData;
import com.example.aksha_parvadiya_project2.OtherCalss.SubCategory;
import com.example.aksha_parvadiya_project2.ProductDetailActivity;
import com.example.aksha_parvadiya_project2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.viewHolder> {

    Context context;
    private List<SubCategory> favoriteProducts;

    public FavouriteAdapter(Context context, List<SubCategory> favoriteProducts) {
        this.context = context;
        this.favoriteProducts = favoriteProducts;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fav_layout, parent, false);
        return new FavouriteAdapter.viewHolder(view);
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.txttitle.setText(favoriteProducts.get(position).getName());
        holder.txtprice.setText("Price: $" + favoriteProducts.get(position).getPrice());
        Glide.with(context).load(favoriteProducts.get(position).getImage()).into(holder.imgfav);
        SubCategory product = favoriteProducts.get(position);
        holder.imgisfav.setOnClickListener(view -> {
            DatabaseReference favoriteRef;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String userId = user.getUid();
            favoriteRef = FirebaseDatabase.getInstance().getReference()
                    .child("Favorites")
                    .child(userId)
                    .child(favoriteProducts.get(position).getId());

            favoriteRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    favoriteProducts.remove(position);
                    notifyItemRemoved(position);

                    Log.d("Favorites", "Product removed from favorites successfully");
                } else {
                    Log.e("Favorites", "Failed to remove product from favorites", task.getException());
                }
            });

        });


        holder.cardFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("id", favoriteProducts.get(position).getId());
                MainClassData mc = MainClassData.getInstance();
                mc.categoryId = mc.favoriteProductsMap.get(favoriteProducts.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.imgiscart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    String userId = user.getUid();
                    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(userId)
                            .child("Cart")
                            .child(product.getId());


                    cartRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            CartItem cartItem = mutableData.getValue(CartItem.class);


                            cartItem = new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImage(), 1);


                            mutableData.setValue(cartItem);


                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            if (committed) {
                                 Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "You need to be logged in to add items to the cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteProducts.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView imgfav, imgisfav, imgiscart;
        TextView txttitle, txtprice;
        CardView cardFav;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imgfav = itemView.findViewById(R.id.imgfav);
            imgisfav = itemView.findViewById(R.id.imgisfav);
            imgiscart = itemView.findViewById(R.id.imgiscart);
            txttitle = itemView.findViewById(R.id.txttitle);
            txtprice = itemView.findViewById(R.id.txtprice);
            cardFav = itemView.findViewById(R.id.cardFav);

        }
    }
}
