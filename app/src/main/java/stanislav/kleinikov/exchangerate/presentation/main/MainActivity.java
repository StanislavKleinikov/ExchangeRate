package stanislav.kleinikov.exchangerate.presentation.main;


import androidx.fragment.app.Fragment;

import stanislav.kleinikov.exchangerate.presentation.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }
}
