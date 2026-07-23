# ProGuard Rules for Aqua Guard release build optimization

# Keep Firebase Realtime Database and Firestore model properties
-keepclassmembers class * {
    @com.google.firebase.database.PropertyName <fields>;
    @com.google.firebase.database.PropertyName <methods>;
}

# Keep Room annotations and entities
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.PrimaryKey <fields>;
}

# Keep Hilt / Dagger generated injection structures
-keep class * implements dagger.hilt.internal.GeneratedComponent
-keep class * extends android.app.Application

# General keep rules to prevent obfuscation issues on serialization models
-keep class com.aquaguard.domain.model.** { *; }
-keep class com.aquaguard.data.local.entity.** { *; }
