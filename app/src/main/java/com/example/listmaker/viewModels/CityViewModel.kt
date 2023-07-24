package com.example.listmaker.viewModels

import androidx.lifecycle.*
import com.example.listmaker.daoObjects.CityDao
import com.example.listmaker.models.City
import kotlinx.coroutines.launch

class CityViewModel(private val itemDao: CityDao,
                    private val landmarkViewModel: LandmarkViewModel
                    ) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allCities: LiveData<List<City>> = itemDao.getItems().asLiveData()



    fun updateCity(
        cityId: Int, cityName: String, cityDescription: String
    ) {
        val updatedCity = getUpdatedCityEntry(cityId, cityName, cityDescription)
        updateCity(updatedCity)
    }


    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateCity(item: City) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewCity(cityName: String, cityDescription: String) {
        val newCity = getNewCityEntry(cityName, cityDescription)
        insertCity(newCity)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertCity(item: City) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteCity(item: City) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveCity(cityId: Int): LiveData<City> {
        return itemDao.getItem(cityId).asLiveData()
    }


    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(cityName: String, cityDescription: String): Boolean {
        if (cityName.isBlank() || cityDescription.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [City] entity class with the item info entered by the user.
     * This will be used to add a new entry to the City database.
     */
    private fun getNewCityEntry(cityName: String,cityDescription: String): City {
        return City(
            cityName = cityName,
            cityDescription = cityDescription
        )
    }

    /**
     * Called to update an existing entry in the Inventory database.
     * Returns an instance of the [City] entity class with the item info updated by the user.
     */
    private fun getUpdatedCityEntry(
        cityId: Int,
        cityName: String,
        cityDescription: String
    ): City {
        return City(
            cityId = cityId,
            cityName = cityName,
            cityDescription = cityDescription
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class CityViewModelFactory(private val itemDao: CityDao,
                           private val landmarkViewModel: LandmarkViewModel
                            ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CityViewModel(itemDao,landmarkViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}