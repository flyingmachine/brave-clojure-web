$(function(){  
  $("#toc > ol > li.active").scrollspy()
  _.defer(function(){
    $('[data-spy="scroll"]').each(function () {
      var $spy = $(this).scrollspy('refresh')
    });
    $("#toc > ol > li.active-section .active").removeClass("active");
    $("#toc > ol > li.active-section > ol > li:first-child").addClass("active");
  })

})
