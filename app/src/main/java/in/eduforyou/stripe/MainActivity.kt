package `in`.eduforyou.stripe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import `in`.eduforyou.stripe.Utils.PUBLISHABLE_KEY
import `in`.eduforyou.stripe.api.ApiInterface
import `in`.eduforyou.stripe.api.ApiUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var paymentSheet: PaymentSheet
    private lateinit var stripe: Stripe

    lateinit var customerId: String
    lateinit var ephemeralKey: String
    lateinit var clientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.payBtn)

        button.setOnClickListener {

            paymentFlow()
        }

        PaymentConfiguration.init(this, PUBLISHABLE_KEY)
        stripe = Stripe(applicationContext, PUBLISHABLE_KEY)

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        getCustomerId()


    }

    private fun paymentFlow() {

        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                "KTechnos",

                PaymentSheet.CustomerConfiguration(
                    customerId, ephemeralKey
                )
            )
        )


        Log.d("clientSecret",clientSecret)
    }

    private var apiInterface : ApiInterface = ApiUtilities.getApiInterface()
    private fun getCustomerId() {

        lifecycleScope.launch(Dispatchers.IO) {

            var res = apiInterface.getCustomer()
            withContext(Dispatchers.Main){

                if(res.isSuccessful && res.body() != null){
                    customerId = res.body()!!.id

                    getEphemeralKey(customerId)
                }
            }
        }
    }

    private fun getEphemeralKey(customerId: String) {
        lifecycleScope.launch(Dispatchers.IO) {

            var res = apiInterface.getEphemeralKey(customerId)
            withContext(Dispatchers.Main){

                if(res.isSuccessful && res.body() != null){

                    ephemeralKey = res.body()!!.id

                    getPaymentIntent(customerId,ephemeralKey)
                }
            }
        }
    }

    private fun getPaymentIntent(customerId: String, ephemeralKey: String) {

        lifecycleScope.launch(Dispatchers.IO) {

            var res = apiInterface.getPaymentIntent(customerId)
            withContext(Dispatchers.Main){

                if(res.isSuccessful && res.body() != null){

                    clientSecret = res.body()!!.client_secret

                    Toast.makeText(this@MainActivity,"Proceed for pay..",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        // implemented in the next steps

        if (paymentSheetResult is PaymentSheetResult.Completed){

            Toast.makeText(this,"Payment Done", Toast.LENGTH_LONG).show()
        }
    }
}