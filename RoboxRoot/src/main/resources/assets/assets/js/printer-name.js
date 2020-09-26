function savePrinterName()
{
    var newName = $('#pname-input').val();
    promisePostCommandToRoot(localStorage.getItem(selectedPrinterVar) + '/remoteControl/renamePrinter', newName)
        .then(goToPage(identityMenu))
        .catch(function(error) { handleException(error, 'printer-name-save-error', false); });
}

function updatePrinterName(nameData)
{
    $('#pname-input').val(nameData.printerName);
    $('#right-button').removeClass('disabled')
}

function printerNameInit()
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        setMachineLogo();
        $('#right-button').on('click', savePrinterName);
        promiseGetCommandToRoot(selectedPrinter + '/remoteControl/nameStatus', null)
            .then(updatePrinterName)
            .catch(function(error) { handleException(error, 'printer-name-init-error', true); });
    }
	else
		goToHomeOrPrinterSelectPage();
}
