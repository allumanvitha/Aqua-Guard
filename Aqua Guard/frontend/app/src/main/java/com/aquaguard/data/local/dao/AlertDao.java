package com.aquaguard.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.aquaguard.data.local.entity.AlertEntity;

import java.util.List;

@Dao
public interface AlertDao {
    @Query("SELECT * FROM alerts WHERE deviceId = :deviceId AND resolved = 0 ORDER BY timestamp DESC")
    LiveData<List<AlertEntity>> getActiveAlerts(String deviceId);

    @Query("SELECT * FROM alerts WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    LiveData<List<AlertEntity>> getAlertHistory(String deviceId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlert(AlertEntity alert);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AlertEntity> alerts);
}
