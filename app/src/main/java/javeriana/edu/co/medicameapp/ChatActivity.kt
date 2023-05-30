package javeriana.edu.co.medicameapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javeriana.edu.co.medicameapp.adapters.MessageAdapter
import javeriana.edu.co.medicameapp.databinding.ActivityChatBinding
import javeriana.edu.co.medicameapp.modelos.Message

class ChatActivity : AppCompatActivity()
{
    private lateinit var chatBinding: ActivityChatBinding

    // Adaptadores
    private lateinit var messageAdapter : MessageAdapter
    private lateinit var messageList : ArrayList<Message>

    // Firebase
    private lateinit var mBdRef : DatabaseReference

    // Espacio único para los mensajes
    var receiverRoom : String? = null
    var senderRoom : String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mBdRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatBinding.textViewTitulo.text = name

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatBinding.sentButton.setOnClickListener {
            // Enviar el mensaje a la DB
            val message = chatBinding.messageBox.text.toString()

            val messageObject = Message(message, senderUid)

            mBdRef.child("chats")
                .child(senderRoom!!)
                .child("messages")
                .push()
                .setValue(messageObject)
                .addOnSuccessListener {
                    mBdRef.child("chats")
                        .child(receiverRoom!!)
                        .child("messages")
                        .push()
                        .setValue(messageObject)
                }
        }
    }
}