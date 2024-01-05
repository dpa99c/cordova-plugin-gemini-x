/*
 Copyright 2024 Dave Alden/Working Edge Ltd.

 Licensed under MIT.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

var GeminiX = {
    SafetySettingLevel: {
        NONE: "NONE",
        ONLY_HIGH: "ONLY_HIGH",
        MEDIUM_AND_ABOVE: "MEDIUM_AND_ABOVE",
        LOW_AND_ABOVE: "LOW_AND_ABOVE",
        UNSPECIFIED: "UNSPECIFIED"
    },
    SafetySettingHarmCategory: {
        HARASSMENT: "HARASSMENT",
        HATE_SPEECH: "HATE_SPEECH",
        SEXUALLY_EXPLICIT: "SEXUALLY_EXPLICIT",
        DANGEROUS_CONTENT: "DANGEROUS_CONTENT",
        UNSPECIFIED: "UNSPECIFIED"
    },

    initModel : function( success, error, params) {
        if(typeof success !== 'function') return error('success callback must be a function');
        if(typeof error !== 'function') return error('error callback must be a function');
        if(typeof params !== 'object') return error('params argument must be an object');
        if(typeof params.modelName !== 'string') return error('params.modelName must be specified');
        if(typeof params.apiKey !== 'string') return error('params.apiKey must be specified');

        cordova.exec(success, error, "GeminiXPlugin", "initModel", [params])
    },
    sendMessage: function(success, error, text, options){
        if(typeof success !== 'function') return error('success callback must be a function');
        if(typeof error !== 'function') return error('error callback must be a function');
        if(typeof text !== 'string') return error('text argument must be a string');
        if(typeof options !== 'undefined' && typeof options !== 'object') return error('options argument must be an object');

        cordova.exec(function(result){
            success(result.response, result.isFinal);
        }, error, "GeminiXPlugin", "sendMessage", [text, options])
    },
    countTokens: function(success, error, text, options){
        if(typeof success !== 'function') return error('success callback must be a function');
        if(typeof error !== 'function') return error('error callback must be a function');
        if(typeof text !== 'string') return error('text argument must be a string');
        if(typeof options !== 'undefined' && typeof options !== 'object') return error('options argument must be an object');

        cordova.exec(success, error, "GeminiXPlugin", "countTokens", [text, options])
    },
    initChat: function(success, error, chatHistory){
        if(typeof success !== 'function') return error('success callback must be a function');
        if(typeof error !== 'function') return error('error callback must be a function');

        cordova.exec(success, error, "GeminiXPlugin", "initChat", [chatHistory])
    },
    sendChatMessage: function(success, error, text, options){
        if(typeof success !== 'function') return error('success callback must be a function');
        if(typeof error !== 'function') return error('error callback must be a function');
        if(typeof text !== 'string') return error('text argument must be a string');
        if(typeof options !== 'undefined' && typeof options !== 'object') return error('options argument must be an object');

        cordova.exec(function(result){
            success(result.response, result.isFinal);
        }, error, "GeminiXPlugin", "sendChatMessage", [text, options])
    },
    countChatTokens: function(success, error, options){
        if(typeof success !== 'function') return error('success callback must be a function');
        if(typeof error !== 'function') return error('error callback must be a function');
        if(typeof options !== 'undefined' && typeof options !== 'object') return error('options argument must be an object');

        cordova.exec(success, error, "GeminiXPlugin", "countChatTokens", [options])
    },
    getChatHistory: function(success, error){
        if(typeof success !== 'function') return error('success callback must be a function');

        cordova.exec(success, error, "GeminiXPlugin", "getChatHistory", [])
    },
};

module.exports = GeminiX;
