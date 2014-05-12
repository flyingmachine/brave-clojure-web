$(function(){  
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


