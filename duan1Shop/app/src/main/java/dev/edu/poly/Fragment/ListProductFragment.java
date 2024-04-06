package dev.edu.poly.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.edu.poly.Adapter.AdapterProducts;
import dev.edu.poly.Fragment.Admin.AddProductFragment;
import dev.edu.poly.Model.Brand;
import dev.edu.poly.Model.Product;
import dev.edu.poly.R;
import dev.edu.poly.databinding.FragmentListProductBinding;


public class ListProductFragment extends Fragment {

    FragmentListProductBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Product");
    public ListProductFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         binding = FragmentListProductBinding.inflate(inflater, container, false);
            View view = binding.getRoot();
            return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataLogin", getActivity().MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");
        if(role.equals("admin")){
            binding.addBtn.setVisibility(View.VISIBLE);
        }

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragment(new AddProductFragment());
            }
        });
        if(getArguments() != null) {
            Brand brand = (Brand) getArguments().getSerializable("brand");
            assert brand != null;
            Query query = database.getReference("Product").orderByChild("brand").equalTo(brand.getId());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Product> list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        list.add(product);
                    }
                    AdapterProducts adapterProduct = new AdapterProducts(getContext(), list);
                    binding.recyclerView.setAdapter(adapterProduct);
                    binding.recyclerView.setHasFixedSize(true);
                    binding.recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }
}