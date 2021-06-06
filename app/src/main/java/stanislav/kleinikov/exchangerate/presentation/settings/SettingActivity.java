package stanislav.kleinikov.exchangerate.presentation.settings;

import androidx.fragment.app.Fragment;

import stanislav.kleinikov.exchangerate.presentation.FragmentActivity;

public class SettingActivity extends FragmentActivity {
    @Override
    protected Fragment createFragment() {
        return SettingFragment.newInstance();
    }
}
