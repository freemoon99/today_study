package com.example.study;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ChatRoomActivity extends AppCompatActivity {
    private LinearLayout chatLayout;
    private ScrollView chatScrollView;
    private EditText messageEditText;
    private Button sendButton;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatLayout = findViewById(R.id.chat_layout);
        chatScrollView = findViewById(R.id.chat_scrollView);
        messageEditText = findViewById(R.id.message_editText);
        sendButton = findViewById(R.id.send_button);
        titleTextView = findViewById(R.id.title_textView);

        // Intent에서 제목을 받아서 표시
        String title = getIntent().getStringExtra("title");
        titleTextView.setText(title);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    // 메시지 전송 처리
                    addMessageToChat(message, true);
                    messageEditText.setText("");
                }
            }
        });
    }

    private void addMessageToChat(String message, boolean isUserMessage) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int padding = getResources().getDimensionPixelSize(R.dimen.bubble_padding);
        int margin = getResources().getDimensionPixelSize(R.dimen.bubble_margin);

        textView.setText(message);
        textView.setTextColor(ContextCompat.getColor(this, isUserMessage ? R.color.black : R.color.white));

        if (isUserMessage) {
            layoutParams.gravity = Gravity.END;
            layoutParams.setMargins(margin, margin, margin, margin);
            textView.setBackgroundResource(R.drawable.bubble_background_user);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setPadding(padding, padding, padding, padding);
        } else {
            layoutParams.gravity = Gravity.START;
            layoutParams.setMargins(margin, margin, margin, margin);
            textView.setBackgroundResource(R.drawable.bubble_background_other);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setPadding(padding, padding, padding, padding);
        }

        textView.setLayoutParams(layoutParams);
        chatLayout.addView(textView);

        // 스크롤을 가장 아래로 이동
        chatScrollView.post(new Runnable() {
            @Override
            public void run() {
                chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
