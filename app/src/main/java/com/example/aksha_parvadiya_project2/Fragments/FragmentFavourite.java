package com.example.aksha_parvadiya_project2.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aksha_parvadiya_project2.Adapters.FavouriteAdapter;
import com.example.aksha_parvadiya_project2.Adapters.MainCatAdapters;
import com.example.aksha_parvadiya_project2.OtherCalss.MainClassData;
import com.example.aksha_parvadiya_project2.OtherCalss.SubCategory;
import com.example.aksha_parvadiya_project2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentFavourite extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MainClassData mc;

    public FragmentFavourite() {
        // Required empty public constructor
    }


    public static FragmentFavourite newInstance(String param1, String param2) {
        FragmentFavourite fragment = new FragmentFavourite();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_favourite, container, false);

        mc=MainClassData.getInstance();
        recyclerfav = view.findViewById(R.id.recyclerfav);
        return view;
    }

    private DatabaseReference databaseReference;
    private List<SubCategory> favoriteProducts = new ArrayList<>();
    RecyclerView recyclerfav;
    FavouriteAdapter favouriteAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            fetchFavoriteProducts(userId);


        }
    }



    private void fetchFavoriteProducts(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Favorites").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mc.favoriteProductsMap.clear();
                        for (DataSnapshot favoriteSnapshot : dataSnapshot.getChildren()) {
                            String productKey = favoriteSnapshot.getKey();
                            String categoryId = favoriteSnapshot.getValue(String.class);
                            mc.favoriteProductsMap.put(productKey, categoryId);
                        }
                        fetchProductDetails();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FetchFavorites", "Failed to load favorites", databaseError.toException());
                    }
                });
    }

    private void fetchProductDetails() {
        favoriteProducts.clear();
        for (Map.Entry<String, String> entry : mc.favoriteProductsMap.entrySet()) {
            String productKey = entry.getKey();
            String categoryId = entry.getValue();

            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
                    .child("Categories")
                    .child(categoryId)
                    .child("Details")
                    .child(productKey);

            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                    if (productSnapshot.exists()) {
                        SubCategory productDetails = productSnapshot.getValue(SubCategory.class);
                        favoriteProducts.add(productDetails);

                        favouriteAdapter = new FavouriteAdapter(getContext(), favoriteProducts);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerfav.setLayoutManager(layoutManager);
                        recyclerfav.setAdapter(favouriteAdapter);

                    } else {
                        Log.e("FetchProductDetails", "Product details not found for key: " + productKey);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FetchProductDetails", "Failed to load product details", databaseError.toException());
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            fetchFavoriteProducts(userId);


        }
    }
}