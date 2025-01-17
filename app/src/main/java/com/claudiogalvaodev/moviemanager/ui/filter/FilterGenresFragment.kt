package com.claudiogalvaodev.moviemanager.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.claudiogalvaodev.moviemanager.databinding.FragmentFilterGenreBinding
import com.claudiogalvaodev.moviemanager.databinding.ItemRadioButtonBinding
import com.claudiogalvaodev.moviemanager.ui.filter.FiltersActivity.Companion.KEY_BUNDLE_CURRENT_VALUE
import com.claudiogalvaodev.moviemanager.utils.extensions.launchWhenResumed
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterGenresFragment: Fragment() {

    private val viewModel: FiltersViewModel by viewModel()
    private val binding by lazy {
        FragmentFilterGenreBinding.inflate(layoutInflater)
    }

    private lateinit var currentValue: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentValue = arguments?.getString(KEY_BUNDLE_CURRENT_VALUE).orEmpty()

        getGenres()
        setObservables()
        setupListener()
    }

    private fun getGenres() {
        viewModel.getAllGenres()
    }

    private fun setObservables() {
        launchWhenResumed {
            viewModel.genres.collectLatest { genres ->
                for(genre in genres) {
                    val newRadioButton = createRadioButton(genre.id, genre.name)
                    binding.fragmentFilterGenreRadiogroup.addView(newRadioButton)
                    if(currentValue == genre.id.toString()) {
                        binding.fragmentFilterGenreRadiogroup.check(newRadioButton.id)
                    }
                }
            }
        }
    }

    private fun createRadioButton(id: Int, text: String): RadioButton {
        val radioButtonBinding = ItemRadioButtonBinding.inflate(layoutInflater)
        val radioButton = radioButtonBinding.filterRadioButton
        radioButton.id = id
        radioButton.text = text
        radioButton.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        if (radioButton.parent != null) (radioButton.parent as ViewGroup).removeView(radioButton)
        return radioButton
    }

    private fun setupListener() {
        binding.fragmentFilterGenreRadiogroup.setOnCheckedChangeListener { _, radioButtonId ->
            val parent = (activity as FiltersActivity)
            parent.changeCurrentValue(radioButtonId.toString())
        }

        binding.filterButtonApply.setOnClickListener {
            (activity as FiltersActivity).saveChangesAndNavigateToPreviousActivity()
        }
    }

}