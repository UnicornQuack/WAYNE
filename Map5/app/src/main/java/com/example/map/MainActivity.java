package com.example.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference databaseReference;
    private String selfIconUrl = "https://i.imgur.com/ssQ5J2j.jpeg";
    private EditText addressEditText;
    private FloatingActionButton fab;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addressEditText = findViewById(R.id.addressEditText);
        fab = findViewById(R.id.fab);

        initFabClickListener();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("friends");

        getLastLocation();
    }

    private void initFabClickListener() {
        if (fab != null) {
            fab.setOnClickListener(v -> {
                if (addressEditText.getVisibility() == View.GONE) {
                    addressEditText.setVisibility(View.VISIBLE);
                } else {
                    addressEditText.setVisibility(View.GONE);
                    searchLocationAndMark();
                }
            });
        } else {
            Log.e("MainActivity", "Floating Action Button not found in layout");
        }
    }

    private void searchLocationAndMark() {
        String address = addressEditText.getText().toString();
        if (address.isEmpty()) {
            Toast.makeText(this, "請輸入地址", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                if (myMap != null) {
                    myMap.addMarker(new MarkerOptions().position(latLng).title("搜尋結果").snippet(address));
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    addressEditText.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "找不到該地址", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "無法搜尋該地址", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(MainActivity.this);
                } else {
                    Toast.makeText(this, "無法加載地圖", Toast.LENGTH_SHORT).show();
                }

                loadFriendsDataFromFirebase();
                addSelfMarker();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        if (currentLocation != null) {
            LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }

        // 設置自定義 InfoWindowAdapter
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView title = infoView.findViewById(R.id.title);
                TextView snippet = infoView.findViewById(R.id.snippet);

                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return infoView;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        // 設置 Marker 點擊事件
        myMap.setOnMarkerClickListener(marker -> {
            if ("self".equals(marker.getTag())) {
                showFunctionDialog(marker);  // 顯示功能對話框
            } else {
                startNavigationToMarker(marker);  // 導航至好友位置
            }
            return true;
        });
    }

    // 導航至好友位置
    private void startNavigationToMarker(Marker marker) {
        LatLng destination = marker.getPosition();
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination.latitude + "," + destination.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "無法啟動 Google 地圖", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadFriendsDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    double latitude = snapshot.child("latitude").getValue(Double.class);
                    double longitude = snapshot.child("longitude").getValue(Double.class);
                    String iconUrl = snapshot.child("icon_url").getValue(String.class);
                    String lastOnlineTime = snapshot.child("lastOnlineTime").getValue(String.class); // 新增最後上線時間

                    addFriendMarker(name, latitude, longitude, iconUrl, lastOnlineTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "讀取好友資料失敗：" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void addSelfMarker() {
        Glide.with(this)
                .asBitmap()
                .load(selfIconUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (myMap != null && currentLocation != null) {
                            LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Bitmap circularBitmap = getCircularBitmap(resource);
                            Marker marker = myMap.addMarker(new MarkerOptions()
                                    .position(userLocation)
                                    .title("你的位置")
                                    .icon(BitmapDescriptorFactory.fromBitmap(circularBitmap)));
                            if (marker != null) {
                                marker.setTag("self");
                            }
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void addFriendMarker(String name, double latitude, double longitude, String iconUrl, String lastOnlineTime) {
        Glide.with(this)
                .asBitmap()
                .load(iconUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (myMap != null) {
                            LatLng friendLocation = new LatLng(latitude, longitude);
                            Marker friendMarker = myMap.addMarker(new MarkerOptions()
                                    .position(friendLocation)
                                    .title(name) // 設定名字作為 title
                                    .snippet("最後上線時間: " + lastOnlineTime) // 設定最後上線時間作為 snippet
                                    .icon(BitmapDescriptorFactory.fromBitmap(getCircularBitmap(resource))));
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }


    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int diameter = 150;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, diameter, diameter, true);
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect srcRect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
        final RectF destRect = new RectF(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        canvas.drawOval(destRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, srcRect, destRect, paint);

        return output;
    }

    private void showFunctionDialog(Marker marker) {
        if ("self".equals(marker.getTag())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View customView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
            builder.setView(customView);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView titleTextView = customView.findViewById(R.id.dialogTitle);
            Button sosButton = customView.findViewById(R.id.sosButton);
            Button lostButton = customView.findViewById(R.id.lostButton);
            Button arrivedButton = customView.findViewById(R.id.arrivedButton);

            titleTextView.setText("選擇操作");

            sosButton.setOnClickListener(v -> {
                sendMessageToMarker(marker, "SOS");
                dialog.dismiss();
            });

            lostButton.setOnClickListener(v -> {
                sendMessageToMarker(marker, "我迷路了");
                dialog.dismiss();
            });

            arrivedButton.setOnClickListener(v -> {
                sendMessageToMarker(marker, "我到了");
                dialog.dismiss();
            });

            dialog.show();
        }
    }


    private void sendMessageToMarker(Marker marker, String message) {
        marker.setSnippet(message);
        marker.showInfoWindow();
//        LatLng markerPosition = marker.getPosition();
//        String userName = "你的名稱";
        Toast.makeText(this, "訊息：" + message, Toast.LENGTH_SHORT).show();

    }


}
