package com.example.aksha_parvadiya_project2.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.aksha_parvadiya_project2.Adapters.MainCatAdapters;
import com.example.aksha_parvadiya_project2.Adapters.SubCatAdapter;
import com.example.aksha_parvadiya_project2.OtherCalss.MainCategory;
import com.example.aksha_parvadiya_project2.OtherCalss.MainClassData;
import com.example.aksha_parvadiya_project2.OtherCalss.SubCategory;
import com.example.aksha_parvadiya_project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment implements MainCatAdapters.OnMainCategoryClickListener, SubCatAdapter.isFavorNotclickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentHome() {
        // Required empty public constructor
    }

    RecyclerView recyclerMainCat, recyclerSubCat;
    ArrayList<MainCategory> mainCatlist = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    MainCatAdapters mainCatAdapters;
    FrameLayout bannerFrame;
    MainClassData mainclass;

    ArrayList<SubCategory> subCatList = new ArrayList<>();


    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerMainCat = view.findViewById(R.id.recyclerMainCat);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mainclass=MainClassData.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase.getReference().child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MainCategory mainCategory = dataSnapshot.getValue(MainCategory.class);
                    mainCatlist.add(mainCategory);

                }
                mainCatAdapters.notifyDataSetChanged();
                SubCatData(mainCatlist.get(0).getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ErrorDatabase", error.getMessage());
            }
        });

        Log.e("MainCat  ", String.valueOf(mainCatlist.size()));
        mainCatAdapters = new MainCatAdapters(getContext(), mainCatlist, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerMainCat.setLayoutManager(layoutManager);
        recyclerMainCat.setAdapter(mainCatAdapters);

        recyclerSubCat = view.findViewById(R.id.recyclerSubCat);
        bannerFrame = view.findViewById(R.id.bannerFrame);




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    SubCatAdapter subCatAdapter;


    @Override
    public void onMainCategoryClicked(String categoryId) {
        Log.e("SubCategory  11   ", categoryId.toString());
        SubCatData(categoryId);
    }


    public void SubCatData(String catId){
        mainclass.categoryId=catId;
        subCatList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Categories").child(catId).child("Details")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot subCategorySnapshot : dataSnapshot.getChildren()) {
                            SubCategory subCategory = subCategorySnapshot.getValue(SubCategory.class);
                            subCatList.add(subCategory);

                            subCatAdapter = new SubCatAdapter(getContext(), subCatList, FragmentHome.this);
                            GridLayoutManager gridlayoutManager = new GridLayoutManager(getContext(), 2);
                            recyclerSubCat.setLayoutManager(gridlayoutManager);
                            recyclerSubCat.setAdapter(subCatAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void OnFavClick(String productId, boolean isfav) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        addProductToFavorites(userId, productId,isfav);

    }
    public void addProductToFavorites(String userId, String productId, boolean favadded) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference()
                .child("Favorites")
                .child(userId)
                .child(productId);


        if (favadded){
            favoritesRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Log.d("Favorites", "Product removed from favorites successfully");
                    subCatAdapter.notifyDataSetChanged();
                } else {
                    Log.e("Favorites", "Failed to remove product from favorites", task.getException());

                }
            });

        }else{
            favoritesRef.setValue(mainclass.categoryId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    subCatAdapter.notifyDataSetChanged();
                    Log.d("Favorites", "Product added to favorites successfully");
                } else {

                    Log.e("Favorites", "Failed to add product to favorites", task.getException());
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
       if (subCatAdapter!=null){
           subCatAdapter.notifyDataSetChanged();
       }
    }

}