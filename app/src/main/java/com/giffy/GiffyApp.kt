package com.giffy

import android.app.Application
import com.giffy.service.GiffyRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GiffyApp : Application() {

    @Inject
    lateinit var repository: GiffyRepository
}
