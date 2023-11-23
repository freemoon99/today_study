package com.example.study;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String nickname;
    private TextView greetingTextView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FusedLocationProviderClient fusedLocationClient; // FusedLocationProviderClient 객체 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greetingTextView = findViewById(R.id.greetingTextView);
        nickname = getIntent().getStringExtra("nickname");
        if (nickname != null) {
            greetingTextView.setText("🎊" + nickname + "sl님, 반갑습니다.");
        }
//        위치 권한 확인요청
        checkLocationPermission();

//      탭바 관련
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StudyListFragment(), "목록보기");
        adapter.addFragment(new StudyMapFragment(), "지도보기");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

//      스터디 추가하기 버튼 관련
        FloatingActionButton addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateStudyButtonClicked(v);
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 이미 허용되어 있는 경우
            showCurrentLocation();
        } else {
            // 위치 권한이 허용되어 있지 않은 경우
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void showCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            greetingTextView.setText("위치 권한이 필요합니다.");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // 위치 정보를 가져오기 위한 지오코딩 서비스 객체 생성
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                // 주어진 위도와 경도에 대한 위치 명칭 가져오기 (최대 1개 결과)
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && !((List<?>) addresses).isEmpty()) {
                                    // 첫 번째 결과에서 위치 명칭 가져오기
                                    Address address = addresses.get(0);
                                    String locationName = address.getAddressLine(0);

                                    // 카드뷰 내부의 TextView를 찾아옵니다.
                                    TextView locationTextView = findViewById(R.id.locationTextView);

                                    // 위치 명칭을 TextView에 설정합니다.
                                    locationTextView.setText("🗺️현재 위치:"+locationName);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }



    private void onCreateStudyButtonClicked(View v) {
        Intent intent = new Intent(MainActivity.this, StudyRegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 허용되었을 경우
                showCurrentLocation();
            } else {
                // 위치 권한이 거부되었을 경우
                greetingTextView.setText("위치 권한이 필요합니다.");
            }
        }
    }

    public String getNickname() {
        return nickname;
    }
}
