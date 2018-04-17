package unifar.unifar.mikubox

import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobile.auth.core.IdentityManager
import android.support.multidex.MultiDexApplication


/**
 * Application class responsible for initializing singletons and other common components.
 */
public class MyApplication : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        initializeApplication()
    }

    private fun initializeApplication() {

        val awsConfiguration = AWSConfiguration(applicationContext)

        // If IdentityManager is not created, create it
        if (IdentityManager.getDefaultIdentityManager() == null) {
            val identityManager = IdentityManager(applicationContext, awsConfiguration)
            IdentityManager.setDefaultIdentityManager(identityManager)
        }

    }

    companion object {
        private val LOG_TAG = MyApplication::class.java.simpleName
    }
}