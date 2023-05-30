package javeriana.edu.co.medicameapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javeriana.edu.co.medicameapp.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity()
{
    private lateinit var chatBinding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)

        val desdeChatMenu = Intent()
        val name = desdeChatMenu.getStringExtra("name")
        val uid = desdeChatMenu.getStringExtra("uid")

        // supportActionBar?.title = name

    }
}