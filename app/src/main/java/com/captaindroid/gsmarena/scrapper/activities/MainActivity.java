package com.captaindroid.gsmarena.scrapper.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.captaindroid.gsmarena.scrapper.adapters.PhoneBrandsRvAdapter;
import com.captaindroid.gsmarena.scrapper.databinding.ActivityMainBinding;
import com.captaindroid.gsmarena.scrapper.db.DbClient;
import com.captaindroid.gsmarena.scrapper.db.dao.MainDao;
import com.captaindroid.gsmarena.scrapper.db.tables.PageAllDevices;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneModel;
import com.captaindroid.gsmarena.scrapper.dto.Head;
import com.captaindroid.gsmarena.scrapper.dto.PhoneConfiguration;
import com.captaindroid.gsmarena.scrapper.dto.SubHeader;
import com.captaindroid.gsmarena.scrapper.dto.StackClass;
import com.captaindroid.gsmarena.scrapper.eventBus.ScrappingStatus;
import com.captaindroid.gsmarena.scrapper.services.BackgroundService;
import com.captaindroid.gsmarena.scrapper.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ir.androidexception.roomdatabasebackupandrestore.Backup;
import ir.androidexception.roomdatabasebackupandrestore.OnWorkFinishListener;
import ir.androidexception.roomdatabasebackupandrestore.Restore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ArrayList<PhoneBrand> phoneBrands = new ArrayList<>();
    private List<PhoneModel> d;

    private MainDao mainDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(new PhoneBrandsRvAdapter(this, phoneBrands));

        mainDao = DbClient.getInstance(MainActivity.this).getAppDatabase().getDao();

//        d = DbClient.getInstance(this).getAppDatabase().getDao().getAllPhoneBrandLinkList();
//        Constants.getApiService().saveAllPhoneBrands(d)
//                .enqueue(new Callback<PhoneBrand>() {
//                    @Override
//                    public void onResponse(Call<PhoneBrand> call, Response<PhoneBrand> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<PhoneBrand> call, Throwable t) {
//
//                    }
//                });

        //DbClient.getInstance(MainActivity.this).getAppDatabase().getDao().updateServerUploadFalse();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                d = mainDao.getAllPhoneModelsWithLimit();
//                //Log.e("details link", d.get(0).getDetailsLink());
//                while (d.size() > 0){
//                    try {
//                        Response<List<PhoneModel>> r = Constants.getApiService().saveAllPhoneDetails(d).execute();
//                        if(r.code() == 200){
//                            for (int i = 0; i < d.size(); i++) {
//                                d.get(i).setUploadToServerDone(true);
//                            }
//                        }
//
//                        if(r.code() == 422){
//                            break;
//                        }
//                        Log.e("r", r.code() + " " + "asdf");
//
//                    }
//                    catch (IOException e) {
//                        Log.e("err", e.toString());
//                        e.printStackTrace();
//                    }
//
//                    mainDao.updatePhoneModels(d);
//                    d = mainDao.getAllPhoneModelsWithLimit();
//                }
//
//            }
//        }).start();



//        InputStream is = null;
//        try {
//            is = getAssets().open("filename.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            String json = new String(buffer, "UTF-8");
//            ArrayList<PhoneModel> pad = new Gson().fromJson(json, new TypeToken<ArrayList<PhoneModel>>(){}.getType());
//            Log.e("pad", pad.size() + "");
//            DbClient.getInstance(this).getAppDatabase().getDao().insertPhoneModels(pad);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




//        List<PhoneModel> pad = DbClient.getInstance(this).getAppDatabase().getDao().getAllPhoneModels();
//
//        File myObj = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/filename.json");
//        try {
//            myObj.createNewFile();
//            FileWriter myWriter = new FileWriter(myObj);
////            myWriter.write(new Gson().toJson(pad));
////            myWriter.close();
//            new Gson().toJson(pad, myWriter);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("err", e.toString());
//        }

//        Log.e("asdf", new Gson().toJson(pad));
//        /*Create an ACTION_SEND Intent*/
//        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//        /*This will be the actual content you wish you share.*/
//        String shareBody = new Gson().toJson(pad);
//        /*The type of the content is text, obviously.*/
//        intent.setType("text/plain");
//        /*Applying information Subject and Body.*/
//        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//        /*Fire!*/
//        startActivity(Intent.createChooser(intent, "sadf"));


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
                    BackgroundService.startWork(MainActivity.this);
                    Log.e("exp", e.toString());
                }
            }
        }).start();


