package stanislav.kleinikov.exchangerate.ui.settings;

import android.support.v4.app.Fragment;

import stanislav.kleinikov.exchangerate.ui.FragmentActivity;

public class SettingActivity extends FragmentActivity {
    @Override
    protected Fragment createFragment() {
        return SettingFragment.newInstance();
    }
}
