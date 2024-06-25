package com.android.burakgunduz.bitirmeprojesi.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class CountryInfo(
    val name: String,
    val countryCode: String
)

data class CityInfo(
    val name: String,
    val cityCode: String
)
data class StreetInfo(
    val name: String,
    val streetCode: String
)

data class TownInfo(
    val name: String,
    val districtCode: String
)


class LocationViewModel : ViewModel() {
    val countryNames = MutableLiveData<List<CountryInfo>>()
    val cityNames = MutableLiveData<MutableList<CityInfo>>()
    private val streetNames = MutableLiveData<MutableList<StreetInfo>>()
    val townNames = MutableLiveData<MutableList<TownInfo>>()
    suspend fun fetchCountryNames() =
        withContext(Dispatchers.IO) {
            val urlString =
                "http://api.geonames.org/search?username=burakgunduz22&featureCode=PCLI&type=json&maxRows=193"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                // Parse the JSON response to get the country names
                val jsonObject = JSONObject(response)
                val updatedList = countryNames.value?.toMutableList() ?: mutableListOf()
                if (jsonObject.has("geonames")) {
                    val geonamesArray = jsonObject.getJSONArray("geonames")
                    updatedList.clear()
                    for (i in 0 until geonamesArray.length()) {
                        val countryObject = geonamesArray.getJSONObject(i)
                        val countryName = countryObject.getString("name")
                        val countryCode = countryObject.getString("countryId")
                        val countryObj = CountryInfo(countryName, countryCode)
                        updatedList.add(countryObj)
                        println("countryName = $countryName, countryCode = $countryCode")
                    }
                    withContext(Dispatchers.Main) {
                        updatedList.sortWith(compareBy { it.name })
                        countryNames.value = updatedList
                        println(updatedList.size)
                    }
                } else {
                    println("No geonames found in the response")
                }
            } finally {
                connection.disconnect()
            }
        }

    suspend fun fetchCitiesOfCountry(
        country: String,
        countryList: List<CountryInfo>,
    ) =
        withContext(Dispatchers.IO) {
            val filterCountryCode = countryList.filter { it.name == country }
            val countryCode = if (filterCountryCode.isNotEmpty()) {
                filterCountryCode[0].countryCode
            } else {
                return@withContext 0
            }
            val urlString =
                "http://api.geonames.org/childrenJSON?geonameId=$countryCode&username=burakgunduz22"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                // Parse the JSON response to get the city names
                val jsonObject = JSONObject(response)
                val updatedList = MutableList(0) { CityInfo("", "") }
                if (jsonObject.has("geonames")) {
                    val geonamesArray = jsonObject.getJSONArray("geonames")
                    for (i in 0 until geonamesArray.length()) {
                        val cityObject = geonamesArray.getJSONObject(i)
                        val cityName = cityObject.getString("name")
                        val cityCode = cityObject.getString("geonameId")
                        val cityObj = CityInfo(cityName, cityCode)
                        updatedList.add(cityObj)
                        println("cityName = $cityName, cityCode = $cityCode")
                    }
                    withContext(Dispatchers.Main) {
                        updatedList.sortWith(compareBy { it.name })
                        cityNames.value?.clear()
                        cityNames.value = updatedList
                        println(cityNames.value?.size)
                    }
                } else {
                    println("No geonames found in the response")
                }
            } finally {
                connection.disconnect()
            }
        }
    suspend fun fetchTownOfCity(
        city: String,
        cityList: MutableList<CityInfo>,
    ) =
        withContext(Dispatchers.IO) {
            val filterCityCode = cityList.filter { it.name == city }
            val cityCode = if (filterCityCode.isNotEmpty()) {
                filterCityCode[0].cityCode
            } else {
                return@withContext 0
            }
            val urlString =
                "http://api.geonames.org/childrenJSON?geonameId=$cityCode&username=burakgunduz22"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                // Parse the JSON response to get the district names
                val jsonObject = JSONObject(response)
                val updatedList = MutableList(0) { TownInfo("", "") }
                if (jsonObject.has("geonames")) {
                    val geonamesArray = jsonObject.getJSONArray("geonames")
                    for (i in 0 until geonamesArray.length()) {
                        val districtObject = geonamesArray.getJSONObject(i)
                        val districtName = districtObject.getString("name")
                        val districtCode = districtObject.getString("geonameId")
                        val districtObj = TownInfo(districtName, districtCode)
                        updatedList.add(districtObj)
                        println("districtName = $districtName, districtCode = $districtCode")
                    }
                    withContext(Dispatchers.Main) {
                        updatedList.sortWith(compareBy { it.name })
                        townNames.value?.clear()
                        townNames.value = updatedList
                        println(townNames.value?.size)
                    }
                } else {
                    println("No geonames found in the response")
                }
            } finally {
                connection.disconnect()
            }
        }
    suspend fun fetchStreetsOfDistrict(
        district: String,
        districtList: List<TownInfo>,
    ) =
        withContext(Dispatchers.IO) {
            val filterDistrictCode = districtList.filter { it.name == district }
            val districtCode = if (filterDistrictCode.isNotEmpty()) {
                filterDistrictCode[0].districtCode
            } else {
                return@withContext 0
            }
            val urlString =
                "http://api.your-api.com/streets?districtId=$districtCode"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                // Parse the JSON response to get the street names
                val jsonObject = JSONObject(response)
                val updatedList = MutableList(0) { StreetInfo("", "") }
                if (jsonObject.has("streets")) {
                    val streetsArray = jsonObject.getJSONArray("streets")
                    for (i in 0 until streetsArray.length()) {
                        val streetObject = streetsArray.getJSONObject(i)
                        val streetName = streetObject.getString("name")
                        val streetCode = streetObject.getString("streetId")
                        val streetObj = StreetInfo(streetName, streetCode)
                        updatedList.add(streetObj)
                        println("streetName = $streetName, streetCode = $streetCode")
                    }
                    withContext(Dispatchers.Main) {
                        updatedList.sortWith(compareBy { it.name })
                        streetNames.value?.clear()
                        streetNames.value = updatedList
                        println(streetNames.value?.size)
                    }
                } else {
                    println("No streets found in the response")
                }
            } finally {
                connection.disconnect()
            }
        }
}
