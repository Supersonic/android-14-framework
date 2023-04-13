package android.app;

import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ComponentInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.p008os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Deprecated
/* loaded from: classes.dex */
public abstract class LauncherActivity extends ListActivity {
    IconResizer mIconResizer;
    Intent mIntent;
    PackageManager mPackageManager;

    /* loaded from: classes.dex */
    public static class ListItem {
        public String className;
        public Bundle extras;
        public Drawable icon;
        public CharSequence label;
        public String packageName;
        public ResolveInfo resolveInfo;

        ListItem(PackageManager pm, ResolveInfo resolveInfo, IconResizer resizer) {
            this.resolveInfo = resolveInfo;
            this.label = resolveInfo.loadLabel(pm);
            ComponentInfo ci = resolveInfo.activityInfo;
            ci = ci == null ? resolveInfo.serviceInfo : ci;
            if (this.label == null && ci != null) {
                this.label = resolveInfo.activityInfo.name;
            }
            if (resizer != null) {
                this.icon = resizer.createIconThumbnail(resolveInfo.loadIcon(pm));
            }
            this.packageName = ci.applicationInfo.packageName;
            this.className = ci.name;
        }

        public ListItem() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ActivityAdapter extends BaseAdapter implements Filterable {
        private final Object lock = new Object();
        protected List<ListItem> mActivitiesList;
        private Filter mFilter;
        protected final IconResizer mIconResizer;
        protected final LayoutInflater mInflater;
        private ArrayList<ListItem> mOriginalValues;
        private final boolean mShowIcons;

        public ActivityAdapter(IconResizer resizer) {
            this.mIconResizer = resizer;
            this.mInflater = (LayoutInflater) LauncherActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mShowIcons = LauncherActivity.this.onEvaluateShowIcons();
            this.mActivitiesList = LauncherActivity.this.makeListItems();
        }

        public Intent intentForPosition(int position) {
            if (this.mActivitiesList == null) {
                return null;
            }
            Intent intent = new Intent(LauncherActivity.this.mIntent);
            ListItem item = this.mActivitiesList.get(position);
            intent.setClassName(item.packageName, item.className);
            if (item.extras != null) {
                intent.putExtras(item.extras);
            }
            return intent;
        }

        public ListItem itemForPosition(int position) {
            List<ListItem> list = this.mActivitiesList;
            if (list == null) {
                return null;
            }
            return list.get(position);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            List<ListItem> list = this.mActivitiesList;
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = this.mInflater.inflate(C4057R.layout.activity_list_item_2, parent, false);
            } else {
                view = convertView;
            }
            bindView(view, this.mActivitiesList.get(position));
            return view;
        }

        private void bindView(View view, ListItem item) {
            TextView text = (TextView) view;
            text.setText(item.label);
            if (this.mShowIcons) {
                if (item.icon == null) {
                    item.icon = this.mIconResizer.createIconThumbnail(item.resolveInfo.loadIcon(LauncherActivity.this.getPackageManager()));
                }
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(item.icon, (Drawable) null, (Drawable) null, (Drawable) null);
            }
        }

        @Override // android.widget.Filterable
        public Filter getFilter() {
            if (this.mFilter == null) {
                this.mFilter = new ArrayFilter();
            }
            return this.mFilter;
        }

        /* loaded from: classes.dex */
        private class ArrayFilter extends Filter {
            private ArrayFilter() {
            }

