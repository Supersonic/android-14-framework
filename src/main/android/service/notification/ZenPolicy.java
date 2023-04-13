package android.service.notification;

import android.content.Context;
import android.media.AudioSystem;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.provider.Contacts;
import android.util.proto.ProtoOutputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class ZenPolicy implements Parcelable {
    public static final int CONVERSATION_SENDERS_ANYONE = 1;
    public static final int CONVERSATION_SENDERS_IMPORTANT = 2;
    public static final int CONVERSATION_SENDERS_NONE = 3;
    public static final int CONVERSATION_SENDERS_UNSET = 0;
    public static final Parcelable.Creator<ZenPolicy> CREATOR = new Parcelable.Creator<ZenPolicy>() { // from class: android.service.notification.ZenPolicy.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ZenPolicy createFromParcel(Parcel source) {
            ZenPolicy policy = new ZenPolicy();
            policy.mPriorityCategories = ZenPolicy.trimList(source.readArrayList(Integer.class.getClassLoader(), Integer.class), 9);
            policy.mVisualEffects = ZenPolicy.trimList(source.readArrayList(Integer.class.getClassLoader(), Integer.class), 7);
            policy.mPriorityCalls = source.readInt();
            policy.mPriorityMessages = source.readInt();
            policy.mConversationSenders = source.readInt();
            return policy;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ZenPolicy[] newArray(int size) {
            return new ZenPolicy[size];
        }
    };
    public static final int NUM_PRIORITY_CATEGORIES = 9;
    public static final int NUM_VISUAL_EFFECTS = 7;
    public static final int PEOPLE_TYPE_ANYONE = 1;
    public static final int PEOPLE_TYPE_CONTACTS = 2;
    public static final int PEOPLE_TYPE_NONE = 4;
    public static final int PEOPLE_TYPE_STARRED = 3;
    public static final int PEOPLE_TYPE_UNSET = 0;
    public static final int PRIORITY_CATEGORY_ALARMS = 5;
    public static final int PRIORITY_CATEGORY_CALLS = 3;
    public static final int PRIORITY_CATEGORY_CONVERSATIONS = 8;
    public static final int PRIORITY_CATEGORY_EVENTS = 1;
    public static final int PRIORITY_CATEGORY_MEDIA = 6;
    public static final int PRIORITY_CATEGORY_MESSAGES = 2;
    public static final int PRIORITY_CATEGORY_REMINDERS = 0;
    public static final int PRIORITY_CATEGORY_REPEAT_CALLERS = 4;
    public static final int PRIORITY_CATEGORY_SYSTEM = 7;
    public static final int STATE_ALLOW = 1;
    public static final int STATE_DISALLOW = 2;
    public static final int STATE_UNSET = 0;
    public static final int VISUAL_EFFECT_AMBIENT = 5;
    public static final int VISUAL_EFFECT_BADGE = 4;
    public static final int VISUAL_EFFECT_FULL_SCREEN_INTENT = 0;
    public static final int VISUAL_EFFECT_LIGHTS = 1;
    public static final int VISUAL_EFFECT_NOTIFICATION_LIST = 6;
    public static final int VISUAL_EFFECT_PEEK = 2;
    public static final int VISUAL_EFFECT_STATUS_BAR = 3;
    private int mPriorityMessages = 0;
    private int mPriorityCalls = 0;
    private int mConversationSenders = 0;
    private ArrayList<Integer> mPriorityCategories = new ArrayList<>(Collections.nCopies(9, 0));
    private ArrayList<Integer> mVisualEffects = new ArrayList<>(Collections.nCopies(7, 0));

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ConversationSenders {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface PeopleType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface PriorityCategory {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface State {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface VisualEffect {
    }

    public int getPriorityConversationSenders() {
        return this.mConversationSenders;
    }

    public int getPriorityMessageSenders() {
        return this.mPriorityMessages;
    }

    public int getPriorityCallSenders() {
        return this.mPriorityCalls;
    }

    public int getPriorityCategoryConversations() {
        return this.mPriorityCategories.get(8).intValue();
    }

    public int getPriorityCategoryReminders() {
        return this.mPriorityCategories.get(0).intValue();
    }

    public int getPriorityCategoryEvents() {
        return this.mPriorityCategories.get(1).intValue();
    }

    public int getPriorityCategoryMessages() {
        return this.mPriorityCategories.get(2).intValue();
    }

    public int getPriorityCategoryCalls() {
        return this.mPriorityCategories.get(3).intValue();
    }

    public int getPriorityCategoryRepeatCallers() {
        return this.mPriorityCategories.get(4).intValue();
    }

    public int getPriorityCategoryAlarms() {
        return this.mPriorityCategories.get(5).intValue();
    }

    public int getPriorityCategoryMedia() {
        return this.mPriorityCategories.get(6).intValue();
    }

    public int getPriorityCategorySystem() {
        return this.mPriorityCategories.get(7).intValue();
    }

    public int getVisualEffectFullScreenIntent() {
        return this.mVisualEffects.get(0).intValue();
    }

    public int getVisualEffectLights() {
        return this.mVisualEffects.get(1).intValue();
    }

    public int getVisualEffectPeek() {
        return this.mVisualEffects.get(2).intValue();
    }

    public int getVisualEffectStatusBar() {
        return this.mVisualEffects.get(3).intValue();
    }

    public int getVisualEffectBadge() {
        return this.mVisualEffects.get(4).intValue();
    }

    public int getVisualEffectAmbient() {
        return this.mVisualEffects.get(5).intValue();
    }

    public int getVisualEffectNotificationList() {
        return this.mVisualEffects.get(6).intValue();
    }

    public boolean shouldHideAllVisualEffects() {
        for (int i = 0; i < this.mVisualEffects.size(); i++) {
            if (this.mVisualEffects.get(i).intValue() != 2) {
                return false;
            }
        }
        return true;
    }

    public boolean shouldShowAllVisualEffects() {
        for (int i = 0; i < this.mVisualEffects.size(); i++) {
            if (this.mVisualEffects.get(i).intValue() != 1) {
                return false;
            }
        }
        return true;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private ZenPolicy mZenPolicy;

        public Builder() {
            this.mZenPolicy = new ZenPolicy();
        }

        public Builder(ZenPolicy policy) {
            if (policy != null) {
                this.mZenPolicy = policy.copy();
            } else {
                this.mZenPolicy = new ZenPolicy();
            }
        }

        public ZenPolicy build() {
            return this.mZenPolicy.copy();
        }

        public Builder allowAllSounds() {
            for (int i = 0; i < this.mZenPolicy.mPriorityCategories.size(); i++) {
                this.mZenPolicy.mPriorityCategories.set(i, 1);
            }
            this.mZenPolicy.mPriorityMessages = 1;
            this.mZenPolicy.mPriorityCalls = 1;
            this.mZenPolicy.mConversationSenders = 1;
            return this;
        }

        public Builder disallowAllSounds() {
            for (int i = 0; i < this.mZenPolicy.mPriorityCategories.size(); i++) {
                this.mZenPolicy.mPriorityCategories.set(i, 2);
            }
            this.mZenPolicy.mPriorityMessages = 4;
            this.mZenPolicy.mPriorityCalls = 4;
            this.mZenPolicy.mConversationSenders = 3;
            return this;
        }

        public Builder showAllVisualEffects() {
            for (int i = 0; i < this.mZenPolicy.mVisualEffects.size(); i++) {
                this.mZenPolicy.mVisualEffects.set(i, 1);
            }
            return this;
        }

        public Builder hideAllVisualEffects() {
            for (int i = 0; i < this.mZenPolicy.mVisualEffects.size(); i++) {
                this.mZenPolicy.mVisualEffects.set(i, 2);
            }
            return this;
        }

        public Builder unsetPriorityCategory(int category) {
            this.mZenPolicy.mPriorityCategories.set(category, 0);
            if (category == 2) {
                this.mZenPolicy.mPriorityMessages = 0;
            } else if (category == 3) {
                this.mZenPolicy.mPriorityCalls = 0;
            } else if (category == 8) {
                this.mZenPolicy.mConversationSenders = 0;
            }
            return this;
        }

        public Builder unsetVisualEffect(int effect) {
            this.mZenPolicy.mVisualEffects.set(effect, 0);
            return this;
        }

        public Builder allowReminders(boolean allow) {
            this.mZenPolicy.mPriorityCategories.set(0, Integer.valueOf(allow ? 1 : 2));
            return this;
        }

        public Builder allowEvents(boolean allow) {
            this.mZenPolicy.mPriorityCategories.set(1, Integer.valueOf(allow ? 1 : 2));
            return this;
        }

        public Builder allowConversations(int audienceType) {
            if (audienceType == 0) {
                return unsetPriorityCategory(8);
            }
            if (audienceType == 3) {
                this.mZenPolicy.mPriorityCategories.set(8, 2);
            } else if (audienceType == 1 || audienceType == 2) {
                this.mZenPolicy.mPriorityCategories.set(8, 1);
            } else {
                return this;
            }
            this.mZenPolicy.mConversationSenders = audienceType;
            return this;
        }

        public Builder allowMessages(int audienceType) {
            if (audienceType == 0) {
                return unsetPriorityCategory(2);
            }
            if (audienceType == 4) {
                this.mZenPolicy.mPriorityCategories.set(2, 2);
            } else if (audienceType == 1 || audienceType == 2 || audienceType == 3) {
                this.mZenPolicy.mPriorityCategories.set(2, 1);
            } else {
                return this;
            }
            this.mZenPolicy.mPriorityMessages = audienceType;
            return this;
        }

        public Builder allowCalls(int audienceType) {
            if (audienceType == 0) {
                return unsetPriorityCategory(3);
            }
            if (audienceType == 4) {
                this.mZenPolicy.mPriorityCategories.set(3, 2);
            } else if (audienceType == 1 || audienceType == 2 || audienceType == 3) {
                this.mZenPolicy.mPriorityCategories.set(3, 1);
            } else {
                return this;
            }
            this.mZenPolicy.mPriorityCalls = audienceType;
            return this;
        }

        public Builder allowRepeatCallers(boolean allow) {
            this.mZenPolicy.mPriorityCategories.set(4, Integer.valueOf(allow ? 1 : 2));
            return this;
        }

        public Builder allowAlarms(boolean allow) {
            this.mZenPolicy.mPriorityCategories.set(5, Integer.valueOf(allow ? 1 : 2));
            return this;
        }

        public Builder allowMedia(boolean allow) {
            this.mZenPolicy.mPriorityCategories.set(6, Integer.valueOf(allow ? 1 : 2));
            return this;
        }

        public Builder allowSystem(boolean allow) {
            this.mZenPolicy.mPriorityCategories.set(7, Integer.valueOf(allow ? 1 : 2));
            return this;
        }

        public Builder allowCategory(int category, boolean allow) {
            switch (category) {
                case 0:
                    allowReminders(allow);
                    break;
                case 1:
                    allowEvents(allow);
                    break;
                case 4:
                    allowRepeatCallers(allow);
                    break;
                case 5:
                    allowAlarms(allow);
                    break;
                case 6:
                    allowMedia(allow);
                    break;
                case 7:
                    allowSystem(allow);
                    break;
            }
            return this;
        }

        public Builder showFullScreenIntent(boolean show) {
            this.mZenPolicy.mVisualEffects.set(0, Integer.valueOf(show ? 1 : 2));
            return this;
        }

        public Builder showLights(boolean show) {
            this.mZenPolicy.mVisualEffects.set(1, Integer.valueOf(show ? 1 : 2));
            return this;
        }

        public Builder showPeeking(boolean show) {
            this.mZenPolicy.mVisualEffects.set(2, Integer.valueOf(show ? 1 : 2));
            return this;
        }

        public Builder showStatusBarIcons(boolean show) {
            this.mZenPolicy.mVisualEffects.set(3, Integer.valueOf(show ? 1 : 2));
            return this;
        }

        public Builder showBadges(boolean show) {
            this.mZenPolicy.mVisualEffects.set(4, Integer.valueOf(show ? 1 : 2));
            return this;
        }

        public Builder showInAmbientDisplay(boolean show) {
            this.mZenPolicy.mVisualEffects.set(5, Integer.valueOf(show ? 1 : 2));
            return this;
        }

        public Builder showInNotificationList(boolean show) {
            this.mZenPolicy.mVisualEffects.set(6, Integer.valueOf(show ? 1 : 2));
            return this;
        }

        public Builder showVisualEffect(int effect, boolean show) {
            switch (effect) {
                case 0:
                    showFullScreenIntent(show);
                    break;
                case 1:
                    showLights(show);
                    break;
                case 2:
                    showPeeking(show);
                    break;
                case 3:
                    showStatusBarIcons(show);
                    break;
                case 4:
                    showBadges(show);
                    break;
                case 5:
                    showInAmbientDisplay(show);
                    break;
                case 6:
                    showInNotificationList(show);
                    break;
            }
            return this;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.mPriorityCategories);
        dest.writeList(this.mVisualEffects);
        dest.writeInt(this.mPriorityCalls);
        dest.writeInt(this.mPriorityMessages);
        dest.writeInt(this.mConversationSenders);
    }

    public String toString() {
        return ZenPolicy.class.getSimpleName() + "{priorityCategories=[" + priorityCategoriesToString() + "], visualEffects=[" + visualEffectsToString() + "], priorityCallsSenders=" + peopleTypeToString(this.mPriorityCalls) + ", priorityMessagesSenders=" + peopleTypeToString(this.mPriorityMessages) + ", priorityConversationSenders=" + conversationTypeToString(this.mConversationSenders) + '}';
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ArrayList<Integer> trimList(ArrayList<Integer> list, int maxLength) {
        if (list == null || list.size() <= maxLength) {
            return list;
        }
        return new ArrayList<>(list.subList(0, maxLength));
    }

    private String priorityCategoriesToString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.mPriorityCategories.size(); i++) {
            if (this.mPriorityCategories.get(i).intValue() != 0) {
                builder.append(indexToCategory(i)).append("=").append(stateToString(this.mPriorityCategories.get(i).intValue())).append(" ");
            }
        }
        return builder.toString();
    }

    private String visualEffectsToString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.mVisualEffects.size(); i++) {
            if (this.mVisualEffects.get(i).intValue() != 0) {
                builder.append(indexToVisualEffect(i)).append("=").append(stateToString(this.mVisualEffects.get(i).intValue())).append(" ");
            }
        }
        return builder.toString();
    }

    private String indexToVisualEffect(int visualEffectIndex) {
        switch (visualEffectIndex) {
            case 0:
                return "fullScreenIntent";
            case 1:
                return Context.LIGHTS_SERVICE;
            case 2:
                return "peek";
            case 3:
                return "statusBar";
            case 4:
                return "badge";
            case 5:
                return AudioSystem.DEVICE_IN_AMBIENT_NAME;
            case 6:
                return "notificationList";
            default:
                return null;
        }
    }

    private String indexToCategory(int categoryIndex) {
        switch (categoryIndex) {
            case 0:
                return "reminders";
            case 1:
                return "events";
            case 2:
                return "messages";
            case 3:
                return "calls";
            case 4:
                return "repeatCallers";
            case 5:
                return "alarms";
            case 6:
                return "media";
            case 7:
                return "system";
            case 8:
                return "convs";
            default:
                return null;
        }
    }

    private String stateToString(int state) {
        switch (state) {
            case 0:
                return "unset";
            case 1:
                return "allow";
            case 2:
                return "disallow";
            default:
                return "invalidState{" + state + "}";
        }
    }

    private String peopleTypeToString(int peopleType) {
        switch (peopleType) {
            case 0:
                return "unset";
            case 1:
                return "anyone";
            case 2:
                return Contacts.AUTHORITY;
            case 3:
                return "starred_contacts";
            case 4:
                return "none";
            default:
                return "invalidPeopleType{" + peopleType + "}";
        }
    }

    public static String conversationTypeToString(int conversationType) {
        switch (conversationType) {
            case 0:
                return "unset";
            case 1:
                return "anyone";
            case 2:
                return "important";
            case 3:
                return "none";
            default:
                return "invalidConversationType{" + conversationType + "}";
        }
    }

    public boolean equals(Object o) {
        if (o instanceof ZenPolicy) {
            if (o == this) {
                return true;
            }
            ZenPolicy other = (ZenPolicy) o;
            return Objects.equals(other.mPriorityCategories, this.mPriorityCategories) && Objects.equals(other.mVisualEffects, this.mVisualEffects) && other.mPriorityCalls == this.mPriorityCalls && other.mPriorityMessages == this.mPriorityMessages && other.mConversationSenders == this.mConversationSenders;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mPriorityCategories, this.mVisualEffects, Integer.valueOf(this.mPriorityCalls), Integer.valueOf(this.mPriorityMessages), Integer.valueOf(this.mConversationSenders));
    }

    private int getZenPolicyPriorityCategoryState(int category) {
        switch (category) {
            case 0:
                return getPriorityCategoryReminders();
            case 1:
                return getPriorityCategoryEvents();
            case 2:
                return getPriorityCategoryMessages();
            case 3:
                return getPriorityCategoryCalls();
            case 4:
                return getPriorityCategoryRepeatCallers();
            case 5:
                return getPriorityCategoryAlarms();
            case 6:
                return getPriorityCategoryMedia();
            case 7:
                return getPriorityCategorySystem();
            case 8:
                return getPriorityCategoryConversations();
            default:
                return -1;
        }
    }

    private int getZenPolicyVisualEffectState(int effect) {
        switch (effect) {
            case 0:
                return getVisualEffectFullScreenIntent();
            case 1:
                return getVisualEffectLights();
            case 2:
                return getVisualEffectPeek();
            case 3:
                return getVisualEffectStatusBar();
            case 4:
                return getVisualEffectBadge();
            case 5:
                return getVisualEffectAmbient();
            case 6:
                return getVisualEffectNotificationList();
            default:
                return -1;
        }
    }

    public boolean isCategoryAllowed(int category, boolean defaultVal) {
        switch (getZenPolicyPriorityCategoryState(category)) {
            case 1:
                return true;
            case 2:
                return false;
            default:
                return defaultVal;
        }
    }

    public boolean isVisualEffectAllowed(int effect, boolean defaultVal) {
        switch (getZenPolicyVisualEffectState(effect)) {
            case 1:
                return true;
            case 2:
                return false;
            default:
                return defaultVal;
        }
    }

    public void apply(ZenPolicy policyToApply) {
        int newState;
        if (policyToApply == null) {
            return;
        }
        for (int category = 0; category < this.mPriorityCategories.size(); category++) {
            if (this.mPriorityCategories.get(category).intValue() != 2 && (newState = policyToApply.mPriorityCategories.get(category).intValue()) != 0) {
                this.mPriorityCategories.set(category, Integer.valueOf(newState));
                if (category == 2) {
                    int i = this.mPriorityMessages;
                    int i2 = policyToApply.mPriorityMessages;
                    if (i < i2) {
                        this.mPriorityMessages = i2;
                    }
                }
                if (category == 3) {
                    int i3 = this.mPriorityCalls;
                    int i4 = policyToApply.mPriorityCalls;
                    if (i3 < i4) {
                        this.mPriorityCalls = i4;
                    }
                }
                if (category == 8) {
                    int i5 = this.mConversationSenders;
                    int i6 = policyToApply.mConversationSenders;
                    if (i5 < i6) {
                        this.mConversationSenders = i6;
                    }
                }
            }
        }
        for (int visualEffect = 0; visualEffect < this.mVisualEffects.size(); visualEffect++) {
            if (this.mVisualEffects.get(visualEffect).intValue() != 2 && policyToApply.mVisualEffects.get(visualEffect).intValue() != 0) {
                this.mVisualEffects.set(visualEffect, policyToApply.mVisualEffects.get(visualEffect));
            }
        }
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1159641169921L, getPriorityCategoryReminders());
        proto.write(1159641169922L, getPriorityCategoryEvents());
        proto.write(1159641169923L, getPriorityCategoryMessages());
        proto.write(1159641169924L, getPriorityCategoryCalls());
        proto.write(1159641169925L, getPriorityCategoryRepeatCallers());
        proto.write(1159641169926L, getPriorityCategoryAlarms());
        proto.write(1159641169927L, getPriorityCategoryMedia());
        proto.write(1159641169928L, getPriorityCategorySystem());
        proto.write(1159641169929L, getVisualEffectFullScreenIntent());
        proto.write(1159641169930L, getVisualEffectLights());
        proto.write(1159641169931L, getVisualEffectPeek());
        proto.write(1159641169932L, getVisualEffectStatusBar());
        proto.write(1159641169933L, getVisualEffectBadge());
        proto.write(1159641169934L, getVisualEffectAmbient());
        proto.write(1159641169935L, getVisualEffectNotificationList());
        proto.write(1159641169937L, getPriorityMessageSenders());
        proto.write(1159641169936L, getPriorityCallSenders());
        proto.end(token);
    }

    public byte[] toProto() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ProtoOutputStream proto = new ProtoOutputStream(bytes);
        proto.write(1159641169921L, getPriorityCategoryCalls());
        proto.write(1159641169922L, getPriorityCategoryRepeatCallers());
        proto.write(1159641169923L, getPriorityCategoryMessages());
        proto.write(1159641169924L, getPriorityCategoryConversations());
        proto.write(1159641169925L, getPriorityCategoryReminders());
        proto.write(1159641169926L, getPriorityCategoryEvents());
        proto.write(1159641169927L, getPriorityCategoryAlarms());
        proto.write(1159641169928L, getPriorityCategoryMedia());
        proto.write(1159641169929L, getPriorityCategorySystem());
        proto.write(1159641169930L, getVisualEffectFullScreenIntent());
        proto.write(1159641169931L, getVisualEffectLights());
        proto.write(1159641169932L, getVisualEffectPeek());
        proto.write(1159641169933L, getVisualEffectStatusBar());
        proto.write(1159641169934L, getVisualEffectBadge());
        proto.write(1159641169935L, getVisualEffectAmbient());
        proto.write(1159641169936L, getVisualEffectNotificationList());
        proto.write(1159641169937L, getPriorityCallSenders());
        proto.write(1159641169938L, getPriorityMessageSenders());
        proto.write(1159641169939L, getPriorityConversationSenders());
        proto.flush();
        return bytes.toByteArray();
    }

    public ZenPolicy copy() {
        Parcel parcel = Parcel.obtain();
        try {
            writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            return CREATOR.createFromParcel(parcel);
        } finally {
            parcel.recycle();
        }
    }
}
