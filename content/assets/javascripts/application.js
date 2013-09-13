$(function(){  
  $("nav").scrollspy()
  _.defer(function(){
    $('[data-spy="scroll"]').each(function () {
      $(this).scrollspy('refresh')
    });
    $("#toc > ol > li.active-section .active").removeClass("active");
    $("#toc > ol > li.active-section > ol > li:first-child").addClass("active");
  })


  var $window = $(window);
  var setBottom = function(){
    var bottom = $window.height() + $window.scrollTop() - $("footer").offset().top
    if (bottom < 0) {
      bottom = 0;
    }
    $('.sticky').css({bottom: bottom})
  }
  $window.scroll(setBottom)
  setBottom();


  
  $(".sticky").sticky({getWidthFrom: 'nav'});
})


