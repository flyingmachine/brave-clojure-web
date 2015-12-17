$(document).ready(function(){
  $("#top-nav").append($(".topnav-content"))
  $(".secondary .wrapper").sticky({
    topSpacing: 60,
    getWidthFrom: ".secondary"
  });

  $("#top-nav").sticky()
});
