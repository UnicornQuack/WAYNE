package com.example.map2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    // 模擬好友位置資料
    List<Friend> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化位置提供者
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // 初始化好友列表
        initFriends();

        // 獲取最後的已知位置
        getLastLocation();
    }

    private void initFriends() {
        long currentTime = System.currentTimeMillis(); // 當前時間的時間戳
        friends.add(new Friend("chaewon", 25.033964, 121.564468, R.drawable.chaewon, "信義區", currentTime - 600000));  // 台北
        friends.add(new Friend("kazuha", 24.99078, 121.54172, R.drawable.kazuha, "文山區", currentTime - 1200000));  // 高雄
        friends.add(new Friend("sakura", 25.01419, 121.53457, R.drawable.sakura, "中正區", currentTime - 300000));  // 台中
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    // 當位置獲取成功，初始化地圖
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // 顯示用戶自己的位置
        LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(userLocation).title("我的位置"));

        // 自動調整視角
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userLocation);  // 包括用戶位置

        // 顯示好友的位置, 自定義圖標並使用 Glide 裁剪成圓形
        for (Friend friend : friends) {
            LatLng friendLocation = new LatLng(friend.getLatitude(), friend.getLongitude());

            // 使用 Glide 將圖標裁剪為圓形，並設置大小
            Glide.with(this)
                    .asBitmap()
                    .load(friend.getIconResId())
                    .transform(new CircleCrop())
                    .override(150, 150)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @NonNull Transition<? super Bitmap> transition) {
                            // 當圖片準備好後，將其設置為地圖標記
                            myMap.addMarker(new MarkerOptions()
                                    .position(friendLocation)
                                    .title(friend.getName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource)));
                            // 包括好友位置
                            builder.include(friendLocation);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // 可以處理 Glide 加載時的佔位圖
                        }
                    });
        }

        // 自動調整地圖視角以顯示所有標記
        LatLngBounds bounds = builder.build();
        myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                showBottomDialog(marker.getTitle());
                return false; // 返回 false 表示事件未消耗，仍會觸發預設行為
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "位置權限被拒絕，請允許權限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showBottomDialog(String friendName) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView userName = bottomSheetView.findViewById(R.id.user_name_text_view);
        ImageView friendAvatar = bottomSheetView.findViewById(R.id.friend_avatar);
        TextView statusMessage = bottomSheetView.findViewById(R.id.status_message_text_view);
        TextView lastOnline = bottomSheetView.findViewById(R.id.last_online_text_view);

        // 根據點擊的標記設置名字
        userName.setText(friendName);

        Friend currentFriend = null;
        for (Friend friend : friends) {
            if (friend.getName().equals(friendName)) {
                currentFriend = friend;
                break;
            }
        }

        if (currentFriend != null) {
            Glide.with(this)
                    .load(currentFriend.getIconResId())
                    .transform(new CircleCrop())
                    .into(friendAvatar);
            statusMessage.setText(currentFriend.getStatusMessage());


            // 將時間戳轉換為具體的時間顯示格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String lastOnlineStr = sdf.format(new Date(currentFriend.getLastOnline()));
            lastOnline.setText("最後上線時間: " + lastOnlineStr);
        }

        // 發送訊息按鈕
        Button sendMessageButton = bottomSheetView.findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "發送訊息給 " + friendName, Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }
}
