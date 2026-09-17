package com.fieldreport.ai.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\bH\u0007J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\nH\u0007J\u0010\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007J\u0010\u0010\f\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u0004H\u0007J\u0010\u0010\r\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a8\u0006\u000e"}, d2 = {"Lcom/fieldreport/ai/data/db/Converters;", "", "()V", "fromMediaType", "", "value", "Lcom/fieldreport/ai/data/model/MediaType;", "fromPhotoLabel", "Lcom/fieldreport/ai/data/model/PhotoLabel;", "fromReportStatus", "Lcom/fieldreport/ai/data/model/ReportStatus;", "toMediaType", "toPhotoLabel", "toReportStatus", "app_debug"})
public final class Converters {
    
    public Converters() {
        super();
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fromReportStatus(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.ReportStatus value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final com.fieldreport.ai.data.model.ReportStatus toReportStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fromPhotoLabel(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.PhotoLabel value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final com.fieldreport.ai.data.model.PhotoLabel toPhotoLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fromMediaType(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.MediaType value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final com.fieldreport.ai.data.model.MediaType toMediaType(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return null;
    }
}