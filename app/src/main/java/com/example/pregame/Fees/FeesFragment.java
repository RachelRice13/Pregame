package com.example.pregame.Fees;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pregame.Model.CartItem;
import com.example.pregame.Model.Player;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("SetTextI18n")
public class FeesFragment extends Fragment {
    private static final String TAG = "FeesFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Player currentPlayer;
    private String currentPlayerId;

    public FeesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fees, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentPlayerId = firebaseAuth.getCurrentUser().getUid();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        getPlayerDetails();
        addMembershipToCart();

        Button viewCartBut = view.findViewById(R.id.view_cart_button);
        viewCartBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction.replace(R.id.container, new CartFragment()).commit();
            }
        });

       return view;
    }

    private void getPlayerDetails() {
        TextView playerNameTv = view.findViewById(R.id.player_name_tv);

        firebaseFirestore.collection("player").document(currentPlayerId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentPlayer = documentSnapshot.toObject(Player.class);
                        playerNameTv.setText(currentPlayer.getFirstName() + " " + currentPlayer.getSurname() + "'s");
                    }
                });
    }

    private void addMembershipToCart() {
        Button fullSeniorBut, halfSeniorBut, fullJuniorBut, halfJuniorBut, fullAcademyBut, halfAcademyBut;

        fullSeniorBut = view.findViewById(R.id.full_season_senior);
        halfSeniorBut = view.findViewById(R.id.half_season_senior);
        fullJuniorBut = view.findViewById(R.id.full_season_junior);
        halfJuniorBut = view.findViewById(R.id.half_season_junior);
        fullAcademyBut = view.findViewById(R.id.full_season_academy);
        halfAcademyBut = view.findViewById(R.id.half_season_academy);

        selectButton(fullSeniorBut, "Full Senior", 300.00);
        selectButton(halfSeniorBut, "Half Senior", 150.00);
        selectButton(fullJuniorBut, "Full Junior", 200.00);
        selectButton(halfJuniorBut, "Half Junior", 100.00);
        selectButton(fullAcademyBut, "Full Academy", 150.00);
        selectButton(halfAcademyBut, "Half Academy", 75.00);

    }

    private void selectButton(Button button, String membershipType, double price) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartItem cartItem = new CartItem(membershipType, price);
                addToFireStore(cartItem);
            }
        });
    }

    private void addToFireStore(CartItem cartItem) {
        firebaseFirestore.collection("player").document(currentPlayerId).collection("cart")
                .add(cartItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Successfully added membership plan to cart");
                        Snackbar.make(view, "Added " + cartItem.getMembershipType() + " Membership Plan to Cart.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to add membership plan to cart.", e);
                        Snackbar.make(view, "Failed to Add " + cartItem.getMembershipType() + " Membership Plan to Cart.", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

}