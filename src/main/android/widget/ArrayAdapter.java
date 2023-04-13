package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes4.dex */
public class ArrayAdapter<T> extends BaseAdapter implements Filterable, ThemedSpinnerAdapter {
    private final Context mContext;
    private LayoutInflater mDropDownInflater;
    private int mDropDownResource;
    private int mFieldId;
    private ArrayAdapter<T>.ArrayFilter mFilter;
    private final LayoutInflater mInflater;
    private final Object mLock;
    private boolean mNotifyOnChange;
    private List<T> mObjects;
    private boolean mObjectsFromResources;
    private ArrayList<T> mOriginalValues;
    private final int mResource;

    public ArrayAdapter(Context context, int resource) {
        this(context, resource, 0, new ArrayList());
    }

    public ArrayAdapter(Context context, int resource, int textViewResourceId) {
        this(context, resource, textViewResourceId, new ArrayList());
    }

    public ArrayAdapter(Context context, int resource, T[] objects) {
        this(context, resource, 0, Arrays.asList(objects));
    }

    public ArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        this(context, resource, textViewResourceId, Arrays.asList(objects));
    }

    public ArrayAdapter(Context context, int resource, List<T> objects) {
        this(context, resource, 0, objects);
    }

    public ArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        this(context, resource, textViewResourceId, objects, false);
    }

    private ArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects, boolean objsFromResources) {
        this.mLock = new Object();
        this.mFieldId = 0;
        this.mNotifyOnChange = true;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDropDownResource = resource;
        this.mResource = resource;
        this.mObjects = objects;
        this.mObjectsFromResources = objsFromResources;
        this.mFieldId = textViewResourceId;
    }

    public void add(T object) {
        synchronized (this.mLock) {
            ArrayList<T> arrayList = this.mOriginalValues;
            if (arrayList != null) {
                arrayList.add(object);
            } else {
                this.mObjects.add(object);
            }
            this.mObjectsFromResources = false;
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void addAll(Collection<? extends T> collection) {
        synchronized (this.mLock) {
            ArrayList<T> arrayList = this.mOriginalValues;
            if (arrayList != null) {
                arrayList.addAll(collection);
            } else {
                this.mObjects.addAll(collection);
            }
            this.mObjectsFromResources = false;
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void addAll(T... items) {
        synchronized (this.mLock) {
            ArrayList<T> arrayList = this.mOriginalValues;
            if (arrayList != null) {
                Collections.addAll(arrayList, items);
            } else {
                Collections.addAll(this.mObjects, items);
            }
            this.mObjectsFromResources = false;
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void insert(T object, int index) {
        synchronized (this.mLock) {
            ArrayList<T> arrayList = this.mOriginalValues;
            if (arrayList != null) {
                arrayList.add(index, object);
            } else {
                this.mObjects.add(index, object);
            }
            this.mObjectsFromResources = false;
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void remove(T object) {
        synchronized (this.mLock) {
            ArrayList<T> arrayList = this.mOriginalValues;
            if (arrayList != null) {
                arrayList.remove(object);
            } else {
                this.mObjects.remove(object);
            }
            this.mObjectsFromResources = false;
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void clear() {
        synchronized (this.mLock) {
            ArrayList<T> arrayList = this.mOriginalValues;
            if (arrayList != null) {
                arrayList.clear();
            } else {
                this.mObjects.clear();
            }
            this.mObjectsFromResources = false;
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void sort(Comparator<? super T> comparator) {
        synchronized (this.mLock) {
            ArrayList<T> arrayList = this.mOriginalValues;
            if (arrayList != null) {
                Collections.sort(arrayList, comparator);
            } else {
                Collections.sort(this.mObjects, comparator);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        this.mNotifyOnChange = notifyOnChange;
    }

    public Context getContext() {
        return this.mContext;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mObjects.size();
    }

    @Override // android.widget.Adapter
    public T getItem(int position) {
        return this.mObjects.get(position);
    }

    public int getPosition(T item) {
        return this.mObjects.indexOf(item);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(this.mInflater, position, convertView, parent, this.mResource);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView, ViewGroup parent, int resource) {
        View view;
        TextView text;
        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }
        try {
            int i = this.mFieldId;
            if (i == 0) {
                text = (TextView) view;
            } else {
                text = (TextView) view.findViewById(i);
                if (text == null) {
                    throw new RuntimeException("Failed to find view with ID " + this.mContext.getResources().getResourceName(this.mFieldId) + " in item layout");
                }
            }
            T item = getItem(position);
            if (item instanceof CharSequence) {
                text.setText((CharSequence) item);
            } else {
                text.setText(item.toString());
            }
            return view;
        } catch (ClassCastException e) {
            Log.m110e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
        }
    }

    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override // android.widget.ThemedSpinnerAdapter
    public void setDropDownViewTheme(Resources.Theme theme) {
        if (theme == null) {
            this.mDropDownInflater = null;
        } else if (theme == this.mInflater.getContext().getTheme()) {
            this.mDropDownInflater = this.mInflater;
        } else {
            Context context = new ContextThemeWrapper(this.mContext, theme);
            this.mDropDownInflater = LayoutInflater.from(context);
        }
    }

    @Override // android.widget.ThemedSpinnerAdapter
    public Resources.Theme getDropDownViewTheme() {
        LayoutInflater layoutInflater = this.mDropDownInflater;
        if (layoutInflater == null) {
            return null;
        }
        return layoutInflater.getContext().getTheme();
    }

    @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = this.mDropDownInflater;
        if (layoutInflater == null) {
            layoutInflater = this.mInflater;
        }
        LayoutInflater inflater = layoutInflater;
        return createViewFromResource(inflater, position, convertView, parent, this.mDropDownResource);
    }

    public static ArrayAdapter<CharSequence> createFromResource(Context context, int textArrayResId, int textViewResId) {
        CharSequence[] strings = context.getResources().getTextArray(textArrayResId);
        return new ArrayAdapter<>(context, textViewResId, 0, Arrays.asList(strings), true);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new ArrayFilter();
        }
        return this.mFilter;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public CharSequence[] getAutofillOptions() {
        List<T> list;
        CharSequence[] explicitOptions = super.getAutofillOptions();
        if (explicitOptions != null) {
            return explicitOptions;
        }
        if (!this.mObjectsFromResources || (list = this.mObjects) == null || list.isEmpty()) {
            return null;
        }
        int size = this.mObjects.size();
        CharSequence[] options = new CharSequence[size];
        this.mObjects.toArray(options);
        return options;
    }

    /* loaded from: classes4.dex */
    private class ArrayFilter extends Filter {
        private ArrayFilter() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence prefix) {
            ArrayList<T> list;
            ArrayList<T> values;
            Filter.FilterResults results = new Filter.FilterResults();
            if (ArrayAdapter.this.mOriginalValues == null) {
                synchronized (ArrayAdapter.this.mLock) {
                    ArrayAdapter.this.mOriginalValues = new ArrayList(ArrayAdapter.this.mObjects);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (ArrayAdapter.this.mLock) {
                    list = new ArrayList<>(ArrayAdapter.this.mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                synchronized (ArrayAdapter.this.mLock) {
                    values = new ArrayList<>(ArrayAdapter.this.mOriginalValues);
                }
                int count = values.size();
                ArrayList<T> newValues = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    T value = values.get(i);
                    String valueText = value.toString().toLowerCase();
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        String[] words = valueText.split(" ");
                        int length = words.length;
                        int i2 = 0;
                        while (true) {
                            if (i2 < length) {
                                String word = words[i2];
                                if (!word.startsWith(prefixString)) {
                                    i2++;
                                } else {
                                    newValues.add(value);
                                    break;
                                }
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
            ArrayAdapter.this.mObjects = (List) results.values;
            if (results.count > 0) {
                ArrayAdapter.this.notifyDataSetChanged();
            } else {
                ArrayAdapter.this.notifyDataSetInvalidated();
            }
        }
    }
}
