package com.example.drivocare.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivocare.data.Car
import com.example.drivocare.data.Event
import com.example.drivocare.usecase.LoadCarsUseCase
import com.example.drivocare.usecase.LoadEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyCarsViewModel(
    private val loadCarsUseCase: LoadCarsUseCase,
    private val loadEventsUseCase: LoadEventsUseCase
) : ViewModel() {

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    val selectedCarIndex = MutableStateFlow(0)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadCarsForUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            loadCarsUseCase(userId).collectLatest {
                _cars.value = it
                _isLoading.value = false
            }
        }
    }

    fun loadEventsForCar(carId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            loadEventsUseCase(carId).collectLatest {
                _events.value = it
                _isLoading.value = false
            }
        }
    }

}