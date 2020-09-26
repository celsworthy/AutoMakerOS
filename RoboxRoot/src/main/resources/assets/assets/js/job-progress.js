function jobCancelAction()
{
    if (!$(this).hasClass('disabled'))
        cancelAction();
}

var jobProgressDetailsMap = 
{
	'clean-nozzle-right': {'title-text': 'clean-nozzle-title-right',
                           'description-text': 'clean-nozzle-description',
                           'image': 'Icon-Menu-Clean-White.svg',
		                   'right-button': {'icon':'Icon-Cancel.svg',
	                                        'action':jobCancelAction,
                                            'add-class':'cancel-action'}},
	'clean-nozzle-left': {'title-text': 'clean-nozzle-title-left',
                          'description-text': 'clean-nozzle-description',
                          'image': 'Icon-Menu-Clean-White.svg',
			              'right-button': {'icon':'Button-Cancel-White.svg',
					                       'action':jobCancelAction,
                                           'add-class':'cancel-action'}},
	'eject-stuck-1': {'title-text': 'eject-stuck-title-1',
                      'description-text': 'eject-stuck-description',
                      'image': 'Icon-Menu-Eject-White.svg',
				      'right-button': {'icon':'Button-Cancel-White.svg',
					                   'action':jobCancelAction,
                                       'extra-classes':'cancel-action'}},
	'eject-stuck-2': {'title-text': 'eject-stuck-title-2',
                      'description-text': 'eject-stuck-description',
                      'image': 'Icon-Menu-Eject-White.svg',
				      'right-button': {'icon':'Button-Cancel-White.svg',
					                   'action':jobCancelAction,
                                       'extra-classes':'cancel-action'}},
	'remove-head': {'title-text': 'remove-head-title',
                    'description-text': 'remove-head-description',
                    'image': 'Icon-Menu-Remove-White.svg',
		            'right-button': {'icon':'Button-Cancel-White.svg',
					                 'action':jobCancelAction,
                                     'extra-classes':'cancel-action'}},
	'purge': {'title-text': 'purge-title',
              'description-text': 'purge-description',
              'image': 'Icon-Menu-Purge-White.svg',
		      'right-button': {'icon':'Button-Cancel-White.svg',
			                   'action':jobCancelAction,
                               'extra-classes':'cancel-action'}},
	'test': {'title-text': 'test-title',
             'description-text': 'test-description',
             'image': 'Icon-Menu-Test-White.svg',
			 'right-button': {'icon':'Button-Cancel-White.svg',
	                          'action':jobCancelAction,
                              'extra-classes':'cancel-action'}},
	'level-gantry': {'title-text': 'level-gantry-title',
                     'description-text': 'level-gantry-description',
                     'image': 'Icon-Menu-Level-White.svg',
		             'right-button': {'icon':'Button-Cancel-White.svg',
	                                  'action':jobCancelAction,
                                      'extra-classes':'cancel-action'}}
};

// The printer remains in idle state for a while before switching to
// the Running Macro state. So we allow up to maxIdleCount occurances
// of the idle state before it returns to the menu page.
var idleCount = 0;
var maxIdleCount = 5;

function jobProgressInit()
{
    var jpDetails = null;
    var jpId = getUrlParameter('id');
    if (jpId != null)
        jpDetails = jobProgressDetailsMap[jpId];
    if (jpDetails != null)
    {
        setMachineLogo();
        setTextFromField(jpDetails, 'title-text');
        setTextFromField(jpDetails, 'description-text');
        setImageFromField(jpDetails, 'image');
        setFooterButton(jpDetails, 'left-button')
        setFooterButton(jpDetails, 'right-button')
        startJobStatusUpdates();
        startActiveErrorHandling();
    }
    else
        goToHomeOrPrinterSelectPage();
}

