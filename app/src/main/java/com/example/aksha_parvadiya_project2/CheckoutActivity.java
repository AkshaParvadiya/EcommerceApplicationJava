package com.example.aksha_parvadiya_project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aksha_parvadiya_project2.OtherCalss.Checkout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    double totalPrice,finaltotal,tax;
    private DatabaseReference mDatabase;
    Button btnsubmit;

    TextView txttotal,txttax,txtsubtotal;
    AppCompatEditText edtxtphone, edtxtaddress, edtemail, edtxtname, edtxtexpiry, edtxtcvv, edtxtcardnumber, edtxtcardname;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        FirebaseApp.initializeApp(this);
        totalPrice = getIntent().getDoubleExtra("total", 0);
        tax=getIntent().getDoubleExtra("tax",0);
        finaltotal=getIntent().getDoubleExtra("finaltotal",0);
        Log.e("total   ", String.valueOf(totalPrice));
        edtxtphone = findViewById(R.id.edtxtphone);
        edtxtaddress = findViewById(R.id.edtxtaddress);
        edtemail = findViewById(R.id.edtemail);
        edtxtname = findViewById(R.id.edtxtname);
        edtxtexpiry = findViewById(R.id.edtxtexpiry);
        edtxtcvv = findViewById(R.id.edtxtcvv);
        edtxtcardnumber = findViewById(R.id.edtxtcardnumber);
        edtxtcardname = findViewById(R.id.edtxtcardname);
        btnsubmit=findViewById(R.id.btnsubmit);
        txtsubtotal=findViewById(R.id.txtsubtotal);
        txttax=findViewById(R.id.txttax);
        txttotal=findViewById(R.id.txttotal);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtsubtotal.setText(String.format("$%.2f", totalPrice));
        txttax.setText(String.format("$%.2f", tax));
        txttotal.setText(String.format("$%.2f", finaltotal));

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edtxtname.getText().toString().trim();
                String email=edtemail.getText().toString().trim();
                String address=edtxtaddress.getText().toString().trim();
                String phone=edtxtphone.getText().toString().trim();
                String cardname=edtxtcardname.getText().toString().trim();
                String cardnumber=edtxtcardnumber.getText().toString().trim();
                String cvv=edtxtcvv.getText().toString().trim().trim();
                String expiry=edtxtexpiry.getText().toString().trim();

                //validation

                if (name.isEmpty()){
                    edtxtname.setError("Please Enter Name");
                }else if(address.isEmpty()){
                    edtxtaddress.setError("Please Enter Address");
                }else if (!isValidPhone(phone)){
                    edtxtphone.setError("Enter Valid phone number");
                }else if(!isValidEmail(email)){
                    edtemail.setError("Enter Email Address");
                }else if(!isValidCreditCardNumber(cardnumber)){
                    edtxtcardnumber.setError("Enter Valid Card Number");
                }else if(!isValidCVV(cvv)){
                    edtxtcvv.setError("Enter valid CVV");
                }else if(!isValidExpiryDate(expiry)){
                    edtxtexpiry.setError("Enter Date in MM/YY format");
                }else{

                    checkout(name, email, address, phone);
                }

            }
        });



    }


    public void checkout(String name, String email, String address, String phone){
        Checkout checkout = new Checkout(name, email, address, phone, finaltotal);
        // Push the checkout data to Firebase
        mDatabase.child("checkoutsDetail").push().setValue(checkout)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CheckoutActivity.this, "Checkout successful!", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(CheckoutActivity.this,ThankYouActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CheckoutActivity.this, "Checkout failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPhone(String phone) {
        return phone.matches("\\+\\d{1,3}\\d{10}");// Adjust regex as necessary
    }


    private boolean isValidCreditCardNumber(String cardNumber) {

        if (cardNumber == null) return false;
        String digitsOnly = cardNumber.replaceAll("\\D", "");
        if (digitsOnly.length()!=16) {
            return false;
        }else{
            return true;
        }

    }
    private boolean isValidCVV(String cvv) {
        return cvv.matches("\\d{3,4}");
    }

    private boolean isValidExpiryDate(String expiryDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/YY", Locale.US);
        sdf.setLenient(false);
        Date expiry = null;
        try {
            expiry = sdf.parse(expiryDate);
        } catch (ParseException e) {
            return false;
        }
        return expiry.after(new Date());
    }



}