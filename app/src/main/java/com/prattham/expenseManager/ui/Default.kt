package com.prattham.expenseManager.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.prattham.expenseManager.R
import com.prattham.expenseManager.fragments.DashBoardFragment
import com.prattham.expenseManager.fragments.ExpenseFragment
import com.prattham.expenseManager.fragments.IncomeFragment
import kotlinx.android.synthetic.main.appbar_layout.*
import org.jetbrains.anko.startActivity


class Default : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        setSupportActionBar(my_toolbar)
        supportActionBar?.title = "XPNSE"

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            my_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().replace(
            R.id.main_frame,
            DashBoardFragment()
        )
            .commit()


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.dashboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.main_frame,
                            DashBoardFragment()
                        ).commit()
                    supportActionBar?.title = "XPNSE"
                    true
                }

                R.id.income -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.main_frame,
                            IncomeFragment()
                        ).commit()
                    supportActionBar?.title = "INCOME"
                    true
                }

                R.id.expense -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.main_frame,
                            ExpenseFragment()
                        ).commit()
                    supportActionBar?.title = "EXPENSE"
                    true
                }

                else -> false
            }
        }
    }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.mainLogout -> {
                mAuth.signOut()
                startActivity<MainActivity>()
                finish()
            }
            R.id.mainAboutApp -> {
                startActivity<AboutAppActivity>()
            }
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {

            R.id.mainLogout -> {
                mAuth.signOut()
                startActivity<MainActivity>()
                finish()
            }
            R.id.mainAboutApp -> {
                startActivity<AboutAppActivity>()
                finish()
            }
        }


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)


        return true

    }


}
