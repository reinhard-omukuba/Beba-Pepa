package com.beba.bepa;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookMatatu extends AppCompatActivity {

    private TextView matatunamebook;
    private TextView saccoBook;
    private TextView regNumberBook;
    private TextView saccrouteBook;
    private TextView farebook;

    private Button btnBookNow;


    private EditText bookInputTime;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_matatu);


        String receivedUserId = getIntent().getStringExtra("Userid");

        matatunamebook = findViewById(R.id.matatunamebook);
        saccoBook = findViewById(R.id.saccoBook);
        regNumberBook = findViewById(R.id.regNumberBook);
        saccrouteBook = findViewById(R.id.saccrouteBook);
        farebook = findViewById(R.id.farebook);
        btnBookNow = findViewById(R.id.btnBookNow);
        bookInputTime = findViewById(R.id.bookInputTime);

        progressDialog = new ProgressDialog(BookMatatu.this);



        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is logged in
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(receivedUserId);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String fullName = document.getString("full_name");
                        String sacco = document.getString("matatuName");
                        String regno = document.getString("regno");
                        String fare = document.getString("fare");
                        String route = document.getString("route");



                        matatunamebook.setText(fullName);
                        saccoBook.setText(sacco);
                        regNumberBook.setText(regno);
                        farebook.setText("KES. " + fare);
                        saccrouteBook.setText(route);



                        btnBookNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //get texr
                                String mytime = bookInputTime.getText().toString();


                                progressDialog.setMessage("Booking");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                //start


                                Map<String, Object> booking = new HashMap<>();
                                booking.put("bookingtime", mytime);
                                booking.put("documentid", "CA");
                                booking.put("userid", userId);
                                booking.put("fare", fare);
                                booking.put("route", route);
                                booking.put("status", "pending");
                                booking.put("MatatuInfo", fullName);
                                booking.put("sacco", sacco);
                                booking.put("regno", regno);
                                booking.put("matatubooked", receivedUserId);






                                db.collection("Bookings")
                                        .add(booking)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                String documentId = documentReference.getId();

                                                // update the document with the ID
                                                documentReference.update("documentid", documentId)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot successfully updated with ID: " + documentId);

                                                                Intent intent = new Intent(BookMatatu.this, LandingPage.class);
                                                                startActivity(intent);
                                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error updating document with ID: " + documentId, e);
                                                            }
                                                        });






                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });




                        //end


                            }
                        });



                    } else {
                        // Document does not exist
                    }
                } else {
                    // Task failed with an exception
                }
            });


    }
}}