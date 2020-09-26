function consoleKey()
{
    var key_char = $(this).attr('char');
    var value = $('#gcode-input').val();
    $('#gcode-input').val(value + key_char);
}

function consoleBackspace()
{
    var value = $('#gcode-input').val();
    $('#gcode-input').val(value.substr(0, value.length - 1));
}

function consoleSpace()
{
    var value = $('#gcode-input').val();
    $('#gcode-input').val(value + ' ');
}

function consoleClear()
{
    $('#gcode-input').val('');
}

function processGCodeResponse(responseData)
{
    if (responseData.forEach !== undefined)
        responseData.forEach(
            function(l)
            {
                $('#gcode-output').val($('#gcode-output').val() + l + '\n');
            });
}

function sendGCode(gcodeToSend)
{
    promisePostCommandToRoot(localStorage.getItem(selectedPrinterVar) + "/remoteControl/executeGCode", gcodeToSend)
        .then(processGCodeResponse)
        .catch(function(error) { handleException(error, 'send-gcode-error', false); });
}

function sendGCodeFromDialog()
{
    var gcodeToSend = $('#gcode-input').val().toUpperCase();
    $('#gcode-output').val($('#gcode-output').val() + gcodeToSend + '\n');
    sendGCode(gcodeToSend);
    consoleClear();
}

function gCodeFilter(e)
{
    var chrTyped = null;
    var chrCode = 0;
    var evt = e ? e : event;
    if (evt.charCode != null)
        chrCode = evt.charCode;
    else if (evt.which != null)
        chrCode = evt.which;
    else if (evt.keyCode != null)
        chrCode = evt.keyCode;

    if (chrCode == 0)
        chrTyped = 'SPECIAL KEY';
    else
        chrTyped = String.fromCharCode(chrCode);

    // Only allow characters valid in GCode.
    if (chrTyped.match(/\d|[XxYyZzEeDdBbGgMmFfSsTt\.-]|[\b]|SPECIAL/))
        return true;
    if (evt.altKey || evt.ctrlKey || chrCode < 28)
        return true;

    // Any other input? Prevent the default response:
    if (evt.preventDefault)
        evt.preventDefault();
    evt.returnValue = false;
    return false;
}

function consoleInit()
{
    //setMachineLogo();
    $('#send-gcode-button').on('click', function() { sendGCodeFromDialog(); });
    $('.console-key').on('click', consoleKey);
    $('.space').on('click', consoleSpace);
    $('.bspace').on('click', consoleBackspace);
    $('.clear').on('click', consoleClear);
    $("#gcode-input").on('keypress', gCodeFilter)
                     .on('keyup',
                         function (e)
                         {
                            if (e.keyCode === 13)
                                sendGCodeFromDialog();
                         })
    // Set back button to return to the correct page.
    var from =  getUrlParameter('from');
    if (from != null && from == 'main')
        $('#left-button').attr('href', mainMenu)
    else        
        $('#left-button').attr('href', controlPage)
    startActiveErrorHandling();
}