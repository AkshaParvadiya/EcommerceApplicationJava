package com.example.aksha_parvadiya_project2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubCatAdapter extends RecyclerView.Adapter<SubCatAdapter.ViewHolder> {

    Context context;
    ArrayList<SubCategory> subCatList;



    public interface isFavorNotclickListener {
        void OnFavClick(String categoryId, boolean isfav);
    }

    isFavorNotclickListener isFavorNot;
    DatabaseReference favoriteRef;

    MainClassData mc;

    public SubCatAdapter(Context context, ArrayList<SubCategory> subCatList, isFavorNotclickListener favorNot) {
        this.context=context;
        this.subCatList=subCatList;
        this.isFavorNot=favorNot;
        mc=MainClassData.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sub_category,parent,false);
        return new SubCatAdapter.ViewHolder(view);
    }
    String userId;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubCategory subCategory = subCatList.get(position);
        holder.txtname.setText(subCategory.getName());
        holder.txtprice.setText("$" + subCategory.getPrice());
        Glide.with(context).load(subCategory.getImage()).into(holder.imgcat);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
             userId = user.getUid();
             favoriteRef = FirebaseDatabase.getInstance().getReference()
                    .child("Favorites")
                    .child(userId)
                    .child(subCatList.get(position).getId());

            favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        holder.isfav.setImageResource(R.drawable.fav);
                        holder.isFavorited = true;
                    } else {
                        holder.isfav.setImageResource(R.drawable.unfav);
                        holder.isFavorited = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        holder.isfav.setOnClickListener(v -> {
            favoriteRef = FirebaseDatabase.getInstance().getReference()
                    .child("Favorites")
                    .child(userId)
                    .child(subCatList.get(position).getId());
            if (holder.isFavorited) {
                favoriteRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        holder.isFavorited = false;
                        holder.isfav.setImageResource(R.drawable.unfav);
                        Log.d("Favorites", "Product removed from favorites successfully");
                    } else {
                        Log.e("Favorites", "Failed to remove product from favorites", task.getException());
                    }
                });
            } else {
                favoriteRef.setValue(mc.categoryId).addOnCompleteListener(task -> {  // Assuming you just want to set 'true' for simplicity
                    if (task.isSuccessful()) {
                        holder.isFavorited = true;
                        holder.isfav.setImageResource(R.drawable.fav);
                        Log.d("Favorites", "Product added to favorites successfully");
                    } else {
                        Log.e("Favorites", "Failed to add product to favorites", task.getException());
                    }
                });
            }
        });



        holder.cardCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ProductDetailActivity.class);
                intent.putExtra("id",subCatList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return subCatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgcat,isfav;
        TextView txtprice,txtname;
        CardView cardCat;
        boolean isFavorited;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgcat=itemView.findViewById(R.id.imgcat);
            txtname=itemView.findViewById(R.id.txtname);
            txtprice=itemView.findViewById(R.id.txtprice);
            cardCat=itemView.findViewById(R.id.cardCat);
            isfav=itemView.findViewById(R.id.isfav);
            isFavorited = false;
        }
    }
}
