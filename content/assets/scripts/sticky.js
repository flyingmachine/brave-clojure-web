$(document).ready(function(){
  var tableOfContentsTopSpacing = 15,
      $window                   = $(window),
      $body                     = $('body');

  // Calculate the height of the visible portion of the footer
  visibleFooterHeight = function() {
    var footerHeight = $('.footer').outerHeight(),
        visibleHeight = footerHeight - ($body.height() - $window.scrollTop() - $window.height());

    // If the result is < 0, reset it to 0
    if (visibleHeight < 0) { visibleHeight = 0; }

    return visibleHeight;
  };

  windowHeightWithoutFooter = function() {
    return $window.height() - visibleFooterHeight();
  };

  $(".secondary .wrapper").sticky({
    topSpacing: tableOfContentsTopSpacing,
    getWidthFrom: ".secondary"
  });

  $(window).on('scroll', function(e) {
    // Whenever the page is scrolled, we need to check if the footer is visible.
    // If it is, we need to make sure the height of the sticky portion (TOC) is
    // adjusted so that the footer does not hide it. In order to do that, we
    // adjust the height if the sticky element to be the window height minus
    // the height of the visible portion of the footer, minus the specified top
    // spacing which the sticky element has.
    //
    // Example: Window height is 480px and 10px of the footer is visible --
    //          the sticky element will be set to 455px tall, (assuming the
    //          top spacing remains 15px).
    $('.secondary .wrapper')
      .css({
        height: windowHeightWithoutFooter() - tableOfContentsTopSpacing
      });
  });
});
