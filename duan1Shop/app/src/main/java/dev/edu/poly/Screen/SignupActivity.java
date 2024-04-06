package dev.edu.poly.Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;

import dev.edu.poly.Model.User;
import dev.edu.poly.R;
import dev.edu.poly.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSignup.setOnClickListener(v -> {
            WaitDialog.show(this,"Loading...");
            String name = binding.editTextTextPersonName.getText().toString();
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            String confirmPassword = binding.editTextRePassword.getText().toString();
            if(name.isEmpty()){
                binding.editTextTextPersonName.setError("Please enter your name");
                return;
            }
            if(email.isEmpty()){
                binding.editTextEmail.setError("Please enter your email");
                return;
            }
            //check email format is valid
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.editTextEmail.setError("Please enter a valid email address");
                return;
            }
            if(password.isEmpty()){
                binding.editTextPassword.setError("Please enter your password");
                return;
            }
            if(confirmPassword.isEmpty()){
                binding.editTextRePassword.setError("Please enter your confirm password");
                return;
            }
            if(!password.equals(confirmPassword)){
                binding.editTextRePassword.setError("Confirm password is not match");
                return;
            }
            User user = new User(reference.push().getKey(),name,email,password);
            Query query = reference.orderByChild("email").equalTo(email);
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    if(task.getResult().getChildrenCount() > 0){
                        binding.editTextEmail.setError("Email is already exist");
                        WaitDialog.dismiss();
                        return;
                    }else{
                        WaitDialog.dismiss();
                        TipDialog.show(
                                this,
                                "Success",
                                TipDialog.TYPE.SUCCESS
                        );
                        reference.child(user.getIdKey()).setValue(user);
                        finish();
                    }
                }else{
                    WaitDialog.dismiss();
                    TipDialog.show(
                            this,
                            "Success",
                            TipDialog.TYPE.SUCCESS
                    );
                    reference.child(user.getIdKey()).setValue(user);
                    finish();
                }
            });
        });
    }

    public void login(View view) {
        finish();
    }
}