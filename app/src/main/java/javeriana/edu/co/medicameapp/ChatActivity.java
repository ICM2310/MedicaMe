package javeriana.edu.co.medicameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import javeriana.edu.co.medicameapp.databinding.ActivityChatBinding;


public class ChatActivity extends AppCompatActivity
{
    ActivityChatBinding bindingChat;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindingChat = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(bindingChat.getRoot());


    }
}