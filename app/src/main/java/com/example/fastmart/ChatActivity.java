// FILE: app/src/main/java/com/example/fastmart/ChatActivity.java
package com.example.fastmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ChatActivity handles real-time messaging between a buyer and a seller.
 * It uses a ListView with a custom BaseAdapter for displaying alternating message bubbles.
 */
public class ChatActivity extends AppCompatActivity {

    private ListView lvMessages;
    private EditText etInput;
    private ImageButton btnSend, btnBack;
    private TextView tvSellerName;

    private List<MessageModel> messageList;
    private ChatAdapter adapter;
    private DatabaseReference chatRef;

    private String currentUserId, targetUserId, conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1. Initialize User Context
        SessionManager sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();
        
        // In a real app, this comes from Intent. Here we use a test ID.
        targetUserId = getIntent().getStringExtra("sellerId");
        if (targetUserId == null) targetUserId = "test_seller_id";

        // 2. Generate Conversation ID (Sorted concatenation of UIDs)
        List<String> ids = new ArrayList<>();
        ids.add(currentUserId);
        ids.add(targetUserId);
        Collections.sort(ids);
        conversationId = ids.get(0) + "_" + ids.get(1);

        // 3. Bind UI Components
        lvMessages = findViewById(R.id.lvMessages);
        etInput = findViewById(R.id.etMessageInput);
        btnSend = findViewById(R.id.btnSendMessage);
        btnBack = findViewById(R.id.btnBack);
        tvSellerName = findViewById(R.id.tvSellerName);

        messageList = new ArrayList<>();
        adapter = new ChatAdapter();
        lvMessages.setAdapter(adapter);

        // 4. Firebase Setup
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(conversationId);
        listenForMessages();

        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Listens for new messages in real-time using ChildEventListener.
     */
    private void listenForMessages() {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageModel message = snapshot.getValue(MessageModel.class);
                if (message != null) {
                    messageList.add(message);
                    adapter.notifyDataSetChanged();
                    lvMessages.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Pushes a new message to the conversation path in Firebase.
     */
    private void sendMessage() {
        String text = etInput.getText().toString().trim();
        if (text.isEmpty()) return;

        MessageModel msg = new MessageModel(currentUserId, targetUserId, text, System.currentTimeMillis());
        chatRef.push().setValue(msg);
        etInput.setText("");
    }

    /**
     * Custom BaseAdapter to handle alternating Sent vs Received message bubbles.
     */
    private class ChatAdapter extends BaseAdapter {
        @Override
        public int getCount() { return messageList.size(); }
        @Override
        public Object getItem(int position) { return messageList.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageModel message = messageList.get(position);
            boolean isSent = message.getSenderId().equals(currentUserId);

            // Inflate appropriate layout based on sender
            LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
            View view = inflater.inflate(isSent ? R.layout.item_message_sent : R.layout.item_message_received, parent, false);

            TextView tvMessage = view.findViewById(isSent ? R.id.tvMessageSent : R.id.tvMessageReceived);
            TextView tvTime = view.findViewById(isSent ? R.id.tvTimeSent : R.id.tvTimeReceived);

            tvMessage.setText(message.getMessageText());

            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            tvTime.setText(sdf.format(new Date(message.getTimestamp())));

            return view;
        }
    }
}