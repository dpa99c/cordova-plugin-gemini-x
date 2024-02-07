cordova-plugin-gemini-x [![Latest Stable Version](https://img.shields.io/npm/v/cordova-plugin-gemini-x.svg)](https://www.npmjs.com/package/cordova-plugin-gemini-x) [![Total Downloads](https://img.shields.io/npm/dt/cordova-plugin-gemini-x.svg)](https://npm-stat.com/charts.html?package=cordova-plugin-gemini-x)
=========================

Cordova plugin to use Google's [Gemini AI](https://ai.google.dev/docs) in Android and iOS mobile apps.

Part of the [GeminiX abstraction layer](https://github.com/dpa99c/gemini-x) for using Google's Gemini AI SDK in cross-platform mobile apps on iOS, Android and Web platforms.

Enables the use of cutting-edge generative AI directly in a Cordova app with no server-side code required. 

Gemini AI models are designed to understand and generate natural language and multi-modal (text and vision) content, and are available in a variety of languages and regions.

Features include:
- Unified cross-platform abstraction for the Gemini AI SDKs for Android and iOS.
- Text-only and multi-modal (text and vision) models.
- Streaming and non-streaming interactions.
- Chat history for multi-turn conversations.
- Counting tokens for input text and images.
- Safety settings for filtering unsafe content.
- Model configuration parameters such as temperature, topP, topK, maxOutputTokens, and stopSequences.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Example project](#example-project)
- [Requirements](#requirements)
  - [Android](#android)
  - [iOS](#ios)
- [Installation](#installation)
- [Setup](#setup)
  - [iOS](#ios-1)
- [Available regions](#available-regions)
- [Usage](#usage)
  - [Constants](#constants)
    - [SafetySettingHarmCategory](#safetysettingharmcategory)
    - [SafetySettingLevel](#safetysettinglevel)
  - [Methods](#methods)
    - [initModel()](#initmodel)
    - [sendMessage()](#sendmessage)
    - [countTokens()](#counttokens)
    - [initChat()](#initchat)
    - [sendChatMessage()](#sendchatmessage)
    - [countChatTokens()](#countchattokens)
    - [getChatHistory()](#getchathistory)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Example project
See the [example project](https://github.com/dpa99c/cordova-plugin-gemini-x-example) which contains a Cordova app project to demonstrate/validate the plugin functionality.

# Requirements
The following requirements must be met in order to use this plugin:

## Android
- Gemini SDK for Android requires Gradle 8.0 or higher.
  - To set this for Cordova, set the following environment variable before running `cordova build`:
    - `export CORDOVA_ANDROID_GRADLE_DISTRIBUTION_URL=https://services.gradle.org/distributions/gradle-8.0-all.zip`
- Android Gradle Plugin 8.0.0 or higher
- Compile SDK version 34 or higher
- AndroidX
- Kotlin 1.9.0 or higher
- Minimum Cordova Android platform version: `cordova-android@12` 

## iOS
- Xcode 15.0 or higher
- Deployment target of iOS 15.0 or higher
- Minimum Cordova iOS platform version: `cordova-android@7` 
 
# Installation
To install the plugin in your Cordova project, run the following command:

    $ cordova plugin add cordova-plugin-gemini-x

# Setup
The following steps are required to setup the plugin for use in your Cordova project:

## iOS
The Gemini SDK is currently only available via Swift Package Manager (not Cocoapods) and Cordova doesn't currently support the Swift Package Manager, so you will need to manually add the SDK to your Xcode project in `platforms/ios`/.
To add the SDK to your Xcode project, follow [these instructions](https://ai.google.dev/tutorials/swift_quickstart#add-sdk).

# Available regions
Currently Gemini is not available in all regions (mainly UK and EU).
The list of currently supported regions is here: https://ai.google.dev/available_regions#available_regions

To bypass the region restriction, use a VPN on the device to connect to a supported region (such as the US) before running the app.

# Usage
The plugin is exposed via the `GeminiX` global namespace.

## Constants

### SafetySettingHarmCategory
Used to specify the harm category for a safety setting during model initialization.
See [Gemini Safety Settings](https://ai.google.dev/docs/safety_setting_gemini) for more information.

- `HARASSMENT` - Negative or harmful comments targeting identity and/or protected attributes.
- `HATE_SPEECH` - Content that is rude, disrespectful, or profane.
- `SEXUALLY_EXPLICIT` - Contains references to sexual acts or other lewd content.
- `DANGEROUS_CONTENT` - Promotes, facilitates, or encourages harmful acts.

### SafetySettingLevel
Used to specify the safety level for a safety setting during model initialization.
See [Gemini Safety Settings](https://ai.google.dev/docs/safety_setting_gemini) for more information.

- `NONE` - Always show regardless of probability of unsafe content
- `ONLY_HIGH` - Block when high probability of unsafe content
- `MEDIUM_AND_ABOVE` - Block when medium or high probability of unsafe content
- `LOW_AND_ABOVE` - Block when low, medium or high probability of unsafe content
- `UNSPECIFIED` - Use the default safety level for the harm category

## Methods

### initModel()
Initializes a Gemini AI model on [iOS](https://ai.google.dev/tutorials/swift_quickstart#initialize-model) or [Android](https://ai.google.dev/tutorials/android_quickstart#initialize-model). 
This must be called before any other methods are called.

For more information on model parameters, see the [Gemini documentation](https://ai.google.dev/docs/concepts#model-parameters).

#### Parameters
- {function} success (required) - function to execute on successfully initializing the model.
- {function} error (required) - function to execute on failure to initialize the model. Will be passed a single argument which is the error message string.
- {object} params (required) - model initialization parameters.
  - {string} modelName (required) -  name of the [Gemini model](https://ai.google.dev/models/gemini) to initialize. Currently the following models are supported:
    - `gemini-pro` - text-only model. Use this for text-only interactions.
    - `gemini-pro-vision` - multi-modal model with text and vision. Use this for interactions involving text and images.
  - {string} apiKey (required) - your API key for the Gemini model. See [Gemini API keys](https://ai.google.dev/tutorials/setup) for more information.
  - {number} temperature (optional) - temperature for the model. 
  - {number} topP (optional) - topP for the model. 
  - {number} topK (optional) - topK for the model. 
  - {number} maxOutputTokens (optional) - maxOutputTokens for the model.
  - {string[]} stopSequences (optional) - stopSequences for the model.
  - {object} safetySettings (optional) - safetySettings for the model. See [Gemini Safety Settings](https://ai.google.dev/docs/safety_setting_gemini) for more information.
    - {SafetySettingHarmCategory} harmCategory (required) - harm category for the safety setting.
    - {SafetySettingLevel} level (required) - safety level for the safety setting.

#### Example usage

##### Basic initialization
```javascript
var params = {
    modelName: 'gemini-pro',
    apiKey: YOUR_GEMINI_API_KEY
};

GeminiX.initModel(function(){
    console.log(`init success`);
}, function(error){
    console.error(`init error: ${error}`);
}, params);
```

##### Advanced initialization
```javascript
var safetySettings = {};
safetySettings[GeminiX.SafetySettingHarmCategory.DANGEROUS_CONTENT] = GeminiX.SafetySettingLevel.LOW_AND_ABOVE;
safetySettings[GeminiX.SafetySettingHarmCategory.HARASSMENT] = GeminiX.SafetySettingLevel.MEDIUM_AND_ABOVE;

var params = {
    modelName: 'gemini-pro',
    apiKey: YOUR_GEMINI_API_KEY,
    temperature: 0.9,
    topP: 0.1,
    topK: 16,
    maxOutputTokens: 2000,
    stopSequences: ["red"],
    safetySettings: safetySettings
};

GeminiX.initModel(function(){
    console.log(`init success`);
}, function(error){
    console.error(`init error: ${error}`);
}, params);
```

### sendMessage()
Sends a message to the Gemini AI model and returns the response.
- Requires a model to be initialized first using `initModel()`.

#### Parameters
- {function} success (required) - function to execute on receiving a response from the model.
  - {string} responseText - the response text from the model.
  - {boolean} isFinal - whether the response is final or partial.
    - If `streamResponse` is `true`, then `isFinal` will be `false` for partial responses and `true` for the final response.
    - If `streamResponse` is `false`, then `isFinal` will always be `true`.
- {function} error (required) - function to execute on failure to send the message to the model. Will be passed a single argument which is the error message string.
- {string} userInputText (required) - the user input text to send to the model.
- {object} options (optional) - additional options for the message.
  - {boolean} streamResponse (optional) - whether to stream the response from the model. 
    - If `true`, then the `success` callback will be called multiple times with partial responses until the final response is received. The final response text will be empty and `isFinal` will be `true`.
    - If `false`, then the `success` callback will be called once with the final response.
    - Default is `false`.
  - {object[]} images (optional) - array of images on the device to send to the model, each specified as an object with the following properties:
    - {string} uri - the URI of the image on the device.
    - {string} mimeType - (optional) the mime type of the image. If not specified, the plugin will attempt to infer the mime type.
    - Only applicable if the model is a multi-modal model.

#### Example usage
```javascript
// A streaming text-only interaction
var userInputText = 'Hello Gemini';
GeminiX.sendMessage(function(responseText, isFinal){
  if(isFinal){
    console.log(`Response complete`);
  } else {
    console.log(`Partial response: ${responseText}`); // append to previous partial response in UI
  }
}, function(error){
    console.error(`Error: ${error}`);
}, userInputText, {
  streamResponse: true
});

// A non-streaming multi-modal interaction
var userInputText = 'What do you think of this image?';
var image = {
    uri: 'file:///path/to/image/on/device/1.jpg',
    mimeType: 'image/jpeg'
};
GeminiX.sendMessage(function(responseText, isFinal){
  console.log(`Response: ${responseText}`); // display in UI
}, function(error){
  console.error(`Error: ${error}`);
}, userInputText, {
  streamResponse: false,
  images: [image]
});
```

### countTokens()
Retrieves the number of tokens used by the given input text and optional images for the current Gemini AI model.
- Requires a model to be initialized first using `initModel()`.

#### Parameters
- {function} success (required) - function to execute on receiving a response from the model.
  - {string} count - the response text from the model.
- {function} error (required) - function to execute on failure to count the tokens. Will be passed a single argument which is the error message string.
- {string} userInputText (required) - the user input text to send to the model.
- {object} options (optional) - additional options for the message.
  - {object[]} images (optional) - array of images on the device to send to the model, each specified as an object with the following properties:
    - {string} uri - the URI of the image on the device.
    - {string} mimeType - (optional) the mime type of the image. If not specified, the plugin will attempt to infer the mime type.
    - Only applicable if the model is a multi-modal model.

#### Example usage
```javascript
// Count tokens for text-only interaction
var userInputText = 'Hello Gemini';
GeminiX.countTokens(function(count){
    console.log(`Token count: ${count}`);
}, function(error){
  console.error(`Error: ${error}`);
}, userInputText);

// Count tokens for multi-modal interaction
var userInputText = 'What do you think of this image?';
var image = {
  uri: 'file:///path/to/image/on/device/1.jpg',
  mimeType: 'image/jpeg'
};
GeminiX.countTokens(function(count){
    console.log(`Token count: ${count}`);
}, function(error){
  console.error(`Error: ${error}`);
}, userInputText, {
  images: [image]
});
```

### initChat()
Initializes a Gemini AI chat session for a multi-turn conversation with an optional chat history.
- Can be used to restore a chat session on app restart.
- Requires a model to be initialized first using `initModel()`.

#### Parameters
- {function} success (required) - function to execute on successfully initializing the chat session.
- {function} error (required) - function to execute on failure to initialize the chat session. Will be passed a single argument which is the error message string.
- {array} chatHistory (optional) - array of chat history items to initialize the chat session with. Each item in the array should be an object with the following properties:
  - {boolean} isUser (required) - whether the chat history item is from the user or the model. Either `text` and/or `imageUris` must be specified.
  - {array} parts (required) - array of parts for the chat history item.
    - Each part in the array should be an object with the following properties:
      - {string} type (required) - the type of the part. Either `text` or `image`
      - {string} content (required) - the content of the part. 
        - If `type` is `text`, then this should be the text content.
        - If `type` is `image`, then this should be an object representing an image on the device with the following properties:
          - {string} uri - the URI of the image on the device.
          - {string} mimeType - (optional) the mime type of the image. If not specified, the plugin will attempt to infer the mime type.
          - Only applicable if the model is a multi-modal model.

#### Example usage
```javascript
// Initialize a text-only chat session with chat history
var chatHistory = [
  {
    isUser: true,
    parts:[
      {
        type: 'text',
        content: 'Hello Gemini'
      }
    ]
  },
  {
    isUser: false,
    parts:[
      {
        type: 'text',
        content: 'Hi there'
      }
    ]
  },
  {
    isUser: true,
    parts:[
      {
        type: 'text',
        content: 'My name is Bob and I live in a small town in Ohio called Springfield. I like to play tennis and go hiking.'
      }
    ]
  },
  {
    isUser: false,
    parts:[
      {
        type: 'text',
        content: 'Nice to meet you Bob'
      }
    ]
  }
];
GeminiX.initChat(function(){
    console.log(`Chat session initialized`);
}, function(error){
  console.error(`Error: ${error}`);
}, chatHistory);

// Initialize a multi-modal chat session with chat history
var chatHistory = [
  {
    isUser: true,
    parts:[
      {
        type: 'text',
        content: 'What do you think of this image?'
      },
      {
        type: 'image',
        content: {
          uri: 'file:///path/to/image/on/device/1.jpg',
          mimeType: 'image/jpeg'
        }
      }
    ]
  },
  {
    isUser: false,
    text: 'I like it'
  },
  {
    isUser: true,
    parts:[
        {
            type: 'text',
            content: 'What about this one?'
        },
        {
            type: 'image',
            content: {
            uri: 'file:///path/to/image/on/device/2.jpg',
            mimeType: 'image/jpeg'
            }
        }
    ]
  },
  {
    isUser: false,
    text: 'I like this one too'
  }
];
GeminiX.initChat(function(){
    console.log(`Chat session initialized`);
}, function(error){
  console.error(`Error: ${error}`);
}, chatHistory);
```

### sendChatMessage()
Sends a message to the Gemini AI chat session and returns the response.
- Requires a chat session to be initialized first using `initChat()`.

#### Parameters
- {function} success (required) - function to execute on receiving a response from the model.
  - {string} responseText - the response text from the model.
  - {boolean} isFinal - whether the response is final or partial.
    - If `streamResponse` is `true`, then `isFinal` will be `false` for partial responses and `true` for the final response.
    - If `streamResponse` is `false`, then `isFinal` will always be `true`.
- {function} error (required) - function to execute on failure to send the message to the model. Will be passed a single argument which is the error message string.
- {string} userInputText (required) - the user input text to send to the model.
- {object} options (optional) - additional options for the message.
  - {boolean} streamResponse - whether to stream the response from the model.
    - If `true`, then the `success` callback will be called multiple times with partial responses until the final response is received. The final response text will be empty and `isFinal` will be `true`.
    - If `false`, then the `success` callback will be called once with the final response.
    - Default is `false`.
  - {object[]} images (optional) - array of images on the device to send to the model, each specified as an object with the following properties:
    - {string} uri - the URI of the image on the device.
    - {string} mimeType - (optional) the mime type of the image. If not specified, the plugin will attempt to infer the mime type.
    - Only applicable if the model is a multi-modal model.

#### Example usage
```javascript
// A streaming text-only interaction
var userInputText = `Tell me where I can find a good restaurant near where I live`;
GeminiX.sendChatMessage(function(responseText, isFinal){
  if(isFinal){
    console.log(`Response complete`);
  } else {
    console.log(`Partial response: ${responseText}`); // append to previous partial response in UI
  }
}, function(error){
    console.error(`Error: ${error}`);
}, userInputText, {
  streamResponse: true
});

// A non-streaming multi-modal interaction
var userInputText = 'What do you think of this image compared to the previous two?';
var image = {
  uri: 'file:///path/to/image/on/device/1.jpg',
  mimeType: 'image/jpeg'
};
GeminiX.sendChatMessage(function(responseText, isFinal){
  console.log(`Response: ${responseText}`); // display in UI
}, function(error){
  console.error(`Error: ${error}`);
}, userInputText, {
  streamResponse: false,
  images: [image]
});
```

### countChatTokens()
Retrieves the number of tokens used by the chat history, and the optional input text and/or images, for the current Gemini AI chat session.
- Requires a chat session to be initialized first using `initChat()`.

#### Parameters
- {function} success (required) - function to execute on receiving a response from the model.
  - {string} count - the response text from the model.
- {function} error (required) - function to execute on failure to count the tokens. Will be passed a single argument which is the error message string.
- {object} options (optional) - additional options for the message.
  - {string} text - the user input text to send to the model.
  - {object[]} images (optional) - array of images on the device to send to the model, each specified as an object with the following properties:
    - {string} uri - the URI of the image on the device.
    - {string} mimeType - (optional) the mime type of the image. If not specified, the plugin will attempt to infer the mime type.
    - Only applicable if the model is a multi-modal model.

#### Example usage
```javascript
// Count tokens for existing chat history only
GeminiX.countChatTokens(function(count){
    console.log(`Token count: ${count}`);
}, function(error){
  console.error(`Error: ${error}`);
});

// Count tokens for an additional multi-modal interaction
var userInputText = 'What do you think of this image?';
var image = {
  uri: 'file:///path/to/image/on/device/1.jpg',
  mimeType: 'image/jpeg'
};
GeminiX.countChatTokens(function(count){
  console.log(`Token count: ${count}`);
}, function(error){
  console.error(`Error: ${error}`);
}, {
  text: userInputText,
  images: [image]
});
```

### getChatHistory()
Retrieves the chat history for the current Gemini AI chat session.
- Requires a chat session to be initialized first using `initChat()`.
- Can be used to store the chat history for restoration on app restart.
- Note that for multi-modal chat history, the image data returned will be the base64-encoded image data rather than the original image URI since the latter is not available in the model's chat history.

#### Parameters
- {function} success (required) - function to execute on receiving a response from the model.
  - {array} chatHistory - the chat history for the current chat session.
    - Each item in the array will be an object with the following properties:
      - {boolean} isUser - whether the chat history item is from the user or the model.
      - {array} parts - array of parts for the chat history item.
        - Each part in the array will be an object with the following properties:
          - {string} type - the type of the part. Either `text` or the mime type of the image.
          - {string} content - the content of the part. Either the text or the base64-encoded image data.
- {function} error (required) - function to execute on failure to retrieve the chat history. Will be passed a single argument which is the error message string.

#### Example usage
```javascript
GeminiX.getChatHistory(function(chatHistory){
    console.log(`Chat history: ${JSON.stringify(chatHistory)}`);
}, function(error){
  console.error(`Error: ${error}`);
});
```
