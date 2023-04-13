package android.widget;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioSystem;
import android.net.Uri;
import android.p008os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import com.android.internal.C4057R;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.WeakHashMap;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class SuggestionsAdapter extends ResourceCursorAdapter implements View.OnClickListener {
    private static final boolean DBG = false;
    private static final long DELETE_KEY_POST_DELAY = 500;
    static final int INVALID_INDEX = -1;
    private static final String LOG_TAG = "SuggestionsAdapter";
    private static final int QUERY_LIMIT = 50;
    static final int REFINE_ALL = 2;
    static final int REFINE_BY_ENTRY = 1;
    static final int REFINE_NONE = 0;
    private boolean mClosed;
    private final int mCommitIconResId;
    private int mFlagsCol;
    private int mIconName1Col;
    private int mIconName2Col;
    private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
    private final Context mProviderContext;
    private int mQueryRefinement;
    private final SearchManager mSearchManager;
    private final SearchView mSearchView;
    private final SearchableInfo mSearchable;
    private int mText1Col;
    private int mText2Col;
    private int mText2UrlCol;
    private ColorStateList mUrlColor;

    public SuggestionsAdapter(Context context, SearchView searchView, SearchableInfo searchable, WeakHashMap<String, Drawable.ConstantState> outsideDrawablesCache) {
        super(context, searchView.getSuggestionRowLayout(), (Cursor) null, true);
        this.mClosed = false;
        this.mQueryRefinement = 1;
        this.mText1Col = -1;
        this.mText2Col = -1;
        this.mText2UrlCol = -1;
        this.mIconName1Col = -1;
        this.mIconName2Col = -1;
        this.mFlagsCol = -1;
        this.mSearchManager = (SearchManager) this.mContext.getSystemService("search");
        this.mSearchView = searchView;
        this.mSearchable = searchable;
        this.mCommitIconResId = searchView.getSuggestionCommitIconResId();
        Context activityContext = searchable.getActivityContext(this.mContext);
        this.mProviderContext = searchable.getProviderContext(this.mContext, activityContext);
        this.mOutsideDrawablesCache = outsideDrawablesCache;
        getFilter().setDelayer(new Filter.Delayer() { // from class: android.widget.SuggestionsAdapter.1
            private int mPreviousLength = 0;

            @Override // android.widget.Filter.Delayer
            public long getPostingDelay(CharSequence constraint) {
                if (constraint == null) {
                    return 0L;
                }
                long delay = constraint.length() < this.mPreviousLength ? SuggestionsAdapter.DELETE_KEY_POST_DELAY : 0L;
                this.mPreviousLength = constraint.length();
                return delay;
            }
        });
    }

    public void setQueryRefinement(int refineWhat) {
        this.mQueryRefinement = refineWhat;
    }

    public int getQueryRefinement() {
        return this.mQueryRefinement;
    }

    @Override // android.widget.CursorAdapter, android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.CursorAdapter, android.widget.CursorFilter.CursorFilterClient
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String query = constraint == null ? "" : constraint.toString();
        if (this.mSearchView.getVisibility() == 0 && this.mSearchView.getWindowVisibility() == 0) {
            try {
                Cursor cursor = this.mSearchManager.getSuggestions(this.mSearchable, query, 50);
                if (cursor != null) {
                    cursor.getCount();
                    return cursor;
                }
            } catch (RuntimeException e) {
                Log.m103w(LOG_TAG, "Search suggestions query threw an exception.", e);
            }
            return null;
        }
        return null;
    }

    public void close() {
        changeCursor(null);
        this.mClosed = true;
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        updateSpinnerState(getCursor());
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        updateSpinnerState(getCursor());
    }

    private void updateSpinnerState(Cursor cursor) {
        Bundle extras = cursor != null ? cursor.getExtras() : null;
        if (extras != null) {
            extras.getBoolean(SearchManager.CURSOR_EXTRA_KEY_IN_PROGRESS);
        }
    }

    @Override // android.widget.CursorAdapter, android.widget.CursorFilter.CursorFilterClient
    public void changeCursor(Cursor c) {
        if (this.mClosed) {
            Log.m104w(LOG_TAG, "Tried to change cursor after adapter was closed.");
            if (c != null) {
                c.close();
                return;
            }
            return;
        }
        try {
            super.changeCursor(c);
            if (c != null) {
                this.mText1Col = c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
                this.mText2Col = c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2);
                this.mText2UrlCol = c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2_URL);
                this.mIconName1Col = c.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1);
                this.mIconName2Col = c.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_2);
                this.mFlagsCol = c.getColumnIndex(SearchManager.SUGGEST_COLUMN_FLAGS);
            }
        } catch (Exception e) {
            Log.m109e(LOG_TAG, "error changing cursor and caching columns", e);
        }
    }

    @Override // android.widget.ResourceCursorAdapter, android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = super.newView(context, cursor, parent);
        v.setTag(new ChildViewCache(v));
        ImageView iconRefine = (ImageView) v.findViewById(C4057R.C4059id.edit_query);
        iconRefine.setImageResource(this.mCommitIconResId);
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class ChildViewCache {
        public final ImageView mIcon1;
        public final ImageView mIcon2;
        public final ImageView mIconRefine;
        public final TextView mText1;
        public final TextView mText2;

        public ChildViewCache(View v) {
            this.mText1 = (TextView) v.findViewById(16908308);
            this.mText2 = (TextView) v.findViewById(16908309);
            this.mIcon1 = (ImageView) v.findViewById(16908295);
            this.mIcon2 = (ImageView) v.findViewById(16908296);
            this.mIconRefine = (ImageView) v.findViewById(C4057R.C4059id.edit_query);
        }
    }

    @Override // android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        CharSequence text2;
        ChildViewCache views = (ChildViewCache) view.getTag();
        int flags = 0;
        int i = this.mFlagsCol;
        if (i != -1) {
            flags = cursor.getInt(i);
        }
        if (views.mText1 != null) {
            String text1 = getStringOrNull(cursor, this.mText1Col);
            setViewText(views.mText1, text1);
        }
        if (views.mText2 != null) {
            CharSequence text22 = getStringOrNull(cursor, this.mText2UrlCol);
            if (text22 != null) {
                text2 = formatUrl(context, text22);
            } else {
                text2 = getStringOrNull(cursor, this.mText2Col);
            }
            if (TextUtils.isEmpty(text2)) {
                if (views.mText1 != null) {
                    views.mText1.setSingleLine(false);
                    views.mText1.setMaxLines(2);
                }
            } else if (views.mText1 != null) {
                views.mText1.setSingleLine(true);
                views.mText1.setMaxLines(1);
            }
            setViewText(views.mText2, text2);
        }
        if (views.mIcon1 != null) {
            setViewDrawable(views.mIcon1, getIcon1(cursor), 4);
        }
        if (views.mIcon2 != null) {
            setViewDrawable(views.mIcon2, getIcon2(cursor), 8);
        }
        int i2 = this.mQueryRefinement;
        if (i2 == 2 || (i2 == 1 && (flags & 1) != 0)) {
            views.mIconRefine.setVisibility(0);
            views.mIconRefine.setTag(views.mText1.getText());
            views.mIconRefine.setOnClickListener(this);
            return;
        }
        views.mIconRefine.setVisibility(8);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof CharSequence) {
            this.mSearchView.onQueryRefine((CharSequence) tag);
        }
    }

    private CharSequence formatUrl(Context context, CharSequence url) {
        if (this.mUrlColor == null) {
            TypedValue colorValue = new TypedValue();
            context.getTheme().resolveAttribute(C4057R.attr.textColorSearchUrl, colorValue, true);
            this.mUrlColor = context.getColorStateList(colorValue.resourceId);
        }
        SpannableString text = new SpannableString(url);
        text.setSpan(new TextAppearanceSpan(null, 0, 0, this.mUrlColor, null), 0, url.length(), 33);
        return text;
    }

    private void setViewText(TextView v, CharSequence text) {
        v.setText(text);
        if (TextUtils.isEmpty(text)) {
            v.setVisibility(8);
        } else {
            v.setVisibility(0);
        }
    }

    private Drawable getIcon1(Cursor cursor) {
        int i = this.mIconName1Col;
        if (i == -1) {
            return null;
        }
        String value = cursor.getString(i);
        Drawable drawable = getDrawableFromResourceValue(value);
        if (drawable != null) {
            return drawable;
        }
        return getDefaultIcon1(cursor);
    }

    private Drawable getIcon2(Cursor cursor) {
        int i = this.mIconName2Col;
        if (i == -1) {
            return null;
        }
        String value = cursor.getString(i);
        return getDrawableFromResourceValue(value);
    }

    private void setViewDrawable(ImageView v, Drawable drawable, int nullVisibility) {
        v.setImageDrawable(drawable);
        if (drawable == null) {
            v.setVisibility(nullVisibility);
            return;
        }
        v.setVisibility(0);
        drawable.setVisible(false, false);
        drawable.setVisible(true, false);
    }

    @Override // android.widget.CursorAdapter, android.widget.CursorFilter.CursorFilterClient
    public CharSequence convertToString(Cursor cursor) {
        String text1;
        String data;
        if (cursor == null) {
            return null;
        }
        String query = getColumnString(cursor, SearchManager.SUGGEST_COLUMN_QUERY);
        if (query != null) {
            return query;
        }
        if (this.mSearchable.shouldRewriteQueryFromData() && (data = getColumnString(cursor, SearchManager.SUGGEST_COLUMN_INTENT_DATA)) != null) {
            return data;
        }
        if (!this.mSearchable.shouldRewriteQueryFromText() || (text1 = getColumnString(cursor, SearchManager.SUGGEST_COLUMN_TEXT_1)) == null) {
            return null;
        }
        return text1;
    }

    @Override // android.widget.CursorAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            return super.getView(position, convertView, parent);
        } catch (RuntimeException e) {
            Log.m103w(LOG_TAG, "Search suggestions cursor threw exception.", e);
            View v = newView(this.mContext, this.mCursor, parent);
            if (v != null) {
                ChildViewCache views = (ChildViewCache) v.getTag();
                TextView tv = views.mText1;
                tv.setText(e.toString());
            }
            return v;
        }
    }

    @Override // android.widget.CursorAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        try {
            return super.getDropDownView(position, convertView, parent);
        } catch (RuntimeException e) {
            Log.m103w(LOG_TAG, "Search suggestions cursor threw exception.", e);
            Context context = this.mDropDownContext == null ? this.mContext : this.mDropDownContext;
            View v = newDropDownView(context, this.mCursor, parent);
            if (v != null) {
                ChildViewCache views = (ChildViewCache) v.getTag();
                TextView tv = views.mText1;
                tv.setText(e.toString());
            }
            return v;
        }
    }

    private Drawable getDrawableFromResourceValue(String drawableId) {
        if (drawableId == null || drawableId.length() == 0 || AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS.equals(drawableId)) {
            return null;
        }
        try {
            int resourceId = Integer.parseInt(drawableId);
            String drawableUri = "android.resource://" + this.mProviderContext.getPackageName() + "/" + resourceId;
            Drawable drawable = checkIconCache(drawableUri);
            if (drawable != null) {
                return drawable;
            }
            Drawable drawable2 = this.mProviderContext.getDrawable(resourceId);
            storeInIconCache(drawableUri, drawable2);
            return drawable2;
        } catch (Resources.NotFoundException e) {
            Log.m104w(LOG_TAG, "Icon resource not found: " + drawableId);
            return null;
        } catch (NumberFormatException e2) {
            Drawable drawable3 = checkIconCache(drawableId);
            if (drawable3 != null) {
                return drawable3;
            }
            Uri uri = Uri.parse(drawableId);
            Drawable drawable4 = getDrawable(uri);
            storeInIconCache(drawableId, drawable4);
            return drawable4;
        }
    }

    private Drawable getDrawable(Uri uri) {
        try {
            String scheme = uri.getScheme();
            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
                ContentResolver.OpenResourceIdResult r = this.mProviderContext.getContentResolver().getResourceId(uri);
                try {
                    return r.f42r.getDrawable(r.f41id, this.mProviderContext.getTheme());
                } catch (Resources.NotFoundException e) {
                    throw new FileNotFoundException("Resource does not exist: " + uri);
                }
            }
            InputStream stream = this.mProviderContext.getContentResolver().openInputStream(uri);
            if (stream == null) {
                throw new FileNotFoundException("Failed to open " + uri);
            }
            Drawable createFromStream = Drawable.createFromStream(stream, null);
            try {
                stream.close();
            } catch (IOException ex) {
                Log.m109e(LOG_TAG, "Error closing icon stream for " + uri, ex);
            }
            return createFromStream;
        } catch (FileNotFoundException fnfe) {
            Log.m104w(LOG_TAG, "Icon not found: " + uri + ", " + fnfe.getMessage());
            return null;
        }
        Log.m104w(LOG_TAG, "Icon not found: " + uri + ", " + fnfe.getMessage());
        return null;
    }

    private Drawable checkIconCache(String resourceUri) {
        Drawable.ConstantState cached = this.mOutsideDrawablesCache.get(resourceUri);
        if (cached == null) {
            return null;
        }
        return cached.newDrawable();
    }

    private void storeInIconCache(String resourceUri, Drawable drawable) {
        if (drawable != null) {
            this.mOutsideDrawablesCache.put(resourceUri, drawable.getConstantState());
        }
    }

    private Drawable getDefaultIcon1(Cursor cursor) {
        Drawable drawable = getActivityIconWithCache(this.mSearchable.getSearchActivity());
        if (drawable != null) {
            return drawable;
        }
        return this.mContext.getPackageManager().getDefaultActivityIcon();
    }

    private Drawable getActivityIconWithCache(ComponentName component) {
        String componentIconKey = component.flattenToShortString();
        if (this.mOutsideDrawablesCache.containsKey(componentIconKey)) {
            Drawable.ConstantState cached = this.mOutsideDrawablesCache.get(componentIconKey);
            if (cached == null) {
                return null;
            }
            return cached.newDrawable(this.mProviderContext.getResources());
        }
        Drawable drawable = getActivityIcon(component);
        Drawable.ConstantState toCache = drawable != null ? drawable.getConstantState() : null;
        this.mOutsideDrawablesCache.put(componentIconKey, toCache);
        return drawable;
    }

    private Drawable getActivityIcon(ComponentName component) {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            ActivityInfo activityInfo = pm.getActivityInfo(component, 128);
            int iconId = activityInfo.getIconResource();
            if (iconId == 0) {
                return null;
            }
            String pkg = component.getPackageName();
            Drawable drawable = pm.getDrawable(pkg, iconId, activityInfo.applicationInfo);
            if (drawable == null) {
                Log.m104w(LOG_TAG, "Invalid icon resource " + iconId + " for " + component.flattenToShortString());
                return null;
            }
            return drawable;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.m104w(LOG_TAG, ex.toString());
            return null;
        }
    }

    public static String getColumnString(Cursor cursor, String columnName) {
        int col = cursor.getColumnIndex(columnName);
        return getStringOrNull(cursor, col);
    }

    private static String getStringOrNull(Cursor cursor, int col) {
        if (col == -1) {
            return null;
        }
        try {
            return cursor.getString(col);
        } catch (Exception e) {
            Log.m109e(LOG_TAG, "unexpected error retrieving valid column from cursor, did the remote process die?", e);
            return null;
        }
    }
}
