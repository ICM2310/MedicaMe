package javeriana.edu.co.medicameapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javeriana.edu.co.medicameapp.R;
import javeriana.edu.co.medicameapp.RealizarPedidoActivity;

public class OrderListAdapter extends ArrayAdapter<String> {

    private static final String TAG = "UserListAdapter";
    private Context mContext;
    public OrderListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context; // initialize mContext
    }






    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_row, parent, false);
        }

        String item = getItem(position);
        String[] parts = item.split("\\|");
        String userName = parts[0];
        String uid = parts[1];
        Log.d(TAG, "getView: " + item);

        TextView userNameTextView = convertView.findViewById(R.id.nombreUsuario);
        TextView uidTextView = convertView.findViewById(R.id.nombreEPS);
        userNameTextView.setText(userName);
        uidTextView.setText(uid);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el UID del elemento seleccionado

                Log.d("IntentAdapter", "onDataChange UID USER: " + uid);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ordersRef = database.getReference("orders");
                Log .d("IntentAdapter", "onDataChange: " + mAuth.getCurrentUser().getUid());
                Query query = ordersRef.orderByChild("usuarioSoliciante").equalTo(uid);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            String orderKey = orderSnapshot.getKey();
                            String estado = orderSnapshot.child("estado").getValue(String.class);
                            if (estado != null && estado.equals("Pendiente")) {
                                // Realizar la actualización para pedidos pendientes
                                DatabaseReference orderRef = ordersRef.child(orderKey);
                                orderRef.child("estado").setValue("Asignado");
                                orderRef.child("usuarioRepartidor").setValue(mAuth.getCurrentUser().getUid());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar errores si es necesario
                    }
                });





                // Crear el intent para iniciar la nueva actividad
                Intent intent = new Intent(mContext, RealizarPedidoActivity.class);
                intent.putExtra("uid", uid);




                // Iniciar la nueva actividad
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }
}
