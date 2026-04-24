package com.boymask.testpay;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface OnProductClick {
        void onClick(Product product);
    }

    private List<Product> list;
    private OnProductClick listener;

    public ProductAdapter(List<Product> list, OnProductClick listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(android.R.id.text1);
            price = v.findViewById(android.R.id.text2);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product p = list.get(position);

        holder.name.setText(p.name);
        holder.price.setText(p.price / 100.0 + " €");

        holder.itemView.setOnClickListener(v -> listener.onClick(p));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}