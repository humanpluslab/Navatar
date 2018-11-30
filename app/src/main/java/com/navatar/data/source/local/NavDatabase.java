package com.navatar.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.navatar.data.NavHistory;

@Database(entities = {NavHistory.class}, version = 1)
public abstract class NavDatabase extends RoomDatabase {

    public abstract NavHistoryDao navHistoryDao();

}
