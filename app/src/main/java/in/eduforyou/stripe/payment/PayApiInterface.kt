package `in`.eduforyou.stripe.payment

import retrofit2.Response
import retrofit2.http.GET

interface PayApiInterface {

    @GET("payment-fetch.php")
    suspend fun fetchPaymentDetails() : Response<PaymentDetailsResponse>
}