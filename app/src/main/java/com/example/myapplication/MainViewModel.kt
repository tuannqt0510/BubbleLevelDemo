package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    private val _flow1: MutableStateFlow<List<String>> = MutableStateFlow(arrayListOf())
    val flow1 : Flow<List<String>> = _flow1
}