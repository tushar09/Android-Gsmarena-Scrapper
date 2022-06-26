package com.captaindroid.gsmarena.scrapper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.captaindroid.gsmarena.scrapper.databinding.RowBrandDeviceBinding;
import com.captaindroid.gsmarena.scrapper.databinding.RowPhoneBrandBinding;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneModel;

import java.util.ArrayList;
import java.util.List;

public class BrandDeviceListRvAdapter extends RecyclerView.Adapter<BrandDeviceListRvAdapter.Holder> {

    private Context context;
    private List<PhoneModel> list;

    public BrandDeviceListRvAdapter(Context context, List<PhoneModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(RowBrandDeviceBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.binding.tvTitle.setText(list.get(position).getPhoneModelName());
        Glide.with(context)
                .load(list.get(position).getImageLink())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.ivPhone);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        RowBrandDeviceBinding binding;

        public Holder(@NonNull RowBrandDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
