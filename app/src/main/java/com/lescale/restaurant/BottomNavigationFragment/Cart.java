package com.lescale.restaurant.BottomNavigationFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lescale.restaurant.Activities.HomeActivity;
import com.lescale.restaurant.Activities.MapsActivity;
import com.lescale.restaurant.Adapters.CartAdapter;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.SendNotification;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.ModelClasses.CartModel;
import com.lescale.restaurant.ModelClasses.OrderModel;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lescale.restaurant.Classes.AppHelper.getDate;
import static com.lescale.restaurant.Classes.AppHelper.getTime;

/**
 * A simple {@link Fragment} subclass.
 */
public class Cart extends Fragment {

    public Cart() {
    }

    private RecyclerView recyclerView;
    private String latitude, longitude;
    private List<CartModel> list;
    private CartAdapter adapter;
    private boolean isCartEmpty;
    private MyDialog myDialog;
    private static final String TAG = "Cart";
    private TextView priceTxt;
    private RadioGroup radioGroup;
    private Button confirmOrderBtn;
    private String ORDER_TYPE = "";
    private TextView getCurrentLocation;
    private String myAdd = "";
    private RadioButton deliver, takeaway;
    private static final String DELIVERY = "Delivery";
    private static final String TAKE_AWAY = "Take_Away";
    private double mDistanceDiff = 0;
    private Dialog phoneDialog;
    private boolean isUserInDeliveryZone;
    private TextInputEditText messageEdit;
    private TextView deliveryFee;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NotNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (AppHelper.getLanguage(getActivity()).equals(Utils.ARABIC)) {
            view = inflater.inflate(R.layout.cart_arabic, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_cart, container, false);
        }

        latitude = AppHelper.getSharedPreferences(getActivity(), Utils.LATITUDE);
        longitude = AppHelper.getSharedPreferences(getActivity(), Utils.LONGITUDE);

        if (!latitude.isEmpty() && !longitude.isEmpty()) {
            mDistanceDiff = AppHelper.getDistanceInKMs(Double.parseDouble(latitude), Double.parseDouble(longitude));
        }

        TextView name = view.findViewById(R.id.name_cnfirmorder);
        TextView phone = view.findViewById(R.id.phone_cnfirmorder);

        deliveryFee = view.findViewById(R.id.delivery_fee);

        deliver = view.findViewById(R.id.delivery);

        takeaway = view.findViewById(R.id.takeaway);

        confirmOrderBtn = view.findViewById(R.id.placeorder_btn);

        messageEdit = view.findViewById(R.id.msg_cart);

        radioGroup = view.findViewById(R.id.radiogroup);

        myDialog = AppHelper.loadDialog(getActivity(), Objects.requireNonNull(getActivity()).getString(R.string.placingorder));

        priceTxt = view.findViewById(R.id.price_cnfirmorder);

        getCurrentLocation = view.findViewById(R.id.location_cnfirmorder);

        setupPhoneDialog();

        HomeActivity homeActivity = (HomeActivity) getActivity();

        if (homeActivity.toCart() == 7) {
            deliver.setChecked(true);
            ORDER_TYPE = DELIVERY;
            getCurrentLocation.setVisibility(View.VISIBLE);
            deliveryFee.setVisibility(View.VISIBLE);
        }

        deliver.setOnClickListener(v -> {
            ORDER_TYPE = DELIVERY;
            getCurrentLocation.setVisibility(View.VISIBLE);
            deliveryFee.setVisibility(View.VISIBLE);

        });

        takeaway.setOnClickListener(v -> {
            ORDER_TYPE = TAKE_AWAY;
            getCurrentLocation.setVisibility(View.GONE);
            deliveryFee.setVisibility(View.GONE);
        });

