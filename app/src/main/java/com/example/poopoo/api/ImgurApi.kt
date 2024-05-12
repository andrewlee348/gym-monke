package com.example.poopoo.api


import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun getBase64Image(image: Bitmap, complete: (String) -> Unit) {
    GlobalScope.launch {
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val b = outputStream.toByteArray()
        complete(Base64.encodeToString(b, Base64.DEFAULT))
    }
}
fun uploadImageToImgur(image: Bitmap, onFinish: (String) -> Unit) : String{
//        loadingView.show()
    var imgurUrl: String = "bruh"
    val albumHash = "0B9eYbp"
    val CLIENT_ID = "097e22d655f84f8"
    getBase64Image(image, complete = { base64Image ->
        GlobalScope.launch(Dispatchers.Default) {
            val url = URL("https://api.imgur.com/3/image")

            val boundary = "Boundary-${System.currentTimeMillis()}"

            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
            httpsURLConnection.setRequestProperty("Token","8b35756ed9209ef49bda24be8e3457cd13057324");
            httpsURLConnection.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=$boundary"
            )

            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true

            var body = ""
            body += "--$boundary\r\n"
            body += "Content-Disposition:form-data; name=\"image\""
            body += "\r\n\r\n$base64Image\r\n"
//            body += "\r\n\r\nbitch\r\n"
            body += "--$boundary\r\n"
            body += ("Content-Disposition: form-data; name=\"title\"\r\n\r\n")
            body += ("0B9eYbp\r\n")
            body += "--$boundary--\r\n"

//            Log.d("TAG", "body is : ${body}")

            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
            withContext(Dispatchers.IO) {
                outputStreamWriter.write(body)
                outputStreamWriter.flush()
            }
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            val data = jsonObject.getJSONObject("data")
//                loadingView.dismiss()
//            Log.d("TAG", "Response is : ${response}")
            Log.d("test", "Link is : ${data.getString("link")}")
            imgurUrl = data.getString("link")
            onFinish(imgurUrl)

//                val myIntent = Intent(this@MainActivity, DetailsActivity::class.java)
//                myIntent.putExtra("details_link", imgurUrl)
//                startActivity(myIntent)
        }
    })
    return imgurUrl
}
