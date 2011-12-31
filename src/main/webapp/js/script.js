$(function() {

  function numberOfTimes(number) {
    if (number == 1) return "once"
    if (number == 2) return "twice"
    return number + " times"
  }

  $("#entity_example").submit(function() {
    var text = $("#textarea").val()
    $.ajax({
      type: 'POST',
      url: "/api/entities",
      data: { text : text},
      success: function(data) {
        $("#extracted-entites .modal-body").html("")
        data.entities.forEach(function(entity) {
          var html = "<h3>"+entity.text+" <small>"+entity.type+", mentioned "+numberOfTimes(entity.frequency)+"</small></h3>"
          $("#extracted-entites .modal-body").append(html)
        })
        $("#extracted-entites").modal("show")
      }
    })
    return false
  })
})