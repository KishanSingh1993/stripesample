package `in`.eduforyou.stripe.payment

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api : PayApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl("http://PUT_YOUR_IP_ADDRESS_HERE/payment-tutorial/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PayApiInterface::class.java)
    }
}