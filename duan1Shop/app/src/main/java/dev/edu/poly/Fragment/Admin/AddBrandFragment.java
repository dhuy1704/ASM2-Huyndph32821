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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

import dev.edu.poly.Fragment.Home.ExploreFragment;
import dev.edu.poly.Model.Brand;
import dev.edu.poly.R;
import dev.edu.poly.databinding.FragmentAddBrandBinding;

public class AddBrandFragment extends Fragment {

    FragmentAddBrandBinding binding;
    private boolean check = false;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Brand");
    private String status = "add";
    private Brand brand = new Brand();
    public AddBrandFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBrandBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null) {
            status = getArguments().getString("status");
            brand = (Brand) getArguments().getSerializable("brand");
            if (status.equals("edit")) {
                binding.deleteBtn.setVisibility(View.VISIBLE);
                binding.addBrandBtn.setText("Update");
                binding.brandName.setText(brand.getName());
                Picasso.get().load(brand.getImage()).into(binding.brandImage);
            }
        }

        binding.cancelBtn.setOnClickListener(v -> {
            getFragment(new ExploreFragment());
        });

        binding.deleteBtn.setOnClickListener(v -> {
            MessageDialog.build()
                    .setTheme(DialogX.THEME.DARK)
                    .setTitle("Xóa thương hiệu")
                    .setMessage("Bạn có chắc chắn muốn xóa thương hiệu này không?")
                    .setOkButton("Xóa")
                    .setOkButtonClickListener((dialog, v1) ->{
                        for (int i = 2; i <= 10; i++) {
                            WaitDialog.show(getActivity(),"Đang xóa...",i/10f);
                        }
                        myRef.child(brand.getId()).removeValue().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                WaitDialog.dismiss();
                                TipDialog.show(getActivity(),"Xóa thành công", TipDialog.TYPE.SUCCESS);
                                getFragment(new ExploreFragment());
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show(getActivity(),"Xóa thất bại", TipDialog.TYPE.ERROR);
                            }
                        });
                        return false;
                    })
                    .show();
        });

        binding.brandImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });


        binding.addBrandBtn.setOnClickListener(v -> {
            String nameBrand = binding.brandName.getText().toString();
            if(nameBrand.isEmpty()){
                TipDialog.show(getActivity(),"Vui lòng nhập tên thương hiệu", TipDialog.TYPE.ERROR);
            }else if(!check && status.equals("add")){
                TipDialog.show(getActivity(),"Vui lòng chọn ảnh thương hiệu", TipDialog.TYPE.ERROR);
            }else{
                Query query = myRef.orderByChild("name").equalTo(nameBrand);
                query.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(task.getResult().getChildrenCount() > 0){
                            TipDialog.show(getActivity(),"Tên thương hiệu đã tồn tại", TipDialog.TYPE.ERROR);
                        }else{
                            uploadImage(nameBrand);
                        }
                    }else{
                        uploadImage(nameBrand);
                    }
                });
            }
        });
    }


    public void uploadImage(String nameBrand){
        WaitDialog.show(getActivity(),"Đang tải...",0.1f);
        String filepath = "images/"+System.currentTimeMillis();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepath);
        storageReference.putBytes(getByteArrayFromImageView(binding.brandImage))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        for (int i = 2; i <= 10; i++) {
                            WaitDialog.show(getActivity(),"Đang tải...",i/10f);
                        }
                        Log.d("Upload","onSuccess");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        if(!status.equals("edit")){
                            brand.setId(myRef.push().getKey());
                        }
                        brand.setName(nameBrand);
                        brand.setImage(uriTask.getResult().toString());
                        myRef.child(brand.getId()).setValue(brand).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                WaitDialog.dismiss();
                                if(!status.equals("edit")) {
                                    binding.brandImage.setImageResource(R.drawable.icons8_add_image_96);
                                    binding.brandName.setText("");
                                    check = false;
                                    TipDialog.show(getActivity(),"Thêm thành công", TipDialog.TYPE.SUCCESS);

                                }else{
                                    TipDialog.show(getActivity(),"Sửa thành công", TipDialog.TYPE.SUCCESS);

                                }
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show(getActivity(),"Thêm thất bại", TipDialog.TYPE.ERROR);
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

    public byte[] getByteArrayFromImageView(ImageView imageView){
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
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
                            binding.brandImage.setImageBitmap(bitmap);
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