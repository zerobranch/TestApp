package com.github.zerobranch.beebox.commons_android.utils.ext

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ArrayRes

fun Spinner.initSpinner(
    @ArrayRes textArrayRes: Int? = null,
    items: List<String> = emptyList(),
    selectedPosition: Int? = null,
    onItemSelected: (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit
) {
    val adapter = if (textArrayRes != null) {
        ArrayAdapter.createFromResource(
            context,
            textArrayRes,
            android.R.layout.simple_spinner_item
        )
    } else {
        ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            items
        )
    }

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.adapter = adapter

    selectedPosition?.run {
        onItemSelectedListener = null
        setSelection(selectedPosition, false)
    }

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            onItemSelected.invoke(parent, view, position, id)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}