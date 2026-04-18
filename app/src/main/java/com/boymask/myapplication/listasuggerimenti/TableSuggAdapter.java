package com.boymask.myapplication.listasuggerimenti;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.myapplication.R;

import java.util.List;

public class TableSuggAdapter extends RecyclerView.Adapter<TableSuggAdapter.ViewHolder> {

    private List<String> data;

    public TableSuggAdapter(List<String> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textItem = itemView.findViewById(R.id.textItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textItem.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}