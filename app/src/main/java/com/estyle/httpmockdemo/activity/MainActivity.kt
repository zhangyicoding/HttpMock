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

    override fun onDestroy() {
        super.onDestroy()
        if (mDisposable != null && !mDisposable!!.isDisposed) {
            mDisposable!!.dispose()
        }
    }
}
