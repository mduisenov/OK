package ru.ok.android.fragments.adman;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd.AppwallAdListener;
import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.target.TargetUtils;
import ru.ok.android.ui.adapters.adman.BannersAdapter;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.WebState;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.tabbar.HideTabbarListView;
import ru.ok.android.utils.localization.LocalizationManager;

public class AdmanBannersFragment extends BaseFragment implements OnItemClickListener {
    private BannersAdapter adapter;
    private HideTabbarListView bannersList;
    private View mMainView;
    private NativeAppwallAd targetAdapter;

    /* renamed from: ru.ok.android.fragments.adman.AdmanBannersFragment.1 */
    class C02921 implements AppwallAdListener {
        C02921() {
        }

        public void onLoad(NativeAppwallAd nativeAppwallAd) {
            List<NativeAppwallBanner> banners = nativeAppwallAd.getBanners();
            if (banners != null) {
                if (banners.size() > 2) {
                    banners = banners.subList(2, banners.size());
                }
                AdmanBannersFragment.this.adapter = new BannersAdapter(AdmanBannersFragment.this.getContext(), AdmanBannersFragment.this.targetAdapter, banners, AdmanBannersFragment.this.getSectionName());
                AdmanBannersFragment.this.bannersList.setAdapter(AdmanBannersFragment.this.adapter);
            }
        }

        public void onNoAd(String s, NativeAppwallAd nativeAppwallAd) {
        }

        public void onClick(NativeAppwallBanner nativeAppwallBanner, NativeAppwallAd nativeAppwallAd) {
        }

        public void onDismissDialog(NativeAppwallAd nativeAppwallAd) {
        }
    }

    public static Bundle newArguments(String sectionName) {
        Bundle args = new Bundle();
        args.putString("extra_section_name", sectionName);
        return args;
    }

    public String getSectionName() {
        return getArguments().getString("extra_section_name");
    }

    protected int getLayoutId() {
        return 2130903110;
    }

    protected CharSequence getTitle() {
        String section = this.targetAdapter.getTitle();
        return (section == null || TextUtils.isEmpty(section)) ? super.getTitle() : section;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LocalizationManager.from(getActivity());
        this.mMainView = LocalizationManager.inflate(getActivity(), 2130903110, container, false);
        this.bannersList = (HideTabbarListView) this.mMainView.findViewById(2131624636);
        this.targetAdapter = TargetUtils.createTargetAdapter(getActivity());
        this.targetAdapter.setListener(new C02921());
        this.targetAdapter.load();
        SmartEmptyView emptyView = (SmartEmptyView) this.mMainView.findViewById(C0263R.id.empty_view);
        emptyView.setWebState(WebState.EMPTY);
        this.bannersList.setEmptyView(emptyView);
        this.bannersList.setOnItemClickListener(this);
        return this.mMainView;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.targetAdapter.handleBannerClick(((BannersAdapter) parent.getAdapter()).getBanner(position));
    }
}
