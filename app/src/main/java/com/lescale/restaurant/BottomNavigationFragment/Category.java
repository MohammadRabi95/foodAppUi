package com.lescale.restaurant.BottomNavigationFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Category extends Fragment {

    public Category() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        ImageView SandwichImg, BurgersImg, TacosImg, SpecialImg, PizzaImg, EntreesImg,
                PatesImg, ViandesImg, GrilladesImg, PouletImg, PoissonsImg, PromosImg,
                Boissons_chaudesImg, MilkshakesImg, JusImg, SaladesImg, DesertImg, Petit_DejemurImg;

         CardView Sandwich, Burgers, Tacos, Special, Pizza, Apetizer,
                pasta, meat, grill, chicken, seafood, Promos,
                hotdrinks, Milkshakes, juice, Salads, Desert, breakfast;

        // ImageViews
        SandwichImg = view.findViewById(R.id.sandwich_img);
        BurgersImg = view.findViewById(R.id.burgers_img);
        TacosImg = view.findViewById(R.id.tacos_img);
        SpecialImg = view.findViewById(R.id.special_img);
        PizzaImg = view.findViewById(R.id.pizza_img);
        EntreesImg = view.findViewById(R.id.entrees_img);
        PatesImg = view.findViewById(R.id.pates_img);
        ViandesImg = view.findViewById(R.id.viandes_img);
        GrilladesImg = view.findViewById(R.id.grillades_img);
        PouletImg = view.findViewById(R.id.poulets_img);
        PoissonsImg = view.findViewById(R.id.poissons_img);
        PromosImg = view.findViewById(R.id.promos_img);
        Boissons_chaudesImg = view.findViewById(R.id.boissons_img);
        MilkshakesImg = view.findViewById(R.id.milkshake_img);
        JusImg = view.findViewById(R.id.jus_img);
        SaladesImg = view.findViewById(R.id.salads_img);
        DesertImg = view.findViewById(R.id.desert_img);
        Petit_DejemurImg = view.findViewById(R.id.petitdejemur_img);

        //CardViews
        Sandwich = view.findViewById(R.id.sandwich);
        Burgers = view.findViewById(R.id.burgers);
        Tacos = view.findViewById(R.id.tacos);
        Special = view.findViewById(R.id.special);
        Pizza = view.findViewById(R.id.pizza);
        Apetizer = view.findViewById(R.id.entrees);
        pasta = view.findViewById(R.id.pates);
        meat = view.findViewById(R.id.viandes);
        grill = view.findViewById(R.id.grillades);
        chicken = view.findViewById(R.id.poulets);
        seafood = view.findViewById(R.id.poissons);
        Promos = view.findViewById(R.id.promos);
        hotdrinks = view.findViewById(R.id.boissons);
        Milkshakes = view.findViewById(R.id.milkshake);
        juice = view.findViewById(R.id.jus);
        Salads = view.findViewById(R.id.salads);
        Desert = view.findViewById(R.id.desert);
        breakfast = view.findViewById(R.id.petitdejemur);

        //Loading Images
        Picasso.get().load(R.drawable.sandwichs).noPlaceholder().fit().centerCrop().noPlaceholder().into(SandwichImg);
        Picasso.get().load(R.drawable.burgers).noPlaceholder().fit().centerCrop().noPlaceholder().into(BurgersImg);
        Picasso.get().load(R.drawable.tacos).noPlaceholder().fit().centerCrop().noPlaceholder().into(TacosImg);
        Picasso.get().load(R.drawable.special).noPlaceholder().fit().centerCrop().noPlaceholder().into(SpecialImg);
        Picasso.get().load(R.drawable.pizzas).noPlaceholder().fit().centerCrop().noPlaceholder().into(PizzaImg);
        Picasso.get().load(R.drawable.entrees).noPlaceholder().fit().centerCrop().noPlaceholder().into(EntreesImg);
        Picasso.get().load(R.drawable.pates).noPlaceholder().fit().centerCrop().noPlaceholder().into(PatesImg);
        Picasso.get().load(R.drawable.viandes).noPlaceholder().fit().centerCrop().noPlaceholder().into(ViandesImg);
        Picasso.get().load(R.drawable.grillades).noPlaceholder().fit().centerCrop().noPlaceholder().into(GrilladesImg);
        Picasso.get().load(R.drawable.chicken).noPlaceholder().fit().centerCrop().noPlaceholder().into(PouletImg);
        Picasso.get().load(R.drawable.poissons).noPlaceholder().fit().centerCrop().noPlaceholder().into(PoissonsImg);
        Picasso.get().load(R.drawable.promos).noPlaceholder().fit().centerCrop().noPlaceholder().into(PromosImg);
        Picasso.get().load(R.drawable.boissons_chaudes).noPlaceholder().fit().centerCrop().noPlaceholder().into(Boissons_chaudesImg);
        Picasso.get().load(R.drawable.milkshakes).noPlaceholder().fit().centerCrop().noPlaceholder().into(MilkshakesImg);
        Picasso.get().load(R.drawable.jus).noPlaceholder().fit().centerCrop().noPlaceholder().into(JusImg);
        Picasso.get().load(R.drawable.salaldes).noPlaceholder().fit().centerCrop().noPlaceholder().into(SaladesImg);
        Picasso.get().load(R.drawable.dessert).noPlaceholder().fit().centerCrop().noPlaceholder().into(DesertImg);
        Picasso.get().load(R.drawable.etit_dejeuner).noPlaceholder().fit().centerCrop().noPlaceholder().into(Petit_DejemurImg);

        //Click Listeners
        Sandwich.setOnClickListener(v -> loadFragment(Utils.SANDWICH));
        Burgers.setOnClickListener(v -> loadFragment(Utils.BURGERS));
        Tacos.setOnClickListener(v -> loadFragment(Utils.TACOS));
        Special.setOnClickListener(v -> loadFragment(Utils.SPECIAL));
        Pizza.setOnClickListener(v -> loadFragment(Utils.PIZZA));
        Apetizer.setOnClickListener(v -> loadFragment(Utils.APETIZER));
        pasta.setOnClickListener(v -> loadFragment(Utils.PASTAS));
        meat.setOnClickListener(v -> loadFragment(Utils.MEAT));
        grill.setOnClickListener(v -> loadFragment(Utils.GRILL));
        chicken.setOnClickListener(v -> loadFragment(Utils.CHICKEN));
        seafood.setOnClickListener(v -> loadFragment(Utils.SEAFOOD));
        Promos.setOnClickListener(v -> loadFragment(Utils.PROMOS));
        hotdrinks.setOnClickListener(v -> loadFragment(Utils.HOT_DRINKS));
        Milkshakes.setOnClickListener(v -> loadFragment(Utils.MILKSHAKES));
        juice.setOnClickListener(v -> loadFragment(Utils.JUICES));
        Salads.setOnClickListener(v -> loadFragment(Utils.SALADS));
        Desert.setOnClickListener(v -> loadFragment(Utils.DESERT));
        breakfast.setOnClickListener(v -> loadFragment(Utils.BREAKFAST));

        return view;
    }

    private void loadFragment(String Category) {
        AppHelper.setCategory(getActivity(),Category);
        Fragment myFragment = new MyMenu();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_frame, myFragment).addToBackStack(null).
                commitAllowingStateLoss();
    }

}
