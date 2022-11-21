package io.alcatraz.bloodpressure

import android.app.Application
import android.content.Context

class BloodPressureApplication : Application() {
    var overallContext: Context? = null
        private set

    //TODO : Check string.xml/Setup versionCode/build.gradle when release update
    //TODO : Set Empty View for all adapter views
    override fun onCreate() {
        overallContext = applicationContext
        super.onCreate()
    }

}