function updateJobStatusFields(statusField, etcField, progressBar, printJobData)
{
    var statusText = "";
    switch(printJobData.printerStatusEnumValue)
    {
        case "PRINTING_PROJECT":
        case "RESUME_PENDING":
        case "RUNNING_MACRO_FILE":
            statusText='<img src="' + imageRoot + 'Icon-Play.svg" class="print-status-icon">';
            break;
        case "PAUSED":
        case "SELFIE_PAUSE":
        case "PAUSE_PENDING":
            statusText='<img src="' + imageRoot + 'Icon-Pause.svg" class="print-status-icon">';
            break;
        case "IDLE":
            statusText='<img src="' + imageRoot + 'Icon-Ready.svg" class="print-status-icon">';
            break;

        // These are all the states of which I am aware.
        case "LOADING_FILAMENT_D":
        case "LOADING_FILAMENT_E":
        case "UNLOADING_FILAMENT_D":
        case "UNLOADING_FILAMENT_E":
        case "CALIBRATING_NOZZLE_ALIGNMENT":
        case "CALIBRATING_NOZZLE_HEIGHT":
        case "CALIBRATING_NOZZLE_OPENING":
        case "OPENING_DOOR":
        case "PURGING_HEAD":
        case "REMOVING_HEAD":
        case "HEATING":
        default:
            break;
    }
    statusText = statusText + i18next.t(printJobData.printerStatusString);
    $(statusField).html(statusText);    
    
    if ((printJobData.printerStatusEnumValue.match("^PRINTING_PROJECT")
            || printJobData.printerStatusEnumValue.match("^RUNNING_MACRO")
            || printJobData.printerStatusEnumValue.match("^PAUSED")
            || printJobData.printerStatusEnumValue.match("^PAUSE_PENDING")
            || printJobData.printerStatusEnumValue.match("^RESUME_PENDING"))
            && printJobData.totalDurationSeconds >= 0
            && printJobData.etcSeconds > 0)
    {
        $(etcField).html(secondsToHM(printJobData.etcSeconds))
                   .closest('div')
                   .removeClass('invisible')  // Show etc text.
                   .next()
                   .removeClass('invisible'); // Show etc icon.
        var timeElapsed = printJobData.totalDurationSeconds - printJobData.etcSeconds;
        if (timeElapsed < 0)
        {
            timeElapsed = 0;
        }
        if (timeElapsed <= 0 || printJobData.totalDurationSeconds <= 0)
        {
            $(progressBar + " .progress-bar").width("0%").html("");
        }
        else
        {
            var progressPercent = Math.round((100 * timeElapsed / printJobData.totalDurationSeconds)) + "%";
            $(progressBar + " .progress-bar").width(progressPercent).html("");
        }
        $(progressBar).closest('.row')
                      .removeClass('invisible');
    }
    else
    {
        $(etcField).html("&nbsp;")
                   .closest('div')
                   .addClass('invisible') // Hide etc text.
                   .next()                   
                   .addClass('invisible'); // Hide etc icon.
        if (printJobData.printerStatusEnumValue.match("^HEATING") &&
           printJobData.heatingProgress >= 0 &&
           printJobData.heatingProgress <= 100)
        {
            $(progressBar + " .progress-bar").width(printJobData.heatingProgress + "%")
                 .html("")
                 .closest('.row')
                 .removeClass('invisible');
        }
        else
        {
            $(progressBar + " .progress-bar").width("0%")
                                             .html("")
                                             .closest('.row')
                                             .addClass('invisible');
        }
    }
}

function updateJobStatus(printJobData)
{
    console.log('updateJobStatus - printerStatus = ' + printJobData.printerStatusEnumValue);
    if (!printJobData.printerStatusEnumValue.match("^IDLE"))
    {
        idleCount = maxIdleCount;
        if (printJobData.printerStatusEnumValue.match("^HEATING") ||
            printJobData.canCancel === true)
        {
            $('.cancel-action').removeClass('disabled');
        }
        else
        {
            $('.cancel-action').addClass('disabled');
        }
    
        updateJobStatusFields('#status-text', '#etc-text', '#progress-bar', printJobData)
    }
    else
    {
        if (++idleCount > maxIdleCount)
            goToHomePage();
    }
}

function getJobStatus()
{
    getStatusData(null, '/printJobStatus', updateJobStatus)
}
    
function startJobStatusUpdates()
{
	var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
		setInterval(getJobStatus, 500);
    }
}
