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

        bindingChat.textViewIncoming.setText("Hola! Sera que por favor me podria traer una caja de Dolex?");
        bindingChat.textViewOutcoming.setText("Hola, claro que si! Ya mismo se la envío");
        bindingChat.textViewIncoming2.setText("Muchisimas gracias!");
    }
}