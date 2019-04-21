package de.florianm.android.mvitest.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

import de.florianm.android.mvitest.R
import io.reactivex.disposables.CompositeDisposable

import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    private val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        disposable.add(viewModel.stateObservable.subscribe {
            onStateChange(it)
        })

        disposable.add(viewModel.effectObservable.subscribe {
            it.apply()
        })

        btn_plus_one.setOnClickListener { viewModel.onPlusOneClicked() }
        btn_reset.setOnClickListener { viewModel.onResetClicked() }
    }

    private fun onStateChange(state: HomeState) {
        txt_plus_one.text = state.counter.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
