package com.plasdor.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.plasdor.app.R
import com.plasdor.app.utils.setImage

class FullScreenImageFragment : Fragment() {

    lateinit var rootView: View
    lateinit var imageView: AppCompatImageView
    var imagePath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_full_screen_image, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        imagePath = arguments?.getString("imagePath").toString()
        imageView = rootView.findViewById(R.id.imageView)


        if (imagePath == null || imagePath == "") {
            imageView!!.background =
                requireContext().resources.getDrawable(R.drawable.img_not_available)
        } else {
            imageView!!.setImage(imagePath)
        }

    }

}