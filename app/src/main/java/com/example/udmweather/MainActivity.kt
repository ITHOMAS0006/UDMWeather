package com.example.udmweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.ListView


class MainActivity : AppCompatActivity() {
    private val buildings = listOf("Engineering Building", "Student Union Building")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buildingList: ListView = findViewById(R.id.buildingList)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, buildings)
        buildingList.adapter = adapter

        buildingList.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, WeatherActivity::class.java)
            intent.putExtra("buildingName", buildings[position])
            startActivity(intent)
        }
    }
}
