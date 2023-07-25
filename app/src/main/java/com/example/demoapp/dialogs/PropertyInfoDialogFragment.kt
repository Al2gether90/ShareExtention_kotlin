package com.example.demoapp.dialogs

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.birjuvachhani.locus.Locus
import com.example.demoapp.R
import com.example.demoapp.databinding.DialogFragmentPropertyInfoBinding
import com.example.demoapp.models.Assignment
import com.example.demoapp.models.InspectionType
import com.example.demoapp.models.dto.CreateAssignmentRequest
import com.example.demoapp.network.Resource
import com.example.demoapp.utils.EMAIL
import com.example.demoapp.utils.hideProgressBar
import com.example.demoapp.utils.showProgressBar
import com.example.demoapp.utils.toast
import com.example.demoapp.viewmodels.AppViewModel
import java.util.*

class PropertyInfoDialogFragment(
    private val onDialogCloseListener: OnDialogCloseListener
) : DialogFragment() {

    private var _binding: DialogFragmentPropertyInfoBinding? = null
    private val binding get() = _binding!!

    private val appViewModel: AppViewModel by viewModels()
    private lateinit var selectedInspectionType: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        _binding = DialogFragmentPropertyInfoBinding.inflate(LayoutInflater.from(requireContext()))
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

        setUpObserver()

        appViewModel.getInspectionType()

        binding.inputAddress.setEndIconOnClickListener {
            Locus.getCurrentLocation(requireContext()) { result ->
                result.location?.let {
                    getAddress(it.latitude, it.longitude)
                }
            }
        }

        binding.btnCreate.setOnClickListener {
            with(binding) {

                if (inputName.editText?.text.toString().trim().isEmpty()) {
                    toast("Please enter name")
                    return@setOnClickListener
                }

                if (inputAddress.editText?.text.toString().trim().isEmpty()) {
                    toast("Please enter address")
                    return@setOnClickListener
                }

                if (!this@PropertyInfoDialogFragment::selectedInspectionType.isInitialized) {
                    toast("Please select inspection type")
                    return@setOnClickListener
                }
                appViewModel.createAssignmentByEmail(
                    CreateAssignmentRequest(
                        email = EMAIL,
                        assignment = Assignment().apply {
                            claimInsuredName = inputName.editText?.text.toString().trim()
                            claimInsuredAddress1 = inputAddress.editText?.text.toString().trim()
                            claimInsuredAddress2 = selectedInspectionType
                        })
                )
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

    }

    private fun setUpObserver() {
        appViewModel.createAssignmentResponse.observe(this) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar(binding.progressBar)
                        response.data?.let {
                            toast("Assignment created successfully")
                            onDialogCloseListener.onClose(success = true, it.assignment)
                            dismiss()
                        }
                    }

                    is Resource.Error -> {
                        hideProgressBar(binding.progressBar)
                        response.message?.let { message ->
                            toast(message)
                        }
                    }

                    is Resource.Loading -> {
                        showProgressBar(binding.progressBar)
                    }
                }
            }
        }

        appViewModel.inspectionTypeResponse.observe(this) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar(binding.progressBar)
                        response.data?.let {
                            setInspectionData(it)
                        }
                    }

                    is Resource.Error -> {
                        hideProgressBar(binding.progressBar)
                        response.message?.let { message ->
                            toast(message)
                        }
                    }

                    is Resource.Loading -> {
                        showProgressBar(binding.progressBar)
                    }
                }
            }
        }

    }

    private fun setInspectionData(list: ArrayList<InspectionType>) {
        val items = list.map { type -> type.value }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        (binding.inputTypeOfInspection.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
        (binding.inputTypeOfInspection.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, index, l ->
            selectedInspectionType = list[index].value.toString()
        }
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        val gcd = Geocoder(
            requireContext(),
            Locale.getDefault()
        )
        val addresses: List<Address>
        try {
            addresses = gcd.getFromLocation(
                latitude,
                longitude, 1
            )
            if (addresses.isNotEmpty()) {
                val locality: String? = addresses[0].takeIf { true }?.locality
                val subLocality: String? = addresses[0].takeIf { true }?.subLocality
                val state: String? = addresses[0].takeIf { true }?.adminArea
                val country: String? = addresses[0].takeIf { true }?.countryName
                val postalCode: String? = addresses[0].takeIf { true }?.postalCode
                val knownName: String? = addresses[0].takeIf { true }?.featureName
                binding.inputAddress.editText?.setText(
                    StringBuilder()
                        .append(" ").append(knownName ?: "")
                        .append(" ").append(subLocality ?: "")
                        .append(" ").append(locality ?: "")
                        .append(" ").append(state ?: "")
                        .append(" ").append(country ?: "")
                        .append(" ").append(postalCode ?: "")
                        .toString()
                )

            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Error getting address for the location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    interface OnDialogCloseListener {
        fun onClose(success: Boolean, assignment: Assignment?)
    }
}