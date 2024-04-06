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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.squareup.picasso.Picasso;

import dev.edu.poly.Fragment.Home.ExploreFragment;
import dev.edu.poly.Model.Cart;
import dev.edu.poly.Model.Product;
import dev.edu.poly.R;
import dev.edu.poly.databinding.FragmentDetailBinding;


public class DetailFragment extends Fragment {

    FragmentDetailBinding binding;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Cart");
    private Product product = new Product();
    private String idUser = "";
    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
            binding.titleTxt.setText(product.getName());
            binding.priceTxt.setText(product.getPrice());
            binding.descriptionTxt.setText(product.getDescription());
            Picasso.get().load(product.getImage()).into(binding.itemPic);
            binding.quantityTxt.setText(product.getQuantity()+"");
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataLogin", getActivity().MODE_PRIVATE);
        idUser = sharedPreferences.getString("key", "");
        binding.backBtn.setOnClickListener(v -> {
            getFragment(new ExploreFragment());
        });

        binding.addToCartBtn.setOnClickListener(v -> {
            //check số lượng
            if(product.getQuantity() == 0){
                TipDialog.show(getActivity(),"Sản phẩm đã hết hàng", TipDialog.TYPE.ERROR);
                return;
            }
            MessageDialog.build()
                    .setTheme(DialogX.THEME.DARK)
                    .setTitle("Thông báo")
                    .setMessage("Bạn có muốn thêm sản phẩm này vào giỏ hàng không ?")
                    .setOkButton("Có", (baseDialog, v1) -> {
                        Query query = myRef.orderByChild("idKey").equalTo(product.getIdKey());
                        query.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                                WaitDialog.show(getActivity(),"Đang thêm vào giỏ hàng...");
                                if(snapshot.exists()){
                                    for (com.google.firebase.database.DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        Cart cart = dataSnapshot.getValue(Cart.class);
                                        int quantity = Integer.parseInt(String.valueOf(cart.getQuantity()));
                                        quantity++;
                                        cart.setQuantity(Integer.parseInt(String.valueOf(quantity)));
                                        myRef.child(cart.getIdKey()).setValue(cart);
                                        TipDialog.show(getActivity(),"Thêm vào giỏ hàng thành công", TipDialog.TYPE.SUCCESS);
                                    }
                                }else{
                                    Cart cart = new Cart();
                                    cart.setIdKey(product.getIdKey());
                                    cart.setIdProduct(product.getIdKey());
                                    cart.setQuantity(1);
                                    cart.setIdUser(idUser);
                                    myRef.child(product.getIdKey()).setValue(cart).addOnSuccessListener(aVoid -> {
                                        TipDialog.show(getActivity(),"Thêm vào giỏ hàng thành công", TipDialog.TYPE.SUCCESS);
                                    }).addOnFailureListener(e -> {
                                        WaitDialog.dismiss();
                                        TipDialog.show(getActivity(),"Thêm vào giỏ hàng thất bại", TipDialog.TYPE.ERROR);
                                    });
                                }
                                WaitDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        return false;
                    })
                    .setCancelButton("Không", (baseDialog, v1) -> {
                        return false;
                    })
                    .show();
        });
    }
    private void getFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }
}