// Note "&nbsp;" (non-breaking space) is used to stop
// empty lines from collapsing to zero height.
var nbsp = '&nbsp;';

var reelIconMap = 
{
    'CUSTOM': 'Icon-Home-CustomReel.svg',
    'NONE': 'Icon-Home-NoReel.svg',
	'SMART': 'Icon-Home-SmartReel.svg',
    'UNKNOWN': 'Icon-Home-UnknownReel.svg'
};

var homeStatusText = "";
var homePrinterName = "";
var homeWebColourString = "";
var homeStatusEnumValue = "";
var homeTotalDurationSeconds = 0;
var homeEtcSeconds = 0;
var homeTimeElapsed = 0;

// Debounce flag to prevent buttons from being clicked multiple times.
// Flag is set when a button is pressed and cleared when the button status is refreshed.
var homeDebounceFlag = true;

function updateNameStatus(nameData)
{
    var machineDetails = getMachineDetails();
    
    $('#'+ machineDetails['icon-class']).removeClass('hidden');
    $('#idle-image').attr('src', imageRoot + machineDetails['icon-background-light']);
    $('#machine-model').html(i18next.t(machineDetails['model']));
    $('#machine-name').html(nameData.printerName);

    // Set the colour of the machine box to be the LED colours.
    // Set the colour of the machine icon to a complimentary colour
    // so that it is visible.
    homeWebColourString = nameData.printerWebColourString;
    $('#machine-icon').css('background-color', nameData.printerWebColourString);
    var backgroundCol = $('#machine-icon').css('background-color');
    var compCol = getComplimentaryOption(backgroundCol, "rgba(0,0,0,0.7)", "rgba(255,255,255,0.7)");
    $('.robox-icon').css({'color' : compCol, 'fill' : compCol});
}

function updateFilamentStatus(materialData, filamentIndex)
{
    var typeValue = null;
    var descriptionValue = null;
    var reelClass = "reel-unknown";
    var showLoaded = false;
    var reelIcon = reelIconMap['NONE'];
    var remaining = -1.0;
    
    if (materialData.attachedFilaments != null &&
        materialData.attachedFilaments.length > filamentIndex)
    {
        filament = materialData.attachedFilaments[filamentIndex];
        showLoaded = filament.materialLoaded;
        if (filament.filamentName !== null && filament.filamentName.length > 0)
        {
            typeValue = filament.materialName;
            descriptionValue = filament.filamentName;
            if (filament.customFlag)
                reelIcon = reelIconMap['CUSTOM'];
            else
                reelIcon = reelIconMap['SMART'];
            remaining = filament.remainingFilament;
            reelClass = 'reel-known';
        }
        else
        {
            if (showLoaded)
            {
                descriptionValue = i18next.t("unknown-filament");
                reelIcon = reelIconMap['UNKNOWN'];
            }
            else
            {
                descriptionValue = i18next.t("no-filament");
                reelIcon = reelIconMap['NONE'];
            }
        }
    }
    
    var filamentName = 'filament-' + (filamentIndex + 1);
    var typeField = '#' + filamentName + '-type';
    var remainingField = '#' + filamentName + '-remaining';
    var descriptionField = '#' + filamentName + '-description';
    var colourField = '#' + filamentName + '-colour';
    var reelImage = '#' + filamentName + '-icon';
    var ejectButton = '#' + filamentName + '-eject';

    if (typeValue !== null)
    {
        $(typeField).html(typeValue)
        if (descriptionValue !== null)
        {
            $(descriptionField).html(descriptionValue);
            $(colourField).html(nbsp);
        }
        else
        {
            $(descriptionField).html(nbsp);
            $(colourField).html(nbsp);
        }
        if (remaining > -1.0)
            $(remainingField).html(remaining.toFixed(0) + 'm');
        else
            $(remainingField).html(nbsp);
    }
    else
    {
        $(typeField).html(nbsp);
        $(descriptionField).html(nbsp);
        $(remainingField).html(nbsp);
        $(colourField).html(nbsp);
    }
    $(descriptionField).closest('.rbx-home-filament-name')
                       .attr('class', 'rbx-home-filament-name ' + reelClass);

    $(reelImage).attr('src', imageRoot + reelIcon)
               
    if (showLoaded)
        $(ejectButton).show();
    else
        $(ejectButton).hide();
}

function eject(materialNumber)
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
    promisePostCommandToRoot(selectedPrinter + '/remoteControl/ejectFilament', materialNumber)
        .then(function() 
              {
	               getStatusData(selectedPrinter, '/printJobStatus', updatePrintJobStatus);
	               getStatusData(selectedPrinter, '/materialStatus', updateMaterialStatus);
              });
}

