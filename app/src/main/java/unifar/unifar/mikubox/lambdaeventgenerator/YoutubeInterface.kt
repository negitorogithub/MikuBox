package unifar.unifar.mikubox.lambdaeventgenerator

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction

interface YoutubeInterface {

    /**
     * Invoke the Lambda function "AndroidBackendLambdaFunction".
     * The function name is the method name.
     */
    @LambdaFunction
    fun GetRandomYoutubeLink(): ResponseClass

}