package unifar.unifar.mikubox

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.amazonaws.mobile.auth.core.IdentityManager
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.auth.policy.actions.DynamoDBv2Actions.Query
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.auth.AWSCredentialsProvider



class MainActivity : AppCompatActivity() {
    private var dynamoDBMapper: DynamoDBMapper? = null

    var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appContext = applicationContext
        val awsConfiguration = AWSConfiguration(applicationContext)
        val credentialsProvider = IdentityManager.getDefaultIdentityManager().credentialsProvider
        userId = IdentityManager(this).cachedUserID
        val dynamoDBClient = AmazonDynamoDBClient(credentialsProvider)
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(awsConfiguration)
                .build()
    }
}
