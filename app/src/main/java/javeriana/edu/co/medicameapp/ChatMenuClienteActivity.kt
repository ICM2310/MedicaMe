package javeriana.edu.co.medicameapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javeriana.edu.co.medicameapp.adapters.UserAdapter
import javeriana.edu.co.medicameapp.databinding.ActivityChatMenuClienteBinding
import javeriana.edu.co.medicameapp.modelos.User

class ChatMenuClienteActivity : AppCompatActivity()
{
    private lateinit var chatMenuClienteBinding: ActivityChatMenuClienteBinding

    // Firebase
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    // Adaptador y RecycleView
    private lateinit var userList : ArrayList<User>
    private lateinit var adapter  : UserAdapter
    private lateinit var userRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        chatMenuClienteBinding = ActivityChatMenuClienteBinding.inflate(layoutInflater)
        setContentView(chatMenuClienteBinding.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef =  FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        chatMenuClienteBinding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        chatMenuClienteBinding.userRecyclerView.adapter = adapter

        // Leer los valores de la DB
        mDbRef.child("users").addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                userList.clear()

                for (postSnapshop in snapshot.children)
                {
                    val currentUser = postSnapshop.getValue(User::class.java)
                    userList.add(currentUser!!)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError)
            {
                Log.i("Chat", "Error $error")
            }

        })
    }
}