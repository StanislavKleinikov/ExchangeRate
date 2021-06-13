package stanislav.kleinikov.exchangerate.presentation.settings

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import by.kirich1409.viewbindingdelegate.viewBinding
import stanislav.kleinikov.exchangerate.R
import stanislav.kleinikov.exchangerate.databinding.FragmentSettingsBinding
import stanislav.kleinikov.exchangerate.domain.Resource
import stanislav.kleinikov.exchangerate.presentation.adapter.BaseRecyclerAdapter
import stanislav.kleinikov.exchangerate.presentation.adapter.BaseRecyclerAdapter.Type.DRAGGABLE

class SettingsFragment : Fragment(), OnStartDragListener {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val viewBinding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SettingsViewModel by viewModel()

    private var touchHelper: ItemTouchHelper? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        activity?.findViewById<Toolbar>(R.id.toolbar)?.title = getString(R.string.title_settings)

        with(viewBinding) {
            viewModel.preferences.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        settingsRecyclerView.adapter = BaseRecyclerAdapter(resource.data, DRAGGABLE)
                    }
                    is Resource.Error -> {
                        resource.handleEvent {
                            Toast.makeText(
                                context,
                                getString(R.string.load_failed), Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    else -> {
                        // ignore other cases
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.apply) {
            viewModel.applyChanges()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStartDrag(viewHolder: ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }
}