package com.captaindroid.gsmarena.scrapper.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.captaindroid.gsmarena.scrapper.db.dao.MainDao;
import com.captaindroid.gsmarena.scrapper.db.tables.PageAllDevices;
import com.captaindroid.gsmarena.scrapper.db.tables.PhoneBrand;

@Database(entities = {PhoneBrand.class, PageAllDevices.class}, exportSchema = true, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MainDao getDao();
}
