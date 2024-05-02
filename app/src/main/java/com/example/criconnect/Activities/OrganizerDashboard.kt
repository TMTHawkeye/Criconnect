package com.example.criconnect.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.etebarian.meowbottomnavigation.MeowBottomNavigation.ClickListener
import com.example.criconnect.Fragments.FAQFragment
import com.example.criconnect.Fragments.HomeFragment
import com.example.criconnect.Fragments.OrganizerCreationFragment
import com.example.criconnect.Fragments.OrganizerHomeFragment
import com.example.criconnect.Fragments.OrganizerTornamentHandlingFragment
import com.example.criconnect.Fragments.SettingsFragment
import com.example.criconnect.Fragments.TeamManagementFragment
import com.example.criconnect.Fragments.TeamStatisticsFragment
import com.example.criconnect.Fragments.UpdateOrganizerProfileFragment
import com.example.criconnect.HelperClasses.Constants
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
import com.example.criconnect.SplashLoginActivity
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityOrganizerDashboardBinding
import com.example.criconnect.databinding.CustomDialogRatingBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrganizerDashboard : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{
    lateinit var binding: ActivityOrganizerDashboardBinding
    private var authProfile: FirebaseAuth? = null
    val teamViewModel: TeamViewModel by viewModel()
    var team: TeamModel? = null
    var nonZeroRating = true
    lateinit var ratingKey: String
    private var exitOnBackPressed = false
    private var currentNavItem = R.id.weather

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setNavigationItemSelectedListener(this)

        binding.navView.bringToFront()
        setSupportActionBar(binding.toolbar)
        val toogle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment())
                .commit()
            binding.navView.setCheckedItem(R.id.nav_home)
        }

        replaceFragment(OrganizerCreationFragment())

        binding.bottomNavigationView.setBackground(null)
        binding.bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.registerTournament) {
                currentNavItem=item.itemId

                replaceFragment(OrganizerHomeFragment())
            } else if (item.itemId == R.id.weather) {
                currentNavItem=item.itemId

                replaceFragment(OrganizerCreationFragment())
            } else if (item.itemId == R.id.teams) {
                currentNavItem=item.itemId

                replaceFragment(OrganizerTornamentHandlingFragment())
            }
            else if (item.itemId == R.id.profile) {
                currentNavItem=item.itemId

                replaceFragment(UpdateOrganizerProfileFragment())
            }
            true
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    if (currentNavItem == R.id.weather) {
                        FirebaseAuth.getInstance().signOut()
                        finish()
//                        finishAffinity()
                    } else {
                        // Navigate to home position
                        replaceFragment(OrganizerCreationFragment())
                        currentNavItem = R.id.weather
                    }
                }
            }
        })


//        binding.meowBottom.add(MeowBottomNavigation.Model(1, R.drawable.baseline_settings_24))
//        binding.meowBottom.add(MeowBottomNavigation.Model(2, R.drawable.baseline_home_24))
//        binding.meowBottom.add(MeowBottomNavigation.Model(3, R.drawable.baseline_settings_24))
//
//        binding.meowBottom.setOnShowListener {
//            var fragment: Fragment? = null
//            when (it.getId()) {
//                1 -> replaceFragment(OrganizerHomeFragment())
//                2 -> replaceFragment(OrganizerCreationFragment())
//                3 -> replaceFragment(OrganizerTornamentHandlingFragment())
//                else->{
//                    replaceFragment(OrganizerHomeFragment())
//                }
//            }
//        }


//        binding.meowBottom.setOnClickMenuListener {
//            var fragment: Fragment? = null
//            when (it.getId()) {
//                1 -> replaceFragment(OrganizerHomeFragment())
//                2 -> replaceFragment(OrganizerCreationFragment())
//                3 -> replaceFragment(OrganizerTornamentHandlingFragment())
//                else->{
//                    replaceFragment(OrganizerHomeFragment())
//                }
//            }
////            replaceFragment(OrganizerHomeFragment())
//        }


//        //set nofication count
//        binding.meowBottom.setCount(1, "10");

        //set default

//        //set nofication count
//        binding.meowBottom.setCount(1, "10");

        //set default
//        binding.meowBottom.show(2, true)


    }


    private fun loadFragment(fragment: Fragment?) {
        fragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, it)
                .commit()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun shareApplication() {
        val appPackageName = packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=$appPackageName"
        )
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }


    private fun showRateUsDialog() {
        ratingKey = "rate"
        val dialog_binding = CustomDialogRatingBinding.inflate(layoutInflater)
        val dialog = Dialog(this@OrganizerDashboard)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()
        dialog_binding.ratingBar.setIsIndicator(false)

        val ratingExisted = Paper.book().read(ratingKey, 0.0f)

        dialog_binding.ratingBar.rating = ratingExisted ?: 0.0f



        dialog_binding.cardSubmit.setOnClickListener {
            val rating = dialog_binding.ratingBar.rating
            handleRatings(dialog, rating, dialog_binding)
        }

    }

    private fun handleRatings(
        rateUsDialog: Dialog,
        rating: Float,
        dialog_binding: CustomDialogRatingBinding
    ) {
        when {
            rating == 0.0f -> {
                nonZeroRating = false
                Toast.makeText(
                    this@OrganizerDashboard,
                    R.string.kindly_rate_our_application,
                    Toast.LENGTH_SHORT
                ).show()
            }

            rating <= 1.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
            }

            rating <= 2.0f -> {
                setRating(ratingKey, rating)

                rateUsDialog.dismiss()
            }

            rating <= 3.0f -> {
                setRating(ratingKey, rating)

                rateUsDialog.dismiss()
            }

            rating <= 4.0f -> {
                setRating(ratingKey, rating)

                rateUsDialog.dismiss()
            }

            rating == 5.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
                val appPackageName = packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }

            else -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
            }


        }

    }

    private fun setRating(key: String, value: Float) {
        Paper.book().write("$key", value)
        nonZeroRating = true
    }

    private fun setExitOnBackPressed(exit: Boolean) {
        exitOnBackPressed = exit
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.nav_home) {
            replaceFragment(HomeFragment())
        } else if (itemId == R.id.nav_settings) {

            val intents = Intent(this@OrganizerDashboard, UserProfileActivity::class.java)

            startActivity(intents)
        } else if (itemId == R.id.nav_share) {
            shareApplication()
//            replaceFragment(shareFragment())
        } else if (itemId == R.id.nav_faq) {
            replaceFragment(FAQFragment())
        } else if (itemId == R.id.nav_rateus) {
            showRateUsDialog()
//            showRateDialog()
        } else if (itemId == R.id.nav_logout) {
            Constants.deleteTeamDataFromSharedPreferences(this@OrganizerDashboard)
            authProfile = FirebaseAuth.getInstance()
            authProfile!!.signOut()
            Toast.makeText(this@OrganizerDashboard, "Logged Out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@OrganizerDashboard, SplashLoginActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
        //rate us dialog box start

    }



}