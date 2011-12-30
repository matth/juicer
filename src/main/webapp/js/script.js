$(function() {
  $("#entity_example").submit(function() {
    var text = $("#textarea").val()
    $.ajax({
      type: 'POST',
      url: "/api/entities",
      data: { text : text},
      success: function(data) {
        $("#extracted-entites .modal-body").html("")
        data.entities.forEach(function(entity) {
          var html = "<h3>"+entity.text+" <small>"+entity.type+", mentioned "+entity.frequency+" times</small></h3>"
          $("#extracted-entites .modal-body").append(html)
        })
        $("#extracted-entites").modal("show")
      }
    })
    return false
  })
})