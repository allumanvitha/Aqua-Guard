package com.aquaguard.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.aquaguard.data.local.entity.UsageEntity;

import java.util.List;

@Dao
public interface UsageDao {
    @Query("SELECT * FROM water_usage WHERE deviceId = :deviceId ORDER BY date DESC")
    LiveData<List<UsageEntity>> getUsageHistory(String deviceId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsage(UsageEntity usage);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UsageEntity> usages);
}
