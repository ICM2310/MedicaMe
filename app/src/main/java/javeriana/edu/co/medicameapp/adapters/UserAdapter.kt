package javeriana.edu.co.medicameapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import javeriana.edu.co.medicameapp.ChatActivity
import javeriana.edu.co.medicameapp.R
import javeriana.edu.co.medicameapp.modelos.User

class UserAdapter(val context : Context, val userList : ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder
    {
        val view : View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)

        return UserViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int)
    {
        val currentUser = userList[position]

        holder.textName.text = currentUser.name

        holder.itemView.setOnClickListener {

            val pasarAChatActivity = Intent(context, ChatActivity::class.java)

            pasarAChatActivity.putExtra("name", currentUser.name)
            pasarAChatActivity.putExtra("uid", FirebaseAuth.getInstance().currentUser?.uid)

            context.startActivity(pasarAChatActivity)
        }
    }

    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }


}