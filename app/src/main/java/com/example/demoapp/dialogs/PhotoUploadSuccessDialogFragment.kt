package com.example.demoapp.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.demoapp.R
import com.example.demoapp.activities.MainActivity
import com.example.demoapp.databinding.DialogPhotoUploadSuccessBinding

class PhotoUploadSuccessDialogFragment : DialogFragment() {

    private var _binding: DialogPhotoUploadSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        _binding = DialogPhotoUploadSuccessBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
//        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOk.setOnClickListener {
            dismiss()
            requireActivity().finishAffinity()
            requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
        }
    }
}