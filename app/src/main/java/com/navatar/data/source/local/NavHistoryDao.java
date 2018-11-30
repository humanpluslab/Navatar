package com.navatar.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.navatar.data.NavHistory;

import java.util.List;

@Dao
public interface NavHistoryDao {

    @Query("SELECT * FROM NavHistory")
    List<NavHistory> getNavHistory();

    @Query("SELECT * from NavHistory WHERE entryid = :navid")
    NavHistory getNavHistoryById(String navid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNavHistory(NavHistory navHistory);

    @Query("DELETE FROM NavHistory WHERE entryid = :navid")
    int deleteNavHistoryById(String navid);
}
