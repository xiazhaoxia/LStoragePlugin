cordova.define("LStorage.LStorage", function(require, exports, module) {
var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'LStorage', 'cleardata', [arg0]);
};

});
