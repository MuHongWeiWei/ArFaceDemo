package com.example.arfacedemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FaceCheckVM : ViewModel() {
    val text = MutableLiveData<String>().apply {
        value = "請點頭"
    }
}