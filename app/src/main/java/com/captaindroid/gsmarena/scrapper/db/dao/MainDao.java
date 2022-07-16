package com.captaindroid.gsmarena.scrapper.db.dao;

import static androidx.room.OnConflictStrategy.ABORT;
import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.captaindroid.gsmarena.scrapper.db.tables.PageAllDevices;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MainDao {
    @Insert(onConflict = IGNORE)
    void insertBook(ArrayList<PhoneBrand> phoneBrands);

    @Insert(onConflict = REPLACE)
    void insertPageAllDevices(List<PageAllDevices> pages);

    @Insert(onConflict = REPLACE)
    void insertPageAllDevices(PageAllDevices pd);

    @Query("select * from PhoneBrand")
    List<PhoneBrand> getAllPhoneBrandLinkList();

    @Query("select * from PhoneBrand where doneAllPage = 0")
    List<PhoneBrand> getAllPhoneBrandNotDoneLinkList();

    @Query("update phonebrand set doneAllPage = 1 where link = :link")
    void updatePhoneBrandToDone(String link);

    @Query("select * from PageAllDevices order by brandName desc")
    List<PageAllDevices> getAllPages();

    @Query("select * from PageAllDevices where doneListingModels = 0 order by brandName desc")
    List<PageAllDevices> getAllUnDonePage();

    @Query("update PageAllDevices set doneListingModels = :b where id = :id")
    void updatePageToDone(int id, boolean b);

    @Insert(onConflict = REPLACE)
    void insertPhoneModels(ArrayList<PhoneModel> phoneModels);

    @Query("select * from PhoneModel")
    List<PhoneModel> getAllPhoneModels();

    @Query("select * from PhoneModel where scrappingDone = 1 and uploadToServerDone = 0 limit 200")
    List<PhoneModel> getAllPhoneModelsWithLimit();

    @Query("select * from PhoneModel where brandName = :brandName")
    List<PhoneModel> getAllPhoneModels(String brandName);

    @Query("select * from PhoneModel where scrappingDone = 0")
    List<PhoneModel> getAllPhoneModelsWhichAreNotDoneScrapping();

    @Query("update PhoneModel set details = :toJson, scrappingDone = 1 where id= :id")
    void updatePhoneModelDataDetails(String toJson, int id);

    @Query("Select count(id) from PhoneModel")
    int getCountPhoneModel();

    @Query("Select count(id) from PhoneModel where scrappingDone = 1")
    int getCountPhoneModelDoneScrapping();

    @Update
    void updatePhoneModels(List<PhoneModel> models);










    @Query("update PhoneModel set uploadToServerDone = 0")
    void updateServerUploadFalse();
}
