package unifar.unifar.mikubox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory
import com.amazonaws.regions.Regions
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import unifar.unifar.mikubox.lambdaeventgenerator.NicoNicoInterface
import unifar.unifar.mikubox.lambdaeventgenerator.YoutubeInterface
import com.google.android.gms.ads.InterstitialAd


class MainActivity : AppCompatActivity() {

    private var nicoNicoUri: Uri? = null
    private var youTubeUri: Uri? = null
    private lateinit var mAdView : AdView
    private lateinit var mInterstitialAd : InterstitialAd

    private lateinit var niconicoButton: Button
    private lateinit var youtubeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeAWS()
        initializeAdMob()
        val adRequest = AdRequest.Builder().build()
        mAdView = findViewById(R.id.main_banner)
        mAdView.loadAd(adRequest)


        niconicoButton = findViewById<Button>(R.id.main_niconicoTextView)
        youtubeButton = findViewById<Button>(R.id.main_youtubeTextView)

        val handlerThread = HandlerThread("networkThread").apply { start() }

        niconicoButton.setOnClickListener {
            nicoNicoUri?.let {
                hideAllButtons()
                Handler().postDelayed(
                        {
                            showAllButtons()
                        }
                        ,5000)

                val i = Intent(Intent.ACTION_VIEW, nicoNicoUri)
                showInterStitialAd()
                Toast.makeText(this,resources.getString(R.string.wait5seconds), Toast.LENGTH_LONG).show()
                Handler(handlerThread.looper).postDelayed(
                        {
                            startActivity(i)
                            nicoNicoUri = Uri.parse(niconicoInterface?.GetRandomNicoNicoLink()?.link)
                            Log.d("miku", "niconicoReloaded")
                        }
                        ,5000)

            }
        }

        youtubeButton.setOnClickListener {
            youTubeUri?.let {
                hideAllButtons()
                Handler().postDelayed(
                        {
                            showAllButtons()
                        }
                        ,5000)
                val i = Intent(Intent.ACTION_VIEW, youTubeUri)
                showInterStitialAd()
                Toast.makeText(this,resources.getString(R.string.wait5seconds), Toast.LENGTH_LONG).show()
                Handler(handlerThread.looper).postDelayed(
                        {
                            startActivity(i)
                            youTubeUri = Uri.parse(youtubeInterface?.GetRandomYoutubeLink()?.link)
                            Log.d("miku", "youtubeReloaded")
                            showAllButtons()
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
    
    private fun hideAllButtons(): Unit {
        niconicoButton.visibility = View.INVISIBLE
        youtubeButton.visibility = View.INVISIBLE
    }
    private fun showAllButtons(): Unit {
        niconicoButton.visibility = View.VISIBLE
        youtubeButton.visibility = View.VISIBLE
    }
}
