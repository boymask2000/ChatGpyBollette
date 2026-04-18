package com.boymask.myapplication.listaparametri;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.myapplication.R;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private final List<RowModel> data;

    public TableAdapter(List<RowModel> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView label;
        EditText value;

        TextWatcher watcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.textLabel);
            value = itemView.findViewById(R.id.editValue);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_editable, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RowModel model = data.get(position);

        // 🔥 colonna fissa
        holder.label.setText(model.getLabel());

        // 🔥 rimuove watcher precedente
        if (holder.watcher != null) {
            holder.value.removeTextChangedListener(holder.watcher);
        }

        // 🔥 set valore senza loop
        holder.value.setText(model.getValue());

        // 🔥 watcher solo sulla seconda colonna
        holder.watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                data.get(pos).setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        holder.value.addTextChangedListener(holder.watcher);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<RowModel> getData() {
        return data;
    }
}