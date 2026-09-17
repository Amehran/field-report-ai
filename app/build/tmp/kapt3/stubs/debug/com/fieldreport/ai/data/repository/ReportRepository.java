package com.fieldreport.ai.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ \u0010\u0015\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0016\u001a\u0004\u0018\u00010\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u001a\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00070\u00062\u0006\u0010\u0011\u001a\u00020\u0012J\u0016\u0010\u0019\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u00062\u0006\u0010\u0011\u001a\u00020\u0012J\u0016\u0010\u001a\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u001cJ&\u0010\u001d\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020!H\u0086@\u00a2\u0006\u0002\u0010\"J\u0016\u0010#\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u001cJ\u001e\u0010$\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0017R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/fieldreport/ai/data/repository/ReportRepository;", "", "reportDao", "Lcom/fieldreport/ai/data/db/ReportDao;", "(Lcom/fieldreport/ai/data/db/ReportDao;)V", "allReports", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/fieldreport/ai/data/db/ReportEntity;", "getAllReports", "()Lkotlinx/coroutines/flow/Flow;", "addMediaItem", "", "item", "Lcom/fieldreport/ai/data/db/MediaItemEntity;", "(Lcom/fieldreport/ai/data/db/MediaItemEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "approveReport", "reportId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMediaItem", "generateLocalMockDraft", "typedNotes", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMediaForReport", "getReportById", "saveReport", "report", "(Lcom/fieldreport/ai/data/db/ReportEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateMediaStoragePath", "mediaId", "path", "isUploaded", "", "(Ljava/lang/String;Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateReport", "updateReportAudioStoragePath", "app_debug"})
public final class ReportRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.fieldreport.ai.data.db.ReportDao reportDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.fieldreport.ai.data.db.ReportEntity>> allReports = null;
    
    public ReportRepository(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.ReportDao reportDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.fieldreport.ai.data.db.ReportEntity>> getAllReports() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.fieldreport.ai.data.db.ReportEntity> getReportById(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.fieldreport.ai.data.db.MediaItemEntity>> getMediaForReport(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveReport(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.ReportEntity report, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateReport(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.ReportEntity report, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addMediaItem(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.MediaItemEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteMediaItem(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.MediaItemEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateMediaStoragePath(@org.jetbrains.annotations.NotNull()
    java.lang.String mediaId, @org.jetbrains.annotations.NotNull()
    java.lang.String path, boolean isUploaded, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateReportAudioStoragePath(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    java.lang.String path, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object generateLocalMockDraft(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.Nullable()
    java.lang.String typedNotes, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object approveReport(@org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}