package com.github.zerobranch.beebox.commons_android.utils

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.GroupieViewHolder as BindingGroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

abstract class BaseBindableItem<T : ViewBinding> : BindableItem<T>() {
    private var isFirstInit = true
    private var state: Parcelable? = null

    override fun createViewHolder(itemView: View): BindingGroupieViewHolder<T> {
        val binding: T = initializeViewBinding(itemView)
        onViewBindingCreated(binding)
        return BindingGroupieViewHolder<T>(binding)
    }

    override fun bind(viewBinding: T, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.bind(viewBinding, position, payloads)
        } else {
            payloads.forEach { payloadSet ->
                (payloadSet as? Set<String>)?.forEach { payload ->
                    bind(viewBinding, position, payload)
                }
            }
        }
    }

    override fun getChangePayload(newItem: Item<*>): Any? {
        val payloads = mutableSetOf<String>()
        setChangePayload(newItem, payloads)
        return payloads.takeIf { it.isNotEmpty() } ?: super.getChangePayload(newItem)
    }

    open fun setChangePayload(newItem: Item<*>, payloads: MutableSet<String>) {}

    open fun bind(viewBinding: T, position: Int, payload: String) {}

    protected fun RecyclerView.saveState() {
        state = layoutManager?.onSaveInstanceState() ?: return
    }

    protected fun RecyclerView.restoreState() {
        if (state == null) return
        layoutManager?.onRestoreInstanceState(state)
    }

    protected val RecyclerView.groupAdapter: GroupAdapter<GroupieViewHolder>?
        get() = adapter as? GroupAdapter<GroupieViewHolder>

    protected fun runOnFirstInit(run: () -> Unit) {
        if (isFirstInit) {
            isFirstInit = false
            run.invoke()
        }
    }

    protected open fun onViewBindingCreated(viewBinding: T) {}
}
