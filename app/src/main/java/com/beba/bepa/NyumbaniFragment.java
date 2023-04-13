package com.beba.bepa;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NyumbaniFragment extends Fragment {


    public NyumbaniFragment() {
        // Required empty public constructor
    }


    private Handler handler;
    private Runnable runnable;


    private GoogleMap mMap;

   // private ActivityMapsBinding binding;

    private LocationManager locationManager;

    LocationListener locationListener;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final float DEFAULT_ZOOM = 15f;
    private Marker mMarker;


    double latitude;
    double longitude;


    private RecyclerView mRecyclerView;
    private UserAdapter mAdapter;
    private List<User> mUsersList;

    private FirebaseFirestore db;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_nyumbani, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Nearby Matatus");





        // Get the location service
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        // Check if the app has permission to access the user's location
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, proceed with your code
            // ...

            Toast.makeText(getActivity(), "Permision granted " , Toast.LENGTH_SHORT).show();

            // Permission is already granted, proceed with getting the location
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Handle location updates
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    // ...

                    LatLng currentLocation = new LatLng(latitude, longitude);


                    //        run the function below
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            // code to update location to Firestore every 1 minute
                            updateLocationToFirestore();
                        }
                    }, 0, 6000000); // run every 1 minute (60,000 milliseconds)



                }

                @Override
                public void onProviderDisabled(String provider) {
                    // Handle provider disabled
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // Handle provider enabled
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Handle status changes
                }
            });

        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }



        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();

        mUsersList = new ArrayList<>();
        mAdapter = new UserAdapter(getContext(), mUsersList);
        mRecyclerView.setAdapter(mAdapter);

        fetchUsers();


        return view;
    }

    public void updateLocationToFirestore(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("latitude", latitude);
        updates.put("longitude", longitude);

        userRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

//

    private void fetchUsers() {
        db.collection("users")
                .whereEqualTo("usertype", "operator")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        mUsersList.add(user);
                    }
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error fetching users: " + e.getMessage());
                });
    }

    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<User> userList;

        public UserAdapter(Context context, List<User> userList) {
            this.userList = userList;
        }




        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public class UserViewHolder extends RecyclerView.ViewHolder{
            private TextView nameTextView;
            private TextView emailTextView;
            private TextView locationTv;

            private TextView matatuSacco;
            private TextView regno;

            UserViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.matName);
//                emailTextView = itemView.findViewById(R.id.emailTextView);

                matatuSacco = itemView.findViewById(R.id.matsacco);
                regno = itemView.findViewById(R.id.matregno);

                locationTv = itemView.findViewById(R.id.loctv);



                // Add an onClick listener to the itemView
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        User user = userList.get(position);
                        String userId = user.getUserId();

                        // Show a toast with the userId
                       Toast.makeText(view.getContext(), "Clicked User Id: " + userId, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(view.getContext(), BookMatatu.class);
                        intent.putExtra("Userid", userId);
                        view.getContext().startActivity(intent);
                    }
                });
            }


            void bind(User user) {
                nameTextView.setText(user.getFull_name());
                matatuSacco.setText(user.getMatatuName());
                regno.setText(user.getRegno());

                locationTv = itemView.findViewById(R.id.loctv);


                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(user.getLatitude(), user.getLongitude(), 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String locationName = address.getAddressLine(0); // Get the first address line
                    // Use locationName as needed

                    locationTv.setText(locationName);
                }


            }


        }
    }





}