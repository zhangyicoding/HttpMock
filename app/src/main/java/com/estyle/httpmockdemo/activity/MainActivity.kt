package com.estyle.httpmockdemo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.estyle.httpmockdemo.BuildConfig
import com.estyle.httpmockdemo.MyApplication
import com.estyle.httpmockdemo.R
import com.estyle.httpmockdemo.httpservice.DishService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private var mDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        mDisposable = (application as MyApplication).retrofit
            .create(DishService::class.java)
            .getDish(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                text_view.text = "BuildConfig.DEBUG: ${BuildConfig.DEBUG} -> $it"
            }
    }

    // 验证release版的assets/httpmock_debug/是否存在
    private fun checkHttpMockDebug() {
        var inputStream:InputStream? = null
        try {
            inputStream = assets.open("httpmock_debug/dish.json")
            val sb = StringBuilder()
            val buff = ByteArray(1024)
            var len = 0
            while (true) {
                len = inputStream.read(buff)
                if (len == -1) break
                sb.append(String(buff, 0, len))
            }
            text_view.text = """
                BuildConfig.DEBUG: ${BuildConfig.DEBUG},
                ${sb.toString()}
            """.trimIndent()
        } catch (e: Exception) {
            text_view.text = """
                BuildConfig.DEBUG: ${BuildConfig.DEBUG},
                error: ${e.message}
            """.trimIndent()
        } finally {
            inputStream?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDisposable != null && !mDisposable!!.isDisposed) {
            mDisposable!!.dispose()
        }
    }
}
