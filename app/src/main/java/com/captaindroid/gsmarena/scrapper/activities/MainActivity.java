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

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect("https://www.gsmarena.com/samsung_galaxy_s22_ultra_5g-11251.php")
                            .headers(Constants.getHeaders())
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36")
                            .get();

                    Element data = doc.getElementById("specs-list");
                    Elements tables = data.select("table");
                    for (int i = 0; i < tables.size(); i++) {
                        Elements row = tables.get(i).select("tr");
                        for (int j = 0; j < row.size(); j++) {
                            Element header = row.get(j).select("th").first();
                            if(header != null){
                                Log.e("data", row.get(j).select("th").first().text());
                            }

                            Elements tds = row.get(j).select("td");
                            for (int k = 0; k < tds.size(); k++) {
                                Element subHead = tds.get(k).getElementsByClass("ttl").first();
                                if(subHead != null){
                                    Log.e("data", "     " + subHead.text());
                                }
                                Element nfo = tds.get(k).getElementsByTag("nfo").first();
                                //Log.e("data", "         " + tds.get(k).text());
                                Log.e("data", "         " + nfo.html());
                            }

                        }
                    }


                } catch (Exception e) {
                    Log.e("exp", e.toString());
                }
            }
        }).start();

        //BackgroundService.startWork(MainActivity.this);
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