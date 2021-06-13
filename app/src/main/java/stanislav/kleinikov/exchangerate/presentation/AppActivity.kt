package stanislav.kleinikov.exchangerate.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import stanislav.kleinikov.exchangerate.R
import stanislav.kleinikov.exchangerate.presentation.main.MainFragment

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        setSupportActionBar(findViewById(R.id.toolbar))

        val manager = supportFragmentManager
        val fragment = manager.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            manager.beginTransaction()
                .add(R.id.fragment_container, MainFragment.newInstance())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}