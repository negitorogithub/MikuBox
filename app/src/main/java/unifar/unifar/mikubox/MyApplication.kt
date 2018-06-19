package unifar.unifar.mikubox

import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobile.auth.core.IdentityManager
import android.support.multidex.MultiDexApplication
import com.amazonaws.auth.AWSCredentialsProvider
import com.google.android.gms.ads.MobileAds


/**
 * Application class responsible for initializing singletons and other common components.
 */
public class MyApplication : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        initializeApplication()
    }

    private fun initializeApplication() {
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID))

    }

    companion object {
        private val LOG_TAG = MyApplication::class.java.simpleName
    }
}