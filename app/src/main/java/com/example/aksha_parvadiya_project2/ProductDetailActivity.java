package com.example.aksha_parvadiya_project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aksha_parvadiya_project2.Adapters.SubCatAdapter;
import com.example.aksha_parvadiya_project2.Fragments.FragmentHome;
import com.example.aksha_parvadiya_project2.OtherCalss.CartItem;
import com.example.aksha_parvadiya_project2.OtherCalss.MainClassData;
import com.example.aksha_parvadiya_project2.OtherCalss.SubCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    String productId;
    MainClassData mc;

    ImageView imgproduct, back_arrow, fav;
    TextView txttitle, txtdetail, txtprice, textViewQuantity;
    Button btncart, buttonDecrease, buttonIncrease;
    FirebaseUser user;
    DatabaseReference favoriteRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productId = getIntent().getStringExtra("id");
        mc = MainClassData.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (productId != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference productRef = database.getReference("Categories")
                    .child(mc.categoryId)
                    .child("Details")
                    .child(productId);

            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Assuming you have a Product class that matches the database structure
                    SubCategory product = dataSnapshot.getValue(SubCategory.class);
                    if (product != null) {
                        updateUI(product);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ProductDetailActivity.this, "Failed to load product.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    String userId;
    boolean isfav = false;
    int quantity = 1;

    public void updateUI(SubCategory subCategory) {
        imgproduct = findViewById(R.id.imgproduct);

        fav = findViewById(R.id.fav);
        txttitle = findViewById(R.id.txttitle);
        txtdetail = findViewById(R.id.txtdetail);
        txtprice = findViewById(R.id.txtprice);
        btncart = findViewById(R.id.btncart);

        Glide.with(this).load(subCategory.getImage()).into(imgproduct);
        txttitle.setText(subCategory.getName());
        txtdetail.setText(subCategory.getDescription());
        txtprice.setText(String.format("$%s", subCategory.getPrice()));

        textViewQuantity = findViewById(R.id.textViewQuantity);
        buttonDecrease = findViewById(R.id.buttonDecrease);
        buttonIncrease = findViewById(R.id.buttonIncrease);

        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) { // Can't have less than 1 item
                    quantity--;
                    textViewQuantity.setText(String.valueOf(quantity));
                }
            }
        });


        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity >= subCategory.getQuantity()) {
                    textViewQuantity.setText(String.valueOf(quantity));
                    Toast.makeText(ProductDetailActivity.this, "Can not Add more items", Toast.LENGTH_SHORT).show();
                } else {
                    quantity++;
                    textViewQuantity.setText(String.valueOf(quantity));
                }

            }
        });


        if (user != null) {
            userId = user.getUid();
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference()
                    .child("Favorites")
                    .child(userId)
                    .child(productId);


            favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        fav.setImageResource(R.drawable.fav);
                        isfav = true; // Update the holder's favorite status
                    } else {
                        fav.setImageResource(R.drawable.unfav);
                        isfav = false; // Update the holder's favorite status
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Error handling...
                }
            });
        }
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtofavs();
            }
        });

        btncart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subCategory.getQuantity() == 0) {
                    new AlertDialog.Builder(ProductDetailActivity.this)
                            .setTitle("Out of Stock")
                            .setMessage("This item is currently out of stock and cannot be added to your cart.")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                } else {
                    int quantityToAdd = Integer.parseInt(textViewQuantity.getText().toString());
                    addToCart(subCategory, quantityToAdd);
                }

            }
        });

    }

    public void addtofavs() {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference()
                .child("Favorites")
                .child(userId)
                .child(productId);

        if (isfav) {
            favoritesRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    isfav = false;
                    fav.setImageResource(R.drawable.unfav); // Update the UI to show the product is not a favorite
                    Log.d("Favorites", "Product removed from favorites successfully");

                } else {
                    Log.e("Favorites", "Failed to remove product from favorites", task.getException());
                }
            });

        } else {
            favoritesRef.setValue(mc.categoryId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                        isfav=true;
                    fav.setImageResource(R.drawable.fav);
                    Log.d("Favorites", "Product added to favorites successfully");
                } else {

                    Log.e("Favorites", "Failed to add product to favorites", task.getException());
                }
            });
        }
    }

    int changedquantity = 0;
    int diff = 0;

    private void addToCart(SubCategory product, int quantityToAdd) {

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


                        if (cartItem == null) {

                            cartItem = new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImage(), quantityToAdd);
                            changedquantity = Integer.parseInt(textViewQuantity.getText().toString());

                        } else {
                            int additionalQuantity = Integer.parseInt(textViewQuantity.getText().toString());
                            int newQuantity = cartItem.getQuantity() + additionalQuantity;
                            cartItem.setQuantity(newQuantity);
                        }
                        mutableData.setValue(cartItem);


                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (committed) {
                        updateProductQuantityInDatabase(product.getId(), changedquantity);
                        Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "You need to be logged in to add items to the cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProductQuantityInDatabase(String productId, int quantityToAddToCart) {
        DatabaseReference productQuantityRef = FirebaseDatabase.getInstance().getReference()
                .child("Categories")
                .child(mc.categoryId)
                .child("Details")
                .child(productId)
                .child("quantity");

        productQuantityRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Get the current quantity from the database
                Integer currentQuantity = mutableData.getValue(Integer.class);
                if (currentQuantity == null) {
                    return Transaction.success(mutableData); // If null, abort the transaction
                }
                // Calculate the new quantity by subtracting the quantity to add to the cart
                int newQuantity = currentQuantity - quantityToAddToCart;
                if (newQuantity <= 0) {
                    newQuantity = 0; // Prevent the quantity from going negative

                }
                // Set the new quantity in the database
                mutableData.setValue(newQuantity);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                // This method will be called once the transaction is complete
                if (committed) {
                    // Transaction was successful
                    Log.d("ProductDetailActivity", "Quantity updated in database.");
                    Toast.makeText(ProductDetailActivity.this, "Quantity updated.", Toast.LENGTH_SHORT).show();
                } else {
                    // Transaction failed
                    Log.d("ProductDetailActivity", "Failed to update quantity in database.");
                    Toast.makeText(ProductDetailActivity.this, "Failed to update quantity.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}