package com.example.criconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityPlayerProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityPlayerProfileBinding
    private val teamViewModel: TeamViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPlayerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            val player = PlayerData(
                binding.playerName.text.toString(),
                binding.fatherName.text.toString(),
                binding.playerCity.text.toString(),
                binding.playerAge.text.toString(),
                binding.playerPhone.text.toString(),
            )
            teamViewModel.savePlayer(player){
                if(it){
                    Toast.makeText(this@PlayerProfileActivity, "Player has been added to team!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this@PlayerProfileActivity, "Problem registering player, Try again!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}