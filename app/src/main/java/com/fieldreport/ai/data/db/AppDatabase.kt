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
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reports ADD COLUMN laborCost REAL")
                db.execSQL("ALTER TABLE reports ADD COLUMN partsCost REAL")
                db.execSQL("ALTER TABLE reports ADD COLUMN totalCost REAL")
                db.execSQL("ALTER TABLE reports ADD COLUMN initialStatus TEXT")
                db.execSQL("ALTER TABLE reports ADD COLUMN resolutionStepsJson TEXT")
                db.execSQL("ALTER TABLE reports ADD COLUMN currentOperationalState TEXT")
                db.execSQL("ALTER TABLE reports ADD COLUMN aiAgentMode TEXT NOT NULL DEFAULT 'CLOUD'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reports ADD COLUMN technicianName TEXT")
                db.execSQL("ALTER TABLE reports ADD COLUMN technicianComments TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reports ADD COLUMN reportTone TEXT NOT NULL DEFAULT 'STANDARD'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "field_report_ai.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
