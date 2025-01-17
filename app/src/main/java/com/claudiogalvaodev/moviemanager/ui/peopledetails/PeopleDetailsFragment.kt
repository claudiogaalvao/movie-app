package com.claudiogalvaodev.moviemanager.ui.peopledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.claudiogalvaodev.moviemanager.R
import com.claudiogalvaodev.moviemanager.databinding.FragmentPeopleDetailsBinding
import com.claudiogalvaodev.moviemanager.ui.adapter.SimplePosterAdapter
import com.claudiogalvaodev.moviemanager.ui.model.MovieModel
import com.claudiogalvaodev.moviemanager.ui.model.PersonModel
import com.claudiogalvaodev.moviemanager.ui.moviedetails.MovieDetailsActivity
import com.claudiogalvaodev.moviemanager.utils.extensions.launchWhenResumed
import com.claudiogalvaodev.moviemanager.utils.format.FormatUtils
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class PeopleDetailsFragment : Fragment() {
    private lateinit var viewModel: PeopleDetailsViewModel
    private val binding by lazy {
        FragmentPeopleDetailsBinding.inflate(layoutInflater)
    }
    private lateinit var moviesAdapter: SimplePosterAdapter

    private val args: PeopleDetailsFragmentArgs by navArgs()

    private val personId by lazy {
        args.personId
    }
    private val leastOneMovieId by lazy {
        args.leastOneMovieId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel { parametersOf(personId, leastOneMovieId) }
        viewModel.isFirstLoading = true

        getPeopleDetails()
        getMovies()
        setupAdapter()
        setupRecyclerView()
        setupObservers()
        setListeners()
    }

    private fun getPeopleDetails() {
        viewModel.getPersonDetails()
    }

    private fun getMovies() {
        viewModel.getMovies()
    }

    private fun bindHeaderInfo(person: PersonModel?) {
        person?.let {
            Picasso.with(binding.root.context).load(it.getProfileImageUrl())
                .into(binding.fragmentPeopleDetailsHeader.fragmentPeopleDetailsProfilePhoto)
            binding.fragmentPeopleDetailsHeader.fragmentPeopleDetailsName.text = it.name
            binding.fragmentPeopleDetailsHeader.fragmentPeopleDetailsDepartment.text = it.knownForDepartment
        }
    }

    private fun bindPersonDetailsInfo(person: PersonModel?) {
        person?.let {
            if(it.biography.isNullOrEmpty()) {
                binding.fragmentPeopleDetailsBiographyLabel.visibility = View.GONE
            } else {
                binding.fragmentPeopleDetailsBiographyLabel.visibility = View.VISIBLE
                binding.fragmentPeopleDetailsBiography.text = it.biography
            }
            person.birthday?.let { birthday ->
                binding.fragmentPeopleDetailsHeader.fragmentPeopleDetailsBirthdate.text =
                    FormatUtils.dateFromAmericanFormatToDateWithMonthName(birthday)
            }

            binding.fragmentPeopleDetailsHeader.fragmentPeopleDetailsAge.text = if(!person.deathday.isNullOrEmpty()) {
                "${context?.getString(R.string.separator_bullet)} ${FormatUtils.dateFromAmericanFormatToDateWithMonthName(person.deathday)}"
            } else {
                person.birthday?.let { birthday ->
                    context?.getString(R.string.age_label, FormatUtils.dateFromAmericanFormatToAge(birthday))
                }
            }

            binding.fragmentPeopleDetailsHeader.fragmentPeopleDetailsBirthplace.text = person.placeOfBirth
        }
    }

    private fun setupAdapter() {
        moviesAdapter = SimplePosterAdapter().apply {
            onItemClick = { movieId ->
                val destination = findNavController().previousBackStackEntry?.destination?.displayName.toString()
                if(movieId == leastOneMovieId.toInt() && destination.contains("movieDetailsFragment")) {
                    findNavController().popBackStack()
                } else {
                    goToMovieDetails(movieId)
                }
            }
            onFullyViewedListener = {
                if(!viewModel.isMoviesLoading) {
                    viewModel.isMoviesLoading = true
                    getMovies()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val layout = GridLayoutManager(context, calcNumberOfColumns())
        binding.fragmentPeopleDetailsMoviesRecyclerview.apply {
            layoutManager = layout
            adapter = moviesAdapter
        }
    }

    private fun setupObservers() {
        launchWhenResumed {
            viewModel.movies.collectLatest { movies ->
                setMoviesList(movies)
                if(viewModel.getSecondPage) {
                    getMovies()
                }
            }
        }

        launchWhenResumed {
            viewModel.personDetails.collectLatest { person ->
                bindHeaderInfo(person)
                bindPersonDetailsInfo(person)
            }
        }
    }

    private fun setListeners() {
        binding.fragmentPeopleDetailsBiography.setOnClickListener {
            binding.fragmentPeopleDetailsBiography.apply {
                maxLines = Integer.MAX_VALUE
                isClickable = false
            }

        }
    }

    private fun setMoviesList(movieModels: List<MovieModel>) {
        moviesAdapter.submitList(movieModels)
    }

    private fun goToMovieDetails(movieId: Int) {
        context?.let {
            startActivity(MovieDetailsActivity.newInstance(it, movieId, ""))
        }
    }

    private fun calcNumberOfColumns(): Int {
        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density

        val spaceBetween = 12
        val marginStart = 16
        val marginEnd = 16
        val widthEachImage = 120

        var countImages = dpWidth - marginStart - marginEnd
        countImages /= (widthEachImage+spaceBetween)
        val numberOfColumns = countImages.roundToInt()
        if(numberOfColumns > 4) {
            viewModel.getSecondPage = true
        }
        return numberOfColumns
    }
}