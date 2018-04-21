package unifar.unifar.mikubox

import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobile.auth.core.IdentityManager
import android.support.multidex.MultiDexApplication
import com.amazonaws.auth.AWSCredentialsProvider




/**
 * Application class responsible for initializing singletons and other common components.
 */
public class MyApplication : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        initializeApplication()
    }

    private fun initializeApplication() {

        val awsConfig = AWSConfiguration(applicationContext)

        // If IdentityManager is not created, create it
        if (IdentityManager.getDefaultIdentityManager() == null) {
            val awsConfiguration = AWSConfiguration(applicationContext)
            val identityManager = IdentityManager(applicationContext, awsConfiguration)
            IdentityManager.setDefaultIdentityManager(identityManager)
        }

        // Register identity providers here.
        // With none registered IdentityManager gets unauthenticated AWS credentials


        val credentialsProvider = IdentityManager.getDefaultIdentityManager().credentialsProvider


    }

    companion object {
        private val LOG_TAG = MyApplication::class.java.simpleName
    }
}