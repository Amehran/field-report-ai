package com.fieldreport.ai.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fieldreport.ai.data.model.MediaType;
import com.fieldreport.ai.data.model.PhotoLabel;
import com.fieldreport.ai.data.model.ReportStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReportDao_Impl implements ReportDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReportEntity> __insertionAdapterOfReportEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<MediaItemEntity> __insertionAdapterOfMediaItemEntity;

  private final EntityDeletionOrUpdateAdapter<MediaItemEntity> __deletionAdapterOfMediaItemEntity;

  private final EntityDeletionOrUpdateAdapter<ReportEntity> __updateAdapterOfReportEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteReport;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateReportAudioStoragePath;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMediaStoragePath;

  public ReportDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReportEntity = new EntityInsertionAdapter<ReportEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reports` (`id`,`userId`,`status`,`customerName`,`jobTitle`,`address`,`referenceNumber`,`typedNotes`,`audioLocalUri`,`audioStoragePath`,`rawTranscript`,`customerSummary`,`workCompletedJson`,`findingsJson`,`recommendationsJson`,`pdfLocalPath`,`createdAt`,`updatedAt`,`approvedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReportEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUserId());
        }
        final String _tmp = __converters.fromReportStatus(entity.getStatus());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        if (entity.getCustomerName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCustomerName());
        }
        if (entity.getJobTitle() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getJobTitle());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getAddress());
        }
        if (entity.getReferenceNumber() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getReferenceNumber());
        }
        if (entity.getTypedNotes() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getTypedNotes());
        }
        if (entity.getAudioLocalUri() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAudioLocalUri());
        }
        if (entity.getAudioStoragePath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAudioStoragePath());
        }
        if (entity.getRawTranscript() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getRawTranscript());
        }
        if (entity.getCustomerSummary() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getCustomerSummary());
        }
        if (entity.getWorkCompletedJson() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getWorkCompletedJson());
        }
        if (entity.getFindingsJson() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getFindingsJson());
        }
        if (entity.getRecommendationsJson() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getRecommendationsJson());
        }
        if (entity.getPdfLocalPath() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getPdfLocalPath());
        }
        statement.bindLong(17, entity.getCreatedAt());
        statement.bindLong(18, entity.getUpdatedAt());
        if (entity.getApprovedAt() == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, entity.getApprovedAt());
        }
      }
    };
    this.__insertionAdapterOfMediaItemEntity = new EntityInsertionAdapter<MediaItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `media_items` (`id`,`reportId`,`type`,`label`,`localUri`,`storagePath`,`sortOrder`,`isUploaded`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaItemEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getReportId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getReportId());
        }
        final String _tmp = __converters.fromMediaType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        final String _tmp_1 = __converters.fromPhotoLabel(entity.getLabel());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_1);
        }
        if (entity.getLocalUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLocalUri());
        }
        if (entity.getStoragePath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getStoragePath());
        }
        statement.bindLong(7, entity.getSortOrder());
        final int _tmp_2 = entity.isUploaded() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
      }
    };
    this.__deletionAdapterOfMediaItemEntity = new EntityDeletionOrUpdateAdapter<MediaItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `media_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaItemEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfReportEntity = new EntityDeletionOrUpdateAdapter<ReportEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reports` SET `id` = ?,`userId` = ?,`status` = ?,`customerName` = ?,`jobTitle` = ?,`address` = ?,`referenceNumber` = ?,`typedNotes` = ?,`audioLocalUri` = ?,`audioStoragePath` = ?,`rawTranscript` = ?,`customerSummary` = ?,`workCompletedJson` = ?,`findingsJson` = ?,`recommendationsJson` = ?,`pdfLocalPath` = ?,`createdAt` = ?,`updatedAt` = ?,`approvedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReportEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUserId());
        }
        final String _tmp = __converters.fromReportStatus(entity.getStatus());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        if (entity.getCustomerName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCustomerName());
        }
        if (entity.getJobTitle() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getJobTitle());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getAddress());
        }
        if (entity.getReferenceNumber() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getReferenceNumber());
        }
        if (entity.getTypedNotes() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getTypedNotes());
        }
        if (entity.getAudioLocalUri() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAudioLocalUri());
        }
        if (entity.getAudioStoragePath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAudioStoragePath());
        }
        if (entity.getRawTranscript() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getRawTranscript());
        }
        if (entity.getCustomerSummary() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getCustomerSummary());
        }
        if (entity.getWorkCompletedJson() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getWorkCompletedJson());
        }
        if (entity.getFindingsJson() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getFindingsJson());
        }
        if (entity.getRecommendationsJson() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getRecommendationsJson());
        }
        if (entity.getPdfLocalPath() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getPdfLocalPath());
        }
        statement.bindLong(17, entity.getCreatedAt());
        statement.bindLong(18, entity.getUpdatedAt());
        if (entity.getApprovedAt() == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, entity.getApprovedAt());
        }
        if (entity.getId() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteReport = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reports WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reports SET status = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateReportAudioStoragePath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reports SET audioStoragePath = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateMediaStoragePath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE media_items SET storagePath = ?, isUploaded = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertReport(final ReportEntity report,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReportEntity.insert(report);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMediaItem(final MediaItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMediaItemEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMediaItem(final MediaItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMediaItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReport(final ReportEntity report,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReportEntity.handle(report);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReport(final String reportId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteReport.acquire();
        int _argIndex = 1;
        if (reportId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, reportId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteReport.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String reportId, final ReportStatus status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromReportStatus(status);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        _argIndex = 2;
        if (reportId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, reportId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReportAudioStoragePath(final String reportId, final String path,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateReportAudioStoragePath.acquire();
        int _argIndex = 1;
        if (path == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, path);
        }
        _argIndex = 2;
        if (reportId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, reportId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateReportAudioStoragePath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMediaStoragePath(final String mediaId, final String path,
      final boolean isUploaded, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMediaStoragePath.acquire();
        int _argIndex = 1;
        if (path == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, path);
        }
        _argIndex = 2;
        final int _tmp = isUploaded ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 3;
        if (mediaId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, mediaId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateMediaStoragePath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ReportEntity>> getAllReports() {
    final String _sql = "SELECT * FROM reports ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reports"}, new Callable<List<ReportEntity>>() {
      @Override
      @NonNull
      public List<ReportEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfJobTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "jobTitle");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfReferenceNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "referenceNumber");
          final int _cursorIndexOfTypedNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "typedNotes");
          final int _cursorIndexOfAudioLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "audioLocalUri");
          final int _cursorIndexOfAudioStoragePath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioStoragePath");
          final int _cursorIndexOfRawTranscript = CursorUtil.getColumnIndexOrThrow(_cursor, "rawTranscript");
          final int _cursorIndexOfCustomerSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "customerSummary");
          final int _cursorIndexOfWorkCompletedJson = CursorUtil.getColumnIndexOrThrow(_cursor, "workCompletedJson");
          final int _cursorIndexOfFindingsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "findingsJson");
          final int _cursorIndexOfRecommendationsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "recommendationsJson");
          final int _cursorIndexOfPdfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfLocalPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfApprovedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "approvedAt");
          final List<ReportEntity> _result = new ArrayList<ReportEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReportEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final ReportStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __converters.toReportStatus(_tmp);
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpJobTitle;
            if (_cursor.isNull(_cursorIndexOfJobTitle)) {
              _tmpJobTitle = null;
            } else {
              _tmpJobTitle = _cursor.getString(_cursorIndexOfJobTitle);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final String _tmpReferenceNumber;
            if (_cursor.isNull(_cursorIndexOfReferenceNumber)) {
              _tmpReferenceNumber = null;
            } else {
              _tmpReferenceNumber = _cursor.getString(_cursorIndexOfReferenceNumber);
            }
            final String _tmpTypedNotes;
            if (_cursor.isNull(_cursorIndexOfTypedNotes)) {
              _tmpTypedNotes = null;
            } else {
              _tmpTypedNotes = _cursor.getString(_cursorIndexOfTypedNotes);
            }
            final String _tmpAudioLocalUri;
            if (_cursor.isNull(_cursorIndexOfAudioLocalUri)) {
              _tmpAudioLocalUri = null;
            } else {
              _tmpAudioLocalUri = _cursor.getString(_cursorIndexOfAudioLocalUri);
            }
            final String _tmpAudioStoragePath;
            if (_cursor.isNull(_cursorIndexOfAudioStoragePath)) {
              _tmpAudioStoragePath = null;
            } else {
              _tmpAudioStoragePath = _cursor.getString(_cursorIndexOfAudioStoragePath);
            }
            final String _tmpRawTranscript;
            if (_cursor.isNull(_cursorIndexOfRawTranscript)) {
              _tmpRawTranscript = null;
            } else {
              _tmpRawTranscript = _cursor.getString(_cursorIndexOfRawTranscript);
            }
            final String _tmpCustomerSummary;
            if (_cursor.isNull(_cursorIndexOfCustomerSummary)) {
              _tmpCustomerSummary = null;
            } else {
              _tmpCustomerSummary = _cursor.getString(_cursorIndexOfCustomerSummary);
            }
            final String _tmpWorkCompletedJson;
            if (_cursor.isNull(_cursorIndexOfWorkCompletedJson)) {
              _tmpWorkCompletedJson = null;
            } else {
              _tmpWorkCompletedJson = _cursor.getString(_cursorIndexOfWorkCompletedJson);
            }
            final String _tmpFindingsJson;
            if (_cursor.isNull(_cursorIndexOfFindingsJson)) {
              _tmpFindingsJson = null;
            } else {
              _tmpFindingsJson = _cursor.getString(_cursorIndexOfFindingsJson);
            }
            final String _tmpRecommendationsJson;
            if (_cursor.isNull(_cursorIndexOfRecommendationsJson)) {
              _tmpRecommendationsJson = null;
            } else {
              _tmpRecommendationsJson = _cursor.getString(_cursorIndexOfRecommendationsJson);
            }
            final String _tmpPdfLocalPath;
            if (_cursor.isNull(_cursorIndexOfPdfLocalPath)) {
              _tmpPdfLocalPath = null;
            } else {
              _tmpPdfLocalPath = _cursor.getString(_cursorIndexOfPdfLocalPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpApprovedAt;
            if (_cursor.isNull(_cursorIndexOfApprovedAt)) {
              _tmpApprovedAt = null;
            } else {
              _tmpApprovedAt = _cursor.getLong(_cursorIndexOfApprovedAt);
            }
            _item = new ReportEntity(_tmpId,_tmpUserId,_tmpStatus,_tmpCustomerName,_tmpJobTitle,_tmpAddress,_tmpReferenceNumber,_tmpTypedNotes,_tmpAudioLocalUri,_tmpAudioStoragePath,_tmpRawTranscript,_tmpCustomerSummary,_tmpWorkCompletedJson,_tmpFindingsJson,_tmpRecommendationsJson,_tmpPdfLocalPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpApprovedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getReportById(final String reportId,
      final Continuation<? super ReportEntity> $completion) {
    final String _sql = "SELECT * FROM reports WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (reportId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, reportId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReportEntity>() {
      @Override
      @Nullable
      public ReportEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfJobTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "jobTitle");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfReferenceNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "referenceNumber");
          final int _cursorIndexOfTypedNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "typedNotes");
          final int _cursorIndexOfAudioLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "audioLocalUri");
          final int _cursorIndexOfAudioStoragePath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioStoragePath");
          final int _cursorIndexOfRawTranscript = CursorUtil.getColumnIndexOrThrow(_cursor, "rawTranscript");
          final int _cursorIndexOfCustomerSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "customerSummary");
          final int _cursorIndexOfWorkCompletedJson = CursorUtil.getColumnIndexOrThrow(_cursor, "workCompletedJson");
          final int _cursorIndexOfFindingsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "findingsJson");
          final int _cursorIndexOfRecommendationsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "recommendationsJson");
          final int _cursorIndexOfPdfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfLocalPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfApprovedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "approvedAt");
          final ReportEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final ReportStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __converters.toReportStatus(_tmp);
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpJobTitle;
            if (_cursor.isNull(_cursorIndexOfJobTitle)) {
              _tmpJobTitle = null;
            } else {
              _tmpJobTitle = _cursor.getString(_cursorIndexOfJobTitle);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final String _tmpReferenceNumber;
            if (_cursor.isNull(_cursorIndexOfReferenceNumber)) {
              _tmpReferenceNumber = null;
            } else {
              _tmpReferenceNumber = _cursor.getString(_cursorIndexOfReferenceNumber);
            }
            final String _tmpTypedNotes;
            if (_cursor.isNull(_cursorIndexOfTypedNotes)) {
              _tmpTypedNotes = null;
            } else {
              _tmpTypedNotes = _cursor.getString(_cursorIndexOfTypedNotes);
            }
            final String _tmpAudioLocalUri;
            if (_cursor.isNull(_cursorIndexOfAudioLocalUri)) {
              _tmpAudioLocalUri = null;
            } else {
              _tmpAudioLocalUri = _cursor.getString(_cursorIndexOfAudioLocalUri);
            }
            final String _tmpAudioStoragePath;
            if (_cursor.isNull(_cursorIndexOfAudioStoragePath)) {
              _tmpAudioStoragePath = null;
            } else {
              _tmpAudioStoragePath = _cursor.getString(_cursorIndexOfAudioStoragePath);
            }
            final String _tmpRawTranscript;
            if (_cursor.isNull(_cursorIndexOfRawTranscript)) {
              _tmpRawTranscript = null;
            } else {
              _tmpRawTranscript = _cursor.getString(_cursorIndexOfRawTranscript);
            }
            final String _tmpCustomerSummary;
            if (_cursor.isNull(_cursorIndexOfCustomerSummary)) {
              _tmpCustomerSummary = null;
            } else {
              _tmpCustomerSummary = _cursor.getString(_cursorIndexOfCustomerSummary);
            }
            final String _tmpWorkCompletedJson;
            if (_cursor.isNull(_cursorIndexOfWorkCompletedJson)) {
              _tmpWorkCompletedJson = null;
            } else {
              _tmpWorkCompletedJson = _cursor.getString(_cursorIndexOfWorkCompletedJson);
            }
            final String _tmpFindingsJson;
            if (_cursor.isNull(_cursorIndexOfFindingsJson)) {
              _tmpFindingsJson = null;
            } else {
              _tmpFindingsJson = _cursor.getString(_cursorIndexOfFindingsJson);
            }
            final String _tmpRecommendationsJson;
            if (_cursor.isNull(_cursorIndexOfRecommendationsJson)) {
              _tmpRecommendationsJson = null;
            } else {
              _tmpRecommendationsJson = _cursor.getString(_cursorIndexOfRecommendationsJson);
            }
            final String _tmpPdfLocalPath;
            if (_cursor.isNull(_cursorIndexOfPdfLocalPath)) {
              _tmpPdfLocalPath = null;
            } else {
              _tmpPdfLocalPath = _cursor.getString(_cursorIndexOfPdfLocalPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpApprovedAt;
            if (_cursor.isNull(_cursorIndexOfApprovedAt)) {
              _tmpApprovedAt = null;
            } else {
              _tmpApprovedAt = _cursor.getLong(_cursorIndexOfApprovedAt);
            }
            _result = new ReportEntity(_tmpId,_tmpUserId,_tmpStatus,_tmpCustomerName,_tmpJobTitle,_tmpAddress,_tmpReferenceNumber,_tmpTypedNotes,_tmpAudioLocalUri,_tmpAudioStoragePath,_tmpRawTranscript,_tmpCustomerSummary,_tmpWorkCompletedJson,_tmpFindingsJson,_tmpRecommendationsJson,_tmpPdfLocalPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpApprovedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<ReportEntity> observeReportById(final String reportId) {
    final String _sql = "SELECT * FROM reports WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (reportId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, reportId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reports"}, new Callable<ReportEntity>() {
      @Override
      @Nullable
      public ReportEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfJobTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "jobTitle");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfReferenceNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "referenceNumber");
          final int _cursorIndexOfTypedNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "typedNotes");
          final int _cursorIndexOfAudioLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "audioLocalUri");
          final int _cursorIndexOfAudioStoragePath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioStoragePath");
          final int _cursorIndexOfRawTranscript = CursorUtil.getColumnIndexOrThrow(_cursor, "rawTranscript");
          final int _cursorIndexOfCustomerSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "customerSummary");
          final int _cursorIndexOfWorkCompletedJson = CursorUtil.getColumnIndexOrThrow(_cursor, "workCompletedJson");
          final int _cursorIndexOfFindingsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "findingsJson");
          final int _cursorIndexOfRecommendationsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "recommendationsJson");
          final int _cursorIndexOfPdfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfLocalPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfApprovedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "approvedAt");
          final ReportEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final ReportStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = __converters.toReportStatus(_tmp);
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpJobTitle;
            if (_cursor.isNull(_cursorIndexOfJobTitle)) {
              _tmpJobTitle = null;
            } else {
              _tmpJobTitle = _cursor.getString(_cursorIndexOfJobTitle);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final String _tmpReferenceNumber;
            if (_cursor.isNull(_cursorIndexOfReferenceNumber)) {
              _tmpReferenceNumber = null;
            } else {
              _tmpReferenceNumber = _cursor.getString(_cursorIndexOfReferenceNumber);
            }
            final String _tmpTypedNotes;
            if (_cursor.isNull(_cursorIndexOfTypedNotes)) {
              _tmpTypedNotes = null;
            } else {
              _tmpTypedNotes = _cursor.getString(_cursorIndexOfTypedNotes);
            }
            final String _tmpAudioLocalUri;
            if (_cursor.isNull(_cursorIndexOfAudioLocalUri)) {
              _tmpAudioLocalUri = null;
            } else {
              _tmpAudioLocalUri = _cursor.getString(_cursorIndexOfAudioLocalUri);
            }
            final String _tmpAudioStoragePath;
            if (_cursor.isNull(_cursorIndexOfAudioStoragePath)) {
              _tmpAudioStoragePath = null;
            } else {
              _tmpAudioStoragePath = _cursor.getString(_cursorIndexOfAudioStoragePath);
            }
            final String _tmpRawTranscript;
            if (_cursor.isNull(_cursorIndexOfRawTranscript)) {
              _tmpRawTranscript = null;
            } else {
              _tmpRawTranscript = _cursor.getString(_cursorIndexOfRawTranscript);
            }
            final String _tmpCustomerSummary;
            if (_cursor.isNull(_cursorIndexOfCustomerSummary)) {
              _tmpCustomerSummary = null;
            } else {
              _tmpCustomerSummary = _cursor.getString(_cursorIndexOfCustomerSummary);
            }
            final String _tmpWorkCompletedJson;
            if (_cursor.isNull(_cursorIndexOfWorkCompletedJson)) {
              _tmpWorkCompletedJson = null;
            } else {
              _tmpWorkCompletedJson = _cursor.getString(_cursorIndexOfWorkCompletedJson);
            }
            final String _tmpFindingsJson;
            if (_cursor.isNull(_cursorIndexOfFindingsJson)) {
              _tmpFindingsJson = null;
            } else {
              _tmpFindingsJson = _cursor.getString(_cursorIndexOfFindingsJson);
            }
            final String _tmpRecommendationsJson;
            if (_cursor.isNull(_cursorIndexOfRecommendationsJson)) {
              _tmpRecommendationsJson = null;
            } else {
              _tmpRecommendationsJson = _cursor.getString(_cursorIndexOfRecommendationsJson);
            }
            final String _tmpPdfLocalPath;
            if (_cursor.isNull(_cursorIndexOfPdfLocalPath)) {
              _tmpPdfLocalPath = null;
            } else {
              _tmpPdfLocalPath = _cursor.getString(_cursorIndexOfPdfLocalPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final Long _tmpApprovedAt;
            if (_cursor.isNull(_cursorIndexOfApprovedAt)) {
              _tmpApprovedAt = null;
            } else {
              _tmpApprovedAt = _cursor.getLong(_cursorIndexOfApprovedAt);
            }
            _result = new ReportEntity(_tmpId,_tmpUserId,_tmpStatus,_tmpCustomerName,_tmpJobTitle,_tmpAddress,_tmpReferenceNumber,_tmpTypedNotes,_tmpAudioLocalUri,_tmpAudioStoragePath,_tmpRawTranscript,_tmpCustomerSummary,_tmpWorkCompletedJson,_tmpFindingsJson,_tmpRecommendationsJson,_tmpPdfLocalPath,_tmpCreatedAt,_tmpUpdatedAt,_tmpApprovedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MediaItemEntity>> getMediaForReport(final String reportId) {
    final String _sql = "SELECT * FROM media_items WHERE reportId = ? ORDER BY sortOrder ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (reportId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, reportId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"media_items"}, new Callable<List<MediaItemEntity>>() {
      @Override
      @NonNull
      public List<MediaItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfReportId = CursorUtil.getColumnIndexOrThrow(_cursor, "reportId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "localUri");
          final int _cursorIndexOfStoragePath = CursorUtil.getColumnIndexOrThrow(_cursor, "storagePath");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final int _cursorIndexOfIsUploaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isUploaded");
          final List<MediaItemEntity> _result = new ArrayList<MediaItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MediaItemEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpReportId;
            if (_cursor.isNull(_cursorIndexOfReportId)) {
              _tmpReportId = null;
            } else {
              _tmpReportId = _cursor.getString(_cursorIndexOfReportId);
            }
            final MediaType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toMediaType(_tmp);
            final PhotoLabel _tmpLabel;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfLabel);
            }
            _tmpLabel = __converters.toPhotoLabel(_tmp_1);
            final String _tmpLocalUri;
            if (_cursor.isNull(_cursorIndexOfLocalUri)) {
              _tmpLocalUri = null;
            } else {
              _tmpLocalUri = _cursor.getString(_cursorIndexOfLocalUri);
            }
            final String _tmpStoragePath;
            if (_cursor.isNull(_cursorIndexOfStoragePath)) {
              _tmpStoragePath = null;
            } else {
              _tmpStoragePath = _cursor.getString(_cursorIndexOfStoragePath);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            final boolean _tmpIsUploaded;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsUploaded);
            _tmpIsUploaded = _tmp_2 != 0;
            _item = new MediaItemEntity(_tmpId,_tmpReportId,_tmpType,_tmpLabel,_tmpLocalUri,_tmpStoragePath,_tmpSortOrder,_tmpIsUploaded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMediaItemsForReport(final String reportId,
      final Continuation<? super List<MediaItemEntity>> $completion) {
    final String _sql = "SELECT * FROM media_items WHERE reportId = ? ORDER BY sortOrder ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (reportId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, reportId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MediaItemEntity>>() {
      @Override
      @NonNull
      public List<MediaItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfReportId = CursorUtil.getColumnIndexOrThrow(_cursor, "reportId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "localUri");
          final int _cursorIndexOfStoragePath = CursorUtil.getColumnIndexOrThrow(_cursor, "storagePath");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final int _cursorIndexOfIsUploaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isUploaded");
          final List<MediaItemEntity> _result = new ArrayList<MediaItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MediaItemEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpReportId;
            if (_cursor.isNull(_cursorIndexOfReportId)) {
              _tmpReportId = null;
            } else {
              _tmpReportId = _cursor.getString(_cursorIndexOfReportId);
            }
            final MediaType _tmpType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfType);
            }
            _tmpType = __converters.toMediaType(_tmp);
            final PhotoLabel _tmpLabel;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfLabel);
            }
            _tmpLabel = __converters.toPhotoLabel(_tmp_1);
            final String _tmpLocalUri;
            if (_cursor.isNull(_cursorIndexOfLocalUri)) {
              _tmpLocalUri = null;
            } else {
              _tmpLocalUri = _cursor.getString(_cursorIndexOfLocalUri);
            }
            final String _tmpStoragePath;
            if (_cursor.isNull(_cursorIndexOfStoragePath)) {
              _tmpStoragePath = null;
            } else {
              _tmpStoragePath = _cursor.getString(_cursorIndexOfStoragePath);
            }
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            final boolean _tmpIsUploaded;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsUploaded);
            _tmpIsUploaded = _tmp_2 != 0;
            _item = new MediaItemEntity(_tmpId,_tmpReportId,_tmpType,_tmpLabel,_tmpLocalUri,_tmpStoragePath,_tmpSortOrder,_tmpIsUploaded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
