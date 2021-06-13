package stanislav.kleinikov.exchangerate.presentation.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import by.kirich1409.viewbindingdelegate.viewBinding
import stanislav.kleinikov.exchangerate.R
import stanislav.kleinikov.exchangerate.databinding.FragmentMainBinding
import stanislav.kleinikov.exchangerate.domain.Resource
import stanislav.kleinikov.exchangerate.presentation.adapter.BaseRecyclerAdapter
import stanislav.kleinikov.exchangerate.presentation.settings.SettingsFragment

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewBinding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private val viewModel: MainViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        activity?.findViewById<Toolbar>(R.id.toolbar)?.title = getString(R.string.title_main)

        with(viewBinding) {
            swipeContainer.setOnRefreshListener {
                viewModel.updateRates()
            }
            swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorPrimaryLight
            )

            viewModel.rates.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        swipeContainer.isRefreshing = false
                        ratesRv.adapter = BaseRecyclerAdapter(resource.data)
                    }
                    is Resource.Error -> {
                        resource.handleEvent {
                            swipeContainer.isRefreshing = false
                            Toast.makeText(
                                context,
                                getString(R.string.load_failed), Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    is Resource.Loading -> swipeContainer.isRefreshing = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.settings) {
            parentFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fragment_container, SettingsFragment.newInstance())
                addToBackStack(null)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}