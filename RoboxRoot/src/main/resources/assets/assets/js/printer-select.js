var connectedPrinterIDs = new Array();

function selectPrinter(printerID)
{
    selectedPrinterID = printerID;
    localStorage.setItem(selectedPrinterVar, printerID);
    window.location.href = homePage;
}

function promisePrinterStatus(printerID)
{
    return promiseGetCommandToRoot(printerID + '/remoteControl', null);
}

function updatePrinterStatus(printerData)
{
    var machineDetails = getMachineDetails(printerData.printerTypeCode)
    var psel = $('.printer-selector[printer-id="' + printerData.printerID + '"]');
    psel.css('background-color', printerData.printerWebColourString)
        // Set the class attribute instead of using add-class as we don't know which classes icon classes need to be removed.
        .attr('class', 'row printer-select-button printer-selector ' + machineDetails['image-class']);
    psel.find('.printer-select-name')
        .html(printerData.printerName);
    var useDark = getComplimentaryOption(psel.css('background-color'), true, false);
    var colourClass = null;
    var colourClassToRemove = null;
    if (useDark)
    {
        colourClass = 'dark';
        colourClassToRemove = 'light';
    }
    else
    {
        colourClass = 'light';
        colourClassToRemove = 'dark';
    }

    var statusClass = null;
    if (printerData.printerStatusEnumValue.match("^PRINTING_PROJECT"))
    {
        statusClass = 'printing';
    }
    else if (printerData.printerStatusEnumValue.match("^PAUSED") ||
             printerData.printerStatusEnumValue.match("^SELFIE_PAUSE") ||
             printerData.printerStatusEnumValue.match("^PAUSE_PENDING"))
    {
        statusClass = 'paused';
    }
    else if (printerData.printerStatusEnumValue.match("^RESUME")
            || printerData.printerStatusEnumValue.match("^RESUME_PENDING"))
    {
        statusClass = 'printing';
    }
    else
    {
        statusClass = 'ready';
    }

    psel.addClass(colourClass)
        .find('.icon-status')
        .removeClass(colourClassToRemove)
        .addClass(colourClass + ' ' + statusClass);
    
    if (!printerData.printerStatusEnumValue.match("^IDLE")
        && printerData.totalDurationSeconds > 0)
    {
        var timeElapsed = printerData.totalDurationSeconds - printerData.etcSeconds;
        if (timeElapsed < 0)
        {
            timeElapsed = 0;
        }
        var progressPercent = (timeElapsed * 1.0 / printerData.totalDurationSeconds) * 100;
        psel.find('.rbx-progress')
            .removeClass('rbx-invisible')
            .find('.progress-bar')
            .css('width', progressPercent + '%');
    } 
    else
    {
        psel.find('.rbx-progress')
            .addClass('rbx-invisible')
            .find('.progress-bar')
            .css('width', '0%');
    }
}

function addPrinter(printerID)
{
    connectedPrinterIDs.push(printerID);
    
    // add to the first free printer-selector.
    $('.printer-selector[printer-id=""]')
        .first()
        .removeClass('rbx-invisible')
        .attr('printer-id', printerID);
}

function deletePrinter(printerID)
{
    var indexToDelete = connectedPrinterIDs.indexOf(printerID);
    connectedPrinterIDs.splice(indexToDelete, 1);
    
    // Hide the printer selector for this printer.
    $('.printer-selector[printer-id="' + printerID + '"]')
        .addClass('rbx-invisible')
        .attr('printer-id', '');
}

function processAddedAndRemovedPrinters(printerIDs)
{
    var printersToDelete = new Array();
    var printersToAdd = new Array();
    for (printerIDToConsider of connectedPrinterIDs)
    {
        if (printerIDs.indexOf(printerIDToConsider) < 0)
        {
            //Not in the list of discovered printers - we need to delete it
            printersToDelete.push(printerIDToConsider);
        }
    }

    for (printerIDToConsider of printerIDs)
    {
        if (connectedPrinterIDs.indexOf(printerIDToConsider) < 0)
        {
            //Not in the list - we need to add it
            printersToAdd.push(printerIDToConsider);
        }
    }

    printersToDelete.forEach(deletePrinter);
    printersToAdd.forEach(addPrinter);
    
	if (connectedPrinterIDs.length > 0)
	{
		$('.ps_row').removeClass('rbx-hidden');
		$('.ps_none').addClass('rbx-hidden');
	}
	else
	{
		$('.ps_row').addClass('rbx-hidden');
		$('.ps_none').removeClass('rbx-hidden');
	}
    return (printersToDelete.length > 0
            || printersToAdd.length > 0);
}

function updatePrinterStatuses()
{
    if (connectedPrinterIDs.length === 0)
    {
        $('.printer-selector').addClass('rbx-invisible');
    }
    else if (connectedPrinterIDs.length == 1)
	{
		selectPrinter(connectedPrinterIDs[0]);
	}
    else
    {
		for (printerID of connectedPrinterIDs)
		{
			promisePrinterStatus(printerID)
                .then(updatePrinterStatus)
                .catch()
        }
    }
}

function getPrinterList()
{
    promiseGetCommandToRoot('discovery/listPrinters', null)
        .then(function (data) {
                processAddedAndRemovedPrinters(data.printerIDs);
                updatePrinterStatuses();
                connectedToServer = true;
            })
        .catch(function () {
                $('.printer-selector').addClass('rbx-invisible');
                connectedToServer = false;
                logout();
            })
}

function getServerStatus()
{
    promiseGetCommandToRoot('discovery/whoareyou', null)
    .then(function (data) {
                $('#serverOnline').text(i18next.t('online'));
                updateServerStatus(data);
                connectedToServer = true;
                if (typeof serverOnline === "function")
                {
                   serverOnline(); 
                }
            })
        .catch(function (data) {
                connectedToServer = false;
                $('#serverOnline').text(i18next.t('offline'));
                updateServerStatus(null);
                if (typeof serverOffline === "function")
                {
                   serverOffline(); 
                }
            });
}

function updateServerStatus(serverData)
{
    if (serverData === null)
    {
        //$('#serverVersion').text("");
        //$(".serverStatusLine").text("");
        //$(".server-name-title").text("");
        $('.server-name').html(nbsp);
        $('.server-ip-address').html(nbsp);
    } else
    {
        if (lastServerData == null
            || serverData.name !== lastServerData.name
            || serverData.serverIP !== lastServerData.serverIP
            || serverData.serverVersion !== lastServerData.serverVersion)
        {
            //$('#serverVersion').text(serverData.serverVersion);
            //$(".serverStatusLine").text(serverData.name);
            //$(".server-name-title").text(serverData.name);
            $('.server-name').html(serverData.name);
            $('.server-ip-address').html(serverData.serverIP);
            //$(".serverIP").text(serverData.serverIP);
            lastServerData = serverData;
        }
    }
}

function getStatus()
{
    getServerStatus();
    getPrinterList();
}

function printerSelectInit()
{
    $('.printer-selector')
        .attr('printer-id', '')
        .addClass('rbx-invisible')
        .on('click', function () {
            var printerSelected = $(this).attr('printer-id');
            if (printerSelected != null)
            {
                selectPrinter(printerSelected);
            }
        });
    
    localStorage.removeItem(selectedPrinterVar);
    getServerStatus();
    getPrinterList();
    setInterval(getStatus, 2000);
}
