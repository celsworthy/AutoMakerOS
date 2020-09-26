function attemptLogin()
{
    var enteredPIN = $("#pin-value").val();
    if (enteredPIN !== "")
    {
        localStorage.setItem(applicationPINVar, enteredPIN);
        console.log("Hello " + localStorage.getItem(applicationPINVar));
        goToHomeOrPrinterSelectPage();
    }
}

function goToPINReset()
{
    location.href = '/pin-reset.html';
}

function login_key()
{
    var pv = $('#pin-value');
    var value = pv.val();
    if (value.length < 4)
    {
        var key_char = $(this).attr('char');
        pv.val(value + key_char);
    }
}

function login_backspace()
{
    var pv = $('#pin-value');
    var value = pv.val();
    if (value.length > 0)
        pv.val(value.substr(0, value.length - 1));
}

function loginInit()
{
    var enteredPIN = localStorage.getItem(applicationPINVar);
    $("#pin-value").val(enteredPIN);
    if (enteredPIN !== null
        && enteredPIN !== "")
    {
        attemptLogin();
    }

    $("#pin-value").on('keyup', function (e) {
        if (e.keyCode === 13) {
            attemptLogin();
        }
    });
    $('.login-key').on('click', login_key);
    $('.bspace').on('click', login_backspace);
    $("#middle-button").on('click', attemptLogin)
}

function indexInit()
{
    titlei18n = "indexPage";
    checkForMobileBrowser();

    var enteredPIN = localStorage.getItem(applicationPINVar);
    if (enteredPIN !== null && enteredPIN !== "")
    {
        var base64EncodedCredentials = $.base64.encode(defaultUser + ":" + enteredPIN);
        $.ajax({
            url: clientURL + printerSelectPage,
            dataType: 'html',
            cache: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Basic " + base64EncodedCredentials);
            },
            type: 'GET',
            success: function (data, textStatus, jqXHR) {
                goToHomeOrPrinterSelectPage();
            },
            error: function (data, textStatus, jqXHR) {
                logout();
            }
        });
    } else
    {
        logout();
    }
}
