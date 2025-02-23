package com.example.inventorymanagement;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * InventoryViewHolder is a custom ViewHolder class used to represent and manage
 * the views for a single inventory item within a RecyclerView. It holds references
 * to the TextViews displaying the item's name and quantity, and a Button for
 * deleting the item.
 * @noinspection CanBeFinal
 */
class InventoryViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewItemName;
    public TextView textViewItemQuantity;


    public InventoryViewHolder(View itemView) {
        super(itemView);
        textViewItemName = itemView.findViewById(R.id.textViewItemName);
        textViewItemQuantity = itemView.findViewById(R.id.textViewItemQuantity);
    }
}