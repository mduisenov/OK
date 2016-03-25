package ru.ok.android.ui.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public abstract class EfficientFragmentAdapter extends PagerAdapter {
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;
    private final FragmentManager mFragmentManager;
    private final ArrayList<Fragment> mFragments;
    private final ArrayList<SavedState> mSavedStates;

    public abstract Fragment instantiateFragment(int i);

    public EfficientFragmentAdapter(FragmentManager fragmentManager) {
        this.mSavedStates = new ArrayList();
        this.mFragments = new ArrayList();
        this.mCurrentPrimaryItem = null;
        this.mCurTransaction = null;
        this.mFragmentManager = fragmentManager;
    }

    public void startUpdate(ViewGroup container) {
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Fragment result = null;
        if (this.mFragments.size() > position) {
            result = (Fragment) this.mFragments.get(position);
        }
        if (result == null) {
            if (this.mCurTransaction == null) {
                this.mCurTransaction = this.mFragmentManager.beginTransaction();
            }
            result = instantiateFragment(position);
            if (this.mSavedStates.size() > position) {
                SavedState savedState = (SavedState) this.mSavedStates.get(position);
                if (savedState != null) {
                    result.setInitialSavedState(savedState);
                }
            }
            while (this.mFragments.size() <= position) {
                this.mFragments.add(null);
            }
            result.setMenuVisibility(false);
            this.mFragments.set(position, result);
            this.mCurTransaction.add(container.getId(), result);
        }
        return result;
    }

    public Fragment geCurrentFragmentAtPosition(int position) {
        if (position < 0 || position >= this.mFragments.size()) {
            return null;
        }
        return (Fragment) this.mFragments.get(position);
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            Fragment fragment = (Fragment) object;
            if (this.mCurTransaction == null) {
                this.mCurTransaction = this.mFragmentManager.beginTransaction();
            }
            while (this.mSavedStates.size() <= position) {
                this.mSavedStates.add(null);
            }
            this.mSavedStates.set(position, this.mFragmentManager.saveFragmentInstanceState(fragment));
            this.mFragments.set(position, null);
            this.mCurTransaction.remove(fragment);
        } catch (Exception e) {
        }
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != this.mCurrentPrimaryItem) {
            if (this.mCurrentPrimaryItem != null) {
                this.mCurrentPrimaryItem.setMenuVisibility(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
            }
            this.mCurrentPrimaryItem = fragment;
        }
    }

    public void finishUpdate(ViewGroup container) {
        if (this.mCurTransaction != null) {
            this.mCurTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }
    }

    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    public final void removeItem(int position) {
        FragmentTransaction transaction = this.mFragmentManager.beginTransaction();
        if (position < this.mFragments.size()) {
            Fragment fragment = (Fragment) this.mFragments.remove(position);
            if (fragment != null) {
                transaction.remove(fragment);
            }
        }
        if (position < this.mSavedStates.size()) {
            this.mSavedStates.remove(position);
        }
        transaction.commitAllowingStateLoss();
        this.mFragmentManager.executePendingTransactions();
        notifyDataSetChanged();
    }

    public final Fragment getCurrentPrimaryItem() {
        return this.mCurrentPrimaryItem;
    }

    public Parcelable saveState() {
        Bundle bundle = null;
        if (!this.mSavedStates.isEmpty()) {
            bundle = new Bundle();
            bundle.putParcelableArrayList("ess", this.mSavedStates);
        }
        int size = this.mFragments.size();
        for (int i = 0; i < size; i++) {
            Fragment fragment = (Fragment) this.mFragments.get(i);
            if (fragment != null) {
                if (bundle == null) {
                    bundle = new Bundle();
                }
                this.mFragmentManager.putFragment(bundle, "f" + i, fragment);
            }
        }
        return bundle;
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            this.mSavedStates.clear();
            this.mFragments.clear();
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            ArrayList<Parcelable> savedStates = bundle.getParcelableArrayList("ess");
            if (savedStates != null) {
                int size = savedStates.size();
                for (int i = 0; i < size; i++) {
                    this.mSavedStates.add((SavedState) savedStates.get(i));
                }
            }
            Iterable<String> keys = bundle.keySet();
            if (this.mFragmentManager != null) {
                for (String key : keys) {
                    if (key.startsWith("f")) {
                        int index = Integer.parseInt(key.substring(1));
                        Fragment fragment = this.mFragmentManager.getFragment(bundle, key);
                        if (fragment != null) {
                            while (this.mFragments.size() <= index) {
                                this.mFragments.add(null);
                            }
                            fragment.setMenuVisibility(false);
                            this.mFragments.set(index, fragment);
                        }
                    }
                }
            }
        }
    }
}
