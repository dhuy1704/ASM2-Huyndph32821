package dev.edu.poly.Screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.animation.Animation;

import dev.edu.poly.Fragment.Admin.ChartFragment;
import dev.edu.poly.Fragment.Home.CartFragment;
import dev.edu.poly.Fragment.Home.ExploreFragment;
import dev.edu.poly.Fragment.Home.HistoryFragment;
import dev.edu.poly.Fragment.ProfileFragment;
import dev.edu.poly.R;
import dev.edu.poly.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getFragment(new ExploreFragment());

        binding.homeBtn.setOnClickListener(v -> {
            getFragment(new ExploreFragment());
            binding.Explorer.setTextColor(getResources().getColor(R.color.green));
            binding.cart.setTextColor(getResources().getColor(R.color.black));
            binding.History.setTextColor(getResources().getColor(R.color.black));
            binding.profile.setTextColor(getResources().getColor(R.color.black));
        });

        binding.cartBtn.setOnClickListener(v -> {
            getFragment(new CartFragment());
            binding.Explorer.setTextColor(getResources().getColor(R.color.black));
            binding.cart.setTextColor(getResources().getColor(R.color.green));
            binding.History.setTextColor(getResources().getColor(R.color.black));
            binding.profile.setTextColor(getResources().getColor(R.color.black));
        });

        binding.historyBtn.setOnClickListener(v -> {
            getFragment(new HistoryFragment());
            binding.Explorer.setTextColor(getResources().getColor(R.color.black));
            binding.cart.setTextColor(getResources().getColor(R.color.black));
            binding.History.setTextColor(getResources().getColor(R.color.green));
            binding.profile.setTextColor(getResources().getColor(R.color.black));
        });
        binding.profileBtn.setOnClickListener(v -> {
            getFragment(new ProfileFragment());
            binding.Explorer.setTextColor(getResources().getColor(R.color.black));
            binding.cart.setTextColor(getResources().getColor(R.color.black));
            binding.History.setTextColor(getResources().getColor(R.color.black));
            binding.profile.setTextColor(getResources().getColor(R.color.green));
        });
    }

    private void getFragment(Fragment fragment) {
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in);
        binding.fragmentContainerView.startAnimation(animation);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }
}