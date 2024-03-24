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
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityTournamentDataBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class TournamentDataActivity : AppCompatActivity() {
    lateinit var binding: ActivityTournamentDataBinding
    val teamViewModel: TeamViewModel by viewModel()

    var dataList: List<TournamentData>? = null
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


//        androidData = DataClass("Team XI", R.string.camera, "RaceCourse", R.drawable.imageupdated10)
//        dataList.add(androidData!!)
//
//        androidData = DataClass(
//            " Heroes Cup",
//            R.string.recyclerview,
//            "Pindi Stadium",
//            R.drawable.imageupdated1
//        )
//        dataList.add(androidData!!)
//
//        androidData =
//            DataClass("Night Cricket Trophy", R.string.date, "f9 park", R.drawable.imageupdated2)
//        dataList.add(androidData!!)
//
//        androidData =
//            DataClass("Champions League", R.string.edit, "Ayub Stadium", R.drawable.iamgeupdated3)
//        dataList.add(androidData!!)
//
//        androidData =
//            DataClass(" Hometown Cup", R.string.rating, "I-8 Ground", R.drawable.imageupdated5)
//        dataList.add(androidData!!)
//
//        setAdapter()

    }

    fun getTournamentsFromDatabase(dialog: ProgressDialog) {
        teamViewModel.getTournament { tournamentList ->
            Log.d("TAGlist", "getTournamentsFromDatabase: $tournamentList")
            dataList = tournamentList
            if (tournamentList?.size != 0) {
                dialog.dismiss()
                setAdapter(tournamentList)
            }
        }
    }


    fun setAdapter(tournamentList: List<TournamentData>?) {
        val gridLayoutManager = GridLayoutManager(this@TournamentDataActivity, 1)
        binding.recyclerView.setLayoutManager(gridLayoutManager)
        adapter = MyAdapter(this@TournamentDataActivity, tournamentList)
        binding.recyclerView.setAdapter(adapter)
    }

    private fun searchList(text: String) {
        val dataSearchList: MutableList<TournamentData> = ArrayList()
        for (data in dataList!!) {
            if (data.tournamentName.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
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

