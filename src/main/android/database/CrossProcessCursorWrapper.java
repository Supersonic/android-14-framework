package android.database;
/* loaded from: classes.dex */
public class CrossProcessCursorWrapper extends CursorWrapper implements CrossProcessCursor {
    public CrossProcessCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    @Override // android.database.CrossProcessCursor
    public void fillWindow(int position, CursorWindow window) {
        if (this.mCursor instanceof CrossProcessCursor) {
            CrossProcessCursor crossProcessCursor = (CrossProcessCursor) this.mCursor;
            crossProcessCursor.fillWindow(position, window);
            return;
        }
        DatabaseUtils.cursorFillWindow(this.mCursor, position, window);
    }

    @Override // android.database.CrossProcessCursor
    public CursorWindow getWindow() {
        if (this.mCursor instanceof CrossProcessCursor) {
            CrossProcessCursor crossProcessCursor = (CrossProcessCursor) this.mCursor;
            return crossProcessCursor.getWindow();
        }
        return null;
    }

    @Override // android.database.CrossProcessCursor
    public boolean onMove(int oldPosition, int newPosition) {
        if (this.mCursor instanceof CrossProcessCursor) {
            CrossProcessCursor crossProcessCursor = (CrossProcessCursor) this.mCursor;
            return crossProcessCursor.onMove(oldPosition, newPosition);
        }
        return true;
    }
}