//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Document doc = null;
//                Stack<StackClass> stack = new Stack<StackClass>();
//                try {
//                    doc = Jsoup.connect("https://www.gsmarena.com/samsung_galaxy_s22_ultra_5g-11251.php")
//                            .headers(Constants.getHeaders())
//                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36")
//                            .get();
//
//                    Element data = doc.getElementById("specs-list");
//                    Elements tables = data.select("table");
//
//                    for (int i = 0; i < tables.size(); i++) {
//                        Elements row = tables.get(i).select("tr");
//                        for (int j = 0; j < row.size(); j++) {
//                            Element header = row.get(j).select("th").first();
//                            PhoneConfiguration phoneConfiguration = new PhoneConfiguration();
//                            if(header != null){
//                                Log.e("data", row.get(j).select("th").first().text());
//                                //phoneConfiguration.setHeader(row.get(j).select("th").first().text());
//                                StackClass exp = new StackClass(row.get(j).select("th").first().text(), 1);
//                                stack.push(exp);
//                            }
//
//                            Elements tds = row.get(j).select("td");
//                            for (int k = 0; k < tds.size(); k++) {
//                                Element subHead = tds.get(k).getElementsByClass("ttl").first();
//                                if(subHead != null){
//                                    Log.e("data", "     " + subHead.text());
//                                    //phoneConfiguration.setSubHeader(subHead.text());
//                                    StackClass exp = new StackClass(subHead.text(), 2);
//                                    stack.push(exp);
//                                }
//                                Element a = tds.get(k).select("> a").first();
//                                if(subHead != null && a != null && a.text().equals(subHead.text())){
//                                    //Log.e("data", "         " + tds.get(k).html());
//                                }else{
//                                    StackClass exp = new StackClass(tds.get(k).html(), 3);
//                                    stack.push(exp);
//                                    Log.e("data", "         " + tds.get(k).html());
//                                }
//
//                                //Log.e("data", "         " + nfo.html());
//                            }
//
//                        }
//                    }
//
//                    ArrayList<String> strings = new ArrayList<>();
//                    ArrayList<SubHeader> subHeads = new ArrayList<>();
//                    String head;
//                    PhoneConfiguration phoneConfiguration = new PhoneConfiguration();
//                    ArrayList<Head> heads = new ArrayList<>();
//                    phoneConfiguration.setHeads(heads);
//                    while (stack.size() > 0){
//                        if(stack.peek().getType() == 3){
//                            strings.add(stack.pop().getData());
//                        }else if(stack.peek().getType() == 2){
//                            SubHeader subHead = new SubHeader();
//                            subHead.setName(stack.pop().getData());
//                            subHead.setData(strings);
//
//                            subHeads.add(subHead);
//
//                            strings = new ArrayList<>();
//
//                        }else{
//                            Head h = new Head();
//                            h.setName(stack.pop().getData());
//                            h.setSubHeaders(subHeads);
//
//                            subHeads = new ArrayList<>();
//
//                            phoneConfiguration.getHeads().add(h);
//                        }
//
//                    }
//
//                    int maxLogSize = 1000;
//                    for(int i = 0; i <= new Gson().toJson(phoneConfiguration).length() / maxLogSize; i++) {
//                        int start = i * maxLogSize;
//                        int end = (i+1) * maxLogSize;
//                        end = end > new Gson().toJson(phoneConfiguration).length() ? new Gson().toJson(phoneConfiguration).length() : end;
//                        Log.e("pc", new Gson().toJson(phoneConfiguration).substring(start, end));
//                    }
//
//
//
//                } catch (Exception e) {
//                    Log.e("exp", e.toString());
//                }
//            }
//        }).start();
//
//        BackgroundService.startWork(MainActivity.this);
    }

    private void updateListview(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.rvList.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWallpaperUpdate(ScrappingStatus dto){
        if(dto.isFinished() == false){
            binding.cvDownloadStatus.setVisibility(View.VISIBLE);
            binding.tvStatus.setText(dto.getScrappingName());
            binding.pbDownload.setIndeterminate(dto.isIndetermination());
            binding.pbDownload.setProgressCompat(dto.getProgress(), true);
            Log.e("progress", dto.getProgress() + " asdf");

            if(dto.getPhoneImageUrl() != null){
                binding.cvImgHolder.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(dto.getPhoneImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.ivImageSmall);
            }else{
                binding.cvImgHolder.setVisibility(View.GONE);
            }


        }else {
            binding.cvDownloadStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}