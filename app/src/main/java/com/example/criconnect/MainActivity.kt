package com.example.criconnect

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.criconnect.Activities.PlayerProfileActivity
import com.example.criconnect.Activities.TeamRegistrationActivity
import com.example.criconnect.Activities.TournamentRegistrationActivity
import com.example.criconnect.Activities.UserProfileActivity
import com.example.criconnect.Fragments.FAQFragment
import com.example.criconnect.Fragments.HomeFragment
import com.example.criconnect.Fragments.SettingsFragment
import com.example.criconnect.Fragments.TeamManagementFragment
import com.example.criconnect.Fragments.TeamStatisticsFragment
import com.example.criconnect.HelperClasses.Constants
import com.example.criconnect.HelperClasses.Constants.deleteTeamDataFromSharedPreferences
import com.example.criconnect.HelperClasses.Constants.getTeamData
import com.example.criconnect.HelperClasses.Constants.storeTeamDataInSharedPreferences
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityMainBinding
import com.example.criconnect.databinding.BottomsheetlayoutBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    private var authProfile: FirebaseAuth? = null
    val teamViewModel: TeamViewModel by viewModel()
    var team: TeamModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (team == null) {
            val dialog = ProgressDialog.show(
                this@MainActivity, "",
                "Fetching Team Data, Please Wait... ", true
            )
            getRegisteredTeamDetails(dialog)
        }


//        CoroutineScope(Dispatchers.Main).launch {
//            isRegistered = getRegisteredState()
//        }

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

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setBackground(null)
        binding.bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.home) {
                replaceFragment(HomeFragment())
            } else if (item.itemId == R.id.shorts) {
                replaceFragment(TeamManagementFragment())
            } else if (item.itemId == R.id.subscriptions) {
                replaceFragment(TeamStatisticsFragment())
            } else if (item.itemId == R.id.library) {
                replaceFragment(SettingsFragment())
//                Intent intent = new Intent(Activity_Main.this, ProfileActivity.class);
//                startActivity(intent);
            }
            true
        })


        binding.fab.setOnClickListener(View.OnClickListener { showBottomDialog() })


    }

    private fun getRegisteredTeamDetails(dialog: ProgressDialog) {
        teamViewModel.getTeamData() { teamData, isAvailable ->
            if (isAvailable) {
                dialog.dismiss()
                if (teamData != null) {
                    storeTeamDataInSharedPreferences(this@MainActivity,teamData)
                }
            }
            else{
                val intent = Intent(this@MainActivity, TeamRegistrationActivity::class.java)
                Handler().postDelayed({
                    dialog.dismiss()
                    startActivity(intent)
                }, 2000)
            }
        }
    }
    private fun showBottomDialog() {
        val dialogBinding = BottomsheetlayoutBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.layoutTournament.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@MainActivity, TournamentRegistrationActivity::class.java)
            startActivity(intent)
        }

        if (team != null) {
            dialogBinding.layoutVideo.visibility = View.GONE
        }

        dialogBinding.layoutVideo.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@MainActivity, TeamRegistrationActivity::class.java)
            startActivity(intent)
        }
        dialogBinding.layoutShorts.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@MainActivity, PlayerProfileActivity::class.java)
            startActivity(intent)
        }
        dialogBinding.layoutLive.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@MainActivity, TeamRegistrationActivity::class.java)
             startActivity(intent)
        }
        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId


        if (itemId == R.id.nav_home) {
            replaceFragment(HomeFragment())
        } else if (itemId == R.id.nav_settings) {
//            replaceFragment(new SettingFragment());
            val intent = intent
            val nameUsers = intent.getStringExtra("name")
            val emailUsers = intent.getStringExtra("email")
            val usernameUsers = intent.getStringExtra("username")
            val passwordUsers = intent.getStringExtra("password")
            val intents = Intent(this@MainActivity, UserProfileActivity::class.java)
            intents.putExtra("name", nameUsers)
            intents.putExtra("email", emailUsers)
            intents.putExtra("username", usernameUsers)
            intents.putExtra("password", passwordUsers)
            startActivity(intents)
        } else if (itemId == R.id.nav_share) {
            shareApplication()
//            replaceFragment(shareFragment())
        } else if (itemId == R.id.nav_faq) {
            replaceFragment(FAQFragment())
        } else if (itemId == R.id.nav_rateus) {
//            showRateDialog()
        } else if (itemId == R.id.nav_logout) {
            deleteTeamDataFromSharedPreferences(this@MainActivity)
            authProfile = FirebaseAuth.getInstance()
            authProfile!!.signOut()
            Toast.makeText(this@MainActivity, "Logged Out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, SplashLoginActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
        //rate us dialog box start

    }

//    override fun onResume() {
//        super.onResume()
//        teamViewModel.getTeamData{teamData,isAvailable->
//            if(isAvailable){
//                if(teamData!=null) {
//                    Paper.book().write("OWNTEAM", teamData)
//                    team= teamData
//                }
//            }
//
//        }
//    }

    suspend fun getRegisteredState(): Boolean {
        return withContext(Dispatchers.IO) {
            val sharedPreferences = getSharedPreferences("USERPREFERENCE", Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(
                "isTeamRegistered",
                false
            ) // Default value is false if the key is not found
        }
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

    override fun onResume() {
        super.onResume()
        team = getTeamData(this@MainActivity)
    }


//    private fun showRateDialog() {
//        val rateUsDialog = RateUsDialog(this@Activity_Main)
//        rateUsDialog.getWindow()
//            .setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))
//        rateUsDialog.setCancelable(false)
//        rateUsDialog.show()
//    }
}