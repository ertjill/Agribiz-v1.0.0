package com.example.agribiz_v100;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.agribiz_v100.customer.CustomerMainActivity;
import com.example.agribiz_v100.farmer.FarmerMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class OnBoard extends AppCompatActivity {

    //On Boarding

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private Button getStartedBtn;

    //redirect screen to customer main screen once already logged in
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if(user.getDisplayName()!=null){
                char c = user.getDisplayName().charAt(user.getDisplayName().length()-1);
                if(c=='c')
                    startActivity(new Intent(getBaseContext(), CustomerMainActivity.class));
                else
                    startActivity(new Intent(getBaseContext(), FarmerMainActivity.class));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.army_green));
        }

        // btnSignup = findViewById(R.id.btnSignup);
        getStartedBtn = findViewById(R.id.getStartedBtn);

        //SLide Indicator
        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);

        //create the slides
        setOnboardingItems();

        //Viewpager that hold the slides
        ViewPager2 onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);


        setupOnboardingIndicators();

        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // An if-else statement that makes Get Started button appear on the last slide
                if (position == 4) {
                    getStartedBtn.setVisibility(View.VISIBLE);
                } else {
                    getStartedBtn.setVisibility(View.INVISIBLE);
                }

                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

    }

    private void setOnboardingItems() {
        List<OnBoardSlide> onboardingItems = new ArrayList<>();

        OnBoardSlide item_one = new OnBoardSlide();
        item_one.setImage(R.drawable.agribiz_logo_white);
        item_one.setTitle("The mobile market at the \npalm of your hand");
        item_one.setDescription("Join Today");

        OnBoardSlide item_two = new OnBoardSlide();
        item_two.setImage(R.drawable.one);
        item_two.setTitle("Shop at your own pace");
        item_two.setDescription("Cart products from various shops");

        OnBoardSlide item_three = new OnBoardSlide();
        item_three.setImage(R.drawable.two);
        item_three.setTitle("Buy fresh products from\nthe farm");
        item_three.setDescription("Fruits, vegetables, poultry, livestock, fertilizers and agri-supplies");

        OnBoardSlide item_four = new OnBoardSlide();
        item_four.setImage(R.drawable.three);
        item_four.setTitle("Support or fund our local\nfarmers");
        item_four.setDescription("Help farmers in what they need in cultivation");

        OnBoardSlide item_five = new OnBoardSlide();
        item_five.setImage(R.drawable.four);
        item_five.setTitle("Help reduce food waste");
        item_five.setDescription("Barter any items in exchange for excess harvest");

        onboardingItems.add(item_one);
        onboardingItems.add(item_two);
        onboardingItems.add(item_three);
        onboardingItems.add(item_four);
        onboardingItems.add(item_five);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);

    }

    private void setupOnboardingIndicators() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0,8, 0);

        for (int i=0; i< indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    public void setCurrentOnboardingIndicator(int index) {
        int count = layoutOnboardingIndicators.getChildCount();

        for (int i=0; i<count; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),
                                R.drawable.onboarding_indicator_active)
                );
            }
            else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),
                                R.drawable.onboarding_indicator_inactive)
                );
            }
        }
    }

    public void goToLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}