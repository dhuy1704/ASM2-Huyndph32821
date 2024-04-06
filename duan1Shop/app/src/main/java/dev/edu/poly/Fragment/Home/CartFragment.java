package dev.edu.poly.Fragment.Home;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;

import java.util.ArrayList;
import java.util.List;

import dev.edu.poly.Adapter.AdapterCart;
import dev.edu.poly.Model.Cart;
import dev.edu.poly.Model.DetailInvoice;
import dev.edu.poly.Model.Invoice;
import dev.edu.poly.R;
import dev.edu.poly.databinding.FragmentCartBinding;


public class CartFragment extends Fragment {

    FragmentCartBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Cart");
    int total = 0;
    private List<Cart> cartList = new ArrayList<>();
    boolean check = false;
    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataLogin", getActivity().MODE_PRIVATE);
        String idUser = sharedPreferences.getString("key", "");
        Query query = database.getReference("Cart").orderByChild("idUser").equalTo(idUser);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.cartRecyclerView.setVisibility(View.VISIBLE);
                    List<Cart> carts = new ArrayList<>();
                    cartList.clear();
                    total = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Cart cart = snapshot1.getValue(Cart.class);
                        carts.add(cart);
                        cartList.add(cart);
                    }
                    for (int i = 0; i < carts.size(); i++) {
                        Query query = database.getReference("Product").orderByChild("idKey").equalTo(carts.get(i).getIdProduct());
                        int finalI = i;

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    int price = Integer.parseInt(dataSnapshot.child("price").getValue().toString());
                                    total += price * carts.get(finalI).getQuantity();
                                    binding.subtotalTxt.setText("$"+total);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    AdapterCart adapterCart = new AdapterCart(carts, getContext());
                    binding.cartRecyclerView.setAdapter(adapterCart);
                    binding.cartRecyclerView.setHasFixedSize(true);
                    binding.cartRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

                }else{
                    total = 0;
                    binding.subtotalTxt.setText("$"+total);
                    binding.cartRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.orderNowBtn.setOnClickListener(v -> {
            String address = binding.editAddress.getText().toString();
            if(address.isEmpty()){
                binding.editAddress.setError("Vui lòng nhập địa chỉ");
                return;
            }
            if(total == 0){
                return;
            }

            //check số lượng
            for (int i = 0; i < cartList.size(); i++) {
                Query query1 = database.getReference("Product").orderByChild("idKey").equalTo(cartList.get(i).getIdProduct());
                int finalI = i;
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            int quantity = Integer.parseInt(dataSnapshot.child("quantity").getValue().toString());
                            if(quantity < cartList.get(finalI).getQuantity()){
                                MessageDialog.build()
                                        .setTheme(DialogX.THEME.DARK)
                                        .setTitle("Thông báo")
                                        .setMessage("Sản phẩm "+dataSnapshot.child("name").getValue().toString()+" đã hết hàng")
                                        .setOkButton("OK", (baseDialog, v1) -> {
                                            check = true;
                                            return false;
                                        })
                                        .show();
                                return;
                            }
                        }
                            MessageDialog.build()
                                    .setTheme(DialogX.THEME.DARK)
                                    .setTitle("Thông báo")
                                    .setMessage("Bạn có muốn đặt hàng không ?")
                                    .setOkButton("Có", (baseDialog, v1) -> {
                                        WaitDialog.show(getActivity(), "Đang đặt hàng...");
                                        Invoice invoice = new Invoice();
                                        invoice.setIdUser(idUser);
                                        invoice.setAddress(address);
                                        invoice.setTotal(String.valueOf(total));
                                        invoice.setEmail(sharedPreferences.getString("email", ""));
                                        invoice.setIdKey(database.getReference("Invoice").push().getKey());
                                        invoice.setDate(String.valueOf(System.currentTimeMillis()));
                                        invoice.setStatus(0);
                                        database.getReference("Invoice").child(invoice.getIdKey()).setValue(invoice);
                                        for (int i = 0; i < cartList.size(); i++) {
                                            DetailInvoice detailInvoice = new DetailInvoice();
                                            detailInvoice.setIdInvoice(invoice.getIdKey());
                                            detailInvoice.setIdKey(database.getReference("DetailInvoice").push().getKey());
                                            detailInvoice.setQuantityProduct(String.valueOf(cartList.get(i).getQuantity()));
                                            detailInvoice.setIdProduct(cartList.get(i).getIdProduct());
                                            database.getReference("DetailInvoice").child(detailInvoice.getIdKey()).setValue(detailInvoice);
                                            database.getReference("Cart").child(cartList.get(i).getIdKey()).removeValue();
                                            //update số lượng
                                            Query query1 = database.getReference("Product").orderByChild("idKey").equalTo(cartList.get(i).getIdProduct());
                                            int finalI = i;
                                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        int quantity = Integer.parseInt(dataSnapshot.child("quantity").getValue().toString());
                                                        int quantityCart = cartList.get(finalI).getQuantity();
                                                        int quantityUpdate = quantity - quantityCart;
                                                        database.getReference("Product").child(cartList.get(finalI).getIdProduct()).child("quantity").setValue(quantityUpdate);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                        WaitDialog.dismiss();
                                        TipDialog.show(getActivity(), "Đặt hàng thành công", TipDialog.TYPE.SUCCESS);
                                        return false;
                                    })
                                    .setCancelButton("Không", (baseDialog, v12) -> {
                                        return false;
                                    })
                                    .show();
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}