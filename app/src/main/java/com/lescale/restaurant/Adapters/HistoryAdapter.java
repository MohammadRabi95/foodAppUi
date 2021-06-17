package com.lescale.restaurant.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.lescale.restaurant.Activities.HomeActivity;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.SendNotification;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.ModelClasses.OrderModel;
import com.lescale.restaurant.R;

import java.util.List;
import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.OrderHolder> {

    private List<OrderModel> list;
    private Activity activity;
    private MyDialog myDialog;

    public HistoryAdapter(List<OrderModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
        myDialog = AppHelper.loadDialog(activity,
                Objects.requireNonNull(activity)
                        .getString(R.string.placingorder));
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        OrderModel model = list.get(position);

        if (model != null) {
            holder.items.setText(model.getItems());
            holder.price.setText(model.getPrice());
            holder.time.setText(model.getTime());
            holder.date.setText(model.getDate());
            holder.orderid.setText("ORDER ID: " + model.getOrderid());
            holder.type.setText(model.getType().replace("_", " "));

            holder.reOrder.setOnClickListener(v -> {
                    reOrder(model.getItems(),model.getType(),AppHelper.getDate(),model.getMessage(),
                            AppHelper.getTime(),model.getPrice(),model.getLatitude(),model.getLongitude(),
                            AppHelper.getUserPhone(activity),AppHelper.getUserName(activity),model.getAddress()
                    ,model.getDeliveryFee());
            });


            holder.delete.setOnClickListener(v -> {
                String msg = activity.getString(R.string.del_cnfm_msg);
                AlertDialog.Builder dial = new AlertDialog.Builder(v.getContext());
                dial.setMessage(msg).setCancelable(false).
                        setPositiveButton(R.string.yes, (dialog, which) -> {
                            String id = model.getpID();
                            DatabaseReference reference = MyFirebaseReferences.userOrderReference()
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

    class OrderHolder extends RecyclerView.ViewHolder {
        TextView date, time, type, price, items, orderid;
        Button reOrder,delete;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_row);
            time = itemView.findViewById(R.id.time_row);
            type = itemView.findViewById(R.id.type_row);
            price = itemView.findViewById(R.id.price_h_row);
            items = itemView.findViewById(R.id.order_details_row);
            orderid = itemView.findViewById(R.id.orderid);
            reOrder = itemView.findViewById(R.id.reorderBtn);
            delete = itemView.findViewById(R.id.deletehistoryOrder);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void reOrder(String order, String type, String date, String message,
                            String time, String price, String latitude,
                            String longitude, String phone, String name,
                         String myAdd, String deliveryFee) {
        myDialog.show();
        final DatabaseReference userOrder = MyFirebaseReferences.userOrderReference();
        DatabaseReference restaurantsOrder = MyFirebaseReferences.restaurantOrderReference();

        final String user_pID = userOrder.push().getKey();
        String res_pID = restaurantsOrder.push().getKey();

        String orderID = AppHelper.getOrderID();
        final OrderModel user_model = new OrderModel(date, time, order, price, phone, name,
                AppHelper.getUID(), orderID, user_pID, type, latitude, longitude, myAdd,message, deliveryFee);

        OrderModel rest_model = new OrderModel(date, time, order, price, phone, name,
                AppHelper.getUID(), orderID, res_pID, type, latitude, longitude, myAdd,message, deliveryFee);

        assert res_pID != null;
        restaurantsOrder.child(res_pID).setValue(rest_model).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                assert user_pID != null;
                userOrder.child(user_pID).setValue(user_model).addOnCompleteListener(task12 -> {
                    if (task12.isSuccessful()) {
                        final DatabaseReference myRef = MyFirebaseReferences.cartReference();
                        myRef.removeValue().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                SendNotification.sendNotification(activity, AppHelper.getUserName(activity));
                                myDialog.dismiss();
                                AppHelper.showToast(activity, Objects.requireNonNull(activity).getString(R.string.orderplacedcnfm));
                                activity.startActivity(new Intent(activity, HomeActivity.class));
                                Objects.requireNonNull(activity).finish();
                            }
                        });
                    }
                });
            }
        });
    }
}
