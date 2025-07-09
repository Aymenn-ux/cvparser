object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://your-server-ip:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: CVParseService = retrofit.create(CVParseService::class.java)
}

interface CVParseService {
    @Multipart
    @POST("process")
    suspend fun processCVs(@Part files: List<MultipartBody.Part>): Response<ProcessResponse>
}

data class ProcessResponse(
    val success: Boolean,
    val results: List<CVResult>?,
    val error: String?
)