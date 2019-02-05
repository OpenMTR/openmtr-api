"use strict";
var openMtrDemo = {

    init: function() {
        var self = this;

        self.buildBaseURL();

        self.demoListeners();
    },

    //Build the base URL for AJAX calls
    buildBaseURL: function() {
        var base = document.location.origin;

        window.baseURL = base + "/openmtr-api";
    },

    demoListeners: function() {
        var self = this;
        //Listen for the click on the submit button
        jQuery('div#formSubmit').on('click', function() {
             self.readMeter();
        });
    },

    readMeter: function() {
    	//Get the results div
    	var results = jQuery('div#readMeterResults');
        //initalize the default form method
        var type = "GET";
        var appendURL = "";

        //initalize the form data
        var formData = "";

        //If there is a file, then upload the file
        var file = document.getElementById('fileElem');
        if(file.value !== "") {
            type = "POST";
            formData = new FormData();
            formData.append('file', file.files[0]);
        }
        else {
            var url = jQuery('input#url');
            appendURL = "?url=" + url.val();
        }



        jQuery.ajax({
            type: type,
            url: window.baseURL + "/api/read_meter" + appendURL,
            data: formData,
            processData: false,
            contentType: false,
            dataType: "json",
            cache: false,
            beforeSend: function() {
                //Put some kind of loading overlay here
            },
            complete: function() {
                //remove the loading overlay
            },
            success: function(data) {
                console.log(data);
                if(data.error === true) {
                    console.log("error: " + data.error_msg);
                    results.html(data.error_msg);
                    return;
                }
                console.log("data: " + data.data);
                results.html(data.data);

            },
            error: function(a,b,c) {
                console.log(a,b,c);
            }
        });
    }

};