cordova.define("LStorage.LStorage", function(require, exports, module) {
var exec = require('cordova/exec');

exports.cleardata = function (arg0, success, error) {
    exec(success, error, 'LStorage', 'cleardata', [arg0]);
};

exports.showdata = function (arg0, success, error) {
    exec(success, error, 'LStorage', 'showdata', [arg0]);
};

exports.openUrl = function (arg0, success, error) {
    exec(success, error, 'LStorage', 'openUrl', [arg0]);
};

});
