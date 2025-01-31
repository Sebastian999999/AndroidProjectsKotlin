import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

class YouTubeVideoInfoRetriever {
    private val kvpList = mutableMapOf<String, String>()

    companion object {
        const val URL_YOUTUBE_GET_VIDEO_INFO = "https://www.youtube.com/get_video_info?&video_id="
        const val KEY_DASH_VIDEO = "dashmpd"
        const val KEY_HLS_VIDEO = "hlsvp"
    }

    @Throws(IOException::class)
    fun retrieve(videoId: String) {
        val targetUrl = "$URL_YOUTUBE_GET_VIDEO_INFO$videoId&el=info&ps=default&eurl=&gl=US&hl=en"
        val client = SimpleHttpClient()
        val output = client.execute(targetUrl, SimpleHttpClient.HTTP_GET, SimpleHttpClient.DEFAULT_TIMEOUT)
        parse(output)
    }

    fun getInfo(key: String): String? = kvpList[key]

    private fun parse(data: String) {
        kvpList.clear()
        data.split("&").forEach { kvpStr ->
            try {
                var decoded = URLDecoder.decode(kvpStr, SimpleHttpClient.ENCODING_UTF_8)
                decoded = URLDecoder.decode(decoded, SimpleHttpClient.ENCODING_UTF_8)
                decoded.split("=", limit = 2).let { parts ->
                    when (parts.size) {
                        2 -> kvpList[parts[0]] = parts[1]
                        1 -> kvpList[parts[0]] = ""
                    }
                }
            } catch (e: Exception) {
                // Handle decoding error
            }
        }
    }

    class SimpleHttpClient {
        companion object {
            const val ENCODING_UTF_8 = "UTF-8"
            const val DEFAULT_TIMEOUT = 10000
            const val HTTP_GET = "GET"
        }

        @Throws(IOException::class)
        fun execute(urlStr: String, httpMethod: String, timeout: Int): String {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = timeout
            conn.requestMethod = httpMethod

            try {
                BufferedInputStream(conn.inputStream).use { inputStream ->
                    return readStream(inputStream)
                }
            } finally {
                conn.disconnect()
            }
        }

        private fun readStream(inputStream: InputStream): String {
            return inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        }
    }
}