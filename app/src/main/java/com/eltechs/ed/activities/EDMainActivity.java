package com.eltechs.ed.activities;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.design.widget.NavigationView.*;
import android.support.v4.app.*;
import android.support.v4.app.FragmentManager.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.eltechs.axs.*;
import com.eltechs.axs.ExagearImageConfiguration.*;
import com.eltechs.axs.activities.*;
import com.eltechs.axs.applicationState.*;
import com.eltechs.axs.helpers.*;
import com.eltechs.ed.*;
import com.eltechs.ed.fragments.*;
import com.eltechs.ed.fragments.AdvancedOptionsFragment.*;
import com.eltechs.ed.fragments.ChooseFileFragment.*;
import com.eltechs.ed.fragments.ChoosePackagesDFragment.*;
import com.eltechs.ed.fragments.ChooseRecipeFragment.*;
import com.eltechs.ed.startupActions.*;
import com.eltechs.ed.startupActions.StartGuest.*;
import com.eltechs.ed.startupActions.WDesktop.*;
import com.kdt.eltechsaxs.*;
import java.io.*;
import java.util.*;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

public class EDMainActivity<StateClass extends ApplicationStateBase<StateClass>> extends FrameworkActivity<StateClass> implements OnRecipeSelectedListener, OnFileSelectedListener, OnPackagesSelectedListener, OnAdvanceSelectedListener {
    private static final String FRAGMENT_TAG_CHOOSE_FILE = "CHOOSE_FILE";
    private static final String FRAGMENT_TAG_CONTAINER_PROP = "CONTAINER_PROP";
    private static final String FRAGMENT_TAG_DESKTOP = "DESKTOP";
    private static final String FRAGMENT_TAG_INSTALL_NEW = "INSTALL_NEW";
    private static final String FRAGMENT_TAG_MANAGE_CONTAINERS = "MANAGE_CONTAINERS";
    private static final String FRAGMENT_TAG_ADVANCED_OPTIONS= "ADVANCED_OPTIONS";
    private static final String FRAGMENT_TAG_START_MENU = "START_MENU";
    private static final int ON_START_ACTION_SHOW_MANAGE_CONTAINERS = 0;
    private static final String TAG = "EDMainActivity";
    private static final File mUserAreaDir = new File(AndroidHelpers.getMainSDCard(), "Download");
    private AppConfig mAppCfg = AppConfig.getInstance(this);
    private InstallRecipe mChosenRecipe;
    /* access modifiers changed from: private */
    public DrawerLayout mDrawerLayout;
    private boolean mIsHomeActionBack;
    private NavigationView mNavigationView;

	private Fragment[] fragments;
	// mChooseXDGLinkFrag, mChooseRecipeFrag, mManageContainersFrag, mAdvancedOptionsFrag;
    private class BackStackChangedListener implements OnBackStackChangedListener {
        private BackStackChangedListener() {
        }

        public void onBackStackChanged() {
            EDMainActivity.this.changeUIByCurFragment();
        }
    }

    private class NavigationItemSelectedListener implements OnNavigationItemSelectedListener {
        private NavigationItemSelectedListener() {
        }

        public boolean onNavigationItemSelected(MenuItem menuItem) {
            String str = null;
            int fragment = -1;
            boolean r2 = false;
            switch (menuItem.getItemId()) {
                case R.id.ed_main_menu_desktop /*2131296369*/:
                    menuItem.setChecked(true);
                    fragment = 0;
                    str = EDMainActivity.FRAGMENT_TAG_DESKTOP;
                    break;
                case R.id.ed_main_menu_start_menu /*2131296373*/:
                    menuItem.setChecked(true);
                    fragment = 1;
                    str = EDMainActivity.FRAGMENT_TAG_START_MENU;
                    break;
                case R.id.ed_main_menu_install_new /*2131296371*/:
                    menuItem.setChecked(true);
					fragment = 2;
                    str = EDMainActivity.FRAGMENT_TAG_INSTALL_NEW;
                    break;
                case R.id.ed_main_menu_manage_containers /*2131296372*/:
                    menuItem.setChecked(true);
					fragment = 3;
                    str = EDMainActivity.FRAGMENT_TAG_MANAGE_CONTAINERS;
                    break;
				case R.id.ed_main_menu_advanced_options: 
					menuItem.setChecked(true);
					fragment = 4;
					str = EDMainActivity.FRAGMENT_TAG_ADVANCED_OPTIONS;
					break;
				/*
                case R.id.ed_main_menu_help:
                    EDMainActivity.this.startActivity(EDHelpActivity.class);
                    break;
				*/
            }
            // fragment = null;
            // str = null;
            if (fragment >= 0) {
                FragmentManager supportFragmentManager = EDMainActivity.this.getSupportFragmentManager();
				int i = 0;
                while (i < supportFragmentManager.getBackStackEntryCount()) {
                    supportFragmentManager.popBackStack();
                    i++;
                }
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.replace(R.id.ed_main_fragment_container, fragments[fragment], str);
                beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                if (menuItem.getItemId() != R.id.ed_main_menu_desktop) {
                    beginTransaction.addToBackStack(null);
                }
                beginTransaction.commit();
                r2 = true;
            }
            EDMainActivity.this.mDrawerLayout.closeDrawers();
            return r2;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.ed_main);
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.ed_main_drawer);
        this.mNavigationView = (NavigationView) findViewById(R.id.ed_main_nav_view);
        NavigationItemSelectedListener navigationItemSelectedListener = new NavigationItemSelectedListener();
        this.mNavigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        setSupportActionBar((Toolbar) findViewById(R.id.ed_main_toolbar));
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator((int) R.drawable.ic_menu_24dp);
        getSupportFragmentManager().addOnBackStackChangedListener(new BackStackChangedListener());
		
