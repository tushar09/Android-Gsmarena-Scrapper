package com.captaindroid.gsmarena.scrapper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.captaindroid.gsmarena.scrapper.R;
import com.captaindroid.gsmarena.scrapper.adapters.BrandDeviceListRvAdapter;
import com.captaindroid.gsmarena.scrapper.databinding.ActivityBrandDeviceListBinding;
import com.captaindroid.gsmarena.scrapper.db.DbClient;

public class BrandDeviceListActivity extends AppCompatActivity {

    private ActivityBrandDeviceListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrandDeviceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvDeviceList.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvDeviceList.setAdapter(new BrandDeviceListRvAdapter(this, DbClient.getInstance(this).getAppDatabase().getDao().getAllPhoneModels(getIntent().getStringExtra("name"))));

    }
}