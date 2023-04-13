package android.database.sqlite;
/* loaded from: classes.dex */
public class SQLiteCantOpenDatabaseException extends SQLiteException {
    public SQLiteCantOpenDatabaseException() {
    }

    public SQLiteCantOpenDatabaseException(String error) {
        super(error);
    }

    public SQLiteCantOpenDatabaseException(String error, Throwable cause) {
        super(error, cause);
    }
}
