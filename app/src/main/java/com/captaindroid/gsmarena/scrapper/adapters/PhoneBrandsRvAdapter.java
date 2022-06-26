package com.captaindroid.gsmarena.scrapper.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.captaindroid.gsmarena.scrapper.activities.BrandDeviceListActivity;
import com.captaindroid.gsmarena.scrapper.databinding.RowPhoneBrandBinding;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;

import java.util.ArrayList;

public class PhoneBrandsRvAdapter extends RecyclerView.Adapter<PhoneBrandsRvAdapter.Holder> {

    private Context context;
    private ArrayList<PhoneBrand> list;

    public PhoneBrandsRvAdapter(Context context, ArrayList<PhoneBrand> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(RowPhoneBrandBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.binding.tvTitle.setText(list.get(position).getName());
        holder.binding.tvItemCount.setText(list.get(position).getTotalItem() + " Devices");
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, BrandDeviceListActivity.class)
                        .putExtra("name", list.get(position).getName())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        RowPhoneBrandBinding binding;

        public Holder(@NonNull RowPhoneBrandBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
