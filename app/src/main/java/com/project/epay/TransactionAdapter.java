package com.project.epay;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final Context context;
    private final List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        // Display title, date, and amount
        holder.tvTitle.setText(getTitle(transaction));
        holder.tvDate.setText(transaction.getDate());       // only date
        holder.tvAmount.setText("₹" + transaction.getAmount());

        // Set amount color
        if (transaction.getType() == Transaction.Type.RECEIVED) {
            holder.tvAmount.setTextColor(Color.parseColor("#388E3C")); // green
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#D32F2F")); // red
        }

        // Details section
        holder.tvDetailId.setText("Transaction ID: " + transaction.getId());
        holder.tvDetailType.setText("Type: " + transaction.getType());
        holder.tvDetailFromTo.setText("Counterpart: " + transaction.getCounterpart());
        holder.tvDetailAmount.setText("Amount: ₹" + transaction.getAmount());
        holder.tvDetailDateTime.setText("Date & Time: " + transaction.getDateTime());
        holder.tvDetailStatus.setText("Status: " + transaction.getStatus());
        holder.tvDetailMethod.setText("Method: " + transaction.getMethod());

        // Expand/collapse logic
        holder.expandArea.setVisibility(transaction.isExpanded() ? View.VISIBLE : View.GONE);
        holder.ivChevron.setRotation(transaction.isExpanded() ? 180 : 0);

        holder.ivChevron.setOnClickListener(v -> {
            transaction.setExpanded(!transaction.isExpanded());
            notifyItemChanged(position);
        });
    }

    private String getTitle(Transaction transaction) {
        switch (transaction.getType()) {
            case RECHARGE:
                return "Recharge to " + transaction.getCounterpart();
            case SENDMONEY:
            case PAID:
                return "Sent to " + transaction.getCounterpart();
            case RECEIVED:
                return "Received from " + transaction.getCounterpart();
            default:
                return "Transaction";
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvAmount;
        TextView tvDetailId, tvDetailType, tvDetailFromTo, tvDetailAmount, tvDetailDateTime, tvDetailStatus, tvDetailMethod;
        LinearLayout expandArea;
        ImageView ivChevron;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);

            tvDetailId = itemView.findViewById(R.id.tvDetailId);
            tvDetailType = itemView.findViewById(R.id.tvDetailType);
            tvDetailFromTo = itemView.findViewById(R.id.tvDetailFromTo);
            tvDetailAmount = itemView.findViewById(R.id.tvDetailAmount);
            tvDetailDateTime = itemView.findViewById(R.id.tvDetailDateTime);
            tvDetailStatus = itemView.findViewById(R.id.tvDetailStatus);
            tvDetailMethod = itemView.findViewById(R.id.tvDetailMethod);

            expandArea = itemView.findViewById(R.id.expandArea);
            ivChevron = itemView.findViewById(R.id.ivChevron);
        }
    }
}
