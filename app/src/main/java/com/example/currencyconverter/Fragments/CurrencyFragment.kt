package com.example.currencyconverter.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.currencyconverter.Adapters.DatabaseAdapter
import com.example.currencyconverter.Adapters.RetrofitAdapter
import com.example.currencyconverter.ApiServices
import com.example.currencyconverter.Classes.Currencies
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

    private lateinit var resultCode: String
    private lateinit var resultSymbol: String

    private lateinit var baseCode: String
    private lateinit var baseSymbol: String

    private var positionSelectedCurrency: Int = 0
    private var positionSelectedCurrency2: Int = 0


    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val autocomplete = view.findViewById<AutoCompleteTextView>(R.id.currency1)
        val autocomplete2 = view.findViewById<AutoCompleteTextView>(R.id.currency2)
        val textInputEditTextBase = view.findViewById<TextInputEditText>(R.id.amount_input)
        val textInputEditTextResult = view.findViewById<TextInputEditText>(R.id.converted_amount)
        val convertButton = view.findViewById<View>(R.id.button)
        val swapButton = view.findViewById<View>(R.id.swap_button)
        val currencyLiveData = DatabaseAdapter.getDatabase(requireContext()).currencyDao().getAllCurrencies()
        currencyLiveData.observe(viewLifecycleOwner) { currencyList ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, currencyList.map { "${it.currencyCode} - ${it.currencyName}" })
            val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, currencyList.map { "${it.currencyCode} - ${it.currencyName}" })

            autocomplete.setAdapter(adapter)
            autocomplete.threshold = 1
            autocomplete2.setAdapter(adapter2)
            autocomplete2.threshold = 1
            autocomplete.setOnItemClickListener { parent, view, position, id ->
                val selectedString = parent.getItemAtPosition(position) as String
                val code = selectedString.substringBefore(" - ").trim()
                val selectedCurrency = currencyList.find { it.currencyCode == code }
                if (selectedCurrency != null) {
                    baseCode = selectedCurrency.currencyCode
                    baseSymbol = selectedCurrency.currencySymbol ?: ""
                    positionSelectedCurrency = currencyList.indexOf(selectedCurrency)
                    autocomplete.setText(selectedCurrency.currencyName, false)
                }
            }
            autocomplete2.setOnItemClickListener { parent, view, position, id ->
                val selectedString2 = parent.getItemAtPosition(position) as String
                val code2 = selectedString2.substringBefore(" - ").trim()
                val selectedCurrency2 = currencyList.find { it.currencyCode == code2 }
                if (selectedCurrency2 != null) {
                    resultCode = selectedCurrency2.currencyCode
                    resultSymbol = selectedCurrency2.currencySymbol ?: ""
                    positionSelectedCurrency = currencyList.indexOf(selectedCurrency2)
                    autocomplete2.setText(selectedCurrency2.currencyName, false)
                }
            }
        }
        convertButton.setOnClickListener {
            val amount = textInputEditTextBase.text.toString().toDoubleOrNull()
            if (amount != null) {
                getExchange(baseCode, resultCode) { rate ->
                    val convertedAmount = amount * rate
                    val formattedAmount = String.format("%.2f", convertedAmount)
                    textInputEditTextResult.setText("$resultSymbol $formattedAmount")
                }
            } else {
                textInputEditTextResult.setText("Invalid input")
            }
        }
        swapButton.setOnClickListener {
            swichtExchange( autocomplete, autocomplete2, currencyLiveData)
        }
    }

    private fun getExchange(baseCode: String, resultCode: String, onResult: (Double) -> Unit) {
        val textExchangeRate = requireView().findViewById<TextView>(R.id.textView4)
        val retrofitAdapter = RetrofitAdapter()
        val apiService = retrofitAdapter.getRetrofit().create(ApiServices::class.java)
        val call = apiService.getLatestRates(base = baseCode, target = resultCode)
        call.enqueue(object : Callback<Rates> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Rates>, response: Response<Rates>) {
                val rate = response.body()?.conversion_rate ?: 0.0
                textExchangeRate.text = "1 $baseCode = ${rate.toBigDecimal()} $resultCode"
                onResult(rate)
            }
            override fun onFailure(call: Call<Rates>, t: Throwable) {
                onResult(0.0)
            }
        })
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun swichtExchange(autocomplete: AutoCompleteTextView,autocomplete2: AutoCompleteTextView,currencyLiveData: LiveData<List<Currencies>>) {
        val textInputEditTextBase = requireView().findViewById<TextInputEditText>(R.id.amount_input)
        val textInputEditTextResult = requireView().findViewById<TextInputEditText>(R.id.converted_amount)
        val amount = textInputEditTextBase.text.toString().toDoubleOrNull() ?: return
        val currencyList = currencyLiveData.value ?: return
        val tempPos = positionSelectedCurrency
        val tempPos2 = positionSelectedCurrency2
        val temp = currencyList[tempPos]
        val temp2 = currencyList[tempPos2]
        // Swap the selected currencies
        autocomplete.setText(temp2.currencyName, false)
        autocomplete2.setText(temp.currencyName, false)
        // Update the base and result codes
        baseCode = temp2.currencyCode
        resultCode = temp.currencyCode
        baseSymbol = temp2.currencySymbol ?: ""
        resultSymbol = temp.currencySymbol ?: ""
        positionSelectedCurrency = tempPos2
        positionSelectedCurrency2 = tempPos
        //Convert the amounts
        getExchange(baseCode, resultCode) { rate ->
            val convertedAmount = amount * rate
            val formattedAmount = String.format("%.2f", convertedAmount)
            textInputEditTextResult.setText("$resultSymbol $formattedAmount")
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
        @JvmStatic
        fun newInstance() =
            CurrencyFragment().apply {
            }
    }
}