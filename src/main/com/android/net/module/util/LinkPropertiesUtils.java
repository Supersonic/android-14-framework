package com.android.net.module.util;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
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
/* loaded from: classes5.dex */
public final class LinkPropertiesUtils {

    /* loaded from: classes5.dex */
    public static class CompareResult<T> {
        public final List<T> added;
        public final List<T> removed;

        public CompareResult() {
            this.removed = new ArrayList();
            this.added = new ArrayList();
        }

        public CompareResult(Collection<T> oldItems, Collection<T> newItems) {
            ArrayList arrayList = new ArrayList();
            this.removed = arrayList;
            this.added = new ArrayList();
            if (oldItems != null) {
                arrayList.addAll(oldItems);
            }
            if (newItems != null) {
                for (T newItem : newItems) {
                    if (!this.removed.remove(newItem)) {
                        this.added.add(newItem);
                    }
                }
            }
        }

        public String toString() {
            return "removed=[" + TextUtils.join(",", this.removed) + "] added=[" + TextUtils.join(",", this.added) + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes5.dex */
    public static class CompareOrUpdateResult<K, T> {
        public final List<T> added = new ArrayList();
        public final List<T> removed = new ArrayList();
        public final List<T> updated = new ArrayList();

        public CompareOrUpdateResult(Collection<T> oldItems, Collection<T> newItems, Function<T, K> keyCalculator) {
            HashMap<K, T> updateTracker = new HashMap<>();
            if (oldItems != null) {
                for (T oldItem : oldItems) {
                    updateTracker.put(keyCalculator.apply(oldItem), oldItem);
                }
            }
            if (newItems != null) {
                for (T newItem : newItems) {
                    T oldItem2 = updateTracker.remove(keyCalculator.apply(newItem));
                    if (oldItem2 != null) {
                        if (!oldItem2.equals(newItem)) {
                            this.updated.add(newItem);
                        }
                    } else {
                        this.added.add(newItem);
                    }
                }
            }
            this.removed.addAll(updateTracker.values());
        }

        public String toString() {
            return "removed=[" + TextUtils.join(",", this.removed) + "] added=[" + TextUtils.join(",", this.added) + "] updated=[" + TextUtils.join(",", this.updated) + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    public static CompareResult<LinkAddress> compareAddresses(LinkProperties left, LinkProperties right) {
        return new CompareResult<>(left != null ? left.getLinkAddresses() : null, right != null ? right.getLinkAddresses() : null);
    }

    public static boolean isIdenticalAddresses(LinkProperties left, LinkProperties right) {
        Collection<InetAddress> leftAddresses = left.getAddresses();
        Collection<InetAddress> rightAddresses = right.getAddresses();
        if (leftAddresses.size() == rightAddresses.size()) {
            return leftAddresses.containsAll(rightAddresses);
        }
        return false;
    }

    public static boolean isIdenticalDnses(LinkProperties left, LinkProperties right) {
        Collection<InetAddress> leftDnses = left.getDnsServers();
        Collection<InetAddress> rightDnses = right.getDnsServers();
        String leftDomains = left.getDomains();
        String rightDomains = right.getDomains();
        if (leftDomains == null) {
            if (rightDomains != null) {
                return false;
            }
        } else if (!leftDomains.equals(rightDomains)) {
            return false;
        }
        if (leftDnses.size() != rightDnses.size()) {
            return false;
        }
        return leftDnses.containsAll(rightDnses);
    }

    public static boolean isIdenticalHttpProxy(LinkProperties left, LinkProperties right) {
        return Objects.equals(left.getHttpProxy(), right.getHttpProxy());
    }

    public static boolean isIdenticalInterfaceName(LinkProperties left, LinkProperties right) {
        return TextUtils.equals(left.getInterfaceName(), right.getInterfaceName());
    }

    public static boolean isIdenticalRoutes(LinkProperties left, LinkProperties right) {
        Collection<RouteInfo> leftRoutes = left.getRoutes();
        Collection<RouteInfo> rightRoutes = right.getRoutes();
        if (leftRoutes.size() == rightRoutes.size()) {
            return leftRoutes.containsAll(rightRoutes);
        }
        return false;
    }
}
