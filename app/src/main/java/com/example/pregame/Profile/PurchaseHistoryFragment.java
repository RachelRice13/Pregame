package com.example.pregame.Profile;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pregame.Model.CartItem;
import com.example.pregame.Model.Purchase;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PurchaseHistoryFragment extends Fragment {
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private ArrayList<Purchase> purchaseArrayList;
    private LinearLayout purchaseTable;

    public PurchaseHistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchase_history, container, false);

        setup();
        setTableRow("Date", "Discount", "Subtotal", true, false, null);
        getDetails();

        return view;
    }

    private void getDetails() {
        firebaseFirestore.collection("player").document(currentUser.getUid()).collection("purchases").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Purchase purchase = document.toObject(Purchase.class);
                                purchaseArrayList.add(purchase);

                                for (Purchase p : purchaseArrayList) {
                                    setTableRow(p.getDate(), p.getDiscountType(), String.valueOf(p.getSubtotal()), false, true, p);
                                }
                            }
                        }
                    }
                });
    }

    private void setTableRow(String columnOne, String columnTwo, String columnThree, boolean bold, boolean image, Purchase purchase) {
        LinearLayout rowLayout = new LinearLayout(getContext());

        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rowLayout.setLayoutParams(layoutParams);
        rowLayout.setPadding(1, 1, 1, 2);
        rowLayout.setBackgroundColor(getResources().getColor(R.color.black));

        if (columnTwo == null)
            columnTwo = "N/A";

        LinearLayout firstColumn = createColumn(columnOne, 2f, bold);
        LinearLayout secondColumn = createColumn(columnTwo, 2.5f, bold);
        LinearLayout thirdColumn = createColumn(columnThree, 1.5f, bold);
        LinearLayout fourthColumn = createLastColumn(image, purchase);

        rowLayout.addView(firstColumn);
        rowLayout.addView(secondColumn);
        rowLayout.addView(thirdColumn);
        rowLayout.addView(fourthColumn);
        purchaseTable.addView(rowLayout);
    }

    private LinearLayout createColumn(String text, float weight, boolean bold) {
        LinearLayout column = new LinearLayout(getContext());
        LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
        columnLayoutParams.setMargins(3, 0, 0, 0);
        column.setLayoutParams(columnLayoutParams);

        TextView textView = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 48);
        textView.setBackgroundColor(getResources().getColor(R.color.white));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(10, 0, 10, 0);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(text);

        if (bold)
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        textView.setLayoutParams(params);

        column.addView(textView);

        return column;
    }

    private LinearLayout createLastColumn(boolean image, Purchase purchase) {
        LinearLayout column = new LinearLayout(getContext());
        LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f);
        columnLayoutParams.setMargins(4, 0, 5, 0);
        column.setLayoutParams(columnLayoutParams);

        ImageView imageView = new ImageView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 48);
        imageView.setBackgroundColor(getResources().getColor(R.color.white));
        imageView.setLayoutParams(params);

        if(image) {
            imageView.setImageResource(R.drawable.ic_info);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPurchaseDetails(purchase);
                }
            });
        } else {
            imageView.setImageResource(R.drawable.ic_white_circle);
        }

        column.addView(imageView);

        return column;
    }

    private void viewPurchaseDetails(Purchase purchase) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View purchaseView = layoutInflater.inflate(R.layout.dialogue_view_purchase_history, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        FloatingActionButton goBack = purchaseView.findViewById(R.id.go_back);
        TextView dateTv = purchaseView.findViewById(R.id.date_tv);
        TextView discountAmountTv = purchaseView.findViewById(R.id.discount_amount_tv);
        TextView discountTypeTv = purchaseView.findViewById(R.id.discount_type_tv);
        TextView totalTv = purchaseView.findViewById(R.id.total_tv);
        TextView subtotalTv = purchaseView.findViewById(R.id.subtotal_tv);
        LinearLayout listOfItems = purchaseView.findViewById(R.id.list_of_items);
        String discountType;

        dateTv.setText(purchase.getDate());
        discountAmountTv.setText("Discount Amount: " + purchase.getDiscountAmount() + "%");
        if (purchase.getDiscountType() == null)
            discountType = "Discount Type: N/A";
        else
            discountType = "Discount Type: " + purchase.getDiscountType();

        discountTypeTv.setText(discountType);
        totalTv.setText("Total: €" + purchase.getTotalPrice());
        subtotalTv.setText("Subtotal: €" + purchase.getSubtotal());

        for (CartItem item : purchase.getItems()) {
            TextView textView = new TextView(getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setTextSize(16f);
            textView.setTextColor(Color.BLACK);
            textView.setLayoutParams(params);
            String itemText = item.getMembershipType() + " (" + item.getPrice() + ")";
            textView.setText(itemText);
            listOfItems.addView(textView);
        }

        alertDialogBuilder.setCancelable(false).setView(purchaseView);
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    private void setup() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        purchaseArrayList = new ArrayList<>();
        purchaseTable = view.findViewById(R.id.purchase_history_table);

        FloatingActionButton goBackBut = view.findViewById(R.id.go_back);
        goBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
            }
        });
    }
}