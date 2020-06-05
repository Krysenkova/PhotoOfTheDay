package com.example.photooftheday.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainViewModel(
    private val liveDataForViewToObserve: MutableLiveData<PictureOfTheDayData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) : ViewModel() {
    fun getData(): LiveData<PictureOfTheDayData> { //LiveData - realisation of concept observer/observable
        sendServerRequest()                        //LiveData - data you are subscribed to
        return liveDataForViewToObserve
    }

    private fun sendServerRequest() {
        liveDataForViewToObserve.value = PictureOfTheDayData.Loading(null)
        retrofitImpl.getRetrofitImpl().getPictureOfTheDay("dd3Zl6fEHnn2qf4ItbosfwyfTOrdScSdobDfbVbN")
            .enqueue(object : //asynchronous request
                Callback<PODServerPResponseData> {
                override fun onResponse(
                    call: Call<PODServerPResponseData>,
                    response: Response<PODServerPResponseData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        liveDataForViewToObserve.value =
                            PictureOfTheDayData.Success(response.body()!!)
                    } else {
                        liveDataForViewToObserve.value =
                            PictureOfTheDayData.Error(Throwable("Unidentified error"))
                    }
                }

                override fun onFailure(call: Call<PODServerPResponseData>, t: Throwable) {
                    liveDataForViewToObserve.value = PictureOfTheDayData.Error(t)
                }
            })
    }
}

//DataModel for saving data from the Internet
data class PODServerPResponseData(
    val date: String?,
    val explanation: String?,
    val hdurl: String?,
    val media_type: String?,
    val service_version: String?,
    val title: String?,
    val url: String?
)

//State Model
//sealed = enum, but with different constructors for the members of the class
sealed class PictureOfTheDayData {
    data class Success(val serverResponseData: PODServerPResponseData) : PictureOfTheDayData()
    data class Error(val error: Throwable) : PictureOfTheDayData()
    data class Loading(val progress: Int?) : PictureOfTheDayData()
}

interface PictureOfTheDayAPI { //using Retrofit
    @GET("planetary/apod") //endpoint
    fun getPictureOfTheDay(@Query("api_key") apiKey: String): Call<PODServerPResponseData>
}

class PODRetrofitImpl {
    fun getRetrofitImpl(): PictureOfTheDayAPI {
        val podRetrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(
                GsonConverterFactory.create( //convert data from gson file
                    GsonBuilder().setLenient().create()
                )
            )
            .build()
        return podRetrofit.create(PictureOfTheDayAPI::class.java)
    }
}