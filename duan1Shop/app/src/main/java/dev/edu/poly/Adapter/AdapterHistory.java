package dev.edu.poly.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dev.edu.poly.Model.Invoice;
import dev.edu.poly.R;
import dev.edu.poly.databinding.HistoryItemBinding;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {

    private Context context;
    private List<Invoice> historyList;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Invoice");

    public AdapterHistory(Context context, List<Invoice> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public AdapterHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new AdapterHistory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolder holder, int position) {
        Invoice invoice = historyList.get(position);
        holder.binding.txtEmail.setText(invoice.getEmail());
        Long date = Long.parseLong(invoice.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
        holder.binding.txtDate.setText(simpleDateFormat.format(date));
        holder.binding.txtTotal.setText("$" + invoice.getTotal());
        if (invoice.getStatus() == 0) {
            holder.binding.txtStatus.setText("Chờ xác nhận");
            holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.blue));
        }
        else if (invoice.getStatus() == 1) {
            holder.binding.txtStatus.setText("Đã xác nhận");
            holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.grey));
        }
        else if (invoice.getStatus() == 2) {
            holder.binding.txtStatus.setText("Đang giao hàng");
            holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.orange));
        }
        else if (invoice.getStatus() == 3) {
            holder.binding.txtStatus.setText("Đã giao hàng");
            holder.binding.txtStatus.setTextColor(context.getResources().getColor(org.angmarch.views.R.color.light_gray));
        }
        else if (invoice.getStatus() == 4) {
            holder.binding.txtStatus.setText("Đã hủy");
            holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.binding.txtAddress.setText(invoice.getAddress());
        SharedPreferences sharedPreferences = context.getSharedPreferences("dataLogin", context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");
        if (role.equals("admin")) {
            holder.binding.linearStatus.setVisibility(View.VISIBLE);
            List<String> list = new LinkedList<>();
            list.add("Chờ xác nhận");
            list.add("Đã xác nhận");
            list.add("Đang giao hàng");
            list.add("Đã giao hàng");
            list.add("Đã hủy");
            holder.binding.spinner.attachDataSource(list);
            holder.binding.spinner.setSelectedIndex(invoice.getStatus());
            holder.binding.spinner.setOnSpinnerItemSelectedListener((parent, view, position1, id) -> {
                if (position1 == 0) {
                    invoice.setStatus(0);
                }
                else if (position1 == 1) {
                    invoice.setStatus(1);
                }
                else if (position1 == 2) {
                    invoice.setStatus(2);
                }
                else if (position1 == 3) {
                    invoice.setStatus(3);
                }
                else if (position1 == 4) {
                    invoice.setStatus(4);
                }
                myRef.child(invoice.getIdKey()).setValue(invoice);
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        HistoryItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HistoryItemBinding.bind(itemView);
        }
    }
}
