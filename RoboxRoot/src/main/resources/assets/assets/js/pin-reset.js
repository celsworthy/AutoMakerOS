var serialFocus = '#printer-serial';

function resetPIN()
{
    var printerURL = "/api/admin/resetPIN/";

    $.ajax({
        url: printerURL,
        cache: false,
        processData: false,
        contentType: "application/json", // send as JSON
        type: 'POST',
        data: $("#printer-serial").val(),
        success: function (data, textStatus, jqXHR) {
            alert(i18next.t('reset-pin-ok'));
            logout();
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert(i18next.t('reset-pin-failed'));
        }
    });
}

function serialKeyClick()
{
    var key_char = $(this).attr('char');
    var value = $(serialFocus).val();
    if(value.length < 6)
    {
        $(serialFocus).val(value + key_char);
    }
}

function serialKeyBackspace()
{
    var value = $(serialFocus).val();
    $(serialFocus).val(value.substr(0, value.length - 1));
}

function pinResetInit()
{
    $("#middle-button").on('click', resetPIN);
    $('.serial-key').on('click', serialKeyClick);
    $('#backspace').on('click', serialKeyBackspace);
}