function updateFilamentEjectStatus(materialData)
{
    var enableEject1Button = false;
    var enableEject2Button = false;

    if (materialData.attachedFilaments!== null)
    {
        enableEject1Button = (materialData.attachedFilaments.length > 0 && materialData.attachedFilaments[0].canEject);
        enableEject2Button = (materialData.attachedFilaments.length > 1 && materialData.attachedFilaments[1].canEject);
    }
    
    if (enableEject1Button)
    {
        $('#filament-1-eject').removeClass('disabled');
    } else
    {
        $('#filament-1-eject').addClass('disabled');
    }

    if (enableEject2Button)
    {
        $('#filament-2-eject').removeClass('disabled');
    } else
    {
        $('#filament-2-eject').addClass('disabled');
    }
    $('.eject-button.disabled').css('background-color', 'rgba(0,0,0,0)')
}

function updateMaterialStatus(materialData)
{
    updateFilamentStatus(materialData, 0);
    updateFilamentStatus(materialData, 1);
    updateFilamentEjectStatus(materialData);
}

function updateHeadStatus(headData)
{
    $('#bed-temp').html(headData.bedTemperature + '\xB0' + 'C');
    $('#ambient-temp').html(headData.ambientTemperature + '\xB0' + 'C');
    var leftNozzleTemperature = nbsp;
    var rightNozzleTemperature = nbsp;
    var numberOfNozzleHeaters = 0;
    if (headData.nozzleTemperature !== null)
        numberOfNozzleHeaters = headData.nozzleTemperature.length;
    switch (numberOfNozzleHeaters)
    {
        case 0:
            $('#left-nozzle-title').parent().addClass('rbx-hidden');
            $('#right-nozzle-title').parent().addClass('rbx-hidden');
            $('.temp-col').removeClass('temp-col-third');
            $('.temp-col').removeClass('temp-col-qtr');
            $('.temp-col').addClass('temp-col-half');
            $('#left-nozzle-title').html(nbsp);
			$('#right-nozzle-title').html(nbsp);
            break;
        case 1:
            $('#left-nozzle-title').parent().addClass('rbx-hidden');
            $('#right-nozzle-title').parent().removeClass('rbx-hidden');
            $('.temp-col').removeClass('temp-col-qtr');
            $('.temp-col').removeClass('temp-col-half');
            $('.temp-col').addClass('temp-col-third');
            $('#left-nozzle-title').html(nbsp);
			if (headData.headTypeCode === 'RBX01-SM' || headData.headTypeCode === 'RBX01-S2')
				$('#right-nozzle-title').html(i18next.t('nozzles'));
			else
				$('#right-nozzle-title').html(i18next.t('nozzle'));
            if (headData.nozzleTemperature[0] !== null)
                rightNozzleTemperature = headData.nozzleTemperature[0] + '\xB0' + 'C';
            break;
        case 2:
            $('#left-nozzle-title').parent().removeClass('rbx-hidden');
            $('#right-nozzle-title').parent().removeClass('rbx-hidden');
            $('.temp-col').removeClass('temp-col-third');
            $('.temp-col').removeClass('temp-col-half');
            $('.temp-col').addClass('temp-col-qtr');
            $('#left-nozzle-title').html(i18next.t('left-nozzle'));
			$('#right-nozzle-title').html(i18next.t('right-nozzle'));
            if (headData.nozzleTemperature[0] !== null)
                leftNozzleTemperature = headData.nozzleTemperature[0] + '\xB0' + 'C';
            if (headData.nozzleTemperature[1] !== null)
                rightNozzleTemperature = headData.nozzleTemperature[1] + '\xB0' + 'C';
            break;
    }
    $('#left-nozzle-temp').html(leftNozzleTemperature);
    $('#right-nozzle-temp').html(rightNozzleTemperature);
}

function updatePrintJobStatus(printJobData)
{
    homeStatusText = printJobData.printerStatusString;
    homeStatusEnumValue = printJobData.printerStatusEnumValue;
    homeTotalDurationSeconds = printJobData.totalDurationSeconds;
    homeEtcSeconds = printJobData.etcSeconds;
    homeTimeElapsed = printJobData.totalDurationSeconds - printJobData.etcSeconds;

    if (!printJobData.printerStatusEnumValue.match("^IDLE"))
    {
        $('#idle-row').addClass('hidden');
        $('#progress-row').removeClass('hidden');
        var showJobRow = false;
        if (printJobData.printJobName == null || printJobData.printJobName.length == 0)
            $('#job-name').html(nbsp);
        else
        {
            $('#job-name').html(printJobData.printJobName);
            showJobRow = true;
        }
        $('#job-created').html(nbsp);
        if (printJobData.totalDurationSeconds == null || printJobData.totalDurationSeconds <= 0)
            $('#job-duration').html(nbsp);
        else
        {
            $('#job-duration').html(secondsToHM(printJobData.totalDurationSeconds));
            // Only show panel if there is a name.
            // showJobRow = true;
        }
        if (printJobData.printJobSettings == null || printJobData.printJobSettings.length == 0)
            $('#job-profile').html(nbsp);
        else
        {
            $('#job-profile').html(printJobData.printJobSettings);
            // Only show panel if there is a name.
            // showJobRow = true;
        }
        if (showJobRow)
            $('#job-row').removeClass('hidden');
        else    
            $('#job-row').addClass('hidden');
        updateJobStatusFields('#status-text', '#etc-text', '#progress-bar', printJobData)
    }
    else
    {
        $('#job-row').addClass('hidden');
        $('#progress-row').addClass('hidden');
        $('#idle-row').removeClass('hidden');
    }
}

