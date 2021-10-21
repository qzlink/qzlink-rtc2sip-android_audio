package com.sdk.android.widget;/*
 * @creator      dean_deng
 * @createTime   2019/2/21 9:33
 * @Desc         ${TODO}
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class MyFragmentTabHost extends TabHost implements TabHost.OnTabChangeListener {
    private final ArrayList<TabInfo> mTabs = new ArrayList();
    private FrameLayout mRealTabContent;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private int mContainerId;
    private OnTabChangeListener mOnTabChangeListener;
    private TabInfo mLastTab;
    private boolean mAttached;

    public MyFragmentTabHost(Context context) {
        super(context, (AttributeSet) null);
        this.initMyFragmentTabHost(context, (AttributeSet) null);
    }

    public MyFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initMyFragmentTabHost(context, attrs);
    }

    private void initMyFragmentTabHost(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{16842995}, 0, 0);
        this.mContainerId = a.getResourceId(0, 0);
        a.recycle();
        super.setOnTabChangedListener(this);
    }

    @SuppressLint("ResourceType")
    private void ensureHierarchy(Context context) {
        if (this.findViewById(16908307) == null) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(1);
            this.addView(ll, new FrameLayout.LayoutParams(-1, -1));
            TabWidget tw = new TabWidget(context);
            tw.setId(16908307);
            tw.setOrientation(0);
            ll.addView(tw, new android.widget.LinearLayout.LayoutParams(-1, -2, 0.0F));
            FrameLayout fl = new FrameLayout(context);
            fl.setId(16908305);
            ll.addView(fl, new android.widget.LinearLayout.LayoutParams(0, 0, 0.0F));
            this.mRealTabContent = fl = new FrameLayout(context);
            this.mRealTabContent.setId(this.mContainerId);
            ll.addView(fl, new android.widget.LinearLayout.LayoutParams(-1, 0, 1.0F));
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setup() {
        throw new IllegalStateException("Must call setup() that takes a Context and FragmentManager");
    }

    public void setup(Context context, FragmentManager manager) {
        this.ensureHierarchy(context);
        super.setup();
        this.mContext = context;
        this.mFragmentManager = manager;
        this.ensureContent();
    }

    @SuppressLint("ResourceType")
    public void setup(Context context, FragmentManager manager, int containerId) {
        this.ensureHierarchy(context);
        super.setup();
        this.mContext = context;
        this.mFragmentManager = manager;
        this.mContainerId = containerId;
        this.ensureContent();
        this.mRealTabContent.setId(containerId);
        if (this.getId() == -1) {
            this.setId(16908306);
        }

    }

    private void ensureContent() {
        if (this.mRealTabContent == null) {
            this.mRealTabContent = (FrameLayout) this.findViewById(this.mContainerId);
            if (this.mRealTabContent == null) {
                throw new IllegalStateException("No tab content FrameLayout found for id " + this.mContainerId);
            }
        }

    }

    public void setOnTabChangedListener(OnTabChangeListener l) {
        this.mOnTabChangeListener = l;
    }

    public void addTab(@NonNull TabSpec tabSpec, @NonNull Class<?> clss, @Nullable Bundle args) {
        tabSpec.setContent(new MyFragmentTabHost.DummyTabFactory(this.mContext));
        String tag = tabSpec.getTag();
        MyFragmentTabHost.TabInfo info = new MyFragmentTabHost.TabInfo(tag, clss, args);
        if (this.mAttached) {
            info.fragment = this.mFragmentManager.findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = this.mFragmentManager.beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }
        }

        this.mTabs.add(info);
        this.addTab(tabSpec);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        String currentTag = this.getCurrentTabTag();
        FragmentTransaction ft = null;
        int i = 0;

        for (int count = this.mTabs.size(); i < count; ++i) {
            MyFragmentTabHost.TabInfo tab = (MyFragmentTabHost.TabInfo) this.mTabs.get(i);
            tab.fragment = this.mFragmentManager.findFragmentByTag(tab.tag);
            if (tab.fragment != null && !tab.fragment.isDetached()) {
                if (tab.tag.equals(currentTag)) {
                    this.mLastTab = tab;
                } else {
                    if (ft == null) {
                        ft = this.mFragmentManager.beginTransaction();
                    }

                    ft.detach(tab.fragment);
                }
            }
        }

        this.mAttached = true;
        ft = this.doTabChanged(currentTag, ft);
        if (ft != null) {
            ft.commit();
            this.mFragmentManager.executePendingTransactions();
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttached = false;
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        MyFragmentTabHost.SavedState ss = new MyFragmentTabHost.SavedState(superState);
        ss.curTab = this.getCurrentTabTag();
        return ss;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof MyFragmentTabHost.SavedState)) {
            super.onRestoreInstanceState(state);
        } else {
            MyFragmentTabHost.SavedState ss = (MyFragmentTabHost.SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            this.setCurrentTabByTag(ss.curTab);
        }
    }

    public void onTabChanged(String tabId) {
        if (this.mAttached) {
            FragmentTransaction ft = this.doTabChanged(tabId, (FragmentTransaction) null);
            if (ft != null) {
                ft.commit();
            }
        }

        if (this.mOnTabChangeListener != null) {
            this.mOnTabChangeListener.onTabChanged(tabId);
        }

    }

    @Nullable
    private FragmentTransaction doTabChanged(@Nullable String tag, @Nullable FragmentTransaction ft) {
        MyFragmentTabHost.TabInfo newTab = this.getTabInfoForTag(tag);
        if (this.mLastTab != newTab) {
            if (ft == null) {
                ft = this.mFragmentManager.beginTransaction();
            }

            if (this.mLastTab != null && this.mLastTab.fragment != null) {
//                ft.detach(this.mLastTab.fragment);
                ft.hide(this.mLastTab.fragment);
            }

            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(this.mContext, newTab.clss.getName(), newTab.args);
                    ft.add(this.mContainerId, newTab.fragment, newTab.tag);
                } else {
//                    ft.attach(newTab.fragment);
                    ft.show(newTab.fragment);
                }
            }

            this.mLastTab = newTab;
        }

        return ft;
    }

    @Nullable
    private MyFragmentTabHost.TabInfo getTabInfoForTag(String tabId) {
        int i = 0;

        for (int count = this.mTabs.size(); i < count; ++i) {
            MyFragmentTabHost.TabInfo tab = (MyFragmentTabHost.TabInfo) this.mTabs.get(i);
            if (tab.tag.equals(tabId)) {
                return tab;
            }
        }

        return null;
    }

    static class SavedState extends View.BaseSavedState {
        String curTab;
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public MyFragmentTabHost.SavedState createFromParcel(Parcel in) {
                return new MyFragmentTabHost.SavedState(in);
            }

            public MyFragmentTabHost.SavedState[] newArray(int size) {
                return new MyFragmentTabHost.SavedState[size];
            }
        };

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in) {
            super(in);
            this.curTab = in.readString();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.curTab);
        }

        public String toString() {
            return "MyFragmentTabHost.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " curTab=" + this.curTab + "}";
        }
    }

    static class DummyTabFactory implements TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            this.mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(this.mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    static final class TabInfo {
        @NonNull
        final String tag;
        @NonNull
        final Class<?> clss;
        @Nullable
        final Bundle args;
        Fragment fragment;

        TabInfo(@NonNull String _tag, @NonNull Class<?> _class, @Nullable Bundle _args) {
            this.tag = _tag;
            this.clss = _class;
            this.args = _args;
        }
    }
}

