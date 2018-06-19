package unifar.unifar.mikubox

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory
import com.amazonaws.regions.Regions
import unifar.unifar.mikubox.lambdaeventgenerator.NicoNicoInterface
import unifar.unifar.mikubox.lambdaeventgenerator.YoutubeInterface


class MainActivity : AppCompatActivity() {

    private var nicoNicoUri: Uri? = null
    private var youTubeUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val niconicoInterface = factory.build(NicoNicoInterface::class.java)
        val youtubeInterface = factory.build(YoutubeInterface::class.java)
        Thread {
            nicoNicoUri = Uri.parse(niconicoInterface.GetRandomNicoNicoLink().link)
            Log.d("miku", "niconicoLoaded")
        }.start()
        Thread{
            youTubeUri = Uri.parse(youtubeInterface.GetRandomYoutubeLink().link)
            Log.d("miku", "youtubeLoaded")
        }.start()


        setContentView(R.layout.activity_main)


// The Lambda function invocation results in a network call.
// Make sure it is not called from the main thread.

        val niconicoTextView = findViewById<Button>(R.id.main_niconicoTextView)
        val youtubeTextView = findViewById<Button>(R.id.main_youtubeTextView)




        niconicoTextView.setOnClickListener {
            nicoNicoUri?.let{
                val i = Intent(Intent.ACTION_VIEW, nicoNicoUri)
                startActivity(i)
            Thread {
                nicoNicoUri = Uri.parse(niconicoInterface.GetRandomNicoNicoLink().link)
                Log.d("miku", "niconicoReloaded")
            }.start()
            }

        }

        youtubeTextView.setOnClickListener {
            youTubeUri?.let {
                val i = Intent(Intent.ACTION_VIEW, youTubeUri)
                startActivity(i)
                Thread {
                    youTubeUri = Uri.parse(niconicoInterface.GetRandomNicoNicoLink().link)
                    Log.d("miku", "youtubeReloaded")
                }.start()
            }



        }

    }
}
