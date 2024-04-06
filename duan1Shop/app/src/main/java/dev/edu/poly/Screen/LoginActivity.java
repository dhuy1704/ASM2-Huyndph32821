package dev.edu.poly.Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;

import dev.edu.poly.R;
import dev.edu.poly.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("users");
    private SharedPreferences sharedPreferences;
    private boolean check = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);
        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.edtemail.getText().toString();
            String password = binding.edtpassword.getText().toString();
            if(email.isEmpty()){
                binding.edtemail.setError("Please enter your email");
                return;
            }
            if(password.isEmpty()){
                binding.edtpassword.setError("Please enter your password");
                return;
            }
            WaitDialog.show(this,"Loading...");
//            reference.get().addOnCompleteListener(task -> {
//                if(task.isSuccessful()){
//                    if(task.getResult().getChildrenCount() > 0){
//                        task.getResult().getChildren().forEach(dataSnapshot -> {
//                            if(dataSnapshot.child("email").getValue().toString().equals(email) && dataSnapshot.child("password").getValue().toString().equals(password)){
//                                WaitDialog.dismiss();
//                                sharedPreferences.edit().putString("email",email).apply();
//                                sharedPreferences.edit().putString("name",dataSnapshot.child("name").getValue().toString()).apply();
//                                sharedPreferences.edit().putString("role",dataSnapshot.child("role").getValue().toString()).apply();
//                                sharedPreferences.edit().putString("key",dataSnapshot.getKey()).apply();
//                                TipDialog.show(this,"Login success", TipDialog.TYPE.SUCCESS);
//                                new android.os.Handler().postDelayed(
//                                        () -> {
//                                            startActivity(new Intent(this,HomeActivity.class));
//                                        }, 1000);
//                            }else{
//                                WaitDialog.dismiss();
//                                TipDialog.show(this,"Login fail", TipDialog.TYPE.ERROR);
//                            }
//                        });
//                    }else{
//                        WaitDialog.dismiss();
//                        TipDialog.show(this,"Login fail", TipDialog.TYPE.ERROR);
//                    }
//                }
//            });
            Query query = reference.orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        dataSnapshot.getChildren().forEach(dataSnapshot1 -> {
                            if(dataSnapshot1.child("password").getValue().toString().equals(password)){
                                WaitDialog.dismiss();
                                sharedPreferences.edit().putString("email",email).apply();
                                sharedPreferences.edit().putString("name",dataSnapshot1.child("name").getValue().toString()).apply();
                                sharedPreferences.edit().putString("role",dataSnapshot1.child("role").getValue().toString()).apply();
                                sharedPreferences.edit().putString("key",dataSnapshot1.getKey()).apply();
                                sharedPreferences.edit().putBoolean("isLogin",true).apply();
                                TipDialog.show(LoginActivity.this,"Login success", TipDialog.TYPE.SUCCESS);
                                new android.os.Handler().postDelayed(
                                        () -> {
                                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                        }, 1000);
                                check = true;
                            }
                        });
                        if(!check){
                            WaitDialog.dismiss();
                            TipDialog.show(LoginActivity.this,"Password incorrect", TipDialog.TYPE.ERROR);
                        }
                    }else{
                        WaitDialog.dismiss();
                        TipDialog.show(LoginActivity.this,"Email not exist", TipDialog.TYPE.ERROR);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    WaitDialog.dismiss();
                    TipDialog.show(LoginActivity.this,"Login fail", TipDialog.TYPE.ERROR);
                }
            });
        });
    }

    public void signup(View view) {
        startActivity(new Intent(this,SignupActivity.class));
    }
}