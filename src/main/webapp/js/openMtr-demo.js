"use strict";
var isAdvancedUpload = (function() {
    var div = document.createElement("div");
    return (("draggable" in div) || ("ondragstart" in div && "ondrop" in div)) && "FormData" in window && "FileReader" in window;
}());
var openMtrDemo = {

    init() {
        var self = this;

        self.buildBaseURL();
        self.demoListeners();
        self.dragNdropListeners();
        self.formListeners();

        jQuery("#submit-image").click(function () {
            //jQuery("#results-modal").modal("show");
            self.readMeter();
        });

        jQuery("#email-question").click(function () {
            jQuery("#email-privacy-modal").modal("show");
        });

        jQuery("div#digit_dropdown.dropdown").dropdown();

        jQuery("div#error_msg i.close").on("click", function() {
            self.hideErrorMsg();
        });
    },

    //Build the base URL for AJAX calls
    buildBaseURL() {
        var base = document.location.origin;
        window.baseURL = base;
    },

    demoListeners() {
        var self = this;
        //Listen for the click on the submit button
        jQuery("div#formSubmit").on("click", function() {
             self.readMeter();
        });
    },

    dragNdropListeners() {
        var self = this;
        var form = jQuery("div#dragNdrop .box");
        var input = form.find("input#file");
        var droppedFiles = false;
        //user allows drag n drop
        if(isAdvancedUpload) {
            form.addClass("has-advanced-upload");

            form.on("drag dragstart dragend dragover dragenter dragleave drop", function(e) {
                e.preventDefault();
                e.stopPropagation();
            })
                .on("dragover dragenter", function() {
                    form.addClass("is-dragover");
                })
                .on("dragleave dragend drop", function() {
                    form.removeClass("is-dragover");
                })
                .on("drop", function(e) {
                    self.droppedFiles = e.originalEvent.dataTransfer.files;
                    form.addClass("upload-ready file-dropped");
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        form.find("div.box__fileDropped img").attr("src", e.target.result);
                        jQuery("div#results-modal img#uploadedFile").attr("src", e.target.result);
                    };
                    reader.readAsDataURL(self.droppedFiles[0]);

                    //For auto submit
                    //form.trigger("submit");
                });
        }

        //For auto submit
        // input.on("change", function() {
        //     form.trigger("submit");
        // });

    },

    formListeners() {
        var self = this;
        var url = jQuery("form.my-form input#url");
        var email = jQuery("form.my-form input#email");
        var numberOfDials = jQuery("form.my-form input#digit-number");

        email.on("keyup", function() {
            if(!self.validateEmail(jQuery(this).val()))
            {
                jQuery(this).parent().parent().addClass("error");
            }
            else
            {
                jQuery(this).parent().parent().removeClass("error");
            }
        });

        url.on("keyup", function() {
            var innerSelf = jQuery(this);
            if(innerSelf.val().length > 0 && !self.validateUrl(jQuery(this).val()))
            {
                jQuery(this).parent().parent().addClass("error");
            }
            else
            {
                jQuery(this).parent().parent().removeClass("error");
            }
        });
    },

    validateEmail(email) {
        return (/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(email));
    },

    validateUrl(url) {
        return (/^([\w\-\.]+[^#?\s]+)(.*)?(#[\w\-]+)?$/.test(url));
    },

    droppedFiles : null,
    readMeter() {
        var self = this;
        var form = jQuery("div#dragNdrop.box");
        var url = jQuery("form.my-form input#url");
        var email = jQuery("form.my-form input#email");
        var numberOfDigits = jQuery("form.my-form input#digit-number");

        if(form.hasClass("is-uploading"))
        {
            return false;
        }

        var appendURL = "";
        var formData = new FormData();

        //If there is a file, then upload the file
        if(isAdvancedUpload && self.droppedFiles) {
            //Possibly upload more than one file
            jQuery.each(self.droppedFiles, function(i, file){
                formData.append("file", file);
            });
        }
        else  {
            //ToDo: Add url validation
            formData.append("url", url.val());
            appendURL = "/url";
        }


        if(self.validateEmail(email.val())) {
            //ToDo: Add email validation
            formData.append("email", email.val());
        }

        if(numberOfDigits.val()) {
            //ToDo: Add number of digit"s validation
            var num = "";
            for(var a = 0; a < parseInt(numberOfDigits.val()); a++) {
                num += "9";
            }
            formData.append("numberOfDials", num);
        }



        jQuery.ajax({
            type: "POST",
            url: window.baseURL + "/api/read_meter" + appendURL,
            data: formData,
            processData: false,
            contentType: false,
            dataType: "json",
            cache: false,
            beforeSend: function() {
                self.hideErrorMsg();
                form.addClass("is-uploading").removeClass("is-error");
                jQuery(".overlay").fadeIn("fast");
            },
            complete: function() {
                form.removeClass("is-uploading");
                jQuery(".overlay").fadeOut("fast");
            },
            success: function(data) {
                //console.log(data);
                if(data.error) {
                    self.displayErrorMsg("Error!", data.error_msg);
                    return false;
                }

                var modal = jQuery("div#results-modal");
                modal.find("p#meterRead").html(data.meter_read.read);
                modal.find("p#meterType").html(data.meter_read.type);
                modal.find("p#processingTime").html(data.processing_time.seconds + " seconds");

                jQuery("#results-modal").modal("show");
                self.resetForm();

            },
            error: function(a,b,c) {
                //console.log(a,b,c);
                var data = a.responseJSON;
                if(a.status === 400 && data.error) {
                    self.displayErrorMsg("Error!", data.error_msg);
                }
            }
        });
    },

    resetForm() {
        var self = this;
        var form = jQuery("div#dragNdrop .box");
        var url = jQuery("form.my-form input#url");
        var email = jQuery("form.my-form input#email");
        var numberOfDigits = jQuery("form.my-form input#digit-number");

        form.find("div.box__fileDropped img").attr("src", "");
        form.removeClass("upload-ready file-dropped");
        numberOfDigits.val("");
        numberOfDigits.parent().find("div.text").html("Select").addClass("default");
        self.droppedFiles = null;


    },

    displayErrorMsg(title, text, level) {
        var self = this;
        var errorMsg = jQuery("div#error_msg");
        if(self.errorMsgID) {
            clearTimeout(self.errorMsgID);
            errorMsg.slideUp(500);
            self.errorMsgID = null;
        }

        errorMsg.find("div#title").html(title);
        errorMsg.find("p#message").html(text);
        errorMsg.slideDown(500);
    },

    hideErrorMsg() {
        var errorMsg = jQuery("div#error_msg");
        errorMsg.slideUp(500);
    }

};
