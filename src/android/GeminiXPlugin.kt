package uk.co.workingedge.gemini.x


import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CallbackContext
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import uk.co.workingedge.gemini.x.lib.BlobHistoryPart
import java.io.ByteArrayOutputStream

import uk.co.workingedge.gemini.x.lib.GeminiX
import uk.co.workingedge.gemini.x.lib.HistoryItem
import uk.co.workingedge.gemini.x.lib.HistoryPart
import uk.co.workingedge.gemini.x.lib.ImageHistoryPart
import uk.co.workingedge.gemini.x.lib.TextHistoryPart

class GeminiXPlugin : CordovaPlugin() {

    override fun execute(
        action: String,
        args: JSONArray,
        callbackContext: CallbackContext
    ): Boolean {
        try {
            if (action == "initModel") {
                initModel(callbackContext, args)
            } else if (action == "sendMessage") {
                sendMessage(callbackContext, args)
            } else if (action == "countTokens") {
                countTokens(callbackContext, args)
            } else if (action == "initChat") {
                initChat(callbackContext, args)
            }  else if (action == "sendChatMessage") {
                sendChatMessage(callbackContext, args)
            } else if (action == "countChatTokens") {
                countChatTokens(callbackContext, args)
            }  else if (action == "getChatHistory") {
                getChatHistory(callbackContext)
            } else {
                return false;
            }
            return true
        } catch (e: Exception) {
            sendPluginError(callbackContext, e.message ?: "Unknown error")
        }
        return false
    }

    /**********************
     * Plugin API functions
     ************************/

    private fun initModel(
        callbackContext: CallbackContext,
        args: JSONArray
    ) {
        val params = args.getJSONObject(0)

        // Required params
        val modelName = params.getString("modelName")
        val apiKey = params.getString("apiKey")

        // Optional params
        var temperature: Float? = null
        if (params.has("temperature")) {
            temperature = params.getLong("temperature").toFloat()
        }

        var topK: Int? = null
        if (params.has("topK")) {
            topK = params.getInt("topK")
        }

        var topP: Float?  = null
        if (params.has("topP")) {
            topP = params.getLong("topP").toFloat()
        }

        var maxOutputTokens: Int?  = null
        if (params.has("maxOutputTokens")) {
            maxOutputTokens = params.getInt("maxOutputTokens")
        }

        var stopSequences: List<String>? = null
        if (params.has("stopSequences")) {
            val stopSequenesJSONArray = params.getJSONArray("stopSequences")
            val stopSequencesArray = arrayListOf<String>()
            for (i in 0 until stopSequenesJSONArray.length()) {
                stopSequencesArray.add(stopSequenesJSONArray.getString(i))
            }
            stopSequences = stopSequencesArray.toList()
        }

        val config = mutableMapOf<String, Any>()
        if (temperature != null) {
            config["temperature"] = temperature
        }
        if (topK != null) {
            config["topK"] = topK
        }
        if (topP != null) {
            config["topP"] = topP
        }
        if (maxOutputTokens != null) {
            config["maxOutputTokens"] = maxOutputTokens
        }
        if (stopSequences != null) {
            config["stopSequences"] = stopSequences
        }

        var safetySettings: Map<String, String>? = null
        if (params.has("safetySettings")) {
            val safetySettingsJSONObject = params.getJSONObject("safetySettings")
            val safetySettingsMap = mutableMapOf<String, String>()
            for (key in safetySettingsJSONObject.keys()) {
                safetySettingsMap[key] = safetySettingsJSONObject.getString(key)
            }
            safetySettings = safetySettingsMap.toMap()
        }

        GeminiX.init(modelName, apiKey, config, safetySettings)

        sendPluginResult(callbackContext)
    }

