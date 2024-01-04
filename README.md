cordova-plugin-clear-data [![Latest Stable Version](https://img.shields.io/npm/v/cordova-plugin-clear-data.svg)](https://www.npmjs.com/package/cordova-plugin-clear-data) [![Total Downloads](https://img.shields.io/npm/dt/cordova-plugin-clear-data.svg)](https://npm-stat.com/charts.html?package=cordova-plugin-clear-data)
=========================

Cordova plugin to clear locally persistent user data on Android and iOS.
 
# Installation

    $ cordova plugin add cordova-plugin-clear-data

# Usage

The plugin is exposed via the `GeminiX` global namespace.

## init()

Initializes the plugin.

### Parameters

- {function} success - (optional) function to execute on successfully initializing the plugin.
- {function} error - (optional) function to execute on failure to initialize the plugin. Will be passed a single argument which is the error message string. 

### Example usage

    GeminiX.init();
