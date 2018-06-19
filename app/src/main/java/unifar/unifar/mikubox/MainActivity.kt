package unifar.unifar.mikubox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory
import com.amazonaws.regions.Regions
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import unifar.unifar.mikubox.lambdaeventgenerator.NicoNicoInterface
import unifar.unifar.mikubox.lambdaeventgenerator.YoutubeInterface
import com.google.android.gms.ads.InterstitialAd


class MainActivity : AppCompatActivity() {

    private var nicoNicoUri: Uri? = null
    private var youTubeUri: Uri? = null

    private lateinit var mInterstitialAd : InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeAWS()
        initializeAdMob()

        val niconicoTextView = findViewById<Button>(R.id.main_niconicoTextView)
        val youtubeTextView = findViewById<Button>(R.id.main_youtubeTextView)

        val handlerThread = HandlerThread("networkThread").apply { start() }

        niconicoTextView.setOnClickListener {
            nicoNicoUri?.let {
                val i = Intent(Intent.ACTION_VIEW, nicoNicoUri)
                showInterStitialAd()

                Handler(handlerThread.looper).postDelayed(
                        {
                            startActivity(i)
                            nicoNicoUri = Uri.parse(niconicoInterface?.GetRandomNicoNicoLink()?.link)
                            Log.d("miku", "niconicoReloaded")
                        }
                        ,5000)
            }
        }

        youtubeTextView.setOnClickListener {
            youTubeUri?.let {
                val i = Intent(Intent.ACTION_VIEW, youTubeUri)
                showInterStitialAd()
                Handler(handlerThread.looper).postDelayed(
                        {
                            startActivity(i)
                            youTubeUri = Uri.parse(youtubeInterface?.GetRandomYoutubeLink()?.link)
                            Log.d("miku", "youtubeReloaded")
                        }
                        , 5000)
            }
        }
    }

    private fun initializeAdMob() {
        mInterstitialAd = InterstitialAd(this).apply {
            adUnitId = resources.getString(R.string.INTERSTITIAL_AD_TEST_ID)
            loadAd(AdRequest.Builder().build())
            adListener = object : AdListener() {
                override fun onAdClosed() {
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }
            }
        }
    }

    private fun showInterStitialAd() {
        mInterstitialAd.let {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.d("miku", "The interstitial wasn't loaded yet.")
            }
        }

    }


    private var niconicoInterface: NicoNicoInterface? = null

    private var youtubeInterface: YoutubeInterface? = null

    private fun initializeAWS(){

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
         niconicoInterface = factory.build(NicoNicoInterface::class.java)
         youtubeInterface = factory.build(YoutubeInterface::class.java)


        Thread {
            nicoNicoUri = Uri.parse(niconicoInterface?.GetRandomNicoNicoLink()?.link)
            Log.d("miku", "niconicoLoaded")
        }.start()
        Thread{
            youTubeUri = Uri.parse(youtubeInterface?.GetRandomYoutubeLink()?.link)
            Log.d("miku", "youtubeLoaded")
        }.start()
    }
}
