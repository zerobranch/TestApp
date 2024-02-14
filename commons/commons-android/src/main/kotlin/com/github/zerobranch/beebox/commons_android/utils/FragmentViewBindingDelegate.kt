package com.github.zerobranch.beebox.commons_android.utils

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var _binding: T? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
                val viewLifecycleOwner = it ?: return@Observer

                viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        mainHandler.post { _binding = null }
                    }
                })
            }

            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(
                    viewLifecycleOwnerLiveDataObserver
                )
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(
                    viewLifecycleOwnerLiveDataObserver
                )
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = _binding

        if (binding != null) return binding

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
        }

        return viewBindingFactory(thisRef.requireView()).also { _binding = it }
    }
}

class ActivityViewBindingDelegate<T : ViewBinding>(
    private val activity: AppCompatActivity,
    private val initializer: (LayoutInflater) -> T
) : ReadOnlyProperty<AppCompatActivity, T> {
    private var _binding: T? = null

    init {
        val lifecycleObserver = object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                if (_binding == null) {
                    _binding = initializer(activity.layoutInflater)
                }

                activity.lifecycle.removeObserver(this)
            }
        }

        activity.lifecycle.addObserver(lifecycleObserver)
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        val binding = _binding

        if (binding != null) return binding

        // This must be on the main thread only
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("This cannot be called from other threads. It should be on the main thread only.")
        }

        return initializer(thisRef.layoutInflater).also { _binding = it }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

fun <T : ViewBinding> AppCompatActivity.viewBinding(initializer: (LayoutInflater) -> T) =
    ActivityViewBindingDelegate(this, initializer)
