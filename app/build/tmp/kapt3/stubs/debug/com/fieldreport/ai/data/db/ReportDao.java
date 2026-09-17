package com.fieldreport.ai.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\fH\'J\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\b\u001a\u00020\tH\'J\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u0011\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0018\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\f2\u0006\u0010\b\u001a\u00020\tH\'J&\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0018\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u001bH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u0016\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u001e\u0010\u001e\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u001e\u0010 \u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010!\u001a\u00020\"H\u00a7@\u00a2\u0006\u0002\u0010#\u00a8\u0006$"}, d2 = {"Lcom/fieldreport/ai/data/db/ReportDao;", "", "deleteMediaItem", "", "item", "Lcom/fieldreport/ai/data/db/MediaItemEntity;", "(Lcom/fieldreport/ai/data/db/MediaItemEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteReport", "reportId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllReports", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/fieldreport/ai/data/db/ReportEntity;", "getMediaForReport", "getMediaItemsForReport", "getReportById", "insertMediaItem", "insertReport", "report", "(Lcom/fieldreport/ai/data/db/ReportEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeReportById", "updateMediaStoragePath", "mediaId", "path", "isUploaded", "", "(Ljava/lang/String;Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateReport", "updateReportAudioStoragePath", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateStatus", "status", "Lcom/fieldreport/ai/data/model/ReportStatus;", "(Ljava/lang/String;Lcom/fieldreport/ai/data/model/ReportStatus;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface ReportDao {
    
    @androidx.room.Query(value = "SELECT * FROM reports ORDER BY updatedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.fieldreport.ai.data.db.ReportEntity>> getAllReports();
    
    @androidx.room.Query(value = "SELECT * FROM reports WHERE id = :reportId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getReportById(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.fieldreport.ai.data.db.ReportEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM reports WHERE id = :reportId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.fieldreport.ai.data.db.ReportEntity> observeReportById(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertReport(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.ReportEntity report, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateReport(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.ReportEntity report, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM reports WHERE id = :reportId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteReport(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE reports SET status = :status WHERE id = :reportId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.ReportStatus status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE reports SET audioStoragePath = :path WHERE id = :reportId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateReportAudioStoragePath(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    java.lang.String path, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE reportId = :reportId ORDER BY sortOrder ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.fieldreport.ai.data.db.MediaItemEntity>> getMediaForReport(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertMediaItem(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.MediaItemEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMediaItem(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.MediaItemEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM media_items WHERE reportId = :reportId ORDER BY sortOrder ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMediaItemsForReport(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.fieldreport.ai.data.db.MediaItemEntity>> $completion);
    
    @androidx.room.Query(value = "UPDATE media_items SET storagePath = :path, isUploaded = :isUploaded WHERE id = :mediaId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateMediaStoragePath(@org.jetbrains.annotations.NotNull()
    java.lang.String mediaId, @org.jetbrains.annotations.NotNull()
    java.lang.String path, boolean isUploaded, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}