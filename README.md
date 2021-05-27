1MillionBot SDK for integrating chatbots on client apps.

### How to use it?

Add to top level gradle.build file
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Add to app module gradle.build file
```
dependencies {
    implementation 'com.github.1millionbot:android-sdk:0.0.5'
}
```

Init the SDK at start app time by calling `OneMillionBot.init` and supplying your designed ApiKey:

```kotlin
OneMillionBot.init(
    application = this,
    credentials = OneMillionBotCredentials(yourApiKey)
)
```

By default, all the non fatal errors will be logged through `android.util.Log.log` if the build type is `Debug`, you can change this behaviour by supplying your own implementation of `Logger`, as such:

```kotlin
class LoggerClient : Logger {
    override fun log(e: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e("error", e.message, e)
        } else {
            //maybe crashlytics?
        }
    }
}

OneMillionBot.init(
    application = this,
    credentials = OneMillionBotCredentials(yourApiKey),
    logger = LoggerClient()
)
```

OneMillionBot.init also accepts an `Environment`, if none is supplied `EnvProduction` is used, which points to 1MillionBot production servers. If you need to use the staging server, you can supply `EnvStaging` when calling `OneMillionBot.init`:

```kotlin
OneMillionBot.init(
    application = this,
    credentials = OneMillionBotCredentials(yourApiKey),
    environment = EnvStaging()
)
```

Finally, add to the desired XML layout the `OneMillionBotView` to show the chat component view:

```xml
<com.onemillionbot.sdk.api.OneMillionBotView
    android:id="@+id/btShowOneMillionBot"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

And call `findViewById<OneMillionBotView>(R.id.btShowOneMillionBot).bind(this)` in the Fragment/Activity supplying the `LifecycleOwner` of the associated screen.

In this repo you can also find a Gradle module called `client` which showcase the SDK integration, to make it work you will need to supply your designed ApiKey in `ClientApp.kt`.
