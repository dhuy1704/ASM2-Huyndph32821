package dev.edu.poly.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.edu.poly.Fragment.Admin.AddProductFragment;
import dev.edu.poly.Fragment.DetailFragment;
import dev.edu.poly.Model.Brand;
import dev.edu.poly.Model.Product;
import dev.edu.poly.R;
import dev.edu.poly.Screen.HomeActivity;
import dev.edu.poly.databinding.ItemProductBinding;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ViewHolder>{


    private Context context;
    private List<Product> productList;

    public AdapterProducts(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public AdapterProducts.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new AdapterProducts.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProducts.ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.binding.titleTxt.setText(product.getName());
        holder.binding.feeTxt.setText(product.getPrice() + " VND");
        Picasso.get().load(product.getImage()).into(holder.binding.pic);

        SharedPreferences sharedPreferences = context.getSharedPreferences("dataLogin", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");
        if (role.equals("admin")) {
            holder.itemView.setOnLongClickListener(v -> {
                getFragment(new AddProductFragment(), product);
                return false;
            });
        }

        holder.itemView.setOnClickListener(v -> {
            getFragment(new DetailFragment(), product);
        });

    }
    private void getFragment(Fragment fragment, Product product) {
        Bundle bundle = new Bundle();
        bundle.putString("status", "edit");
        bundle.putSerializable("product", product);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = ((HomeActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragmentContainerView, fragment).commit();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductBinding.bind(itemView);
        }
    }
}
