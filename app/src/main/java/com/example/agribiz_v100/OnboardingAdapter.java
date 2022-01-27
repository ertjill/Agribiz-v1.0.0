package com.example.agribiz_v100;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final List<OnBoardSlide> onboardingItems;

    public OnboardingAdapter(List<OnBoardSlide> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.fragment_on_board_slides, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {

        private final TextView onboardingTitle;
        private final TextView textDescription;
        private final ImageView imageOnboarding;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            onboardingTitle = itemView.findViewById(R.id.onboardingTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            imageOnboarding = itemView.findViewById(R.id.imageOnboarding);
        }

        void setOnboardingData(OnBoardSlide onboardingItem) {
            onboardingTitle.setText(onboardingItem.getTitle());
            textDescription.setText(onboardingItem.getDescription());
            imageOnboarding.setImageResource(onboardingItem.getImage());
        }
    }
}
