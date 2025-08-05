package com.example.currencyconverter.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.currencyconverter.Adapters.DatabaseAdapter
import com.example.currencyconverter.Adapters.RetrofitAdapter
import com.example.currencyconverter.ApiServices
import com.example.currencyconverter.Classes.Rates
import com.example.currencyconverter.R
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



/**
 * A simple [Fragment] subclass.
 * Use the [CurrencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrencyFragment : Fragment() {
    // TODO: Rename and change types of parameters

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val autocomplete = view.findViewById<AutoCompleteTextView>(R.id.currency1)
        val autocomplete2 = view.findViewById<AutoCompleteTextView>(R.id.currency2)
        val textInputEditTextBase = view.findViewById<TextInputEditText>(R.id.amount_input)
        val textInputEditTextResult = view.findViewById<TextInputEditText>(R.id.converted_amount)
        val convertButton = view.findViewById<View>(R.id.button)
        var baseCode: String? = null
        var resultCode: String? = null
        var resultSymbol: String? = null
        val currencyLiveData = DatabaseAdapter.getDatabase(requireContext()).currencyDao().getAllCurrencies()
        currencyLiveData.observe(viewLifecycleOwner) { currencyList ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                currencyList.map { "${it.currencyCode} - ${it.currencyName}" }
            )
            autocomplete.setAdapter(adapter)
            autocomplete2.setAdapter(adapter)
            autocomplete.setOnItemClickListener { parent, view, position, id ->
                val selectedCurrency = currencyList[position]
                autocomplete.setText(selectedCurrency.currencyName, false)
                baseCode = selectedCurrency.currencyCode
            }
            autocomplete2.setOnItemClickListener { parent, view, position, id ->
                val selectedCurrency2 = currencyList[position]
                autocomplete2.setText(selectedCurrency2.currencyName, false)
                resultCode = selectedCurrency2.currencyCode
                resultSymbol = selectedCurrency2.currencySymbol ?: ""
            }
        }
        convertButton.setOnClickListener {
            val amount = textInputEditTextBase.text.toString().toDoubleOrNull()
            if (amount != null && baseCode != null && resultCode != null) {
                val retrofitAdapter = RetrofitAdapter()
                val apiService = retrofitAdapter.getRetrofit().create(ApiServices::class.java)
                val call = apiService.getLatestRates(base = baseCode, target = resultCode)
                call.enqueue(object : Callback<Rates> {
                    override fun onResponse(call: Call<Rates>, response: Response<Rates>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                val rate = responseBody.conversion_rate
                                val convertedAmount = amount * rate
                                val formattedAmount = String.format("%.2f", convertedAmount)
                                textInputEditTextResult.setText("$resultSymbol $formattedAmount")
                            } else {
                                textInputEditTextResult.setText("No data received")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Rates?>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
            } else {

                textInputEditTextResult.setText("Invalid input")
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_currency, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CurrencyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CurrencyFragment().apply {
            }
    }
}