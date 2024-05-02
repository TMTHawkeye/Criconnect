package com.example.criconnect.Activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.criconnect.Adapters.MyAdapter
import com.example.criconnect.ModelClasses.DataClass
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.ModelClasses.tournamentDataClass
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.ViewModels.TournamentViewModel
import com.example.criconnect.databinding.ActivityTournamentDataBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class TournamentDataActivity : AppCompatActivity() {
    lateinit var binding: ActivityTournamentDataBinding
//    val teamViewModel: TeamViewModel by viewModel()
    val tournamentViewModel: TournamentViewModel by viewModel()

    var dataList: List<tournamentDataClass>? = null
    var adapter: MyAdapter? = null
    var androidData: DataClass? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTournamentDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.search.clearFocus()
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
        val dialog = ProgressDialog.show(
            this@TournamentDataActivity, "",
            "Saving Player Data, Please Wait... ", true
        )
        getTournamentsFromDatabase(dialog)


    }

    fun getTournamentsFromDatabase(dialog: ProgressDialog) {
//        teamViewModel.getTournament { tournamentList ->
//            Log.d("TAGlist", "getTournamentsFromDatabase: $tournamentList")
//            dataList = tournamentList
//            if (tournamentList?.size != 0) {
//                dialog.dismiss()
//                setAdapter(tournamentList)
//            }
//        }

        tournamentViewModel.getTournaments { registeredTournamentList ->
            Log.d("TAGlist", "getTournamentsFromDatabase: $registeredTournamentList")
            dataList = registeredTournamentList
            dialog.dismiss()

            if (registeredTournamentList?.size != 0) {
                setAdapter(registeredTournamentList)
            }
        }
    }


    fun setAdapter(tournamentList: List<tournamentDataClass>?) {
        val gridLayoutManager = GridLayoutManager(this@TournamentDataActivity, 1)
        binding.recyclerView.setLayoutManager(gridLayoutManager)
        adapter = MyAdapter(this@TournamentDataActivity, tournamentList)
        binding.recyclerView.setAdapter(adapter)
    }

    private fun searchList(text: String) {
        val dataSearchList: MutableList<tournamentDataClass> = ArrayList()
        dataList?.let {
            for (data in it) {
                if (data.name?.toLowerCase()!!
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    dataSearchList.add(data)
                }
            }
            if (dataSearchList.isEmpty()) {
                Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show()
            } else {
                adapter!!.setSearchList(dataSearchList)
            }
        }
    }
}

