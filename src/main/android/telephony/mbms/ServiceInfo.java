package android.telephony.mbms;

import android.p008os.Parcel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes3.dex */
public class ServiceInfo {
    static final int MAP_LIMIT = 1000;
    private final String className;
    private final List<Locale> locales;
    private final Map<Locale, String> names;
    private final String serviceId;
    private final Date sessionEndTime;
    private final Date sessionStartTime;

    public ServiceInfo(Map<Locale, String> newNames, String newClassName, List<Locale> newLocales, String newServiceId, Date start, Date end) {
        if (newNames == null || newClassName == null || newLocales == null || newServiceId == null || start == null || end == null) {
            throw new IllegalArgumentException("Bad ServiceInfo construction");
        }
        if (newNames.size() > 1000) {
            throw new RuntimeException("bad map length " + newNames.size());
        }
        if (newLocales.size() > 1000) {
            throw new RuntimeException("bad locales length " + newLocales.size());
        }
        HashMap hashMap = new HashMap(newNames.size());
        this.names = hashMap;
        hashMap.putAll(newNames);
        this.className = newClassName;
        this.locales = new ArrayList(newLocales);
        this.serviceId = newServiceId;
        this.sessionStartTime = (Date) start.clone();
        this.sessionEndTime = (Date) end.clone();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServiceInfo(Parcel in) {
        int mapCount = in.readInt();
        if (mapCount > 1000 || mapCount < 0) {
            throw new RuntimeException("bad map length" + mapCount);
        }
        this.names = new HashMap(mapCount);
        while (true) {
            int mapCount2 = mapCount - 1;
            if (mapCount <= 0) {
                break;
            }
            Locale locale = (Locale) in.readSerializable(Locale.class.getClassLoader(), Locale.class);
            String name = in.readString();
            this.names.put(locale, name);
            mapCount = mapCount2;
        }
        this.className = in.readString();
        int localesCount = in.readInt();
        if (localesCount > 1000 || localesCount < 0) {
            throw new RuntimeException("bad locale length " + localesCount);
        }
        this.locales = new ArrayList(localesCount);
        while (true) {
            int localesCount2 = localesCount - 1;
            if (localesCount > 0) {
                Locale l = (Locale) in.readSerializable(Locale.class.getClassLoader(), Locale.class);
                this.locales.add(l);
                localesCount = localesCount2;
            } else {
                this.serviceId = in.readString();
                this.sessionStartTime = (Date) in.readSerializable(Date.class.getClassLoader(), Date.class);
                this.sessionEndTime = (Date) in.readSerializable(Date.class.getClassLoader(), Date.class);
                return;
            }
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        Set<Locale> keySet = this.names.keySet();
        dest.writeInt(keySet.size());
        for (Locale l : keySet) {
            dest.writeSerializable(l);
            dest.writeString(this.names.get(l));
        }
        dest.writeString(this.className);
        int localesCount = this.locales.size();
        dest.writeInt(localesCount);
        for (Locale l2 : this.locales) {
            dest.writeSerializable(l2);
        }
        dest.writeString(this.serviceId);
        dest.writeSerializable(this.sessionStartTime);
        dest.writeSerializable(this.sessionEndTime);
    }

    public CharSequence getNameForLocale(Locale locale) {
        if (!this.names.containsKey(locale)) {
            throw new NoSuchElementException("Locale not supported");
        }
        return this.names.get(locale);
    }

    public Set<Locale> getNamedContentLocales() {
        return Collections.unmodifiableSet(this.names.keySet());
    }

    public String getServiceClassName() {
        return this.className;
    }

    public List<Locale> getLocales() {
        return this.locales;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public Date getSessionStartTime() {
        return this.sessionStartTime;
    }

    public Date getSessionEndTime() {
        return this.sessionEndTime;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ServiceInfo)) {
            return false;
        }
        ServiceInfo that = (ServiceInfo) o;
        if (Objects.equals(this.names, that.names) && Objects.equals(this.className, that.className) && Objects.equals(this.locales, that.locales) && Objects.equals(this.serviceId, that.serviceId) && Objects.equals(this.sessionStartTime, that.sessionStartTime) && Objects.equals(this.sessionEndTime, that.sessionEndTime)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.names, this.className, this.locales, this.serviceId, this.sessionStartTime, this.sessionEndTime);
    }
}