		this.fragments = new Fragment[5];
		
    }
	
	@Override
	public void onAdvanceSelected(InstallRecipe recipe) {
		if (!recipe.mAdvancedName.equals("undefined")) {
			switch (recipe.mAdvancedName) {
				case "reinstall": {
					new File(((ExagearImageAware) getApplicationState()).getExagearImage().getPath(), ExagearImagePaths.IMG_VERSION).delete();
					signalUserInteractionFinished(UserRequestedAction.GO_FURTHER);
				} break;
				case "runlinux": {
					// signalUserInteractionFinished(UserRequestedAction.GO_FURTHER);
				} break;
			}
		}
	}

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        changeUIByCurFragment();
    }

    private void setHomeIsActionBack(boolean z) {
        this.mIsHomeActionBack = z;
        getSupportActionBar().setHomeAsUpIndicator(this.mIsHomeActionBack ? 0 : R.drawable.ic_menu_24dp);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006d, code lost:
        if (r0.equals(FRAGMENT_TAG_DESKTOP) != false) goto L_0x0071;
     */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0090  */
    public void changeUIByCurFragment() {
        String tag = getSupportFragmentManager().findFragmentById(R.id.ed_main_fragment_container).getTag();
        char c = 0;
        if (tag == null) {
            setHomeIsActionBack(false);
            return;
        }
        if (tag.equals(FRAGMENT_TAG_CONTAINER_PROP) || tag.equals(FRAGMENT_TAG_CHOOSE_FILE)) {
            setHomeIsActionBack(true);
        } else {
            setHomeIsActionBack(false);
        }
        int hashCode = tag.hashCode();
        if (hashCode != -2019877892) {
            if (hashCode != -1058712772) {
                if (hashCode != -487288020) {
                    if (hashCode == 2807996 && tag.equals(FRAGMENT_TAG_INSTALL_NEW)) {
                        c = 2;
                        switch (c) {
                            case 0:
                                this.mNavigationView.setCheckedItem(R.id.ed_main_menu_desktop);
                                break;
                            case 1:
                                this.mNavigationView.setCheckedItem(R.id.ed_main_menu_start_menu);
                                break;
                            case 2:
                                this.mNavigationView.setCheckedItem(R.id.ed_main_menu_install_new);
                                break;
                            case 3:
                                this.mNavigationView.setCheckedItem(R.id.ed_main_menu_manage_containers);
                                break;
                        }
                    }
                } else if (tag.equals(FRAGMENT_TAG_MANAGE_CONTAINERS)) {
                    c = 3;
                    switch (c) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                    }
                }
            } else if (tag.equals(FRAGMENT_TAG_START_MENU)) {
                c = 1;
                switch (c) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        }
        c = 65535;
        switch (c) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mIsHomeActionBack) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.mDrawerLayout.openDrawer((int) GravityCompat.START);
        }
        return true;
    }

    public void onRecipeSelected(InstallRecipe installRecipe) {
        this.mChosenRecipe = installRecipe;
        ChooseFileFragment chooseFileFragment = new ChooseFileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ChooseFileFragment.ARG_ROOT_PATH, mUserAreaDir.getAbsolutePath());
        bundle.putString(ChooseFileFragment.ARG_DOWNLOAD_URL, this.mChosenRecipe.getDownloadURL());
        chooseFileFragment.setArguments(bundle);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.ed_main_fragment_container, chooseFileFragment, FRAGMENT_TAG_CHOOSE_FILE);
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
    }

    public void onFileSelected(String str) {
        getApplicationState().getStartupActionsCollection().addAction(new StartGuest());
        signalUserInteractionFinished(UserRequestedAction.GO_FURTHER);
    }
/*
    public void onContRunGuideRes(boolean z) {
        if (this.mChoosenXDGLink != null) {
            startXDGLink(this.mChoosenXDGLink);
        }
    }

    private void startXDGLink(XDGLink xDGLink) {
        getApplicationState().getStartupActionsCollection().addAction(new StartGuest(new RunXDGLink(xDGLink)));
        signalUserInteractionFinished(UserRequestedAction.GO_FURTHER);
    }
*/
    public void onPackagesSelected(List<ContainerPackage> list) {
        // getApplicationState().getStartupActionsCollection().addAction(new StartGuest(new InstallPackage(this.mChoosenCont, list)));
        this.mAppCfg.setEDMainOnStartAction(Integer.valueOf(0));
        signalUserInteractionFinished(UserRequestedAction.GO_FURTHER);
    }

    public void onManageContainerSettingsClick() {
        ContainerSettingsFragment containerSettingsFragment = new ContainerSettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("CONT_ID", 0l);
        containerSettingsFragment.setArguments(bundle);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.ed_main_fragment_container, containerSettingsFragment, FRAGMENT_TAG_CONTAINER_PROP);
        beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
    }
}
