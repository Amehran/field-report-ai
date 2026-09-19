package com.fieldreport.ai.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Query("SELECT * FROM reports ORDER BY updatedAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE id = :reportId")
    suspend fun getReportById(reportId: String): ReportEntity?

    @Query("SELECT * FROM reports WHERE id = :reportId")
    fun observeReportById(reportId: String): Flow<ReportEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: String)
    
    @Query("UPDATE reports SET status = :status WHERE id = :reportId")
    suspend fun updateStatus(reportId: String, status: com.fieldreport.ai.data.model.ReportStatus)

    @Query("UPDATE reports SET audioStoragePath = :path WHERE id = :reportId")
    suspend fun updateReportAudioStoragePath(reportId: String, path: String)

    @Query("UPDATE reports SET audioLocalUri = :uri WHERE id = :reportId")
    suspend fun updateAudioLocalUri(reportId: String, uri: String)

    @Query("SELECT * FROM media_items WHERE reportId = :reportId ORDER BY sortOrder ASC")
    fun getMediaForReport(reportId: String): Flow<List<MediaItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(item: MediaItemEntity)

    @Delete
    suspend fun deleteMediaItem(item: MediaItemEntity)

    @Query("SELECT * FROM media_items WHERE reportId = :reportId ORDER BY sortOrder ASC")
    suspend fun getMediaItemsForReport(reportId: String): List<MediaItemEntity>

    @Query("UPDATE media_items SET storagePath = :path, isUploaded = :isUploaded WHERE id = :mediaId")
    suspend fun updateMediaStoragePath(mediaId: String, path: String, isUploaded: Boolean)
}
