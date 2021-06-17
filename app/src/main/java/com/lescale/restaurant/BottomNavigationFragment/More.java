package com.lescale.restaurant.BottomNavigationFragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.lescale.restaurant.Classes.Utils.FACEBOOK_PAGE_ID;
import static com.lescale.restaurant.Classes.Utils.FACEBOOK_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class More extends Fragment implements View.OnClickListener {

    public More() {
    }

    private TextView fb, web, share, whatsapp, phone, email;
    private TextView textView, prestDesc;
    private boolean isOpened = false;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more, container, false);

        fb = view.findViewById(R.id.facebook);
        web = view.findViewById(R.id.web);
        share = view.findViewById(R.id.share);
        whatsapp = view.findViewById(R.id.whatsapp);
        phone = view.findViewById(R.id.telephone);
        email = view.findViewById(R.id.email);
        textView = view.findViewById(R.id.presentation);
        prestDesc = view.findViewById(R.id.presentation_description);

        fb.setOnClickListener(this);
        web.setOnClickListener(this);
        share.setOnClickListener(this);
        whatsapp.setOnClickListener(this);
        phone.setOnClickListener(this);
        email.setOnClickListener(this);
        textView.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.email: {
                String mail = "Riadh.azzabi@gmail.com";
                String[] emails = mail.split(",");
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "LESCALE Restaurant");
                emailIntent.setType("text/plain");
                startActivity(emailIntent);
            }
            break;
            case R.id.telephone: {
                Intent intent0 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+22237111777"));
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.CALL_PHONE}, 1);
                    AppHelper.showCenterToast(getActivity(), getString(R.string.pressagantocall));
                } else {
                    startActivity(intent0);
                }
            }
            break;
            case R.id.whatsapp: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://api.whatsapp.com/send?phone=" + "+22237111777")));
            }
            break;
            case R.id.share: {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "LESCALE Restaurant");
                    String shareMessage = "\nLet me recommend you LESCALE Restaurants App\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + "com.lescale.restaurant";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    Log.e("Share Intent Error", e.getMessage());
                }
            }
            break;
            case R.id.web: {
                String url = "http://www.lescale.co/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            break;
            case R.id.facebook: {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL();
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
            break;

            case R.id.presentation:
                if (isOpened) {
                    prestDesc.setVisibility(View.GONE);
                    isOpened = false;
                } else {
                    prestDesc.setVisibility(View.VISIBLE);
                    isOpened = true;
                }
                break;
        }
    }

    private String getFacebookPageURL() {
        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL;
        }
    }
}
