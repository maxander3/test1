package com.kz.planning.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.kz.planning.app.databinding.FragmentTabBinding


class TabFragment : Fragment() {

    private var myBinding: FragmentTabBinding? = null
    private val binding: FragmentTabBinding get() = requireNotNull(myBinding)

    private val pageAdapter: TabPageAdapter by lazy {
        TabPageAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        myBinding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
    }

    private fun setupTabs() {
        with(binding) {
            tabLayout.apply {
                addTab(newTab().apply {
                    setIcon(R.drawable.home_ic)
                    text = "Курс"
                })
                addTab(newTab().apply {
                    setIcon(R.drawable.clock_ic)
                    text = "График"
                })
                addTab(newTab().apply {
                    setIcon(R.drawable.graph_ic)
                    text = "Информация"
                })
            }
            viewpager.apply {
                adapter = pageAdapter
                isUserInputEnabled = false
            }
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewpager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myBinding = null
    }
}


class TabPageAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int) = when (position) {
        0 -> {
            CurrencyFragment()
        }
        1 -> {
            GraphFragment()
        }
        2 -> {
            InfoFragment()
        }
        else -> {
            Fragment()
        }
    }

}
