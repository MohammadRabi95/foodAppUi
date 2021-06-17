package com.lescale.restaurant.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.ModelClasses.CartModel;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private List<CartModel> list;
    private Activity activity;

    public CartAdapter(List<CartModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.cart_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, final int position) {
        final CartModel cartModel = list.get(position);
        if (cartModel != null){
            int p = Integer.parseInt(cartModel.getPrice()) * Integer.parseInt(cartModel.getQuantity());
                holder.title.setText(cartModel.getTitle());
                holder.price.setText(AppHelper.setPrice(String.valueOf(p)));
                holder.quantity.setText(cartModel.getQuantity());



                holder.delete.setOnClickListener(v -> {
                    String msg = activity.getString(R.string.del_cnfm_msg);
                    AlertDialog.Builder dial = new AlertDialog.Builder(v.getContext());
                    dial.setMessage(msg).setCancelable(false).
                            setPositiveButton(R.string.yes, (dialog, which) -> {
                                String id = cartModel.getItem_id();
                                DatabaseReference reference = MyFirebaseReferences.cartReference()
                                        .child(id);
                                reference.removeValue().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()){
                                        notifyItemRemoved(position);
                                        dialog.dismiss();
                                        AppHelper.showToast(activity,activity.getString(R.string.del_succ_msg));
                                    }
                                });
                            }).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
                    AlertDialog dialog = dial.create();
                    dialog.show();
                });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CartHolder extends RecyclerView.ViewHolder{

        TextView title,price,quantity;
        ImageView delete;
        public CartHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
