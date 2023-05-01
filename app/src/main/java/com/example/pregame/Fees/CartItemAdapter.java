package com.example.pregame.Fees;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.Model.CartItem;
import com.example.pregame.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ExampleViewHolder>{
    private View view;
    private List<CartItem> cartItems;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private String currentPlayerId;

    public CartItemAdapter(ArrayList<CartItem> cartItems, Context context, String currentPlayerId) {
        this.cartItems = cartItems;
        this.context = context;
        this.currentPlayerId = currentPlayerId;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView membershipTypeTv, priceTv;
        ImageView deleteIv;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);

            firebaseFirestore = FirebaseFirestore.getInstance();
            membershipTypeTv = itemView.findViewById(R.id.membership_type_tv);
            priceTv = itemView.findViewById(R.id.membership_price_tv);
            deleteIv = itemView.findViewById(R.id.delete_membership_iv);
        }
    }

    @NonNull
    @Override
    public CartItemAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.row_layout_cart_item, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ExampleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartItem cartItem = cartItems.get(position);

        holder.membershipTypeTv.setText(cartItem.getMembershipType());
        holder.priceTv.setText("€" + cartItem.getPrice());

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMembership(position, cartItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void deleteMembership(int position, CartItem cartItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this item?")
                .setTitle("Delete Task")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseFirestore.collection("player").document(currentPlayerId).collection("cart").document(cartItem.CartItemId).delete();
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        Snackbar.make(view, "Deleted " + cartItem.getMembershipType() + " Membership", Snackbar.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        notifyItemChanged(position);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
