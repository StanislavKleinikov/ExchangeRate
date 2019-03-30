package stanislav.kleinikov.exchangerate.ui.main;

import android.support.v4.app.Fragment;

import stanislav.kleinikov.exchangerate.ui.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }
}
