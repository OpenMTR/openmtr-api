"use strict";
var isAdvancedUpload = (function() {
    var div = document.createElement("div");
    return (("draggable" in div) || ("ondragstart" in div && "ondrop" in div)) && "FormData" in window && "FileReader" in window;
}());
var mobilecheck = (function() {
    var check = false;
    ((function(a){
        if(
            /(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||
            /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4))
        )
        {
            check = true;
        }
    })(navigator.userAgent||navigator.vendor||window.opera));
    return check;
}());
var openMtrDemo = {

    init() {
        var self = this;

        self.buildBaseURL();
        self.demoListeners();
        self.dragNdropListeners();
        self.formListeners();

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
        jQuery("button#submit-image").on("click", function(e) {
            e.preventDefault();
             self.readMeter();
        });

        jQuery("div#exampleImages img").on("click", function() {
            var read = jQuery(this).attr("data-read");
            var type = jQuery(this).attr("data-type");
            var time = jQuery(this).attr("data-time");
            var img = jQuery(this).attr("src");
            self.displayModal(read,  type,  time,  img);
        });
    },

    dragNdropListeners() {
        var self = this;
        var form = jQuery("div#dragNdrop .box");
        var input = form.find("input#file");
        var droppedFiles = false;
        //user allows drag n drop
        if(isAdvancedUpload && !mobilecheck) {
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
                    form.addClass("upload-ready");
                    self.displayImg();
                });
            form.find("input[type='file']").on("change", function (e) {
                self.droppedFiles = e.currentTarget.files;
                self.displayImg();
            });
        }
        else {
            form.find("input[type='file']").on("change", function (e) {
                self.droppedFiles = e.currentTarget.files;
                form.addClass("has-success");
            });
        }


        jQuery("div#dragNdrop span#close i").on("click", function() {
            self.droppedFiles = null;
            jQuery("div#dragNdrop span#close").fadeOut("fast");
            jQuery("div#dragNdrop img").attr("src", "").css("max-height", "0px");
            form.removeClass("upload-ready file-dropped");
            jQuery("div#dragNdrop .box").fadeIn("fast");
        });


    },

    displayImg() {
        var self = this;
        var reader = new FileReader();
        reader.onload = function(e) {
            jQuery("div#results-modal img#uploadedFile").attr("src", e.target.result);
            var height = jQuery("div#dragNdrop").height();
            jQuery("div#dragNdrop .box").fadeOut("fast");
            jQuery("div#dragNdrop img").attr("src", e.target.result).css("max-height", height);
            jQuery("div#dragNdrop span#close").fadeIn("fast");
        };
        reader.readAsDataURL(self.droppedFiles[0]);
    },

    displayModal(read, type, time, img) {
        var modal = jQuery("div#results-modal");
        modal.find("p#meterRead").html(read);
        modal.find("p#meterType").html(type);
        modal.find("p#processingTime").html(time);
        modal.find("img#uploadedFile").attr("src", img);
        modal.modal("show");
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

        self.hideErrorMsg();

        if(form.hasClass("is-uploading"))
        {
            return false;
        }

        var appendURL = "";
        var formData = new FormData();

        if(!self.droppedFiles && url.val().length === 0) {
            self.displayErrorMsg("Error", "Please submit an image to read.");
            return false;
        }
        else if(self.droppedFiles) {
            //Possibly upload more than one file
            jQuery.each(self.droppedFiles, function(i, file){
                formData.append("file", file);
            });
        }
        else  {
            if(!self.validateUrl(url.val())) {
                self.displayErrorMsg("Error", "The URL address is invalid.");
                return false;
            }
            formData.append("url", url.val());
            appendURL = "/url";
        }


        if(self.validateEmail(email.val())) {
            formData.append("email", email.val());
        }
        else {
            self.displayErrorMsg("Error", "The E-mail address is invalid.");
            return false;
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
                var time = "";
                if (parseInt(data.processing_time.minutes) > 0) {
                    time += (parseInt(data.processing_time.minutes) === 1)
                        ? data.processing_time.minutes + " minute "
                        : data.processing_time.minutes + " minutes ";
                }
                modal.find("p#processingTime").html(time + data.processing_time.seconds + " seconds");

                jQuery("#results-modal").modal("show");
                self.resetForm();

            },
            error: function(a,b,c) {
                //console.log(a,b,c);
                var data = a.responseJSON;
                if(a.status === 400 && data.error) {
                    self.displayErrorMsg("Error!", data.error_msg);
                    return false;
                }
                if(a.status === 500) {
                    self.displayErrorMsg("System Error.", "I'm sorry. I can't process your request at this time. Please try again later.");
                    return false;
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


        jQuery("div#dragNdrop img").attr("src", "").css("max-height", "0px");
        jQuery("div#dragNdrop span#close").fadeOut("fast");

        form.removeClass("upload-ready has-success is-error");
        form.fadeIn("fast");
        numberOfDigits.val("");
        numberOfDigits.parent().find("div.text").html("Select").addClass("default");
        self.droppedFiles = null;

        url.val("");


    },

    advancedShown: 0,

    toggleAdvanced() {
        var self = this;
        var advanced = jQuery("div#advancedTab");

        if(self.advancedShown === 0) {
            advanced.slideDown(500);
            self.advancedShown = 1;
        }
        else {
            advanced.slideUp(500);
            self.advancedShown = 0;
        }
    },

    errorMsgID: null,
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
