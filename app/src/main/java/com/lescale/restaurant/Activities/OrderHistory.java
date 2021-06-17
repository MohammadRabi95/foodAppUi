package com.lescale.restaurant.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lescale.restaurant.Adapters.HistoryAdapter;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.ModelClasses.OrderModel;
import com.lescale.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<OrderModel> list;
    private MyDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerView = findViewById(R.id.history_recyclerView);
        myDialog = AppHelper.loadDialog(OrderHistory.this,getString(R.string.loading));
        TextView clearList = findViewById(R.id.clear);

        list = new ArrayList<>();

        clearList.setOnClickListener(v -> {
            assert list.size() > 0;
            MyFirebaseReferences.userOrderReference().removeValue();
            startActivity(new Intent(OrderHistory.this,OrderHistory.class));
            finish();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppHelper.getCurrentUser() != null) {
            myDialog.show();
            getOrdersFromFirebase();
        }
    }

    private void getOrdersFromFirebase(){
        DatabaseReference reference = MyFirebaseReferences.userOrderReference();
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        OrderModel model = ds.getValue(OrderModel.class);
                        if (model != null){
                            list.add(model);
                            myDialog.dismiss();
                        }
                        adapter = new HistoryAdapter(list, OrderHistory.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    AppHelper.showToast(OrderHistory.this,getString(R.string.nopreviosorder));
                    myDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                myDialog.dismiss();
                AppHelper.showToast(OrderHistory.this,databaseError.getMessage());
            }
        });
    }
}
