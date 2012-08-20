$(function(){
  var $win = $(window)
    , $toc = $("#toc") 
    , navTop = $('#toc').length && $('#toc').offset().top - 40
    , isFixed = 0
  
  $(".secondary").scrollspy()
  _.defer(function(){
    $('[data-spy="scroll"]').each(function () {
      var $spy = $(this).scrollspy('refresh')
    });
    $(".secondary .active").removeClass("active");
    $(".secondary #toc > ol > li:first-child").addClass("active");
  })

})
