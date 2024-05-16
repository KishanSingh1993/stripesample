package `in`.eduforyou.stripe.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import `in`.eduforyou.stripe.R
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        fetchPaymentDetails()

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        findViewById<Button>(R.id.payButton).setOnClickListener {
            presentPaymentSheet()
        }

    }


    private fun fetchPaymentDetails() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.fetchPaymentDetails()
                if (response.isSuccessful && response.body() != null) {
                    val customerId = response.body()!!.customer
                    val ephemeralKey = response.body()!!.ephemeralKey
                    val paymentIntent = response.body()!!.paymentIntent
                    val publishableKeyFromServer = response.body()!!.publishableKey

                    paymentIntentClientSecret = paymentIntent
                    customerConfig = PaymentSheet.CustomerConfiguration( customerId, ephemeralKey)
                    val publishableKey = publishableKeyFromServer
                    PaymentConfiguration.init(this@PaymentActivity, publishableKey)

                } else {
                    Toast.makeText(this@PaymentActivity, "Something wrong from server side", Toast.LENGTH_SHORT).show()
                }
            } catch (e : Exception) {
                Toast.makeText(this@PaymentActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            } finally {
                Toast.makeText(this@PaymentActivity, "Fetch Payment Details completed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        // implemented in the next steps
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
                Toast.makeText(this@PaymentActivity, "Payment Cancelled", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
                Toast.makeText(this@PaymentActivity, "Payment Failed", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                print("Completed")
                Toast.makeText(this@PaymentActivity, "Payment Successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "EduForYou",
                customer = customerConfig,
            )
        )
    }
}