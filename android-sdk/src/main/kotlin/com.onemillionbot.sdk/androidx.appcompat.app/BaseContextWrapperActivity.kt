package androidx.appcompat.app

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.LayoutRes
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

abstract class BaseContextWrapperActivity : AppCompatActivity {
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    constructor() : super()

    /**
     * More info about the bug/workaround: https://stackoverflow.com/a/58004553/1525990
     * --- START ---
     */
    private val baseContextWrappingDelegate by lazy {
        BaseContextWrapperDelegate(super.getDelegate())
    }

    override fun getDelegate() = baseContextWrappingDelegate
    override fun createConfigurationContext(overrideConfiguration: Configuration) = get<OneMillionBotContextWrapper> {
        parametersOf(super.createConfigurationContext(overrideConfiguration))
    }

    /**
     * --- END ---
     */

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(get<OneMillionBotContextWrapper> { parametersOf(base) })
    }
}
