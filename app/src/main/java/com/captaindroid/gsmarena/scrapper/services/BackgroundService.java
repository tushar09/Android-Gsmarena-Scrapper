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
import com.captaindroid.gsmarena.scrapper.dto.Head;
import com.captaindroid.gsmarena.scrapper.dto.PhoneConfiguration;
import com.captaindroid.gsmarena.scrapper.dto.StackClass;
import com.captaindroid.gsmarena.scrapper.dto.SubHeader;
import com.captaindroid.gsmarena.scrapper.eventBus.ScrappingStatus;
import com.captaindroid.gsmarena.scrapper.utils.Constants;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BackgroundService extends JobIntentService {

    private MainDao mainDao;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private static ScrappingStatus scrappingStatus = new ScrappingStatus();

    @Override
    public void onCreate() {
        super.onCreate();
        mainDao = DbClient.getInstance(this).getAppDatabase().getDao();
    }

    public static void startWork(Context context){
        scrappingStatus.setIndetermination(true);
        scrappingStatus.setFinished(false);
        scrappingStatus.setProgress(0);
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

                    scrappingStatus.setScrappingName("Scrapping " + pageAllDevices.get(i).getBrandName() + ": " + pageAllDevices.get(i).getLink());
                    EventBus.getDefault().post(scrappingStatus);
                    mBuilder.setContentText("Scrapping " + pageAllDevices.get(i).getBrandName() + ": " + pageAllDevices.get(i).getLink());
                    mNotificationManager.notify(1, mBuilder.build());

                    doc = Jsoup.connect("https://www.gsmarena.com/" + URLEncoder.encode(pageAllDevices.get(i).getLink()) )
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
                    scrappingStatus.setScrappingName("Please connect / or change the vpn connection");
                    EventBus.getDefault().post(scrappingStatus);
                    mBuilder.setContentText("Please connect / or change the vpn connection");
                    mNotificationManager.notify(1, mBuilder.build());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    i--;
                }
            }
        }


        List<PageAllDevices> pageAllDevices = mainDao.getAllUnDonePage();
        Log.e("page size", pageAllDevices.size() + "");

        for (int i = 0; i < pageAllDevices.size(); i++) {
            Log.e("e " + i, pageAllDevices.get(i).getLink());
            Document doc = null;
            try {

                scrappingStatus.setScrappingName("Scrapping " + pageAllDevices.get(i).getBrandName() + ": " + pageAllDevices.get(i).getLink());
                EventBus.getDefault().post(scrappingStatus);
                mBuilder.setContentText("Scrapping " + pageAllDevices.get(i).getBrandName() + ": " + pageAllDevices.get(i).getLink());
                mNotificationManager.notify(1, mBuilder.build());

                doc = Jsoup.connect("https://www.gsmarena.com/" + URLEncoder.encode(pageAllDevices.get(i).getLink()))
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
                mainDao.updatePageToDone(pageAllDevices.get(i).getId(), true);
            } catch (Exception e) {
                scrappingStatus.setScrappingName("Please connect / or change the vpn connection");
                EventBus.getDefault().post(scrappingStatus);
                mBuilder.setContentText("Please connect / or change the vpn connection");
                mNotificationManager.notify(1, mBuilder.build());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                i--;
            }
        }

        List<PhoneModel> phoneModels = mainDao.getAllPhoneModelsWhichAreNotDoneScrapping();
        for (int l = 0; l < phoneModels.size(); l++) {
            Document doc = null;
            Stack<StackClass> stack = new Stack<StackClass>();
            try {

                double totalSize = mainDao.getCountPhoneModel();
                double totalSizeDone = mainDao.getCountPhoneModelDoneScrapping();

                scrappingStatus.setScrappingName("Scrapping " + phoneModels.get(l).getBrandName() + ": " + phoneModels.get(l).getPhoneModelName());
                scrappingStatus.setPhoneImageUrl(phoneModels.get(l).getImageLink());
                scrappingStatus.setProgress((int) ((totalSizeDone / totalSize) * 100));
                scrappingStatus.setIndetermination(false);
                EventBus.getDefault().post(scrappingStatus);
                mBuilder.setContentText("Scrapping " + phoneModels.get(l).getBrandName() + ": " + phoneModels.get(l).getPhoneModelName());
                mNotificationManager.notify(1, mBuilder.build());

                String url = "https://www.gsmarena.com/" + URLEncoder.encode(phoneModels.get(l).getDetailsLink(), "UTF-8");
                //String url = "https://www.gsmarena.com/" + phoneModels.get(l).getDetailsLink().replaceAll("\\[", "%5B").replaceAll("]", "%5D");
                Log.e("details", url);
                doc = Jsoup.connect(url)
                        .headers(Constants.getHeaders())
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36")
                        .get();

                Element data = doc.getElementById("specs-list");
                Elements tables = data.select("table");

                for (int i = 0; i < tables.size(); i++) {
                    Elements row = tables.get(i).select("tr");
                    for (int j = 0; j < row.size(); j++) {
                        Element header = row.get(j).select("th").first();
                        PhoneConfiguration phoneConfiguration = new PhoneConfiguration();
                        if(header != null){
                            StackClass exp = new StackClass(row.get(j).select("th").first().text(), 1);
                            stack.push(exp);
                        }

                        Elements tds = row.get(j).select("td");
                        for (int k = 0; k < tds.size(); k++) {
                            Element subHead = tds.get(k).getElementsByClass("ttl").first();
                            if(subHead != null){
                                StackClass exp = new StackClass(subHead.text(), 2);
                                stack.push(exp);
                            }
                            Element a = tds.get(k).select("> a").first();
                            if(subHead != null && a != null && a.text().equals(subHead.text())){
                                //Log.e("data", "         " + tds.get(k).html());
                            }else{
                                StackClass exp = new StackClass(tds.get(k).html(), 3);
                                stack.push(exp);
                                Log.e("data", "         " + tds.get(k).html());
                            }

                            //Log.e("data", "         " + nfo.html());
                        }

                    }
                }

                ArrayList<String> strings = new ArrayList<>();
                ArrayList<SubHeader> subHeads = new ArrayList<>();
                String head;
                PhoneConfiguration phoneConfiguration = new PhoneConfiguration();
                ArrayList<Head> heads = new ArrayList<>();
                phoneConfiguration.setHeads(heads);
                while (stack.size() > 0){
                    if(stack.peek().getType() == 3){
                        strings.add(stack.pop().getData());
                    }else if(stack.peek().getType() == 2){
                        SubHeader subHead = new SubHeader();
                        subHead.setName(stack.pop().getData());
                        subHead.setData(strings);

                        subHeads.add(subHead);

                        strings = new ArrayList<>();

                    }else{
                        Head h = new Head();
                        h.setName(stack.pop().getData());
                        h.setSubHeaders(subHeads);

                        subHeads = new ArrayList<>();

                        phoneConfiguration.getHeads().add(h);
                    }

                }

                mainDao.updatePhoneModelDataDetails(new Gson().toJson(phoneConfiguration), phoneModels.get(l).getId());

                int maxLogSize = 1000;
                for(int i = 0; i <= new Gson().toJson(phoneConfiguration).length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i+1) * maxLogSize;
                    end = end > new Gson().toJson(phoneConfiguration).length() ? new Gson().toJson(phoneConfiguration).length() : end;
                    Log.e("pc", new Gson().toJson(phoneConfiguration).substring(start, end));
                }


                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }


            } catch (Exception e) {
                l--;
                scrappingStatus.setScrappingName("Please connect / or change the vpn connection");
                EventBus.getDefault().post(scrappingStatus);
                mBuilder.setContentText("Please connect / or change the vpn connection");
                mNotificationManager.notify(1, mBuilder.build());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Log.e("exp", e.toString());
            }
        }

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