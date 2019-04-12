// JQuery for responsive Navbar

$(document).ready(function () {
    var mobile = {
        navOpen: 0,
        init() {
            var self = this;
            var mobileNav  = $("div#mobile-menu");
            $('.menu-toggle').on('click', function (evt) {
                evt.stopPropagation();
                if (self.navOpen === 0) {
                    mobileNav.slideDown(500);
                    self.navOpen = 1;
                }
                else {
                    mobileNav.slideUp(500);
                    self.navOpen = 0;
                }
            });

        }



    };

    mobile.init();
});

