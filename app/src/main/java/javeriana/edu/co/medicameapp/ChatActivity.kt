package javeriana.edu.co.medicameapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        chatBinding.chatRecycleView.layoutManager = LinearLayoutManager(this)
        chatBinding.chatRecycleView.adapter = messageAdapter

        // Mostrar los mensajes en el RecycleView de Chats
        mBdRef.child( "chats")
            .child(senderRoom!!)
            .child("messages")
            .addValueEventListener(object : ValueEventListener
            {
                override fun onDataChange(snapshot: DataSnapshot)
                {
                    messageList.clear()

                    for (postSnapshot in snapshot.children)
                    {
                        val message = postSnapshot.getValue(Message::class.java)

                        messageList.add(message!!)
                    }

                    messageAdapter.notifyDataSetChanged()
                }


                override fun onCancelled(error: DatabaseError)
                {
                    Log.i("Chat", "Error $error")
                }

            })


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

            chatBinding.messageBox.setText("")
        }
    }
}