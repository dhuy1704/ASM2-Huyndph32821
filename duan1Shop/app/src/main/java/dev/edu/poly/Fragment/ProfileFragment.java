package dev.edu.poly.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import dev.edu.poly.Fragment.Admin.ChartFragment;
import dev.edu.poly.Fragment.Admin.DonhangFragment;
import dev.edu.poly.R;
import dev.edu.poly.Screen.IntroActivity;
import dev.edu.poly.databinding.FragmentProfileBinding;


public class  ProfileFragment extends Fragment {


    FragmentProfileBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataLogin",getContext().MODE_PRIVATE);
        String name = sharedPreferences.getString("name","");
        String email = sharedPreferences.getString("email","");
        String key = sharedPreferences.getString("key","");
        String role = sharedPreferences.getString("role","");
        if(role.equals("admin")){
            binding.btnChangeName.setVisibility(View.GONE);
            binding.linearAdmin.setVisibility(View.VISIBLE);

            binding.btnAdminDoanhThu.setOnClickListener(v -> {
                getFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new ChartFragment()).commit();
            });

            binding.btnAdminDonHang.setOnClickListener(v -> {
                getFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new DonhangFragment()).commit();
            });
        }
        binding.txtName.setText(name);
        binding.txtEmail.setText(email);

        binding.btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(requireContext(), IntroActivity.class));
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            if(binding.linearChangePassword.getVisibility() == View.VISIBLE){
                binding.linearChangePassword.setVisibility(View.GONE);
                return;
            }
            binding.linearChangePassword.setVisibility(View.VISIBLE);
            binding.btnConfirm.setOnClickListener(v1 -> {
                String password = binding.editCurrentPassword.getText().toString();
                String newPassword = binding.editNewPassword.getText().toString();
                String confirmPassword = binding.editConfirmPassword.getText().toString();
                if(password.isEmpty()){
                    binding.editCurrentPassword.setError("Please enter your password");
                    return;
                }
                if(newPassword.isEmpty()){
                    binding.editNewPassword.setError("Please enter your new password");
                    return;
                }
                if(confirmPassword.isEmpty()){
                    binding.editConfirmPassword.setError("Please enter your confirm password");
                    return;
                }
                if(!newPassword.equals(confirmPassword)){
                    binding.editConfirmPassword.setError("Confirm password not match");
                    return;
                }

                Query query = myRef.child("users").orderByChild("password").equalTo(password);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            dataSnapshot.getChildren().forEach(dataSnapshot1 -> {
                                myRef.child("users").child(key).child("password").setValue(newPassword);
                                binding.linearChangePassword.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), "Change password success", Toast.LENGTH_SHORT).show();
                            });
                        }else{
                            binding.editCurrentPassword.setError("Password not match");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            });
        });

        binding.btnChangeName.setOnClickListener(v -> {
            if(binding.linearChangeName.getVisibility() == View.VISIBLE){
                binding.linearChangeName.setVisibility(View.GONE);
                return;
            }
            binding.linearChangeName.setVisibility(View.VISIBLE);
            binding.btnConfirmName.setOnClickListener(v1 -> {
                String name1 = binding.editName.getText().toString();
                if(name1.isEmpty()){
                    binding.editName.setError("Please enter your name");
                    return;
                }
                myRef.child("users").child(key).child("name").setValue(name1);
                binding.linearChangeName.setVisibility(View.GONE);
                binding.txtName.setText(name1);
                Toast.makeText(requireContext(), "Change name success", Toast.LENGTH_SHORT).show();
            });
        });
    }
}