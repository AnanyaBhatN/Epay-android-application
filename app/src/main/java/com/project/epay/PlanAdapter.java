package com.project.epay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private Context context;
    private List<Plan> planList;
    private int selectedPosition = -1;

    public PlanAdapter(Context context, List<Plan> planList) {
        this.context = context;
        this.planList = planList;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planList.get(position);
        holder.price.setText(plan.getPrice());
        holder.benefits.setText(plan.getBenefits());
        holder.validity.setText(plan.getValidity());

        // Highlight selected card
        holder.cardView.setStrokeColor(position == selectedPosition ?
                context.getResources().getColor(R.color.primary_blue) :
                context.getResources().getColor(android.R.color.darker_gray));

        holder.cardView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public Plan getSelectedPlan() {
        if (selectedPosition != -1) {
            return planList.get(selectedPosition);
        }
        return null;
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView price, benefits, validity;
        MaterialCardView cardView;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView; // item_plan root is MaterialCardView
            price = itemView.findViewById(R.id.planPrice);
            benefits = itemView.findViewById(R.id.planBenefits);
            validity = itemView.findViewById(R.id.planValidity);
        }
    }
}
