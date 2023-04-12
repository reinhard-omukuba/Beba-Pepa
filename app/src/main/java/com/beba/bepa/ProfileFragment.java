package com.beba.bepa;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView fullNameTextView;
    private TextView emailTextView;

    private Button btnlogout;

    private GoogleMap mMap;
    private TextView locationTextView;
    private FusedLocationProviderClient fusedLocationClient;

    private TextView location_text_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Account");

        fullNameTextView = view.findViewById(R.id.full_name_text_view);
        emailTextView = view.findViewById(R.id.email_text_view);
        location_text_view = view.findViewById(R.id.location_text_view);
        btnlogout = view.findViewById(R.id.btnlogout);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is logged in
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String fullName = document.getString("full_name");
                        String email = document.getString("email");
                        Double longi = document.getDouble("longitude");
                        Double lati = document.getDouble("latitude");
                        // Use the full name and email address as needed

                        fullNameTextView.setText(fullName);
                        emailTextView.setText(email);

//                        double latitude = Double.parseDouble(lati);
//
//                        double longitude = Double.parseDouble(longi);


                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(lati, longi, 1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (addresses.size() > 0) {
                            Address address = addresses.get(0);
                            String locationName = address.getAddressLine(0); // Get the first address line
                            // Use locationName as needed

                            location_text_view.setText(locationName);
                        }



                    } else {
                        // Document does not exist
                    }
                } else {
                    // Task failed with an exception
                }
            });
//

            btnlogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });


        }




        return view;
    }





}