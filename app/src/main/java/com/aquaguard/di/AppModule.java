package com.aquaguard.di;

import android.content.Context;

import androidx.room.Room;

import com.aquaguard.data.local.AquaGuardDatabase;
import com.aquaguard.data.local.dao.AlertDao;
import com.aquaguard.data.local.dao.UsageDao;
import com.aquaguard.data.repository.AlertRepositoryImpl;
import com.aquaguard.data.repository.AuthRepositoryImpl;
import com.aquaguard.data.repository.DeviceRepositoryImpl;
import com.aquaguard.data.repository.WaterRepositoryImpl;
import com.aquaguard.domain.repository.AlertRepository;
import com.aquaguard.domain.repository.AuthRepository;
import com.aquaguard.domain.repository.DeviceRepository;
import com.aquaguard.domain.repository.WaterRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseDatabase provideFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Provides
    @Singleton
    public static AquaGuardDatabase provideRoomDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context,
                AquaGuardDatabase.class,
                "aquaguard_db"
        ).fallbackToDestructiveMigration().build();
    }

    @Provides
    public static UsageDao provideUsageDao(AquaGuardDatabase db) {
        return db.usageDao();
    }

    @Provides
    public static AlertDao provideAlertDao(AquaGuardDatabase db) {
        return db.alertDao();
    }

    @Module
    @InstallIn(SingletonComponent.class)
    public interface RepositoryModule {

        @Binds
        @Singleton
        AuthRepository bindAuthRepository(AuthRepositoryImpl impl);

        @Binds
        @Singleton
        DeviceRepository bindDeviceRepository(DeviceRepositoryImpl impl);

        @Binds
        @Singleton
        WaterRepository bindWaterRepository(WaterRepositoryImpl impl);

        @Binds
        @Singleton
        AlertRepository bindAlertRepository(AlertRepositoryImpl impl);
    }
}
