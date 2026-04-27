package com.boymask.myapplication.listastoricobollette;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.myapplication.DettaglioStoricoBollettaActivity;
import com.boymask.myapplication.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class TableStoricoBolletteAdapter extends RecyclerView.Adapter<TableStoricoBolletteAdapter.ViewHolder> {

    private final List<RowStoricoBolletteModel> data;
    private int selectedPosition = -1;

    public TableStoricoBolletteAdapter(List<RowStoricoBolletteModel> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dataLabel;
        View root;
        TextWatcher watcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView;
            dataLabel = itemView.findViewById(R.id.dataLabel);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sorico_bolletta, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RowStoricoBolletteModel model = data.get(position);

        // 📅 Set data formattata
        holder.dataLabel.setText(toDateString(model.getData()));

        // 🎨 Evidenzia selezione
        holder.root.setBackgroundColor(
                position == selectedPosition ?
                        Color.LTGRAY : Color.TRANSPARENT
        );

        // 🖱️ Click riga
        holder.root.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            // aggiorna selezione
            int previous = selectedPosition;
            selectedPosition = pos;

            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);

            RowStoricoBolletteModel item = data.get(pos);

            Context context = v.getContext();

            // 🚀 Apri Activity di dettaglio
            Intent intent = new Intent(context, DettaglioStoricoBollettaActivity.class);
            intent.putExtra("id", item.getId()); // assicurati che esista getId()
            context.startActivity(intent);

        });
    }

    // 📅 Conversione timestamp → stringa leggibile
    private String toDateString(long timestamp) {

        LocalDateTime dateTime = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return dateTime.format(formatter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<RowStoricoBolletteModel> getData() {
        return data;
    }
}