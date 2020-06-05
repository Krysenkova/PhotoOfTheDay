package com.example.photooftheday.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import coil.api.load
import com.example.photooftheday.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val observer = Observer<PictureOfTheDayData> { renderData(it) }
        viewModel.getData().observe(viewLifecycleOwner, observer)
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                if (url.isNullOrEmpty()) {
                    //showDialog( "Error","Message from server is empty")
                } else {
                    image_view.load(url)
                    date.text = serverResponseData.date
                    message.text = serverResponseData.explanation
                    picture_title.text = serverResponseData.title
                }
            }
            is PictureOfTheDayData.Loading -> {
                //showViewLoading()
            }
            is PictureOfTheDayData.Error -> {
                //showDialog("Error", data.error.message)
            }
        }
    }
    companion object {
        fun newInstance()= MainFragment()
    }
}
