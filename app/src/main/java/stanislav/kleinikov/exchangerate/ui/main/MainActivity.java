package stanislav.kleinikov.exchangerate.ui.main;

import android.support.v4.app.Fragment;

import stanislav.kleinikov.exchangerate.ui.FragmentActivity;

public class MainActivity extends FragmentActivity {

    public static final String DEBUG_TAG = "debugTag";

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }
}
