package com.example.bikercontrol.data.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikercontrol.R;
import com.example.bikercontrol.data.model.OilModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OilAdapter extends RecyclerView.Adapter<OilAdapter.ViewHolder> {

    private final List<OilModel> oilList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OilModel oil);
    }

    public OilAdapter(List<OilModel> oilList) {
        this.oilList = oilList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_oilchange, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OilModel oil = oilList.get(position);

        // Formato de fecha para mostrar
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Setear valores
        holder.oilChange.setText(dateFormat.format(oil.getOilChange()));
        holder.kilometer.setText(String.format(Locale.getDefault(), "%.2f km", oil.getKilometer()));
        holder.nextChange.setText(dateFormat.format(oil.getNextOilChange()));

        // Manejo del clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(oil);
            }
        });
    }

    @Override
    public int getItemCount() {
        return oilList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView oilChange;
        public TextView kilometer;
        public TextView nextChange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            oilChange = itemView.findViewById(R.id.tvListOilChange);
            kilometer = itemView.findViewById(R.id.tvListKilometer);
            nextChange = itemView.findViewById(R.id.tvNextChange);
        }
    }
}