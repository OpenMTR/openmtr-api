// JQuery for responsive Navbar

    function toggleNavMenu() {
        $('.mobile-nav-menu').toggleClass('open');
    }

    function navMenuIsOpen() {
        return $('.mobile-nav-menu').hasClass('open');
}

    $(document).ready(function () {
        $('.menu-toggle').on('click', function (evt) {
            toggleNavMenu();
            evt.stopPropagation();
        });
    $('.mobile-nav-menu').on('click', function (evt) {
        evt.stopPropagation();
    });
        $(window).on('click', function () {
            if (navMenuIsOpen()) {
        toggleNavMenu();
    }
});
        $(window).resize(function () {
            if (window.innerWidth > 645 && navMenuIsOpen()) {
        toggleNavMenu();
    }
});
});

