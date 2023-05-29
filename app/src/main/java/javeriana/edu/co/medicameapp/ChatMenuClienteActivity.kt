package javeriana.edu.co.medicameapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import javeriana.edu.co.medicameapp.adapters.UserAdapter
import javeriana.edu.co.medicameapp.databinding.ActivityChatMenuClienteBinding
import javeriana.edu.co.medicameapp.modelos.User

class ChatMenuClienteActivity : AppCompatActivity()
{
    private lateinit var chatMenuClienteBinding: ActivityChatMenuClienteBinding

    //
    private lateinit var userRecyclerView : RecyclerView

    // Adaptador
    private lateinit var userList : ArrayList<User>
    private lateinit var adapter  : UserAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        chatMenuClienteBinding = ActivityChatMenuClienteBinding.inflate(layoutInflater)
        setContentView(chatMenuClienteBinding.root)

        userList = ArrayList()
        adapter = UserAdapter(this, userList)
    }
}