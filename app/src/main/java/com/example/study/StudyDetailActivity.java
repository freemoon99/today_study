package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudyDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private TextView participantsTextView;
    private Button chatButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_detail);

        db = FirebaseFirestore.getInstance();

        titleTextView = findViewById(R.id.title_textView);
        descriptionTextView = findViewById(R.id.description_textView);
        locationTextView = findViewById(R.id.location_textView);
        participantsTextView = findViewById(R.id.participants_textView);
        chatButton = findViewById(R.id.chat_button);

        // Intent로 전달받은 스터디 정보를 표시
        String studyId = getIntent().getStringExtra("studyId");

        // Firestore에서 해당 스터디 문서 가져오기
        db.collection("studies").document(studyId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String title = document.getString("title");
                                String description = document.getString("description");
                                String location = document.getString("location");
                                String participants = document.getString("participants");

                                titleTextView.setText("💡 제목: "+title);
                                descriptionTextView.setText("🗛 내용:"+description);
                                locationTextView.setText("🧭 위치:"+location);
                                participantsTextView.setText("👤 모집인원:"+participants);
                                Log.d("StudyDetailActivity", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("StudyDetailActivity", "No such document");
                            }
                        } else {
                            Log.d("StudyDetailActivity", "get failed with ", task.getException());
                            Toast.makeText(StudyDetailActivity.this, "스터디 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅방으로 이동
                String title = titleTextView.getText().toString(); // 제목 가져오기

                Intent intent = new Intent(StudyDetailActivity.this, ChatRoomActivity.class);
                intent.putExtra("title", title.substring(7));
                startActivity(intent);

            }
        });
    }
}
