package dev.edu.poly.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.edu.poly.R;
import dev.edu.poly.databinding.ChartItemBinding;

public class AdapterChart extends RecyclerView.Adapter<AdapterChart.ViewHolder>{

    private List<dev.edu.poly.Model.Chart> chartList;
    private Context context;

    public AdapterChart(List<dev.edu.poly.Model.Chart> chartList, Context context) {
        this.chartList = chartList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterChart.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chart_item, parent, false);
        return new AdapterChart.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChart.ViewHolder holder, int position) {
        dev.edu.poly.Model.Chart chart = chartList.get(position);
        holder.binding.tvName.setText(chart.getName());
        holder.binding.tvPrice.setText("$"+chart.getPrice());
        holder.binding.tvQuantity.setText("x"+chart.getQuantity()+"");
        double total = Double.parseDouble(chart.getPrice())*Integer.parseInt(chart.getQuantity());
        holder.binding.tvTotal.setText("$"+total);
        Picasso.get().load(chart.getImage()).into(holder.binding.ivImage);
    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ChartItemBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChartItemBinding.bind(itemView);
        }
    }
}
