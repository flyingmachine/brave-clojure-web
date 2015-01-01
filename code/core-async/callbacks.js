$.get("/cemetaries", function(cemetaries) {
  $.each(cemetaries, function(cemetary){
    doSomething(cemetary);
    $.put("/cemetaries/" + cemetary.id, function(cemetary){
      updateCemetaryDom(cemetary);
    })
  })
});
