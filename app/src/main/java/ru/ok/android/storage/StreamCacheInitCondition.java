package ru.ok.android.storage;

import android.os.ConditionVariable;

public final class StreamCacheInitCondition {
    private final ConditionVariable deletedFeedsStorageCondition;
    private volatile boolean isOpen;
    private final ConditionVariable likeStorageCondition;
    private final ConditionVariable mtPollsStorageCondition;
    private final Storages storages;
    private final ConditionVariable subscriptionStorageCondition;

    void onLikeStorageInitialized() {
        this.likeStorageCondition.open();
    }

    void onDeletedFeedsStorageInitialized() {
        this.deletedFeedsStorageCondition.open();
    }

    void onSubscriptionStorageInitialized() {
        this.subscriptionStorageCondition.open();
    }

    void onMtPollsStorageInitialized() {
        this.mtPollsStorageCondition.open();
    }

    void block() {
        if (!this.isOpen) {
            this.storages.getLikeManager();
            this.likeStorageCondition.block();
            this.storages.getDeletedFeedsManager();
            this.deletedFeedsStorageCondition.block();
            this.storages.getStreamSubscriptionManager();
            this.subscriptionStorageCondition.block();
            this.storages.getMtPollsManager();
            this.mtPollsStorageCondition.block();
            this.isOpen = true;
        }
    }

    StreamCacheInitCondition(Storages storages) {
        this.likeStorageCondition = new ConditionVariable();
        this.deletedFeedsStorageCondition = new ConditionVariable();
        this.subscriptionStorageCondition = new ConditionVariable();
        this.mtPollsStorageCondition = new ConditionVariable();
        this.isOpen = false;
        this.storages = storages;
    }
}
