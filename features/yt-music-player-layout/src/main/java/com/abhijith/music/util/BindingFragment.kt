package com.abhijith.music.util

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val viewBindingFactory: (LayoutInflater) -> T
) : ReadOnlyProperty<Fragment, T> {

    private var binding: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return binding ?: viewBindingFactory(LayoutInflater.from(fragment.requireContext())).also { binding = it }
    }

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                }
            })
        }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (LayoutInflater) -> T): FragmentViewBindingDelegate<T> {
    return FragmentViewBindingDelegate(this, viewBindingFactory)
}
