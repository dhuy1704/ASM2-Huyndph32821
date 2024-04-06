package dev.edu.poly.Fragment.Admin;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dev.edu.poly.Fragment.Home.ExploreFragment;
import dev.edu.poly.Fragment.ListProductFragment;
import dev.edu.poly.Model.Brand;
import dev.edu.poly.Model.Product;
import dev.edu.poly.R;
import dev.edu.poly.databinding.FragmentAddProductBinding;

public class AddProductFragment extends Fragment {


    FragmentAddProductBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Product");
    private DatabaseReference myRef2 = database.getReference("Brand");
    private List<String> listBrand = new ArrayList<String>();
    private List<Brand> listBrand2 = new ArrayList<Brand>();
    private boolean check = false;
    private String status = "add";
    private Product product = new Product();

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        binding.btnDelete.setOnClickListener(v -> {
            MessageDialog.build()
                    .setTheme(DialogX.THEME.DARK)
                    .setTitle("Xóa thương hiệu")
                    .setMessage("Bạn có chắc chắn muốn xóa thương hiệu này không?")
                    .setOkButton("Xóa")
                    .setOkButtonClickListener((dialog, v1) -> {
                        if (status.equals("edit")) {
                            myRef.child(product.getIdKey()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    TipDialog.show(getActivity(), "Xóa thành công", TipDialog.TYPE.SUCCESS);
                                    getFragment(new ListProductFragment());
                                } else {
                                    TipDialog.show(getActivity(), "Xóa thất bại", TipDialog.TYPE.ERROR);
                                }
                            });
                        } else {
                            getFragment(new ListProductFragment());
                        }
                        return false;
                    })
                    .setCancelButton("Hủy")
                    .setCancelButtonClickListener((dialog, v12) -> {
                        return false;
                    })
                    .show();
        });

        binding.btnCancel.setOnClickListener(v -> {
            getFragment(new ExploreFragment());
        });

        myRef2.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listBrand.clear();
                    listBrand2.clear();
                    for (com.google.firebase.database.DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Brand brand = snapshot1.getValue(Brand.class);
                        listBrand2.add(brand);
                    }
                    for (int i = 0; i < listBrand2.size(); i++) {
                        listBrand.add(listBrand2.get(i).getName());
                    }
                    List<String> listBrand3 = new LinkedList<>(listBrand);
                    listBrand3.add(0, "Chọn thương hiệu");
                    binding.spinner.attachDataSource(listBrand3);
                    binding.spinner.setSelectedIndex(0);
                    if (getArguments() != null) {
                        status = getArguments().getString("status");
                        product = (Product) getArguments().getSerializable("product");
                        if (status.equals("edit")) {
                            binding.btnAdd.setText("Sửa");
                            binding.btnDelete.setVisibility(View.VISIBLE);
                            binding.edtName.setText(product.getName());
                            binding.edtPrice.setText(product.getPrice() + "");
                            binding.edtDescription.setText(product.getDescription());
                            binding.edtQuantity.setText(product.getQuantity() + "");
                            Log.d("TAGAPI", "onCreateView: " + product.getQuantity());
                            Picasso.get().load(product.getImage()).into(binding.btnAddImage);
                            for (int i = 0; i < listBrand2.size(); i++) {
                                if (listBrand2.get(i).getId().equals(product.getBrand())) {
                                    binding.spinner.setSelectedIndex(i + 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.btnAdd.setOnClickListener(v -> {
            if (binding.spinner.getSelectedIndex() == 0) {
                Toast.makeText(getContext(), "Vui lòng chọn thương hiệu", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = binding.edtName.getText().toString();
            String price = binding.edtPrice.getText().toString();
            String description = binding.edtDescription.getText().toString();
            String quantity = binding.edtQuantity.getText().toString();
            String brand = "";

            for (int i = 0; i < listBrand2.size(); i++) {
                if (listBrand2.get(i).getName().equals(binding.spinner.getSelectedItem().toString())) {
                    brand = listBrand2.get(i).getId();
                    break;
                }
            }
            if (!check && status.equals("add")) {
                Toast.makeText(getActivity(), "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.isEmpty()) {
                binding.edtName.setError("Không được để trống");
            } else if (price.isEmpty()) {
                binding.edtPrice.setError("Không được để trống");
            } else if (description.isEmpty()) {
                binding.edtDescription.setError("Không được để trống");
            } else {
                uploadImage(name, price, description, brand, quantity);
            }
        });
    }


    public void uploadImage(String nameBrand, String price, String description, String brand, String quantity) {
        WaitDialog.show(getActivity(), "Đang tải...", 0.1f);
        String filepath = "images/" + System.currentTimeMillis();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepath);
        storageReference.putBytes(getByteArrayFromImageView(binding.btnAddImage))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        for (int i = 2; i <= 10; i++) {
                            WaitDialog.show(getActivity(), "Đang tải...", i / 10f);
                        }
                        Log.d("Upload", "onSuccess");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        if (!status.equals("edit")) {
                            product.setIdKey(myRef.push().getKey());
                        }
                        product.setName(nameBrand);
                        product.setImage(uriTask.getResult().toString());
                        product.setPrice(price);
                        product.setDescription(description);
                        product.setBrand(brand);
                        product.setQuantity(Integer.parseInt(quantity));
                        myRef.child(product.getIdKey()).setValue(product).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                WaitDialog.dismiss();
                                if (!status.equals("edit")) {
                                    binding.btnAddImage.setImageResource(R.drawable.icons8_add_image_96);
                                    binding.edtName.setText("");
                                    binding.edtPrice.setText("");
                                    binding.edtDescription.setText("");
                                    binding.spinner.setSelectedIndex(0);
                                    binding.edtQuantity.setText("");
                                    check = false;
                                    TipDialog.show(getActivity(), "Thêm thành công", TipDialog.TYPE.SUCCESS);
                                } else {
                                    TipDialog.show(getActivity(), "Sửa thành công", TipDialog.TYPE.SUCCESS);
                                }
                            } else {
                                WaitDialog.dismiss();
                                Toast.makeText(getActivity(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public byte[] getByteArrayFromImageView(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.btnAddImage.setImageBitmap(bitmap);
                            check = true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void getFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }
}