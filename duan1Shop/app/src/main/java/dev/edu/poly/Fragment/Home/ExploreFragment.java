package dev.edu.poly.Fragment.Home;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.edu.poly.Adapter.AdapterBrand;
import dev.edu.poly.Adapter.AdapterProducts;
import dev.edu.poly.Fragment.Admin.AddBrandFragment;
import dev.edu.poly.Model.Brand;
import dev.edu.poly.Model.Product;
import dev.edu.poly.R;
import dev.edu.poly.databinding.FragmentExploreBinding;


public class ExploreFragment extends Fragment {


    FragmentExploreBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Brand");
    private DatabaseReference myRef2 = database.getReference("Product");

    public ExploreFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataLogin", getActivity().MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");
        if(role.equals("admin")){
            binding.imageButtonAddBrand.setVisibility(View.VISIBLE);
        }

        binding.textView2.setText(sharedPreferences.getString("name", ""));

        binding.imageButtonAddBrand.setOnClickListener(v -> {
            getFragment(new AddBrandFragment());
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Brand> brandList = new ArrayList<>();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Brand brand = snapshot1.getValue(Brand.class);
                        brandList.add(brand);
                    }
                    binding.viewBrand.setAdapter(new AdapterBrand(brandList, getContext()));
                    binding.viewBrand.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Product> productList = new ArrayList<>();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Product product = snapshot1.getValue(Product.class);
                        productList.add(product);
                    }
                    binding.viewPopularProduct.setAdapter(new AdapterProducts(getContext(),productList));
                    binding.viewPopularProduct.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }
}