            @Override // android.widget.Filter
            protected Filter.FilterResults performFiltering(CharSequence prefix) {
                Filter.FilterResults results = new Filter.FilterResults();
                if (ActivityAdapter.this.mOriginalValues == null) {
                    synchronized (ActivityAdapter.this.lock) {
                        ActivityAdapter.this.mOriginalValues = new ArrayList(ActivityAdapter.this.mActivitiesList);
                    }
                }
                if (prefix == null || prefix.length() == 0) {
                    synchronized (ActivityAdapter.this.lock) {
                        ArrayList<ListItem> list = new ArrayList<>(ActivityAdapter.this.mOriginalValues);
                        results.values = list;
                        results.count = list.size();
                    }
                } else {
                    String prefixString = prefix.toString().toLowerCase();
                    ArrayList<ListItem> values = ActivityAdapter.this.mOriginalValues;
                    int count = values.size();
                    ArrayList<ListItem> newValues = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        ListItem item = values.get(i);
                        String[] words = item.label.toString().toLowerCase().split(" ");
                        int wordCount = words.length;
                        int k = 0;
                        while (true) {
                            if (k < wordCount) {
                                String word = words[k];
                                if (!word.startsWith(prefixString)) {
                                    k++;
                                } else {
                                    newValues.add(item);
                                    break;
                                }
                            }
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;
            }

            @Override // android.widget.Filter
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                ActivityAdapter.this.mActivitiesList = (List) results.values;
                if (results.count > 0) {
                    ActivityAdapter.this.notifyDataSetChanged();
                } else {
                    ActivityAdapter.this.notifyDataSetInvalidated();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class IconResizer {
        private Canvas mCanvas;
        private int mIconHeight;
        private int mIconWidth;
        private final Rect mOldBounds = new Rect();

        public IconResizer() {
            this.mIconWidth = -1;
            this.mIconHeight = -1;
            Canvas canvas = new Canvas();
            this.mCanvas = canvas;
            canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
            Resources resources = LauncherActivity.this.getResources();
            int dimension = (int) resources.getDimension(17104896);
            this.mIconHeight = dimension;
            this.mIconWidth = dimension;
        }

        public Drawable createIconThumbnail(Drawable icon) {
            int width = this.mIconWidth;
            int height = this.mIconHeight;
            int iconWidth = icon.getIntrinsicWidth();
            int iconHeight = icon.getIntrinsicHeight();
            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            }
            if (width <= 0 || height <= 0) {
                return icon;
            }
            if (width < iconWidth || height < iconHeight) {
                float ratio = iconWidth / iconHeight;
                if (iconWidth > iconHeight) {
                    height = (int) (width / ratio);
                } else if (iconHeight > iconWidth) {
                    width = (int) (height * ratio);
                }
                Bitmap.Config c = icon.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                Bitmap thumb = Bitmap.createBitmap(this.mIconWidth, this.mIconHeight, c);
                Canvas canvas = this.mCanvas;
                canvas.setBitmap(thumb);
                this.mOldBounds.set(icon.getBounds());
                int x = (this.mIconWidth - width) / 2;
                int y = (this.mIconHeight - height) / 2;
                icon.setBounds(x, y, x + width, y + height);
                icon.draw(canvas);
                icon.setBounds(this.mOldBounds);
                Drawable icon2 = new BitmapDrawable(LauncherActivity.this.getResources(), thumb);
                canvas.setBitmap(null);
                return icon2;
            } else if (iconWidth < width && iconHeight < height) {
                Bitmap.Config c2 = Bitmap.Config.ARGB_8888;
                Bitmap thumb2 = Bitmap.createBitmap(this.mIconWidth, this.mIconHeight, c2);
                Canvas canvas2 = this.mCanvas;
                canvas2.setBitmap(thumb2);
                this.mOldBounds.set(icon.getBounds());
                int x2 = (width - iconWidth) / 2;
                int y2 = (height - iconHeight) / 2;
                icon.setBounds(x2, y2, x2 + iconWidth, y2 + iconHeight);
                icon.draw(canvas2);
                icon.setBounds(this.mOldBounds);
                Drawable icon3 = new BitmapDrawable(LauncherActivity.this.getResources(), thumb2);
                canvas2.setBitmap(null);
                return icon3;
            } else {
                return icon;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        PackageManager packageManager = getPackageManager();
        this.mPackageManager = packageManager;
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)) {
            requestWindowFeature(5);
            setProgressBarIndeterminateVisibility(true);
        }
        onSetContentView();
        this.mIconResizer = new IconResizer();
        Intent intent = new Intent(getTargetIntent());
        this.mIntent = intent;
        intent.setComponent(null);
        this.mAdapter = new ActivityAdapter(this.mIconResizer);
        setListAdapter(this.mAdapter);
        getListView().setTextFilterEnabled(true);
        updateAlertTitle();
        updateButtonText();
        if (!this.mPackageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)) {
            setProgressBarIndeterminateVisibility(false);
        }
    }

    private void updateAlertTitle() {
        TextView alertTitle = (TextView) findViewById(C4057R.C4059id.alertTitle);
        if (alertTitle != null) {
            alertTitle.setText(getTitle());
        }
    }

    private void updateButtonText() {
        Button cancelButton = (Button) findViewById(16908313);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() { // from class: android.app.LauncherActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    LauncherActivity.this.finish();
                }
            });
        }
    }

    @Override // android.app.Activity
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        updateAlertTitle();
    }

    @Override // android.app.Activity
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        updateAlertTitle();
    }

    protected void onSetContentView() {
        setContentView(C4057R.layout.activity_list);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.ListActivity
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = intentForPosition(position);
        startActivity(intent);
    }

    protected Intent intentForPosition(int position) {
        ActivityAdapter adapter = (ActivityAdapter) this.mAdapter;
        return adapter.intentForPosition(position);
    }

    protected ListItem itemForPosition(int position) {
        ActivityAdapter adapter = (ActivityAdapter) this.mAdapter;
        return adapter.itemForPosition(position);
    }

    protected Intent getTargetIntent() {
        return new Intent();
    }

    protected List<ResolveInfo> onQueryPackageManager(Intent queryIntent) {
        return this.mPackageManager.queryIntentActivities(queryIntent, 0);
    }

    protected void onSortResultList(List<ResolveInfo> results) {
        Collections.sort(results, new ResolveInfo.DisplayNameComparator(this.mPackageManager));
    }

    public List<ListItem> makeListItems() {
        List<ResolveInfo> list = onQueryPackageManager(this.mIntent);
        onSortResultList(list);
        ArrayList<ListItem> result = new ArrayList<>(list.size());
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            ResolveInfo resolveInfo = list.get(i);
            result.add(new ListItem(this.mPackageManager, resolveInfo, null));
        }
        return result;
    }

    protected boolean onEvaluateShowIcons() {
        return true;
    }
}
