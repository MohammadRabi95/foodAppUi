package com.lescale.restaurant.BottomNavigationFragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lescale.restaurant.Adapters.MenuAdapter;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.ModelClasses.CartModel;
import com.lescale.restaurant.ModelClasses.MenuModel;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyMenu extends Fragment implements TextWatcher {

    public MyMenu() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<MenuModel> list;
    private MyDialog myDialog;
    private String quantity = "";
    private EditText searchBar;
    private static final String TAG = "MyMenu";

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mymenu, container, false);
        recyclerView = view.findViewById(R.id.menu_recyclerview);
        searchBar = view.findViewById(R.id.search_bar);
        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        myDialog = AppHelper.loadDialog(getActivity(),Objects.requireNonNull(getActivity()).getString(R.string.loading));
        searchBar.addTextChangedListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        myDialog.show();
        getMenuFromFirebase();
    }

    private void getMenuFromFirebase(){
        Log.d(TAG, "getMenuFromFirebase: ");
        DatabaseReference reference = MyFirebaseReferences.menuReference(getActivity());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        MenuModel menuModel = ds.getValue(MenuModel.class);
                        if (menuModel != null) {
                            list.add(menuModel);
                            myDialog.dismiss();
                        }
                        adapter = new MenuAdapter(list, getActivity());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    AppHelper.showToast(getActivity(), Objects.requireNonNull(getActivity()).getString(R.string.emptymenu) + AppHelper.getCategory(getActivity()));
                    myDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " , databaseError.toException());
                AppHelper.showToast(getActivity(),databaseError.getMessage());
                myDialog.dismiss();
            }
        });
    }

    private  String getQuantity(String id){

        if (AppHelper.getCurrentUser() != null) {
            DatabaseReference reference1 = MyFirebaseReferences.cartReference().child(id);
            System.out.println("ID: " + id);
            reference1.keepSynced(true);
            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        quantity = dataSnapshot.child("quantity").getValue(String.class);
                        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + quantity);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if ("".equals(quantity) || null == quantity){
            return "0";
        }else {
            return quantity;
        }
    }

    private void filter(String text) {
        Log.d(TAG, "filter: ");
        List<MenuModel> filteredList = new ArrayList<>();
        for (MenuModel item : list) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d(TAG, "beforeTextChanged: ");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(TAG, "onTextChanged: ");
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(TAG, "afterTextChanged: ");
        if (!list.isEmpty()) {
            filter(s.toString());
        }
    }
}
