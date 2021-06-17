package com.lescale.restaurant.BottomNavigationFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lescale.restaurant.Activities.HomeActivity;
import com.lescale.restaurant.Activities.Language;
import com.lescale.restaurant.Activities.OrderHistory;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.MyDialog;
import com.lescale.restaurant.Classes.MyFirebaseReferences;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {

    public Settings() {
        // Required empty public constructor
    }

    private MyDialog myDialog;
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView orderHistory = view.findViewById(R.id.order_history);
        TextView logOut = view.findViewById(R.id.order_logout);
        TextView changeLanguage = view.findViewById(R.id.lang);

        myDialog = AppHelper.loadDialog(getActivity(),Objects.requireNonNull(getActivity()).getString(R.string.siginingOut));

        orderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OrderHistory.class);
            startActivity(intent);
        });

        changeLanguage.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Language.class));
            Objects.requireNonNull(getActivity()).finish();
        } );

        logOut.setOnClickListener(v -> {
            myDialog.show();
            DatabaseReference reference = MyFirebaseReferences.userInfoReference();
            reference.child(Utils.DEVICE_TOKEN).setValue(" ").addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signOut();
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }
            });

        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myDialog.dismiss();
    }
}
