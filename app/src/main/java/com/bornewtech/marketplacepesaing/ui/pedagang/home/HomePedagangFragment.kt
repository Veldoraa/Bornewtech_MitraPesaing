//package com.bornewtech.marketplacepesaing.ui.pedagang.home
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.navigation.Navigation
//import com.bornewtech.marketplacepesaing.R
//import com.bornewtech.marketplacepesaing.databinding.FragmentHomePedagangBinding
//
//class HomePedagangFragment : Fragment() {
//
//    private var _binding: FragmentHomePedagangBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle? ): View {
//        // Inflate the layout for this fragment
//        _binding = FragmentHomePedagangBinding.inflate(inflater, container,false)
//        val view = binding.root
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.btnInpBarang.setOnClickListener(
//            Navigation.createNavigateOnClickListener(R.id.action_homePedagangFragment_to_inputBarangFragment)
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//
//
//}