package com.giffy.list

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.giffy.R
import com.giffy.databinding.FragmentGifsListBinding

class GifsListFragment : Fragment(R.layout.fragment_gifs_list) {

    private lateinit var binding: FragmentGifsListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
    }
}
