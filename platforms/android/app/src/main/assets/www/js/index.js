/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
//        var parentElement = document.getElementById(id);
//        var listeningElement = parentElement.querySelector('.listening');
//        var receivedElement = parentElement.querySelector('.received');
//
//        listeningElement.setAttribute('style', 'display:none;');
//        receivedElement.setAttribute('style', 'display:block;');
//
//        console.log('Received Event: ' + id);
        //window.location.href="http://www.baidu.com";
        var dbname = "https_m.baidu.com_0.localstorage";

       function getLocalStorageData(cb){
             cordova.plugin.lstorage.showdata({
                        name:dbname
                    },function(res){
                        console.log(res);

                        if(cb && res.rows){
                            cb(res.rows);
                        }
                    },function(e){
                        alert("failed")
                    });
       }

       function showLocalStorageData(rows){
            var lis=[];
            for(var i=0;i<rows.length;i++){
                lis.push("<li id='"+rows[i].key+"'>"+rows[i].key+"</a></li>")
            }

            $("#localstore").html(lis.toString(""));

            $("li").click(function(){
                 var key=$(this).attr("id");
                 alert(key);
                 cordova.plugin.lstorage.cleardata({
                    name:dbname,
                    keyword:key
                 },
                 function(res){
                    alert(res.rowsAffected);
                    getLocalStorageData(showLocalStorageData);
                 },
                 function(res){
                    alert("failed");
                 });

                 return false;
            });
       }
       getLocalStorageData(showLocalStorageData);

       $("#btnGo").click(function(){
            var _url=$("#url").val();
            if(_url){
                cordova.plugin.lstorage.openUrl({
                    url:_url
                });
            }
       });


    }
};

app.initialize();