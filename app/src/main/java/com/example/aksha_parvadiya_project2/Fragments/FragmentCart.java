package com.example.aksha_parvadiya_project2.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aksha_parvadiya_project2.Adapters.CartAdapter;
import com.example.aksha_parvadiya_project2.CheckoutActivity;
import com.example.aksha_parvadiya_project2.OtherCalss.CartItem;
import com.example.aksha_parvadiya_project2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FragmentCart extends Fragment implements CartAdapter.onUpdateListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentCart() {
        // Required empty public constructor
    }

    RecyclerView recyclerCart;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    TextView txtsubtotal,txttax,txttotal;
    Button btncheckout;

    public static FragmentCart newInstance(String param1, String param2) {
        FragmentCart fragment = new FragmentCart();
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

    LinearLayout linearrecycler,lineartext;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_cart, container, false);
        recyclerCart = view.findViewById(R.id.recyclerCart);
        txtsubtotal=view.findViewById(R.id.txtsubtotal);
        txttotal=view.findViewById(R.id.txttotal);
        txttax=view.findViewById(R.id.txttax);
        btncheckout=view.findViewById(R.id.btncheckout);
        lineartext=view.findViewById(R.id.lineartext);
        linearrecycler=view.findViewById(R.id.linearrecycler);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchCartItems();
    }

    private void fetchCartItems() {
        cartItems = new ArrayList<>();

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Cart");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    cartItems.add(cartItem);

                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerCart.setLayoutManager(layoutManager);

                if (cartItems.size()==0){
                    linearrecycler.setVisibility(View.GONE);
                    lineartext.setVisibility(View.VISIBLE);
                }else{
                    linearrecycler.setVisibility(View.VISIBLE);
                    lineartext.setVisibility(View.GONE);
                    adapter = new CartAdapter(getContext(), cartItems,FragmentCart.this);
                    recyclerCart.setAdapter(adapter);
                    double total = adapter.calculateTotal();
                    txtsubtotal.setText(String.format("$%.2f", total));
                    txttax.setText(String.format("$%.2f", adapter.calculateTax()));
                    txttotal.setText(String.format("$%.2f", adapter.calculateTax()+total));
                    adapter.notifyDataSetChanged();
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load cart items.", Toast.LENGTH_SHORT).show();
            }
        });

        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CheckoutActivity.class);
                intent.putExtra("total",adapter.calculateTotal());
                intent.putExtra("tax",adapter.calculateTax());
                intent.putExtra("finaltotal",adapter.calculateTax()+adapter.calculateTotal());
                startActivity(intent);
            }
        });

    }


    @Override
    public void onItemUpdated(int quantity) {

        double total = adapter.calculateTotal();
        txtsubtotal.setText(String.format("$%.2f", total));
        txttax.setText(String.format("$%.2f", adapter.calculateTax()));
        txttotal.setText(String.format("$%.2f", adapter.calculateTax()+total));

        if (cartItems.size()==0){
            linearrecycler.setVisibility(View.GONE);
            lineartext.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        fetchCartItems();
    }
}