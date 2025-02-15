package com.bornewtech.mitrapesaing.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnInpBarang.setOnClickListener {
            // untuk menuju ke arah Activity input barang
            findNavController().navigate(R.id.action_navigation_homeFragment_to_navigation_inputBarangActivity)
//            Navigation.createNavigateOnClickListener(R.id.action_navigation_homeFragment_to_navigation_inputBarang)
        }
    }

    // dapatkan data dari rv ke fragmenthome

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}