        getCurrentLocation.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MapsActivity.class));
        });

        if (!AppHelper.getSharedPreferences(getActivity(), Utils.USER_ADDRESS).isEmpty()) {
            getCurrentLocation.setText(AppHelper.getSharedPreferences(getActivity(), Utils.USER_ADDRESS));
            myAdd = AppHelper.getSharedPreferences(getActivity(), Utils.USER_ADDRESS);
        }

        name.setText(AppHelper.getUserName(getActivity()));
        phone.setText(AppHelper.getUserPhone(getActivity()));

        confirmOrderBtn.setOnClickListener(v -> {
            if (!AppHelper.getUserPhone(getActivity()).isEmpty()) {
                Log.i(TAG, "Order type: " + ORDER_TYPE + " Date: " + getDate() + " Time: " + getTime() + "");
                if (!"".equals(getAllItems())) {
                    if (ORDER_TYPE.equals(DELIVERY)) {
                        if (isUserInDeliveryZone) {
                            if (myAdd.isEmpty()) {
                                AppHelper.showCenterToast(getActivity(), Objects.requireNonNull(getActivity()).getString(R.string.addReqForDelivery));
                            } else {
                                placeOrder(getAllItems(), ORDER_TYPE, getDate(), messageEdit.getText().toString(), getTime(),
                                        String.valueOf(getTotalPrice()), latitude, longitude
                                        , AppHelper.getUserPhone(getActivity()), AppHelper.getUserName(getActivity()), myAdd);
                            }
                        } else {
                            noDeliveryZoneDialog();
                        }
                    } else if (ORDER_TYPE.equals(TAKE_AWAY)) {
                        placeOrder(getAllItems(), ORDER_TYPE, getDate(), messageEdit.getText().toString(), getTime(),
                                String.valueOf(getTotalPrice()), " ", " "
                                , AppHelper.getUserPhone(getActivity()), AppHelper.getUserName(getActivity()), "");

                    } else {
                        AppHelper.showCenterToast(getActivity(), Objects.requireNonNull(getActivity()).getString(R.string.select));
                    }
                } else {
                    AppHelper.showCenterToast(getActivity(), Objects.requireNonNull(getActivity()).getString(R.string.cartEmpty));
                }
            } else {
                phoneDialog.show();
            }
        });

        {
            recyclerView = view.findViewById(R.id.cartRecyclerview);
            myDialog = AppHelper.loadDialog(getActivity(), getString(R.string.loading));
            list = new ArrayList<>();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setStackFromEnd(true);
            linearLayoutManager.setReverseLayout(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
        }

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        if (getActivity() == null)
                            return;
                        getActivity().runOnUiThread(() -> {
                                priceTxt.setText(getFormalPrice(getTotalPrice()));
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        deliveryFee.setText(getActivity().getString(R.string.deliverfee) + "           " + getDeliveryPrice(mDistanceDiff));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppHelper.getCurrentUser() != null) {
            myDialog.show();
            isCartEmpty();
            getCartFromFirebase();
        }
    }

    private void getCartFromFirebase() {
        myDialog.show();
        DatabaseReference reference = MyFirebaseReferences.cartReference();
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        CartModel cartModel = ds.getValue(CartModel.class);
                        if (cartModel != null) {
                            list.add(cartModel);
                            myDialog.dismiss();
                        }
                        adapter = new CartAdapter(list, getActivity());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    myDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                AppHelper.showToast(getActivity(), databaseError.getMessage());
                myDialog.dismiss();
            }
        });
    }

    @NotNull
    private String getAllItems() {
        StringBuilder allItems = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            CartModel model = list.get(i);
            allItems.append(model.getQuantity()).append(" ").append(model.getTitle()).append("             ")
                    .append(AppHelper.setPrice(model.getPrice())).append("\n");
        }
        return allItems.toString();
    }

    private boolean isCartEmpty() {
        DatabaseReference reference = MyFirebaseReferences.cartReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isCartEmpty = false;
                } else {
                    isCartEmpty = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return isCartEmpty;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void placeOrder(String order, String type, String date, String message,
                            String time, String price, String latitude,
                            String longitude, String phone, String name, String myAdd) {
        myDialog.show();
        final DatabaseReference userOrder = MyFirebaseReferences.userOrderReference();
        DatabaseReference restaurantsOrder = MyFirebaseReferences.restaurantOrderReference();

        final String user_pID = userOrder.push().getKey();
        String res_pID = restaurantsOrder.push().getKey();

        String orderID = AppHelper.getOrderID();

        String deliveryFee = String.valueOf(getDeliveryPrice(mDistanceDiff));

        final OrderModel user_model = new OrderModel(date, time, order, price, phone,
                name, AppHelper.getUID(), orderID, user_pID, type, latitude, longitude,
                myAdd, message, deliveryFee);

        OrderModel rest_model = new OrderModel(date, time, order, price, phone,
                name, AppHelper.getUID(), orderID, res_pID, type, latitude, longitude,
                myAdd, message, deliveryFee);

        assert res_pID != null;
        restaurantsOrder.child(res_pID).setValue(rest_model).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                assert user_pID != null;
                userOrder.child(user_pID).setValue(user_model).addOnCompleteListener(task12 -> {
                    if (task12.isSuccessful()) {
                        final DatabaseReference myRef = MyFirebaseReferences.cartReference();
                        myRef.removeValue().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                SendNotification.sendNotification(getActivity(), AppHelper.getUserName(getActivity()));
                                AppHelper.setSharedPreferences(getActivity(), Utils.TOTAL_PRICE, "");
                                AppHelper.setSharedPreferences(getActivity(), Utils.ALL_ITEMS, "");
                                myDialog.dismiss();
                                AppHelper.showToast(getActivity(), Objects.requireNonNull(getActivity()).getString(R.string.orderplacedcnfm));
                                startActivity(new Intent(getActivity(), HomeActivity.class));
                                Objects.requireNonNull(getActivity()).finish();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myDialog.dismiss();
    }


    @NotNull
    private String getFormalPrice(int price) {
        if (deliver.isChecked()) {
            if (mDistanceDiff != 0) {
                price += getDeliveryPrice(mDistanceDiff);
            }
        }
        if (getActivity() != null)
            return getActivity().getString(R.string.total) + price + " MRU";
        else return "";
    }

    @NotNull
    private int getTotalPrice() {
        int price = 0;
        for (int i = 0; i < list.size(); i++) {
            CartModel model = list.get(i);
            int q = Integer.parseInt(model.getQuantity());
            int p = Integer.parseInt(model.getPrice());
            price += q * p;
        }
        return price;
    }

    @Contract(pure = true)
    private int getDeliveryPrice(double mDistanceDiff) {
        int deliveryCharges = 0;
        if (mDistanceDiff > 0 && mDistanceDiff < 2.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 20;
        } else if (mDistanceDiff > 2.0 && mDistanceDiff < 4.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 30;
        } else if (mDistanceDiff > 4.0 && mDistanceDiff < 6.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 40;
        } else if (mDistanceDiff > 6.0 && mDistanceDiff < 8.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 50;
        } else if (mDistanceDiff > 8.0 && mDistanceDiff < 10.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 60;
        } else if (mDistanceDiff > 10.0 && mDistanceDiff < 12.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 70;
        } else if (mDistanceDiff > 12.0 && mDistanceDiff < 14.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 80;
        } else if (mDistanceDiff > 14.0 && mDistanceDiff < 16.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 90;
        } else if (mDistanceDiff > 16.0 && mDistanceDiff < 18.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 100;
        } else if (mDistanceDiff > 18.0 && mDistanceDiff < 20.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 120;
        } else if (mDistanceDiff > 20.0 && mDistanceDiff < 22.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 140;
        } else if (mDistanceDiff > 22.0 && mDistanceDiff < 24.1) {
            isUserInDeliveryZone = true;
            deliveryCharges = 150;
        } else {
            isUserInDeliveryZone = false;
        }
        return deliveryCharges;
    }

    private void noDeliveryZoneDialog() {
        String msg = getString(R.string.noDeliverzone);
        AlertDialog.Builder dial = new AlertDialog.Builder(getContext());
        dial.setTitle("Sorry !!");
        dial.setMessage(msg).setCancelable(false).
                setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog dialog = dial.create();
        dialog.show();
    }

    private void setupPhoneDialog() {
        phoneDialog = new Dialog(getActivity());
        phoneDialog.setContentView(R.layout.phone_dialog);
        phoneDialog.setCancelable(true);
        TextInputEditText phoneEditText = phoneDialog.findViewById(R.id.phone_t_d);
        Button okButtonDialog = phoneDialog.findViewById(R.id.phone_d);
        okButtonDialog.setOnClickListener(v -> {
            String phone = phoneEditText.getText().toString().trim();
            if (phone.isEmpty()) {
                phoneEditText.setError(getString(R.string.num_req));
                phoneEditText.requestFocus();
            } else {
                MyFirebaseReferences.userPhoneNumber().setValue(phone)
                        .addOnCompleteListener(task -> {
                            AppHelper.setUserPhone(getActivity(), phone);
                            phoneDialog.dismiss();
                        });
            }
        });
    }
}
