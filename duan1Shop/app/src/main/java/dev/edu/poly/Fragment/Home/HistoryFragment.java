package dev.edu.poly.Fragment.Home;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import dev.edu.poly.Adapter.AdapterHistory;
import dev.edu.poly.Model.Invoice;
import dev.edu.poly.R;
import dev.edu.poly.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {

    FragmentHistoryBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Invoice");
    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataLogin", getActivity().MODE_PRIVATE);
        String key = sharedPreferences.getString("key", "");

        Query query = myRef.orderByChild("idUser").equalTo(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Invoice> list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Invoice invoice = dataSnapshot.getValue(Invoice.class);
                        list.add(invoice);
                    }
                    AdapterHistory adapterHistory = new AdapterHistory( getContext(), list);
                    binding.recyclerView.setAdapter(adapterHistory);
                    binding.recyclerView.setHasFixedSize(true);
                    binding.recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}