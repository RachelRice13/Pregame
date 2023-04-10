package com.example.pregame.Fees;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pregame.HomePage.PlayerHomeFragment;
import com.example.pregame.Model.CartItem;
import com.example.pregame.Model.PaymentMethod;
import com.example.pregame.Model.Purchase;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentFragment extends Fragment {
    private static final String TAG = "PaymentFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private FragmentTransaction transaction;
    private Purchase purchase;

    public PaymentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        transaction = getFragmentManager().beginTransaction();
        Bundle bundle = getArguments();
        purchase = (Purchase) bundle.getSerializable("purchase");
        Log.e(TAG, purchase.getDiscountType() + "\t\t" + purchase.getDiscountAmount());

        buttons();

        return view;
    }

    private void makePayment() {
        EditText cardNumberEt, cardholdersNameEt, expiryDateMonthEt, expiryDateYearEt, securityCodeEt;
        cardNumberEt = view.findViewById(R.id.card_number_et);
        cardholdersNameEt = view.findViewById(R.id.cardholder_name_et);
        expiryDateMonthEt = view.findViewById(R.id.expiry_date_month_et);
        expiryDateYearEt = view.findViewById(R.id.expiry_date_year_et);
        securityCodeEt = view.findViewById(R.id.security_code_et);

        String cardNumber = cardNumberEt.getText().toString();
        String cardholderName = cardholdersNameEt.getText().toString();
        String expiryDate =  expiryDateMonthEt.getText().toString() + "/" + expiryDateYearEt.getText().toString();
        String securityCode = securityCodeEt.getText().toString();

        PaymentMethod paymentMethod = new PaymentMethod(cardNumber, cardholderName, expiryDate, securityCode);
        purchase.setPaymentMethod(paymentMethod);

        addToFirestore(purchase);
        for (CartItem cartItem : purchase.getItems()) {
            deleteFromFireStore(cartItem.CartItemId);
        }

        Snackbar.make(view, "Successfully paid for membership", Snackbar.LENGTH_LONG).show();
        transaction.replace(R.id.container, new PlayerHomeFragment()).commit();
    }

    private void addToFirestore(Purchase purchase) {
        firebaseFirestore.collection("player").document(userId).collection("purchases")
                .add(purchase)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        Log.d(TAG, "Added Purchase to DB");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, "Failed to Add Purchase to DB", Snackbar.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
    }

    private void deleteFromFireStore(String cartItemId) {
        firebaseFirestore.collection("player").document(userId).collection("cart").document(cartItemId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Basket Item successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting basket item", e);
                    }
                });
    }

    private void buttons() {
        Button paymentButton = view.findViewById(R.id.payment_button);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new CartFragment()).commit();
                Snackbar.make(view, "Cancelled payment", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}