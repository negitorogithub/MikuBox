package unifar.unifar.mikubox

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
import com.google.ads.mediation.admob.AdMobAdapter
import hotchemi.android.rate.AppRate
import android.support.design.widget.Snackbar
import com.google.android.gms.ads.*
import unifar.unifar.mikubox.lambdaeventgenerator.NicoNicoInterface
import unifar.unifar.mikubox.lambdaeventgenerator.YoutubeInterface


class MainActivity : AppCompatActivity() {

    private var nicoNicoUri: Uri? = null
    private var youTubeUri: Uri? = null
    private var mAdView : AdView? = null
    private lateinit var mInterstitialAd : InterstitialAd

    private var niconicoButton: Button? = null
    private var youtubeButton: Button? = null

    private lateinit var networkThread: HandlerThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        niconicoButton = findViewById<Button>(R.id.main_niconicoTextView)
        youtubeButton = findViewById<Button>(R.id.main_youtubeTextView)
        mAdView = findViewById(R.id.main_banner)
        networkThread = HandlerThread("networkThread").apply { start() }

        monitorAppRate()

    }

    override fun onStart() {
        super.onStart()

        if (isConnected(this)) {
            initializeAWS()
        }else{
            encourageUserToConnectNet(networkThread)
        }
        setOnclickListeners(networkThread)

        initializeApplication()

        initializeAdMob()
        val extras = Bundle()
        val adRequest = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()
        mAdView?.loadAd(adRequest)
        AppRate.showRateDialogIfMeetsConditions(this)



    }

    private fun setOnclickListeners(networkThread: HandlerThread) {
        niconicoButton?.setOnClickListener { _ ->
            nicoNicoUri?.let {
                val i = Intent(Intent.ACTION_VIEW, nicoNicoUri)

                //showInterStitialAd()
                //Toast.makeText(this, resources.getString(R.string.wait5seconds), Toast.LENGTH_LONG).show()
                Handler(networkThread.looper).post {
                    startActivity(i)
                    if (isConnected(this)) {
                        nicoNicoUri = Uri.parse(niconicoInterface?.GetRandomNicoNicoLink()?.link)
                    }else{
                        encourageUserToConnectNet(networkThread)
                    }
                    Log.d("miku", "niconicoReloaded")
                }

            }
        }

        youtubeButton?.setOnClickListener { _ ->
            youTubeUri?.let {

                val i = Intent(Intent.ACTION_VIEW, youTubeUri)
                //showInterStitialAd()
                //Toast.makeText(this, resources.getString(R.string.wait5seconds), Toast.LENGTH_LONG).show()
                Handler(networkThread.looper).post {
                    startActivity(i)
                    if (isConnected(this)) {
                        youTubeUri = Uri.parse(youtubeInterface?.GetRandomYoutubeLink()?.link)
                    }else{
                        encourageUserToConnectNet(networkThread)
                    }
                    Log.d("miku", "youtubeReloaded")
                }
            }
        }
    }

    private fun monitorAppRate() {
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10
                .setRemindInterval(1) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener { which ->
                    // callback listener.
                    Log.d(MainActivity::class.java.name, Integer.toString(which))
                }
                .monitor()
    }

    private fun initializeAdMob() {
        mInterstitialAd = InterstitialAd(this).apply {
            adUnitId = resources.getString(R.string.INTERSTITIAL_AD_ID)
            loadAd(AdRequest.Builder().build())
            adListener = object : AdListener() {
                override fun onAdClosed() {
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                }
            }
        }
    }

    private fun initializeApplication() {
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID))

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
    
    private fun hideAllViews(){
        Handler(mainLooper).post{
            niconicoButton?.visibility = View.INVISIBLE
            youtubeButton?.visibility = View.INVISIBLE
            mAdView?.visibility = View.INVISIBLE
        }
    }
    private fun showAllViews(){
        Handler(mainLooper).post{
            niconicoButton?.visibility = View.VISIBLE
            youtubeButton?.visibility = View.VISIBLE
            mAdView?.visibility = View.VISIBLE
        }
    }

    private fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return if (networkInfo != null) {
            cm.activeNetworkInfo.isConnected
        } else false
    }

    // オンクリックリスナーを再設定するためにスレッドが必要
    private fun encourageUserToConnectNet(networkThread: HandlerThread): Unit {
        if (!isConnected(this)) {
            Snackbar.make(findViewById(R.id.main_constraintLayout), "Connection failure. Could you please check your internet connection?", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry"
                    ) { _: View? -> encourageUserToConnectNet(networkThread)}
                    .show()
        }else{
            initializeAWS()
            setOnclickListeners(networkThread)
        }
    }
}
