package unifar.unifar.mikubox

import android.content.AsyncTaskLoader
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory
import com.amazonaws.regions.Regions
import unifar.unifar.mikubox.lambdaeventgenerator.MyInterface
import unifar.unifar.mikubox.lambdaeventgenerator.RequestClass


class MainActivity : AppCompatActivity() {
    //private var dynamoDBMapper: DynamoDBMapper? = null

    //var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Amazon Cognito 認証情報プロバイダーを初期化します
        val credentialsProvider = CognitoCachingCredentialsProvider(
                applicationContext,
                resources.getString(R.string.idpoolid), // ID プールの ID
                Regions.US_EAST_2 // リージョン
        )
// Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
        val factory = LambdaInvokerFactory
                .builder()
                .context(applicationContext)
                .region(Regions.US_EAST_2)
                .credentialsProvider(credentialsProvider)
                .build()

// Create the Lambda proxy object with a default Json data binder.
// You can provide your own data binder by implementing
// LambdaDataBinder.
        val myInterface = factory.build(MyInterface::class.java)

        val request = RequestClass()
// The Lambda function invocation results in a network call.
// Make sure it is not called from the main thread.

        Thread({
            //Log.d("awsLambda",myInterface.GetRandomNicoNicoLink().link)
            val uri = Uri.parse(myInterface.GetRandomNicoNicoLink().link)
            val i = Intent(Intent.ACTION_VIEW,uri)
            startActivity(i)
        }).start()


        // If IdentityManager is not created, create it
        /*
        if (IdentityManager.getDefaultIdentityManager() == null) {
            val awsConfiguration = AWSConfiguration(applicationContext)
            val identityManager = IdentityManager(applicationContext, awsConfiguration)
            IdentityManager.setDefaultIdentityManager(identityManager)
        }

        val appContext = applicationContext
        val awsConfiguration = AWSConfiguration(applicationContext)
        val credentialsProvider = IdentityManager.getDefaultIdentityManager().credentialsProvider
        val dynamoDBClient = AmazonDynamoDBClient(credentialsProvider)
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(awsConfiguration)
                .build()
        readNews()
        */
    }
/*
    private fun readNews() {
        Thread(Runnable {
            val newsItem = dynamoDBMapper?.load(
                    VocaloidSongsMoblieDO::class.java,
                    "A"
                                )

            Log.d("News Item:", newsItem.toString())
        }).start()
    }
    */
}
