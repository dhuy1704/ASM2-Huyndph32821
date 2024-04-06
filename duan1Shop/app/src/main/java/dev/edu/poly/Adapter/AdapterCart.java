package dev.edu.poly.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.edu.poly.Model.Cart;
import dev.edu.poly.Model.Product;
import dev.edu.poly.R;
import dev.edu.poly.databinding.ItemCartBinding;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViewHolder>{

    private List<Cart> cartList;
    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Product");

    public AdapterCart(List<Cart> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterCart.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new AdapterCart.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCart.ViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        Query query = reference.orderByChild("idKey").equalTo(cart.getIdProduct());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    holder.binding.titleTxt.setText(product.getName());
                    holder.binding.feeEachItem.setText(product.getPrice());
                    holder.binding.numberItemTxt.setText(cart.getQuantity()+"");
                    double price = Integer.parseInt(product.getPrice()) * cart.getQuantity();
                    holder.binding.totalEachItem.setText(price+"");
                    Picasso.get().load(product.getImage()).into(holder.binding.pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.binding.minusCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.getQuantity() > 1){
                    cart.setQuantity(cart.getQuantity()-1);
                    database.getReference("Cart").child(cart.getIdKey()).setValue(cart);
                }
            }
        });

        holder.binding.plusCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.setQuantity(cart.getQuantity()+1);
                database.getReference("Cart").child(cart.getIdKey()).setValue(cart);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MessageDialog.build()
                        .setTheme(DialogX.THEME.DARK)
                        .setTitle("Thông báo")
                        .setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng không ?")
                        .setOkButton("Có", (baseDialog, v1) -> {
                            database.getReference("Cart").child(cart.getIdKey()).removeValue();
                            return false;
                        })
                        .setCancelButton("Không", (baseDialog, v12) -> {
                            return false;
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
}
