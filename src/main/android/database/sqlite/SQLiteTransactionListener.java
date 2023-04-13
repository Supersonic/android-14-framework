package android.database.sqlite;
/* loaded from: classes.dex */
public interface SQLiteTransactionListener {
    void onBegin();

    void onCommit();

    void onRollback();
}
