package dev.edu.poly.Fragment.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.edu.poly.Adapter.AdapterChart;
import dev.edu.poly.Model.Chart;
import dev.edu.poly.Model.DetailInvoice;
import dev.edu.poly.Model.Invoice;
import dev.edu.poly.R;
import dev.edu.poly.databinding.FragmentChartBinding;

public class ChartFragment extends Fragment {


    FragmentChartBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    public ChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.txtDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Chọn ngày");
            MaterialDatePicker materialDatePicker = builder.build();
            materialDatePicker.show(getParentFragmentManager(), "DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                String format = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                binding.txtDate.setText(simpleDateFormat.format(materialDatePicker.getSelection()));
            });
        });
        binding.btnView.setOnClickListener(v -> {
            String date = binding.txtDate.getText().toString();
            if (date.isEmpty()) {
                binding.txtDate.setError("Please choose date");
                return;
            }
            Calendar calendar = Calendar.getInstance();
            String[] arr = date.split("/");
            calendar.set(Integer.parseInt(arr[2]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[0]));
            myRef.child("Invoice").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int total = 0;
                        List<Chart> listChart = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            int status = Integer.parseInt(dataSnapshot.child("status").getValue().toString());
                            if(status == 0 || status == 1 || status == 2 || status == 4){//khi đã giao hàng mới tính doanh thu
                                continue; //bỏ qua các đơn hàng chưa giao
                            }
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTimeInMillis(Long.parseLong(dataSnapshot.child("date").getValue().toString()));
                            if (calendar1.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                                if (calendar1.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                                    if (calendar1.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                                        total += Integer.parseInt(dataSnapshot.child("total").getValue().toString());
                                        Invoice invoice = dataSnapshot.getValue(Invoice.class);
                                        assert invoice != null;
                                        Log.e("TAGAPI", "onDataChange: " + invoice.getIdKey());
                                        myRef.child("DetailInvoice").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        DetailInvoice detailInvoice = dataSnapshot.getValue(DetailInvoice.class);
                                                        if (detailInvoice.getIdInvoice().equals(invoice.getIdKey())) {
                                                            myRef.child("Product").addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                            if (dataSnapshot.getKey().equals(detailInvoice.getIdProduct())) {
                                                                                Chart chart = new Chart();
                                                                                chart.setName(dataSnapshot.child("name").getValue().toString());
                                                                                chart.setPrice(dataSnapshot.child("price").getValue().toString());
                                                                                chart.setQuantity(detailInvoice.getQuantityProduct());
                                                                                chart.setImage(dataSnapshot.child("image").getValue().toString());
                                                                                listChart.add(chart);
                                                                            }
                                                                        }
                                                                        for (int i = 0; i < listChart.size(); i++) {
                                                                            for (int j = i + 1; j < listChart.size(); j++) {
                                                                                if (listChart.get(i).getName().equals(listChart.get(j).getName())) {
                                                                                    int quantity = Integer.parseInt(listChart.get(i).getQuantity()) + Integer.parseInt(listChart.get(j).getQuantity());
                                                                                    listChart.get(i).setQuantity(quantity + "");
                                                                                    listChart.remove(j);
                                                                                }
                                                                            }
                                                                        }

                                                                        AdapterChart adapterChart = new AdapterChart(listChart, getContext());
                                                                        binding.recyclerView.setAdapter(adapterChart);
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
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                        binding.txtTotal.setText("$" + total);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }
}