    private fun sendMessage(
        callbackContext: CallbackContext,
        args: JSONArray
    ) {
        var streamResponse = false
        var imageUris = JSONArray()

        val inputText = args.getString(0)
        if(!args.isNull(1)){
            val opts = args.getJSONObject(1)
            if(opts.has("streamResponse")){
                streamResponse = opts.getBoolean("streamResponse")
            }
            if(opts.has("imageUris")){
                imageUris = opts.getJSONArray("imageUris")
            }
        }

        val images:List<Bitmap> = getBitmapsForUris(imageUris)

        GeminiX.sendMessage(
            { response, isFinal ->
                val result = JSONObject()
                result.put("response", response)
                result.put("isFinal", isFinal)
                sendPluginResult(callbackContext, result, streamResponse && !isFinal)
            },
            { error ->
                sendPluginError(callbackContext, error, streamResponse)
            }, inputText, images, streamResponse)
    }

    private fun countTokens(
        callbackContext: CallbackContext,
        args: JSONArray
    ) {
        var imageUris = JSONArray()

        val inputText = args.getString(0)
        if(!args.isNull(1)){
            val opts = args.getJSONObject(1)
            if(opts.has("imageUris")){
                imageUris = opts.getJSONArray("imageUris")
            }
        }

        val images:List<Bitmap> = getBitmapsForUris(imageUris)

        GeminiX.countTokens(
            { response ->
                sendPluginResult(callbackContext, response)
            },
            { error ->
                sendPluginError(callbackContext, error)
            }, inputText, images)
    }

    private fun initChat(
        callbackContext: CallbackContext,
        args: JSONArray
    ) {
        val history: MutableList<HistoryItem> = mutableListOf()
        if(!args.isNull(0)){
            val jsonHistory = args.getJSONArray(0)
            for (i in 0 until jsonHistory.length()) {
                val item = jsonHistory.getJSONObject(i)
                val isUser = item.getBoolean("isUser")

                val parts = item.getJSONArray("parts")
                val historyParts: MutableList<HistoryPart> = mutableListOf()

                for (j in 0 until parts.length()) {
                    val part = parts.getJSONObject(j)
                    val type = part.getString("type")

                    var historyPart:HistoryPart;
                    when (type) {
                        "text" -> {
                            val text = part.getString("content")
                            historyPart = TextHistoryPart(text)
                        }
                        "image" -> {
                            val uri = part.getString("content")
                            val bitmap = getBitmapFromUri(uri)
                            historyPart = ImageHistoryPart(bitmap)
                        }
                        else -> {
                            throw Exception("Unsupported part type: $type")
                        }
                    }
                    historyParts.add(historyPart)
                }
                val historyItem = HistoryItem(historyParts, isUser)
                history.add(historyItem)
            }
        }

        GeminiX.initChat(
            {
                sendPluginResult(callbackContext)
            },
            { error ->
                sendPluginError(callbackContext, error)
            }, history)
    }

    private fun sendChatMessage(
        callbackContext: CallbackContext,
        args: JSONArray
    ) {
        var streamResponse = false
        var imageUris = JSONArray()

        val inputText = args.getString(0)
        if(!args.isNull(1)){
            val opts = args.getJSONObject(1)
            if(opts.has("streamResponse")){
                streamResponse = opts.getBoolean("streamResponse")
            }
            if(opts.has("imageUris")){
                imageUris = opts.getJSONArray("imageUris")
            }
        }

        val images:List<Bitmap> = getBitmapsForUris(imageUris)

        GeminiX.sendChatMessage(
            { response, isFinal ->
                val result = JSONObject()
                result.put("response", response)
                result.put("isFinal", isFinal)
                sendPluginResult(callbackContext, result, streamResponse && !isFinal)
            },
            { error ->
                sendPluginError(callbackContext, error, streamResponse)
            }, inputText, images, streamResponse)
    }

    private fun countChatTokens(
        callbackContext: CallbackContext,
        args: JSONArray
    ) {
        var inputText:String? = null
        var imageUris = JSONArray()

        if(!args.isNull(0)){
            val opts = args.getJSONObject(0)
            if(opts.has("text")){
                inputText = opts.getString("text")
            }
            if(opts.has("imageUris")){
                imageUris = opts.getJSONArray("imageUris")
            }
        }

        val images:List<Bitmap> = getBitmapsForUris(imageUris)

        GeminiX.countChatTokens(
            { response ->
                sendPluginResult(callbackContext, response)
            },
            { error ->
                sendPluginError(callbackContext, error)
            }, inputText, images
        )
    }

