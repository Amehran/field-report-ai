package com.fieldreport.ai.data.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\fH\u00c6\u0003JQ\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020\fH\u00d6\u0001J\t\u0010%\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006&"}, d2 = {"Lcom/fieldreport/ai/data/model/LocalMediaItem;", "", "id", "", "reportId", "type", "Lcom/fieldreport/ai/data/model/MediaType;", "label", "Lcom/fieldreport/ai/data/model/PhotoLabel;", "localUri", "storagePath", "sortOrder", "", "(Ljava/lang/String;Ljava/lang/String;Lcom/fieldreport/ai/data/model/MediaType;Lcom/fieldreport/ai/data/model/PhotoLabel;Ljava/lang/String;Ljava/lang/String;I)V", "getId", "()Ljava/lang/String;", "getLabel", "()Lcom/fieldreport/ai/data/model/PhotoLabel;", "getLocalUri", "getReportId", "getSortOrder", "()I", "getStoragePath", "getType", "()Lcom/fieldreport/ai/data/model/MediaType;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class LocalMediaItem {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String reportId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.fieldreport.ai.data.model.MediaType type = null;
    @org.jetbrains.annotations.NotNull()
    private final com.fieldreport.ai.data.model.PhotoLabel label = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String localUri = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String storagePath = null;
    private final int sortOrder = 0;
    
    public LocalMediaItem(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.MediaType type, @org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.PhotoLabel label, @org.jetbrains.annotations.NotNull()
    java.lang.String localUri, @org.jetbrains.annotations.Nullable()
    java.lang.String storagePath, int sortOrder) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getReportId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fieldreport.ai.data.model.MediaType getType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fieldreport.ai.data.model.PhotoLabel getLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLocalUri() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getStoragePath() {
        return null;
    }
    
    public final int getSortOrder() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fieldreport.ai.data.model.MediaType component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fieldreport.ai.data.model.PhotoLabel component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    public final int component7() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.fieldreport.ai.data.model.LocalMediaItem copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String reportId, @org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.MediaType type, @org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.PhotoLabel label, @org.jetbrains.annotations.NotNull()
    java.lang.String localUri, @org.jetbrains.annotations.Nullable()
    java.lang.String storagePath, int sortOrder) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}