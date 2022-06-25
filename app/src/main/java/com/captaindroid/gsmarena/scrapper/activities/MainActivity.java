package com.captaindroid.gsmarena.scrapper.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.captaindroid.gsmarena.scrapper.adapters.PhoneBrandsRvAdapter;
import com.captaindroid.gsmarena.scrapper.databinding.ActivityMainBinding;
import com.captaindroid.gsmarena.scrapper.db.DbClient;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;
import com.captaindroid.gsmarena.scrapper.services.BackgroundService;
import com.captaindroid.gsmarena.scrapper.utils.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ArrayList<PhoneBrand> phoneBrands = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(new PhoneBrandsRvAdapter(this, phoneBrands));

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect("https://www.gsmarena.com/makers.php3")
                            .headers(Constants.getHeaders())
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36")
                            .get();
                    Elements table = doc.select("tbody tr td");
                    phoneBrands.clear();
                    updateListview();
                    for (int i = 0; i < table.size(); i++) {
                        Element link = table.get(i).select("> a").first();
                        Element count = link.select("span").first();
                        PhoneBrand pb = new PhoneBrand();
                        pb.setName(link.text().replaceAll(count.text(), "").trim());
                        pb.setTotalItem(Integer.parseInt(count.text().replaceAll("devices", "").trim()));
                        pb.setLink(link.attr("href"));
                        pb.setCreatedAt(System.currentTimeMillis());
                        pb.setUpdatedAt(System.currentTimeMillis());
                        phoneBrands.add(pb);
                    }
                    updateListview();
                    DbClient.getInstance(MainActivity.this).getAppDatabase().getDao().insertBook(phoneBrands);
                    BackgroundService.startWork(MainActivity.this);

                } catch (IOException e) {
                    Log.e("exp", e.toString());
                }
            }
        }).start();


    }

    private void updateListview(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.rvList.getAdapter().notifyDataSetChanged();
            }
        });
    }
}