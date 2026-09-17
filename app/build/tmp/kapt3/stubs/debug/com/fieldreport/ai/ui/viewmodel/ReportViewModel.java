package com.fieldreport.ai.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00072\b\b\u0002\u0010\u001a\u001a\u00020\u001bJ\u0006\u0010\u001c\u001a\u00020\u0018J\u0010\u0010\u001d\u001a\u00020\u00182\b\u0010\u001e\u001a\u0004\u0018\u00010\u0007J\u000e\u0010\u001f\u001a\u00020\u00182\u0006\u0010 \u001a\u00020\u0007J\u000e\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020#J\"\u0010$\u001a\u00020\u00182\u0006\u0010%\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\u00072\n\b\u0002\u0010\'\u001a\u0004\u0018\u00010\u0007J\u000e\u0010(\u001a\u00020\u00182\u0006\u0010)\u001a\u00020\u000fJ\u0014\u0010*\u001a\u00020\u00182\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00070\nJ\u0014\u0010,\u001a\u00020\u00182\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00070\nJ\u0014\u0010-\u001a\u00020\u00182\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00070\nR\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001d\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0019\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0019\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\rR\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/fieldreport/ai/ui/viewmodel/ReportViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_currentReportId", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "allReports", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/fieldreport/ai/data/db/ReportEntity;", "getAllReports", "()Lkotlinx/coroutines/flow/StateFlow;", "currentMedia", "Lcom/fieldreport/ai/data/db/MediaItemEntity;", "getCurrentMedia", "currentReport", "getCurrentReport", "currentReportId", "getCurrentReportId", "repository", "Lcom/fieldreport/ai/data/repository/ReportRepository;", "addPhoto", "", "uriString", "label", "Lcom/fieldreport/ai/data/model/PhotoLabel;", "approveReport", "generateReportDraft", "typedNotes", "setCurrentReportId", "id", "shareReportPdf", "context", "Landroid/content/Context;", "startNewReport", "customerName", "jobTitle", "address", "togglePhotoLabel", "item", "updateFindings", "items", "updateRecommendations", "updateWorkCompleted", "app_debug"})
@kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
public final class ReportViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.fieldreport.ai.data.repository.ReportRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.fieldreport.ai.data.db.ReportEntity>> allReports = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _currentReportId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currentReportId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.fieldreport.ai.data.db.ReportEntity> currentReport = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.fieldreport.ai.data.db.MediaItemEntity>> currentMedia = null;
    
    public ReportViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.fieldreport.ai.data.db.ReportEntity>> getAllReports() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrentReportId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.fieldreport.ai.data.db.ReportEntity> getCurrentReport() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.fieldreport.ai.data.db.MediaItemEntity>> getCurrentMedia() {
        return null;
    }
    
    public final void startNewReport(@org.jetbrains.annotations.NotNull()
    java.lang.String customerName, @org.jetbrains.annotations.NotNull()
    java.lang.String jobTitle, @org.jetbrains.annotations.Nullable()
    java.lang.String address) {
    }
    
    public final void setCurrentReportId(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
    }
    
    public final void addPhoto(@org.jetbrains.annotations.NotNull()
    java.lang.String uriString, @org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.model.PhotoLabel label) {
    }
    
    public final void togglePhotoLabel(@org.jetbrains.annotations.NotNull()
    com.fieldreport.ai.data.db.MediaItemEntity item) {
    }
    
    public final void generateReportDraft(@org.jetbrains.annotations.Nullable()
    java.lang.String typedNotes) {
    }
    
    public final void updateWorkCompleted(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> items) {
    }
    
    public final void updateFindings(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> items) {
    }
    
    public final void updateRecommendations(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> items) {
    }
    
    public final void approveReport() {
    }
    
    public final void shareReportPdf(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}