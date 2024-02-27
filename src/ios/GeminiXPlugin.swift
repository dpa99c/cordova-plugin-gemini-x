extension String: Error {}



@objc(GeminiXPlugin)
class GeminiXPlugin : CDVPlugin{
    
    /**********************
     * Plugin API functions
     ************************/
    @objc(initModel:)
    func initModel(_ command: CDVInvokedUrlCommand) {
        let params = (command.arguments[0] as? [String: Any])!
        
        GeminiX.initModel(onSuccess: {
            self.sendPluginSuccess(command: command, keepCallback:false)
        }, onError: { error in
            self.sendPluginError(command: command, error: "\(error)", keepCallback:false)
        }, params:params)
    }
    
    @objc(sendMessage:)
    func sendMessage(_ command:CDVInvokedUrlCommand) {
        let inputText = command.arguments[0] as! String
        let options = command.arguments[1] as! [String:Any]
        let streamResponse = options["streamResponse"] as? Bool ?? false
        let modelImages:[ImageDataWithType] = GeminiX.getImagesFromOptions(options: options)
        
        GeminiX.sendMessage(onSuccess: { response, isFinal, success in
            let result = [
                "response": response,
                "isFinal": isFinal
            ]
            self.sendPluginSuccess(command: command, result: result,  keepCallback:streamResponse && !isFinal)
            success()
        }, onError: { error in
            self.sendPluginError(command: command, error: "\(error)", keepCallback:streamResponse)
        }, inputText: inputText, images: modelImages, streamResponse: streamResponse)
    }
    
    @objc(countTokens:)
    func countTokens(_ command:CDVInvokedUrlCommand) {
        let inputText = command.arguments[0] as! String
        let options = command.arguments[1] as! [String:Any]
        let modelImages:[ImageDataWithType] = GeminiX.getImagesFromOptions(options: options)
        
        GeminiX.countTokens(onSuccess: { result in
            self.sendPluginSuccess(command: command, result: "\(result)",  keepCallback:false)
        }, onError: { error in
            self.sendPluginError(command: command, error: "\(error)", keepCallback:false)
        }, inputText: inputText, images: modelImages)
    }
    
    @objc(initChat:)
    func initChat(_ command:CDVInvokedUrlCommand) {
        do{
            var history: [HistoryItem] = []
            let history_object = command.arguments[0]
            let history_json = try JSONSerialization.data(withJSONObject: history_object, options: [])
            if let history_array = try JSONSerialization.jsonObject(with: history_json, options: []) as? [[String: Any]] {
                for item in history_array {
                    if let isUser = item["isUser"] as? Bool {
                        if let parts = item["parts"] as? [Any]{
                            var historyParts:[HistoryPart] = []
                            for _part in parts {
                                if let part = _part as? [String:Any]{
                                    if let type = part["type"] as? String {
                                        if type == "text" {
                                            if let content = part["content"] as? String {
                                                let textPart = TextHistoryPart(content: content)
                                                historyParts.append(textPart)
                                            }
                                        }
                                        if type == "image" {
                                            if let uri = part["content"] as? String {
                                                let image:UIImage? = GeminiX.getImageForUri(imageUri: uri)
                                                if(image != nil){
                                                    let imagePart = ImageHistoryPart(content: image!)
                                                    historyParts.append(imagePart)
                                                }else{
                                                    throw "Image file not found: \(uri)"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            let historyItem = HistoryItem(parts:historyParts, isUser:isUser)
                            history.append(historyItem)
                        }
                    }
                }
            }
            
            GeminiX.initChat(onSuccess: {
                self.sendPluginSuccess(command: command, keepCallback:false)
            }, onError: { error in
                self.sendPluginError(command: command, error: "\(error)", keepCallback:false)
            }, history: history)
        }catch{
            self.sendPluginError(command: command, error: "\(error)", keepCallback:false)
        }
    }
    
    @objc(sendChatMessage:)
    func sendChatMessage(_ command:CDVInvokedUrlCommand) {
        let inputText = command.arguments[0] as! String
        let options = command.arguments[1] as! [String:Any]
        let streamResponse = options["streamResponse"] as? Bool ?? false
        let modelImages:[ImageDataWithType] = GeminiX.getImagesFromOptions(options: options)
        
        GeminiX.sendChatMessage(onSuccess: { response, isFinal, success in
            let result = [
                "response": response,
                "isFinal": isFinal
            ]
            self.sendPluginSuccess(command: command, result: result,  keepCallback:streamResponse && !isFinal)
            success()
        }, onError: { error in
            self.sendPluginError(command: command, error: "\(error)", keepCallback:streamResponse)
        }, inputText: inputText, images: modelImages, streamResponse: streamResponse)
    }
    
    @objc(countChatTokens:)
    func countChatTokens(_ command:CDVInvokedUrlCommand) {
        let options = command.arguments[0] as! [String:Any]

        var inputText = options["text"] as? String ?? nil;
        if(inputText == ""){
            inputText = nil
        }

        let modelImages:[ImageDataWithType] = GeminiX.getImagesFromOptions(options: options)
        
        GeminiX.countChatTokens(onSuccess: { result in
            self.sendPluginSuccess(command: command, result: "\(result)",  keepCallback:false)
        }, onError: { error in
            self.sendPluginError(command: command, error: "\(error)", keepCallback:false)
        }, inputText: inputText, images: modelImages)
    }

    @objc(getChatHistory:)
    func getChatHistory(_ command:CDVInvokedUrlCommand) {
        GeminiX.getChatHistory(onSuccess: { history in
            var historyArray:[[String:Any]] = []
            for item in history {
                var itemParts:[[String:Any]] = []
                for part in item.parts {
                    var partDict:[String:Any] = [:]
                    if let textPart = part as? TextHistoryPart {
                        partDict["type"] = "text"
                        partDict["content"] = textPart.content
                    }
                    if let blobPart = part as? BlobHistoryPart {
                        partDict["type"] = blobPart.mimeType
                        if let content = blobPart.content as? Data{
                            partDict["content"] = content.base64EncodedString()
                        }
                    }
                    itemParts.append(partDict)
                }
                let itemDict:[String:Any] = [
                    "isUser": item.isUser,
                    "parts": itemParts
                ]
                historyArray.append(itemDict)
            }
            self.sendPluginSuccess(command: command, result: historyArray,  keepCallback:false)
        }, onError: { error in
            self.sendPluginError(command: command, error: "\(error)", keepCallback:false)
        })
    }


    /**
     * Internal functions
     */

    
    func sendPluginNoResult(command: CDVInvokedUrlCommand) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_NO_RESULT)
        pluginResult!.setKeepCallbackAs(true)
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }
    
    func sendPluginSuccess(command: CDVInvokedUrlCommand, keepCallback:Bool) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
        pluginResult!.setKeepCallbackAs(keepCallback)
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }
    
    func sendPluginSuccess(command: CDVInvokedUrlCommand, result: String, keepCallback:Bool) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: result)
        pluginResult!.setKeepCallbackAs(keepCallback)
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }
    
    func sendPluginSuccess(command: CDVInvokedUrlCommand, result: [String: Any], keepCallback:Bool) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: result)
        pluginResult!.setKeepCallbackAs(keepCallback)
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }
    
    func sendPluginSuccess(command: CDVInvokedUrlCommand, result: [[String: Any]], keepCallback:Bool) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: result)
        pluginResult!.setKeepCallbackAs(keepCallback)
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }
    
    func sendPluginError(command: CDVInvokedUrlCommand, error:String, keepCallback:Bool) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: error)
        pluginResult!.setKeepCallbackAs(keepCallback)
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }
}
