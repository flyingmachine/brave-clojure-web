$(document).ready(function(){
  $("#top-nav").append($(".topnav-content"))
  $(".chapter-nav .wrapper").sticky({
    topSpacing: 60,
    getWidthFrom: ".chapter-nav"
  });

  $("#top-nav").sticky()
});
