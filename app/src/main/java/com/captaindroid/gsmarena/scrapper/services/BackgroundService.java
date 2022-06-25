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
        List<String> phoneBrands = mainDao.getAllPhoneBrandLinkList();
        List<PageAllDevices> pageAllDevices = new ArrayList<>();
        for (int i = 0; i < phoneBrands.size(); i++) {
            PageAllDevices pad = new PageAllDevices();
            pad.setLink(phoneBrands.get(i));
            pageAllDevices.add(pad);
        }
        mainDao.insertPageAllDevices(pageAllDevices);
        pageAllDevices.clear();
        pageAllDevices.addAll(mainDao.getAllPages());

        for (PageAllDevices pageAllDevice : pageAllDevices) {
            Document doc = null;
            try {
                Log.e("hitting", "https://www.gsmarena.com/" + pageAllDevice.getLink());
                doc = Jsoup.connect("https://www.gsmarena.com/" + pageAllDevice.getLink())
                        .headers(Constants.getHeaders())
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36")
                        .get();
                Element nav = doc.getElementsByClass("nav-pages").first();
                Log.e("size", nav.select("> a").size() + " " + nav.text());
                for (int i = 0; i < nav.select("> a").size(); i++) {
                    Log.e("a", nav.select("> a").get(i).attr("href"));
                }
            } catch (IOException e) {
                Log.e("e", e.toString());
            }
            break;
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