function pauseResumePrint()
{
    if (homeDebounceFlag !== true)
    {
        var mode = $(this).attr('mode');
	    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
        switch (mode)
        {
            case 'p':
                promisePostCommandToRoot(selectedPrinter + "/remoteControl/pause", null);
                break;
            case 'r':
                promisePostCommandToRoot(selectedPrinter + "/remoteControl/resume", null);
                break;
            default:
                break;
        }
	    getStatusData(selectedPrinter, '/printJobStatus', updatePrintJobStatus);
	    getStatusData(selectedPrinter, '/controlStatus', updateControlStatus);
    }
}

function cancelPrint()
{
    if (homeDebounceFlag !== true &&
       !$('#cancel-button').hasClass('disabled'))
    {
        cancelAction();
        var selectedPrinter = localStorage.getItem(selectedPrinterVar);
        getStatusData(selectedPrinter, '/printJobStatus', updatePrintJobStatus);
        getStatusData(selectedPrinter, '/controlStatus', updateControlStatus);
        homeDebounceFlag = true;
    }
}

function updateControlStatus(controlData)
{
    if (controlData.canPause === true)
    {
        $('#pause-resume-button').removeClass('disabled resume')
                                 .addClass('pause')
                                 .attr('mode', 'p');
                                 
    } else if (controlData.canResume === true)
    {
        $('#pause-resume-button').removeClass('disabled pause')
                                 .attr('mode', 'r')
                                 .addClass('resume');
    }
    else
    {
        $('#pause-resume-button').addClass('disabled')
                                 .attr('mode', 'd');
    }
    
    if (controlData.printerStatusEnumValue.match("^HEATING") ||
        controlData.canCancel === true)
    {
        $('#cancel-button').removeClass('disabled');
    }
    else
    {
        $('#cancel-button').addClass('disabled');
    }
    
    if (controlData.printerStatusEnumValue.match("^PRINTING_PROJECT"))
    {
        $('#tweak-button').removeClass('disabled')
                          .removeClass('invisible');
    }
    else
    {
        $('#tweak-button').addClass('disabled')
                          .addClass('invisible');
    }

    if (controlData.canOpenDoor === true)
    {
        $('#right-button').removeClass('disabled');
    }
    else
    {
        $('#right-button').addClass('disabled');
    }
    
    homeDebounceFlag = false;
}

function updateHomeData(printerData)
{
    localStorage.setItem(printerTypeVar, printerData.printerTypeCode);
    updateNameStatus(printerData);
    updateFilamentStatus(printerData, 0);
    updateFilamentStatus(printerData, 1);
    updateFilamentEjectStatus(printerData);
    updateHeadStatus(printerData);
    updatePrintJobStatus(printerData);
    updateControlStatus(printerData);
    currentPrinterData = printerData;
}

function updateHomeServerStatus(data)
{
    $('#machine-ip').text(data.serverIP);
    //$('#software-version').text(data.serverVersion);
}

function clearHomeServerStatus(data)
{
    $('#machine-ip').text("---.---.---.---");
    //$('#software-version').text("*");
}

function getHomeData()
{
	var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        promiseGetCommandToRoot('discovery/whoareyou', null)
            .then(updateHomeServerStatus)
            .catch(clearHomeServerStatus);
		
        getPrinterStatus(selectedPrinter, updateHomeData);
	}
	else
		goToHomeOrPrinterSelectPage();
}

function startHomeUpdates()
{
	var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
		setInterval(function() { getPrinterStatus(null, updateHomeData); }, 2000);
    }
}

function prepareHomeLeftButton()
{
    promiseGetCommandToRoot('discovery/listPrinters', null)
        .then(function (data)
              {
                  if (data.printerIDs.length > 1)
                      setFooterButton({'left-button': {'icon': 'Icon_Menu_Back.svg',
									                   'href': printerSelectPage}},
                                       'left-button');
                  else
                      setFooterButton({}, 'left-button');   
              })
        .catch(function ()
               {
                   setFooterButton({}, 'left-button');
               });
}

function startHomeLeftButtonUpdates()
{
    setInterval(prepareHomeLeftButton, 2000);
}

function homeInit()
{
    localStorage.setItem(printerTypeVar, "RBX01");
    $('#filament-1-eject').on('click', function() { eject(1); });
    $('#filament-2-eject').on('click', function() { eject(2); });
    $('#pause-resume-button').on('click', pauseResumePrint);
    $('#cancel-button').on('click', cancelPrint);
    setFooterButton({'right-button': {'icon': 'Icon-Move-Unlock.svg',
									  'action': function() { sendGCode('G37 S'); }}},
                    'right-button');
    getHomeData();
    startHomeUpdates();
    prepareHomeLeftButton();
    startHomeLeftButtonUpdates();
    startActiveErrorHandling();
}
