package com.github.zerobranch.beebox.commons_android.utils.ext

import com.google.android.material.tabs.TabLayout

fun TabLayout.addTabSelectedListener(
    onTabReselected: ((Int) -> Unit)? = null,
    onTabSelected: (Int) -> Unit
) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            onTabSelected.invoke(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            onTabReselected?.invoke(tab.position)
        }
    })
}