package com.example.listmaker.viewModels

import androidx.lifecycle.*
import com.example.listmaker.daoObjects.LandmarkDao
import com.example.listmaker.models.City
import com.example.listmaker.models.Landmark
import kotlinx.coroutines.launch

class LandmarkViewModel(private val itemDao: LandmarkDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<Landmark>> = itemDao.getItems().asLiveData()

    fun updateLandmark(

        landmarkName: String,
        landmarkDescription: String,
    ) {
        val updatedLandmark = getUpdatedLandmarkEntry(landmarkName, landmarkDescription)
        updateLandmark(updatedLandmark)
    }


    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateLandmark(item: Landmark) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewLandmark(landmarkName: String, landmarkDescription: String) {
        val newLandmark = getNewLandmarkEntry(landmarkName,landmarkDescription)
        insertLandmark(newLandmark)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertLandmark(item: Landmark) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteLandmark(item: Landmark) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveLandmark(landmarkName: String): LiveData<Landmark> {
        return itemDao.getItem(landmarkName).asLiveData()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(landmarkName: String,landmarkDescription: String): Boolean {
        if (landmarkName.isBlank() || landmarkDescription.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [City] entity class with the item info entered by the user.
     * This will be used to add a new entry to the City database.
     */
    private fun getNewLandmarkEntry(landmarkName: String,landmarkDescription: String): Landmark {
        return Landmark(
            landmarkName = landmarkName,
            landmarkDescription = landmarkDescription
        )
    }

    /**
     * Called to update an existing entry in the Inventory database.
     * Returns an instance of the [City] entity class with the item info updated by the user.
     */
    private fun getUpdatedLandmarkEntry(
        landmarkName: String,
        landmarkDescription: String
    ): Landmark {
        return Landmark(
            landmarkName = landmarkName,
            landmarkDescription = landmarkDescription
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class LandmarkViewModelFactory(private val itemDao: LandmarkDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LandmarkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LandmarkViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}