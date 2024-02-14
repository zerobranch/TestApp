package com.github.zerobranch.beebox.commons_android.utils.delegates

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupieAdapter
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Fragment.groupAdapter(
    recyclerViewCallBack: () -> RecyclerView
) = FragmentAdapterDelegate(this, recyclerViewCallBack)

fun Fragment.groupAdapter() = FragmentAdapterDelegate(this, null)

class FragmentAdapterDelegate(
    private val fragment: Fragment,
    private val recyclerViewReceiver: (() -> RecyclerView)?
) : ReadOnlyProperty<Fragment, GroupieAdapter> {
    private var _adapter: GroupieAdapter? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
                val viewLifecycleOwner = it ?: return@Observer

                viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        mainHandler.post {
                            _adapter = null
                            fragment.view ?: return@post
                            recyclerViewReceiver?.invoke()?.adapter = null
                        }
                    }
                })
            }

            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): GroupieAdapter {
        val adapter = _adapter

        if (adapter != null) return adapter

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get adapter when Fragment views are destroyed.")
        }

        return GroupieAdapter().also { _adapter = it }
    }
}
