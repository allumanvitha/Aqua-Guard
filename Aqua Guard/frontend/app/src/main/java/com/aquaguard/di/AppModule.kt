package com.aquaguard.di

import android.content.Context
import androidx.room.Room
import com.aquaguard.data.local.AquaGuardDatabase
import com.aquaguard.data.local.dao.AlertDao
import com.aquaguard.data.local.dao.UsageDao
import com.aquaguard.data.repository.AlertRepositoryImpl
import com.aquaguard.data.repository.AuthRepositoryImpl
import com.aquaguard.data.repository.DeviceRepositoryImpl
import com.aquaguard.data.repository.WaterRepositoryImpl
import com.aquaguard.domain.repository.AlertRepository
import com.aquaguard.domain.repository.AuthRepository
import com.aquaguard.domain.repository.DeviceRepository
import com.aquaguard.domain.repository.WaterRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): AquaGuardDatabase {
        return Room.databaseBuilder(
            context,
            AquaGuardDatabase::class.java,
            "aquaguard_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUsageDao(db: AquaGuardDatabase): UsageDao = db.usageDao

    @Provides
    fun provideAlertDao(db: AquaGuardDatabase): AlertDao = db.alertDao
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindWaterRepository(impl: WaterRepositoryImpl): WaterRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository
}
