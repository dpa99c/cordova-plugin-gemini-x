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

#import "GeminiXPlugin.h"

@implementation GeminiXPlugin

/***************
 * Plugin API
 ***************/
-(void)init:(CDVInvokedUrlCommand*)command {
    @try {
       [self sendPluginSuccess:command];
    }@catch (NSException *exception) {
        [self handleException:exception command:command];
    }
}


/***************
 * Helper Methods
 ***************/
- (void)sendPluginSuccess:(CDVInvokedUrlCommand*)command{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}
                               
- (void)sendPluginError:(NSString*)message command:(CDVInvokedUrlCommand*)command{
   CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                               messageAsString:[message stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLHostAllowedCharacterSet]]];
   [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)handleException:(NSException*)exception command:(CDVInvokedUrlCommand*)command{
   [self sendPluginError:[NSString stringWithFormat:@"EXCEPTION: %@", exception.reason] command:command];
}

@end
