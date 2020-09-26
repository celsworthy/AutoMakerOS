var intervalID = null;
var intervalDwell = 1000;
var errorsStorageKey = 'errors';
var activeErrorClass = 'active-error';

function clearActiveError()
{
    var pr = localStorage.getItem(selectedPrinterVar);
    var errorCode = parseInt($('#active-error-dialog').attr('data-error-code'));
    
    if (pr !== null && !isNaN(errorCode))
    {
        promisePostCommandToRoot(pr + '/remoteControl/clearError', errorCode)
                .then(function (data) 
                      {
                          $('#active-error-dialog').attr('data-error-code', '');
                          $('#active-error-dialog').modal('hide');
                          $('#active-error-dialog').removeClass(activeErrorClass);
                      })
                .catch(function(error) { handleException(error, 'active-error-clear-error', false); });
    }
}

function continueActiveError()
{
    var pr = localStorage.getItem(selectedPrinterVar);

    if (pr !== null)
    {
        resumeAction()
            .then(clearActiveError)
            .catch(function(error) { handleException(error, 'active-error-continue-error', false); });
    }
}

function abortActiveError()
{
    var pr = localStorage.getItem(selectedPrinterVar);

    if (pr !== null)
    {
        cancelAction()
            .then(clearActiveError)
            .catch(function(error) { handleException(error, 'active-error-abort-error', false); });
    }
}

function handleActiveErrors(activeErrorData)
{
    var errorsToStore = []
    
    if (activeErrorData.activeErrors !== null &&
	    activeErrorData.activeErrors.length > 0)
    {
        for (activeError of activeErrorData.activeErrors)
        {
            var errorCode = activeError.errorCode;
            if (errorCode === 25 || errorCode === 33 || errorCode === 41)
            {
                // Ignore these errors for now
            } else if (errorAlreadySeen(errorCode))
            {
                errorsToStore.push(errorCode);
            } else if (!$('#active-error-dialog').hasClass(activeErrorClass))
            {
                errorsToStore.push(errorCode);
                // Error needs to be raised for the user
                var errorMessage = activeError.errorMessage;
                if (errorMessage.length > 64)
                    errorMessage = errorMessage.substring(0, 60).concat(" ...");
                $('#active-error-dialog').attr('data-error-code', errorCode);
                $('#active-error-title').text(activeError.errorTitle);
                $('#active-error-summary').text(errorMessage);
                var options = activeError.options;
                // ABORT(1),
                // CLEAR_CONTINUE(2),
                // RETRY(4),
                // OK(8),
                // OK_ABORT(16),
                // OK_CONTINUE(32);
                if ((options & 17) !== 0) // ABORT or OK_ABORT
                    $('#active-error-abort').removeClass('hidden');
                else
                    $('#active-error-abort').addClass('hidden');

                if (options === 0 || // Nothing or
                    (options & 46) !== 0) // CLEAR_CONTINUE or RETRY or OK or OK_CONTINUE.
                    $('#active-error-continue').removeClass('hidden');
                else
                    $('#active-error-continue').addClass('hidden');
                
                
                if (errorCode === 28)
                {
                    // E_UNLOAD_ERROR
                    $('#active-error-custom').removeClass('hidden');
                    $('#active-error-custom').attr('onclick','ejectFilament1()');
                    $('#active-error-custom').html('Eject Filament');
                } else if (errorCode === 29)
                {
                    // D_UNLOAD_ERROR
                    $('#active-error-custom').removeClass('hidden');
                    $('#active-error-custom').attr('onclick','ejectFilament0()');
                    $('#active-error-custom').html('Eject Filament');
                } else
                {
                    $('#active-error-custom').addClass('hidden');
                }
                
                $('#active-error-dialog').modal('show');
                $('#active-error-dialog').addClass(activeErrorClass);
            }
        }
    }
    else
    {
        $('#active-error-dialog').attr('data-error-code', '');
        $('#active-error-dialog').modal('hide');
        $('#active-error-dialog').removeClass(activeErrorClass);
    }
    
    sessionStorage.setItem(errorsStorageKey, JSON.stringify(errorsToStore));
}

