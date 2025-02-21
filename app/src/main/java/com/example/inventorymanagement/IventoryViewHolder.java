package com.example.inventorymanagement;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

class InventoryViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewItemName;
    public TextView textViewItemQuantity;
    public Button buttonDeleteItem;

    public InventoryViewHolder(View itemView) {
        super(itemView);
        textViewItemName = itemView.findViewById(R.id.textViewItemName);
        textViewItemQuantity = itemView.findViewById(R.id.textViewItemQuantity);
        buttonDeleteItem = itemView.findViewById(R.id.buttonDeleteItem);
    }
}