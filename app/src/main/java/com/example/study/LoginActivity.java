package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText nicknameEditText;
    private Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nicknameEditText = findViewById(R.id.nickname_editText);
        enterButton = findViewById(R.id.enter_button);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nicknameEditText.getText().toString();
                if (!TextUtils.isEmpty(nickname)) {
                    // 로그인 처리
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("nickname", nickname);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