    private fun getChatHistory(
        callbackContext: CallbackContext
    ) {
        GeminiX.getChatHistory(
            { history ->
                val result = JSONArray()
                for (item in history) {
                    val itemJSON = JSONObject()
                    itemJSON.put("isUser", item.isUser)
                    val partsJSON = JSONArray()
                    for (part in item.parts) {
                        val partJSON = JSONObject()
                        when (part) {
                            is TextHistoryPart -> {
                                partJSON.put("type", "text")
                                partJSON.put("content", part.content)
                            }
                            is ImageHistoryPart -> {
                                partJSON.put("type", "image/bitmap")
                                partJSON.put("content", bitmapToBase64(part.content))
                            }
                            is BlobHistoryPart -> {
                                partJSON.put("type", part.mimeType)
                                val contentString = Base64.encodeToString(part.content, Base64.DEFAULT)
                                partJSON.put("content", contentString)
                            }
                        }
                        partsJSON.put(partJSON)
                    }
                    itemJSON.put("parts", partsJSON)
                    result.put(itemJSON)
                }
                sendPluginResult(callbackContext, result)
            },
            { error ->
                sendPluginError(callbackContext, error)
            }
        )
    }

    /********************
     * Internal functions
     *******************/
    private fun getBitmapsForUris(imageUris: JSONArray): List<Bitmap> {
        val images = mutableListOf<Bitmap>()
        for (i in 0 until imageUris.length()) {
            val uri = imageUris.getString(i)
            val bitmap = getBitmapFromUri(uri)
            images.add(bitmap)
        }
        return images
    }

    private fun getBitmapFromUri(uri: String): Bitmap {
        return MediaStore.Images.Media.getBitmap(
            cordova.context.contentResolver,
            Uri.parse(uri)
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun sendPluginResult(
        callbackContext: CallbackContext,
        keepCallback: Boolean = false
    ) {
        val pluginResult = PluginResult(PluginResult.Status.OK)
        pluginResult.keepCallback = keepCallback
        callbackContext.sendPluginResult(pluginResult)
    }

    private fun sendPluginResult(
        callbackContext: CallbackContext,
        result: String,
        keepCallback: Boolean = false
    ) {
        val pluginResult = PluginResult(PluginResult.Status.OK, result)
        pluginResult.keepCallback = keepCallback
        callbackContext.sendPluginResult(pluginResult)
    }

    private fun sendPluginResult(
        callbackContext: CallbackContext,
        result: Int,
        keepCallback: Boolean = false
    ) {
        val pluginResult = PluginResult(PluginResult.Status.OK, result)
        pluginResult.keepCallback = keepCallback
        callbackContext.sendPluginResult(pluginResult)
    }

    private fun sendPluginResult(
        callbackContext: CallbackContext,
        result: JSONObject,
        keepCallback: Boolean = false
    ) {
        val pluginResult = PluginResult(PluginResult.Status.OK, result)
        pluginResult.keepCallback = keepCallback
        callbackContext.sendPluginResult(pluginResult)
    }

    private fun sendPluginResult(
        callbackContext: CallbackContext,
        result: JSONArray,
        keepCallback: Boolean = false
    ) {
        val pluginResult = PluginResult(PluginResult.Status.OK, result)
        pluginResult.keepCallback = keepCallback
        callbackContext.sendPluginResult(pluginResult)
    }

    private fun sendPluginError(
        callbackContext: CallbackContext,
        error: String,
        keepCallback: Boolean = false
    ) {
        val pluginResult = PluginResult(PluginResult.Status.ERROR, error)
        pluginResult.keepCallback = keepCallback
        callbackContext.sendPluginResult(pluginResult)
    }

}
