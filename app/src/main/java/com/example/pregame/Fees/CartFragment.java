package com.example.pregame.Fees;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pregame.Model.CartItem;
import com.example.pregame.Model.PaymentMethod;
import com.example.pregame.Model.Purchase;
import com.example.pregame.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SetTextI18n")
public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private  FragmentTransaction transaction;
    private String currentPlayerId, discountType;
    private ListenerRegistration listenerRegistration;
    private ArrayList<CartItem> cartItems;
    private CartItemAdapter cartItemAdapter;
    private double totalPrice = 0, subtotal = 0, discountAmount = 0;
    private TextView totalPriceTv, subtotalTv, discountAmountTv, discountTypeTv;
    private EditText discountCodeEt;

    public CartFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentPlayerId = firebaseAuth.getCurrentUser().getUid();
        transaction = getFragmentManager().beginTransaction();

        discountCodeEt = view.findViewById(R.id.discount_code_et);
        totalPriceTv = view.findViewById(R.id.total_price_tv);
        subtotalTv = view.findViewById(R.id.subtotal_price_tv);
        discountAmountTv = view.findViewById(R.id.discount_amount_tv);
        discountTypeTv = view.findViewById(R.id.discount_type_tv);

        buttons();
        setPricesTextViews();
        buildRecyclerView();

        return view;
    }

    private void buildRecyclerView() {
        cartItems = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.cart_items_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        cartItemAdapter = new CartItemAdapter(cartItems, getContext(), currentPlayerId);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cartItemAdapter);
        populateList();
    }

    private void populateList() {
        Query query = firebaseFirestore.collection("player").document(currentPlayerId).collection("cart").orderBy("price", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        CartItem cartItem = change.getDocument().toObject(CartItem.class).withId(change.getDocument().getId());
                        cartItems.add(cartItem);
                        subtotal += cartItem.getPrice();
                        totalPrice = subtotal;
                        cartItemAdapter.notifyDataSetChanged();
                        setPricesTextViews();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    private void applyDiscountCode() {
        String discountCode = discountCodeEt.getText().toString();

        boolean coachDiscount = applyDiscount(discountCode, "COACH2023", 0.2);
        boolean studentDiscount = applyDiscount(discountCode, "STUDENT2023", 0.3);

        if (!coachDiscount && !studentDiscount) {
            Snackbar.make(view, "Invalid discount code!", Snackbar.LENGTH_LONG).show();
        }
        setPricesTextViews();
        discountCodeEt.setText("");
    }

    private boolean applyDiscount(String enteredCode, String discountCode, double discountValue) {
        if (enteredCode.equals(discountCode)) {
            discountAmount = discountValue * 100;
            totalPrice = subtotal - (subtotal*discountValue);
            discountTypeTv.setText(discountCode);
            discountType = discountCode;
            discountCodeEt.setEnabled(false);
            return true;
        }
        return false;
    }

    private void setPricesTextViews() {
        subtotalTv.setText("€" + subtotal);
        totalPriceTv.setText("€" + totalPrice);
        discountAmountTv.setText(discountAmount + "%");
    }

    private void goToPayment() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Purchase purchase = new Purchase(cartItems, totalPrice, subtotal, discountAmount, date, discountType, new PaymentMethod());

        if (!cartItems.isEmpty()) {
            PaymentFragment paymentFragment = new PaymentFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("purchase", purchase);
            paymentFragment.setArguments(bundle);
            transaction.replace(R.id.container, paymentFragment).commit();
        } else {
            Snackbar.make(view,"Cart is empty", Snackbar.LENGTH_LONG).show();
        }
    }

    private void buttons() {
        Button applyDiscountButton = view.findViewById(R.id.apply_discount_button);
        applyDiscountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyDiscountCode();
            }
        });

        FloatingActionButton goBackButton = view.findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new FeesFragment()).commit();
            }
        });

        Button paymentButton = view.findViewById(R.id.payment_button);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPayment();
            }
        });
    }
}