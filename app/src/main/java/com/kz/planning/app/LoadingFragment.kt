package com.kz.planning.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.kz.planning.app.databinding.FragmentLoadingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoadingFragment : Fragment() {

    private var myBinding: FragmentLoadingBinding? = null
    private val binding get() = myBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        myBinding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoadingAnimation()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_cotainer, TabFragment()).commit()
        }
    }

    private fun startLoadingAnimation() {
        binding.spinner.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim))
    }

    override fun onDestroy() {
        super.onDestroy()
        myBinding = null
    }
}