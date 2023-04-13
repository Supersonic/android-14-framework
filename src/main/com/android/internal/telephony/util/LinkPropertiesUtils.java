package com.android.internal.telephony.util;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.text.TextUtils;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
/* loaded from: classes.dex */
public final class LinkPropertiesUtils {

    /* loaded from: classes.dex */
    public static class CompareResult<T> {
        public final List<T> added;
        public final List<T> removed;

        public CompareResult() {
            this.removed = new ArrayList();
            this.added = new ArrayList();
        }

        public CompareResult(Collection<T> collection, Collection<T> collection2) {
            ArrayList arrayList = new ArrayList();
            this.removed = arrayList;
            this.added = new ArrayList();
            if (collection != null) {
                arrayList.addAll(collection);
            }
            if (collection2 != null) {
                for (T t : collection2) {
                    if (!this.removed.remove(t)) {
                        this.added.add(t);
                    }
                }
            }
        }

        public String toString() {
            return "removed=[" + TextUtils.join(",", this.removed) + "] added=[" + TextUtils.join(",", this.added) + "]";
        }
    }

    /* loaded from: classes.dex */
    public static class CompareOrUpdateResult<K, T> {
        public final List<T> added = new ArrayList();
        public final List<T> removed = new ArrayList();
        public final List<T> updated = new ArrayList();

        public CompareOrUpdateResult(Collection<T> collection, Collection<T> collection2, Function<T, K> function) {
            HashMap hashMap = new HashMap();
            if (collection != null) {
                for (T t : collection) {
                    hashMap.put(function.apply(t), t);
                }
            }
            if (collection2 != null) {
                for (T t2 : collection2) {
                    Object remove = hashMap.remove(function.apply(t2));
                    if (remove != null) {
                        if (!remove.equals(t2)) {
                            this.updated.add(t2);
                        }
                    } else {
                        this.added.add(t2);
                    }
                }
            }
            this.removed.addAll(hashMap.values());
        }

        public String toString() {
            return "removed=[" + TextUtils.join(",", this.removed) + "] added=[" + TextUtils.join(",", this.added) + "] updated=[" + TextUtils.join(",", this.updated) + "]";
        }
    }

    public static CompareResult<LinkAddress> compareAddresses(LinkProperties linkProperties, LinkProperties linkProperties2) {
        return new CompareResult<>(linkProperties != null ? linkProperties.getLinkAddresses() : null, linkProperties2 != null ? linkProperties2.getLinkAddresses() : null);
    }

    public static boolean isIdenticalAddresses(LinkProperties linkProperties, LinkProperties linkProperties2) {
        List addresses = linkProperties.getAddresses();
        List addresses2 = linkProperties2.getAddresses();
        if (addresses.size() == addresses2.size()) {
            return addresses.containsAll(addresses2);
        }
        return false;
    }

    public static boolean isIdenticalDnses(LinkProperties linkProperties, LinkProperties linkProperties2) {
        List<InetAddress> dnsServers = linkProperties.getDnsServers();
        List<InetAddress> dnsServers2 = linkProperties2.getDnsServers();
        String domains = linkProperties.getDomains();
        String domains2 = linkProperties2.getDomains();
        if (domains == null) {
            if (domains2 != null) {
                return false;
            }
        } else if (!domains.equals(domains2)) {
            return false;
        }
        if (dnsServers.size() == dnsServers2.size()) {
            return dnsServers.containsAll(dnsServers2);
        }
        return false;
    }

    public static boolean isIdenticalHttpProxy(LinkProperties linkProperties, LinkProperties linkProperties2) {
        return Objects.equals(linkProperties.getHttpProxy(), linkProperties2.getHttpProxy());
    }

    public static boolean isIdenticalInterfaceName(LinkProperties linkProperties, LinkProperties linkProperties2) {
        return TextUtils.equals(linkProperties.getInterfaceName(), linkProperties2.getInterfaceName());
    }

    public static boolean isIdenticalRoutes(LinkProperties linkProperties, LinkProperties linkProperties2) {
        List<RouteInfo> routes = linkProperties.getRoutes();
        List<RouteInfo> routes2 = linkProperties2.getRoutes();
        if (routes.size() == routes2.size()) {
            return routes.containsAll(routes2);
        }
        return false;
    }
}
