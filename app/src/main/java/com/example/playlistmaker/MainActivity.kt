package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val search = findViewById<Button>(R.id.search)
//        val mediatek = findViewById<Button>(R.id.mediatek)
        val settings = findViewById<Button>(R.id.settings)

        settings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
//
//            search.setOnClickListener(this@MainActivity)
//            mediatek.setOnClickListener(this@MainActivity)

//        val imageClickListener: View.OnClickListener = View.OnClickListener { Toast.makeText(this@MainActivity, "Нажали на картинку!", Toast.LENGTH_SHORT).show() }
//
//        image.setOnClickListener(imageClickListener)
        }

//        override fun onClick(p0: View?) {
//            when (p0?.id) {
//                R.id.search -> {
//                    Toast.makeText(this, "Сработала кнопка поиска", Toast.LENGTH_SHORT).show()
//                }
//
//                R.id.mediatek -> {
//                    Toast.makeText(this, "Сработала кнопка медиатеки", Toast.LENGTH_SHORT).show()
//                }
//
//            }
//
//
//        }
    }
}