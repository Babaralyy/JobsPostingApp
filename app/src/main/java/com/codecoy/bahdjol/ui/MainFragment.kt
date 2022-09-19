package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentMainBinding
import com.google.android.material.navigation.NavigationView


class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var mBinding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        setUpNavDrawer()

        replaceFragment(ServicesFragment())
        mBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.iHome -> {
                    replaceFragment(ServicesFragment())
                }
                R.id.iCalendar -> {
                    replaceFragment(CalendarFragment())
                }
                R.id.iInfo -> {
                    replaceFragment(FeedbackFragment())
                }
                R.id.iBell -> {
                    replaceFragment(NotificationFragment())
                }

            }
            true
        }


    }

    private fun setUpNavDrawer() {

        mBinding.navView.bringToFront()

        mBinding.navView.setNavigationItemSelectedListener(requireActivity() as NavigationView.OnNavigationItemSelectedListener)

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLay, fragment)
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

}