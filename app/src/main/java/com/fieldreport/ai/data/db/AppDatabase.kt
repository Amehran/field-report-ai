package com.fieldreport.ai.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ReportEntity::class, MediaItemEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE reports ADD COLUMN laborCost REAL")
                database.execSQL("ALTER TABLE reports ADD COLUMN partsCost REAL")
                database.execSQL("ALTER TABLE reports ADD COLUMN totalCost REAL")
                database.execSQL("ALTER TABLE reports ADD COLUMN initialStatus TEXT")
                database.execSQL("ALTER TABLE reports ADD COLUMN resolutionStepsJson TEXT")
                database.execSQL("ALTER TABLE reports ADD COLUMN currentOperationalState TEXT")
                database.execSQL("ALTER TABLE reports ADD COLUMN aiAgentMode TEXT NOT NULL DEFAULT 'CLOUD'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "field_report_ai.db"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
