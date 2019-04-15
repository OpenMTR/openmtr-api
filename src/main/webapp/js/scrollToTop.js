((function() {
    /**
     * Scroll to top
     * via of w3Schools
     */
    var style = "<style>button#scrollToTop {\n" +
        "    display: none; /* Hidden by default */\n" +
        "    position: fixed; /* Fixed/sticky position */\n" +
        "    bottom: 5px; /* Place the button at the bottom of the page */\n" +
        "    right: 15px; /* Place the button 30px from the right */\n" +
        "    z-index: 99; /* Make sure it does not overlap */\n" +
        "    border: 1px solid #dbdbdb;\n" +
        "    outline: none; /* Remove outline */\n" +
        "    background-color: #fff; /* Set a background color */\n" +
        "    color: #2684CF; /* Text color */\n" +
        "    cursor: pointer; /* Add a mouse pointer on hover */\n" +
        "    padding: 10px 20px; /* Some padding */\n" +
        "    border-radius: 10px; /* Rounded corners */\n" +
        "    font-size: 18px; /* Increase font size */\n" +
        "    text-align: center;\n" +
        "    opacity: 1;\n" +
        "}\n" +
        "button#scrollToTop:hover {\n" +
        "    background-color: #2684CF;\n" +
        "    color: #fff;\n" +
        "}</style>";
    jQuery("head").append(style);

    jQuery("body").append("<button id=\"scrollToTop\" title=\"Go to top\"><i class=\"angle up icon\"></i> </button>");
    var button = jQuery("button#scrollToTop");

    jQuery(document).on("scroll", function() {
        if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
            button.css("display", "block");
        } else {
            button.css("display", "none");
        }
    });
    button.on("click", function() {
        jQuery("html, body").animate({
            scrollTop: "0"
        }, "slow");
    });
})());