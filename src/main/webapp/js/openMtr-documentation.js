"use strict";
var openMtrDoc = {
    init() {
        var self = this;
        self.tabListener();
    },

    tabListener() {
        jQuery("div.tabs a").on("click", function() {
            if (jQuery(this).hasClass("disabled")) {
                return false;
            }

            jQuery(this)
                .parent()
                .find(".active")
                .removeClass("active");

            jQuery(this).addClass("active");


            var toActivate = jQuery(this).attr("data-content");
            var mainTab = jQuery(this).parent().attr("data-parenttab");

            var oldActiveContent = jQuery(mainTab + " > div.active");
            oldActiveContent.removeClass("active");

            var newActiveContent = jQuery("div" + mainTab + " > div" + toActivate);
            newActiveContent.addClass("active");
        });
    }

};