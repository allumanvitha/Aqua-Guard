package com.aquaguard.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.aquaguard.data.local.dao.AlertDao;
import com.aquaguard.data.local.dao.UsageDao;
import com.aquaguard.data.local.entity.AlertEntity;
import com.aquaguard.data.local.entity.UsageEntity;

@Database(
    entities = {UsageEntity.class, AlertEntity.class},
    version = 1,
    exportSchema = false
)
public abstract class AquaGuardDatabase extends RoomDatabase {
    public abstract UsageDao usageDao();
    public abstract AlertDao alertDao();
}
