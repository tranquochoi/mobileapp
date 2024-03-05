import com.example.myaiapp.JishoApiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface JishoApiService {
    @GET("api/v1/search/words")
    suspend fun searchWords(@Query("keyword") keyword: String): JishoApiResponse

    companion object {
        private const val BASE_URL = "https://jisho.org/"

        fun create(): JishoApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JishoApiService::class.java)
        }
    }
}
