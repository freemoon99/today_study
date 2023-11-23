package com.example.study;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudyMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private List<Marker> studyMarkers;

    public StudyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_map, container, false);

        studyMarkers = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Firestore에서 저장된 위치 가져와서 마커 표시
        showStudyMarkers();
    }

    private void showStudyMarkers() {
        // Firestore에서 저장된 위치 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("studies")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // 기존 마커 제거
                        clearStudyMarkers();

                        // 가져온 위치를 마커로 표시
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> study = document.getData();
                            String location = (String) study.get("location");
                            LatLng studyLocation = convertLocationStringToLatLng(location);
                            if (studyLocation != null) {
                                Marker studyMarker = googleMap.addMarker(new MarkerOptions()
                                        .position(studyLocation)
                                        .title((String) study.get("title")));
                                studyMarkers.add(studyMarker);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "위치 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private LatLng convertLocationStringToLatLng(String location) {
        // 위치 문자열을 LatLng 객체로 변환
        // 예: "LatLng(37.12345, 127.12345)" ->
        try {
            int startIndex = location.indexOf("(") + 1;
            int endIndex = location.indexOf(")");
            String latLngString = location.substring(startIndex, endIndex);
            String[] latLngArray = latLngString.split(",");
            double latitude = Double.parseDouble(latLngArray[0].trim());
            double longitude = Double.parseDouble(latLngArray[1].trim());
            return new LatLng(latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void clearStudyMarkers() {
        // 모든 스터디 마커 제거
        if (studyMarkers != null) {
            for (Marker marker : studyMarkers) {
                marker.remove();
            }
            studyMarkers.clear();
        }
    }
}
