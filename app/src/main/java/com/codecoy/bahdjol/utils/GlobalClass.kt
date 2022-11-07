package com.codecoy.bahdjol.utils

import android.app.Application
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class GlobalClass: Application() {
    companion object{

        var drawer: DrawerLayout? = null
        lateinit var frag: Fragment
        lateinit var bottomNavigation: BottomNavigationView

    }
}