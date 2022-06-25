package com.captaindroid.gsmarena.scrapper.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.captaindroid.gsmarena.scrapper.db.tables.PageAllDevices;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MainDao {
    @Insert
    void insertBook(ArrayList<PhoneBrand> phoneBrands);

    @Insert(onConflict = REPLACE)
    void insertPageAllDevices(List<PageAllDevices> pages);

    @Query("select link from PhoneBrand")
    List<String> getAllPhoneBrandLinkList();

    @Query("select * from PageAllDevices")
    List<PageAllDevices> getAllPages();
}
