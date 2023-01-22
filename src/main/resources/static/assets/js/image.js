$('document').ready(function() {

    $('#editButton').on('click', function(event) {
        var href = $(this).attr('href');
        $.get(href, function(image, status) {
            $('#id-edit').val(image.id);
            $('#title-edit').val(image.title);
        });
        $('#editModal').modal();
    });

    $('#deleteButton').on('click', function(event) {
        var href=$(this).attr('href');
        $('#confirmDeleteButton').attr('href', href);
        $('#deleteModal').modal();
    });

    $('#cfButton').on('click', function(event) {
        var href=$(this).attr('href');

        var form = document.getElementById('cfForm') || null;
        if(form) {
           form.action = href;
        }
    });

});