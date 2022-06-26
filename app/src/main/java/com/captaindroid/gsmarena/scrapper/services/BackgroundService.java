package com.captaindroid.gsmarena.scrapper.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.captaindroid.gsmarena.scrapper.R;
import com.captaindroid.gsmarena.scrapper.activities.MainActivity;
import com.captaindroid.gsmarena.scrapper.db.DbClient;
import com.captaindroid.gsmarena.scrapper.db.dao.MainDao;
import com.captaindroid.gsmarena.scrapper.db.tables.PageAllDevices;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneModel;
import com.captaindroid.gsmarena.scrapper.utils.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends JobIntentService {

    private MainDao mainDao;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        mainDao = DbClient.getInstance(this).getAppDatabase().getDao();
    }

    public static void startWork(Context context){
        enqueueWork(
                context,
                BackgroundService.class,
                1,
                new Intent()
        );
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        sendNotification("");
        startForeground(1, mBuilder.build());
        insertFirstPagesOfAllDevices();
    }

    private void insertFirstPagesOfAllDevices(){
        List<PhoneBrand> phoneBrands = mainDao.getAllPhoneBrandLinkList();
        List<PhoneBrand> phoneBrandsNotDoneAll = mainDao.getAllPhoneBrandNotDoneLinkList();
        Log.e("phone brand", phoneBrands.size() + "");
        Log.e("phone brand not", phoneBrandsNotDoneAll.size() + "");
        if(phoneBrands.size() == 0 || phoneBrandsNotDoneAll.size() > 0){
            Log.e("phone brand", "GOt inside");
            List<PageAllDevices> pageAllDevices = new ArrayList<>();
            for (int i = 0; i < phoneBrands.size(); i++) {
                PageAllDevices pad = new PageAllDevices();
                pad.setLink(phoneBrands.get(i).getLink());
                pad.setBrandName(phoneBrands.get(i).getName());
                pageAllDevices.add(pad);
            }
            mainDao.insertPageAllDevices(pageAllDevices);
            pageAllDevices.clear();
            pageAllDevices.addAll(mainDao.getAllPages());

            for (int i = 0; i < pageAllDevices.size(); i++) {
                Document doc = null;
                try {
                    Log.e("hitting", "https://www.gsmarena.com/" + pageAllDevices.get(i).getLink());
                    doc = Jsoup.connect("https://www.gsmarena.com/" + pageAllDevices.get(i).getLink())
                            .headers(Constants.getHeaders())
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36")
                            .get();
                    Element nav = doc.getElementsByClass("nav-pages").first();
                    if(nav != null){
                        //Log.e("size", nav.select("> a").size() + " " + nav.text());
                        for (int x = 0; x < nav.select("> a").size(); x++) {
                            PageAllDevices pd = new PageAllDevices();
                            pd.setLink(nav.select("> a").get(x).attr("href"));
                            pd.setBrandName(pageAllDevices.get(i).getBrandName());
                            mainDao.insertPageAllDevices(pd);
                            Log.e("a", nav.select("> a").get(x).attr("href"));
                        }

                    }
                    mainDao.updatePhoneBrandToDone(pageAllDevices.get(i).getLink());

                } catch (IOException e) {
                    Log.e("e", e.getMessage());
                    i--;
                }
            }
        }


        List<PageAllDevices> pageAllDevices = mainDao.getAllPages();
        Log.e("page size", pageAllDevices.size() + "");

        for (int i = 0; i < pageAllDevices.size(); i++) {
            Log.e("e " + i, pageAllDevices.get(i).getLink());
            Document doc = null;
            try {
                doc = Jsoup.connect("https://www.gsmarena.com/" + pageAllDevices.get(i).getLink())
                        .headers(Constants.getHeaders())
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36")
                        .get();

                Element main = doc.getElementsByClass("makers").first();
                Elements allPhones = main.select("ul li");

                ArrayList<PhoneModel> phoneModels = new ArrayList<>();
                for (int j = 0; j < allPhones.size(); j++) {
                    PhoneModel pm = new PhoneModel();
                    pm.setBrandName(pageAllDevices.get(i).getBrandName());
                    pm.setPhoneModelName(allPhones.get(j).select("strong").text());
                    pm.setDetailsLink(allPhones.get(j).select("> a").attr("href"));
                    pm.setImageLink(allPhones.get(j).select("img").attr("src"));
                    pm.setToolTips(allPhones.get(j).select("img").attr("title"));
                    phoneModels.add(pm);
                    Log.e(pageAllDevices.get(i).getBrandName(), allPhones.get(j).select("img").attr("title"));
                    mainDao.insertPhoneModels(phoneModels);
                }
            } catch (IOException e) {
                Log.e("e", e.getMessage());
                //i--;
                break;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

    }

    private void sendNotification(String fileName) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Working on scrapping")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(fileName))
                .setOngoing(true)
                .setContentText("Scrapping is going on")
                .setSmallIcon(R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId("1");
        }

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setOngoing(true);
        mBuilder.setOnlyAlertOnce(true);
        mNotificationManager.notify(1, mBuilder.build());
    }
}