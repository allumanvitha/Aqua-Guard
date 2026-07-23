package com.aquaguard.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.aquaguard.data.local.entity.UsageEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UsageDao_Impl implements UsageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UsageEntity> __insertionAdapterOfUsageEntity;

  public UsageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUsageEntity = new EntityInsertionAdapter<UsageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `water_usage` (`id`,`deviceId`,`date`,`totalLiters`,`waterSavedLiters`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UsageEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getDeviceId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDeviceId());
        }
        if (entity.getDate() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDate());
        }
        statement.bindDouble(4, entity.getTotalLiters());
        statement.bindDouble(5, entity.getWaterSavedLiters());
      }
    };
  }

  @Override
  public void insertUsage(final UsageEntity usage) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUsageEntity.insert(usage);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertAll(final List<UsageEntity> usages) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUsageEntity.insert(usages);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<UsageEntity>> getUsageHistory(final String deviceId) {
    final String _sql = "SELECT * FROM water_usage WHERE deviceId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (deviceId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, deviceId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"water_usage"}, false, new Callable<List<UsageEntity>>() {
      @Override
      @Nullable
      public List<UsageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTotalLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalLiters");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final List<UsageEntity> _result = new ArrayList<UsageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UsageEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpDeviceId;
            if (_cursor.isNull(_cursorIndexOfDeviceId)) {
              _tmpDeviceId = null;
            } else {
              _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            }
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final float _tmpTotalLiters;
            _tmpTotalLiters = _cursor.getFloat(_cursorIndexOfTotalLiters);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            _item = new UsageEntity(_tmpId,_tmpDeviceId,_tmpDate,_tmpTotalLiters,_tmpWaterSavedLiters);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