function errorAlreadySeen(errorCode)
{
    var storedErrorCodes = JSON.parse(sessionStorage.getItem(errorsStorageKey));
    if (storedErrorCodes !== null && storedErrorCodes.length > 0)
    {
        for (storedCode of storedErrorCodes)
        {
            if (storedCode === errorCode)
            {
                return true;
            }
        }
    }
    
    return false;
}

function ejectFilament0()
{
    ejectFilamentX(0);
}

function ejectFilament1()
{
    ejectFilamentX(1);
}

function ejectFilamentX(extruderNumber)
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
    promisePostCommandToRoot(selectedPrinter + '/remoteControl/ejectFilament', extruderNumber)
        .then(getStatusData(null, '/materialStatus', updateControlMaterialStatus))
        .then(clearActiveError)
        .catch(function(error) { handleException(error, 'active-error-custom-error', false); });
}

function checkForActiveErrors()
{
    var pr = localStorage.getItem(selectedPrinterVar);

    if (pr !== null)
    {
        promiseGetCommandToRoot(pr + '/remoteControl/activeErrorStatus', null)
            .then(handleActiveErrors)
            .catch(function(error)
                   {
                        // Active error request returned an error,
                        // so cancel repeat requests.
                        if (error.name !== 'InternalError' && intervalID !== null)
                        {
                            clearInterval(intervalID);
                            intervalDwell = 3000;
                            intervalID = setInterval(checkForActiveErrors, intervalDwell);
                        }
                        handleException(error, 'active-error-check-error', false);
                   });
    }
}

function startActiveErrorHandling()
{
    var pr = localStorage.getItem(selectedPrinterVar);

    if (pr !== null)
    {
         var errorDialogText =
			'<div id="active-error-dialog" class="modal rbx" role="dialog" tabindex="-1" data-backdrop="static" data-keyboard="false" data-error-code="">'
				+ '<div class="modal-dialog modal-lg" role="document">'
				+ '<div class="modal-content">'
				+ '<div class="modal-body rbx">'
				+ '<div class="row vertical-align">'
				+ '<div style="float: left; margin-right: 1.5vh;"><img id="active-error-icon" src="assets/img/Blank.svg" style="width: 10vh;"></div>'
				+ '<div style="float: left;">'
				+ '<div class="row">'
				+ '<div><span id="active-error-title" class="rbx-text-large" style="line-height: 5vh;">&nbsp;</span></div>'
				+ '</div>'
				+ '<div class="row">'
				+ '<div><span id="active-error-summary" class="rbx-text-large" style="font-weight: 400;  line-height: 5vh;">&nbsp;</span></div>'
				+ '</div>'
				+ '</div>'
				+ '</div>'
				+ '<div class="row">'
				+ '<div>'
				+ '<p id="active-error-details" class="rbx-text-bigbody" style="margin: 1.5vh 0vh">&nbsp;</p>'
				+ '</div>'
				+ '</div>'
				+ '</div>'
				+ '<div class="modal-footer rbx">'
				+ '<button id="active-error-continue" class="btn btn-default rbx-modal localised" type="button" data-dismiss="modal" data-i18n="clear-continue">Clear and Continue</button>'
				+ '<button id="active-error-abort" class="btn btn-default rbx-modal localised" type="button" data-i18n="abort">Abort</button>'
                + '<button id="active-error-custom" class="btn btn-default rbx-modal localised" type="button" data-i18n="custom">Custom</button></div>'
				+ '</div>'
				+ '</div>'
				+ '</div>';
        $('body').append(errorDialogText);
        $('#active-error-continue').on('click', continueActiveError);
        $('#active-error-abort').on('click', abortActiveError);

        // Set off the error notifier.
        intervalID = setInterval(checkForActiveErrors, intervalDwell);
    }
}