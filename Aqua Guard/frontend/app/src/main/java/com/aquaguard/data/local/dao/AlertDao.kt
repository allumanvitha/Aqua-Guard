package com.aquaguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aquaguard.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts WHERE deviceId = :deviceId AND resolved = 0 ORDER BY timestamp DESC")
    fun getActiveAlerts(deviceId: String): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getAlertHistory(deviceId: String): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<AlertEntity>)
}
