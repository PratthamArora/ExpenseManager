package com.prattham.expenseManager.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.prattham.expenseManager.R
import kotlinx.android.synthetic.main.activity_about_app.*


class AboutAppActivity : AppCompatActivity() {

    private var colorOffSet = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        val window = this.window

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

// finally change the color
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.about_app_profileImage
        )


        about_app_toolbar.title = "About the Dev"
        setSupportActionBar(about_app_toolbar)
        about_app_AppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, i ->
            colorOffSet = -(i)
            if (colorOffSet > 255)
                colorOffSet = 255

            //hide toolbar
            about_app_toolbar.background.alpha = colorOffSet
            about_app_toolbar.alpha = colorOffSet / 256f
        })

        btn_github.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.github_link))
            )
            startActivity(intent)
        }
    }

}
