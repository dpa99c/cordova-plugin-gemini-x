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

package uk.co.workingedge;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

public class GeminiXPlugin extends CordovaPlugin
{

    /**
     * Constructor.
     */
    public GeminiXPlugin() {}

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute (String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if(action.equals("init") ) {
                init(callbackContext, args);
            }else{
                handleError("Unknown plugin action: " + action, callbackContext);
                return false;
            }
            return true;
        } catch (Exception e) {
            handleException(e, callbackContext);
            return false;
        }
    }

    private void init(CallbackContext callbackContext, JSONArray args){
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                   sendPluginSuccess(callbackContext);
                } catch (Exception e) {
                    handleException(e, callbackContext);
                }
            }
        });
    }

     /*
     * Helper methods
     */
    private void sendPluginSuccess(CallbackContext callbackContext){
        PluginResult result = new PluginResult(PluginResult.Status.OK);
        result.setKeepCallback(false);
        callbackContext.sendPluginResult(result);
    }

    private void handleError(String msg, CallbackContext callbackContext){
        callbackContext.error(msg);
    }

    private void handleException(Exception e, CallbackContext callbackContext){
        String msg = e.getMessage();
        msg = "Exception occurred: ".concat(msg);
        handleError(msg, callbackContext);
    }
}
