package tech.soc.soar

import android.app.Application
import tech.soc.soar.di.AppDependencies

class SoarApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppDependencies.initialize(
            context = this
        )
    }
}