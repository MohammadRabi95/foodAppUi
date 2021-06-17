package com.lescale.restaurant.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lescale.restaurant.Activities.AuthActivity;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.ModelClasses.CartModel;
import com.lescale.restaurant.ModelClasses.MenuModel;
import com.lescale.restaurant.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {

    private List<MenuModel> list;
    private Activity context;
    private String myCount;
    private DatabaseReference reference;

    public MenuAdapter(List<MenuModel> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (AppHelper.getLanguage(context).equals(Utils.ARABIC)) {
            return new MenuHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_arabic, parent, false));
        } else {
            return new MenuHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_row, parent, false));
        }
        }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {

        final MenuModel menuModel = list.get(position);

        if (menuModel != null) {
            AtomicInteger quantityCount = new AtomicInteger();
            if (AppHelper.getCurrentUser() != null) {
                reference = MyFirebaseReferences.cartReference();
            }

            holder.title.setText(menuModel.getTitle());
            holder.desc.setText(menuModel.getDescription());
            holder.price.setText(AppHelper.setPrice(menuModel.getPrice()));

            Picasso.get().load(menuModel.getImageUrl()).noPlaceholder()
                    .fit().centerCrop().into(holder.imageView);
            if (getQuantity(menuModel.getId()) != null) {
                quantityCount.set(Integer.parseInt(getQuantity(menuModel.getId())));
            }
            holder.quantity.setText(quantityCount + "");

            holder.minus.setOnClickListener(v -> {
                if (AppHelper.getCurrentUser() != null) {
                    quantityCount.getAndDecrement();
                    if (quantityCount.get() < 0) {
                        quantityCount.set(0);
                    } else {
                        holder.quantity.setText(quantityCount + "");
                        if (quantityCount.get() > 0) {
                            addToCart(menuModel, quantityCount.get(), reference);
                        } else if (quantityCount.get() == 0) {
                            removeFromCart(menuModel.getId());
                        }
                    }
                } else {
                    noUserDialog();
                }
            });

            holder.plus.setOnClickListener(v -> {
                if (AppHelper.getCurrentUser() != null) {
                    quantityCount.getAndIncrement();
                    if (quantityCount.get() > 99) {
                        quantityCount.set(1);
                    } else {
                        addToCart(menuModel, quantityCount.get(), reference);
                        holder.quantity.setText(quantityCount + "");
                    }
                } else {
                    noUserDialog();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MenuHolder extends RecyclerView.ViewHolder {

        ImageView imageView, plus, minus;
        TextView title, desc, quantity, price;

        public MenuHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.menu_img);
            title = itemView.findViewById(R.id.menu_title);
            desc = itemView.findViewById(R.id.menu_desc);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price_desc);
        }

    }

    private void addToCart(@NotNull MenuModel menuModel, int quantityCount, @NotNull DatabaseReference reference) {
        CartModel cartModel = new CartModel(menuModel.getId(), "", quantityCount + "",
                menuModel.getPrice(), menuModel.getTitle());
        if (AppHelper.getCurrentUser() != null) {
            reference.child(menuModel.getId()).setValue(cartModel).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    AppHelper.showCenterToast(context, context.getString(R.string.addedtocart));
                }
            });
        }
    }

    private void removeFromCart(String id) {
        if (AppHelper.getCurrentUser() != null) {
            DatabaseReference reference = MyFirebaseReferences.cartReference();
            reference.child(id).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                }
            });
        }
    }

    private String getQuantity(String id) {

        final String[] quantity = {""};
        if (AppHelper.getCurrentUser() != null) {
            DatabaseReference reference1 = MyFirebaseReferences.cartReference().child(id);
            reference1.keepSynced(true);
            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        quantity[0] = dataSnapshot.child("quantity").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (quantity[0].isEmpty()) {
            return "0";
        } else {
            return quantity[0];
        }
    }
    public void filterList(List<MenuModel> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    private void noUserDialog() {
        Dialog mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.no_user_login_dialog);
        mDialog.show();
        mDialog.findViewById(R.id.login_dialog).setOnClickListener(v -> {
            context.startActivity(new Intent(context, AuthActivity.class));
            context.finish();
            mDialog.dismiss();
        });

    }
}
