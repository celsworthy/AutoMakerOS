function printUSBJob()
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        var usbPrintData = {};
        usbPrintData.printJobID = $(this).attr('job-id');
        usbPrintData.printJobPath = $(this).attr('job-path');
        promisePostCommandToRoot(selectedPrinter + "/remoteControl/printUSBJob",
                                 usbPrintData)
            .then(goToHomePage);
    }
}

function updateUSBPrintData(suitablePrintJobs)
{
    // From reprint.js
    updateSuitableJobData(suitablePrintJobs, "no-usb-job-", printUSBJob)
}

function usbPrintInit()
{
    getUSBData();
}

function getUSBData() 
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        setMachineLogo();
        promisePostCommandToRoot(selectedPrinter + '/remoteControl/listUSBPrintableJobs', null)
                    .then(updateUSBPrintData)
                    .catch(function(error) { handleException(error, 'usb-print-init-error', true); });
    }
	else
		goToHomeOrPrinterSelectPage();
}