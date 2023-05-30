package javeriana.edu.co.medicameapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            var email = user.email

            // Probar a ver si farmacias muestra users y user muestra farmacias.
            // email = "farmaciacountry@gmail.com"

            Toast.makeText(baseContext, "Email del usuario: $email", Toast.LENGTH_SHORT).show()

            mDbRef.child("pharmacies").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener
            {
                override fun onDataChange(snapshot: DataSnapshot)
                {
                    if (snapshot.exists())
                    {
                        chatMenuClienteBinding.textViewTitulo.text = "Usuarios"

                        // Correo está en pharmacies, mostrar datos de users
                        mDbRef.child("users").addValueEventListener(object : ValueEventListener
                        {
                            override fun onDataChange(snapshot: DataSnapshot)
                            {
                                userList.clear()

                                for (postSnapshop in snapshot.children)
                                {
                                    val currentUser = postSnapshop.getValue(User::class.java)

                                    if (mAuth.currentUser?.uid != currentUser?.id)
                                    {

                                    }

                                    userList.add(currentUser!!)
                                }

                                adapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError)
                            {
                                Log.i("Chat Menu", "Error $error")
                            }
                        })
                    }

                    else
                    {
                        chatMenuClienteBinding.textViewTitulo.text = "Puntos de distribución"

                        // Correo no está en pharmacies, mostrar datos de pharmacies
                        mDbRef.child("pharmacies").addValueEventListener(object : ValueEventListener
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

                override fun onCancelled(databaseError: DatabaseError)
                {
                    Log.w("Chat", "Error al consultar correo en pharmacies", databaseError.toException())
                }
            })
        }

    }
}