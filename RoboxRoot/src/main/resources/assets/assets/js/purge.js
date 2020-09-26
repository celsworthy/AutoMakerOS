function sToN(s)
{
    var n = parseInt(s);
    if (isNaN(n))
        n = -1;
    return n;
}

function initiatePurge()
{
    var targetData = {'targetTemperature':[-1, -1],
                      'lastTemperature':[-1, -1],
                      'newTemperature':[-1, -1],
                      'safetyOn':safetiesOn()};
    if ($('#left-nozzle .rbx-checkbox:visible').is(':checked'))
    {
        targetData['lastTemperature'][0] = sToN($('#left-nozzle .last-temp').text());
        targetData['newTemperature'][0] = sToN($('#left-nozzle .new-temp').text());
        targetData['targetTemperature'][0] = sToN($('#left-nozzle .purge-temp').val());
    }
    if ($('#right-nozzle .rbx-checkbox:visible').is(':checked'))
    {
        var rIndex = $('#right-nozzle').attr('nozzle-index');
        targetData['lastTemperature'][rIndex] = sToN($('#right-nozzle .last-temp').text());
        targetData['newTemperature'][rIndex] = sToN($('#right-nozzle .new-temp').text());
        targetData['targetTemperature'][rIndex] = sToN($('#right-nozzle .purge-temp').val());
    }

    promisePostCommandToRoot(localStorage.getItem(selectedPrinterVar) + "/remoteControl/purgeToTarget", targetData)
            .then(function() { console.log("Purging to target"); })
            .catch(function()
                   {
                        console.log("Failed to send purge command");
                   });
    goToPage(purgeStatus);
}

function setPurgeButtonState()
{
    var enabled = false;
    $('.rbx-checkbox:visible').each(function() { if ($(this).is(':checked')) enabled = true; });
    if (enabled)
        $('#right-button').removeClass('disabled'); 
    else
        $('#right-button').addClass('disabled');
}

function switchPanelState()
{
    var checkbox = $(this);
    var state = checkbox.is(':checked');
    var panel = checkbox.closest('.panel');
    panelId = panel.attr('id');
    setPanelState(panelId, state);
    setPurgeButtonState();
}

function setPanelState(panelId, state)
{
    var materialColor = null;
    var textColor = null;
    var inputColor = null;
    if (state)
    {
        textColor = 'white';
        inputColor = 'black';
        if (panelId == 'right-nozzle')
            materialColor = materialColor1;
        else // left-nozzle
            materialColor = materialColor2;
        $('#' + panelId + ' .rbx-spinner').removeClass('disabled');
    }
    else
    {
        textColor = 'grey';
        inputColor = 'grey';
        materialColor = 'grey';
        $('#' + panelId + ' .rbx-spinner').addClass('disabled');
    }
    
    $('#' + panelId + ' .rbx-text').css('color', textColor)
    $('#' + panelId + ' .rbx-numeric-input').css('color', inputColor)
    $('#' + panelId + ' .rbx-colour-material').css('color', materialColor)
}

function updatePanelTemp(panelId, field, value)
{
    if (value == null || value < 0)
        $('#' + panelId + ' .' + field).html('-');
    else
        $('#' + panelId + ' .' + field).html(value.toFixed(0));
 }

function updatePanelDescription(panelId, field, value)
{
    if (value == null || value.length == 0)
        $('#' + panelId + ' .' + field).html(nbsp);
    else
        $('#' + panelId + ' .' + field).html(value);
 }

function updatePanelHeadData(panelId, state, lastTemp)
{
    setPanelState(panelId, state);
    updatePanelTemp(panelId, 'last-temp', lastTemp);
 }

function updatePanelMaterialData(panelId, newTemp, description)
{
    updatePanelTemp(panelId, 'new-temp', newTemp);
    updatePanelDescription(panelId, 'material-description', description);
} 

function completePanelUpdate(panelId, showPanel, lastTemp, newTemp)
{
    var panel = '#' + panelId;
    if (showPanel)
    {
        var t = newTemp;
        if (lastTemp > 0)
            t = 0.5 * (lastTemp + newTemp);
        $(panel).removeClass('hidden');        
        $('#' + panelId + ' .purge-temp').val(t.toFixed(0));
    }
    else
        $(panel).addClass('hidden');
} 

function updatePurgeHeadData(headData)
{
    // For a single nozzle head, the right nozzle is nozzle 0 in the array.
    // For a dual nozzle head, the left nozzle is nozzle 0, the right nozzle is nozzle 1.
    $('#right-nozzle').attr('nozzle-index', headData.nozzleCount - 1);
    $('#right-nozzle-check').prop('checked', true);
    updatePanelHeadData('right-nozzle', true, headData.rightNozzleLastFTemp);
    if (headData.dualMaterialHead && headData.nozzleCount > 1)
    {
        $('#left-nozzle-check').prop('checked', true);
        updatePanelHeadData('left-nozzle', true, headData.leftNozzleLastFTemp);
    }
    else
    {
        updatePanelHeadData('left-nozzle', false, 0);
    }
    return headData;
}

function updatePurgeMaterialData(materialData)
{
    if (materialData.attachedFilaments.length > 0)
        updatePanelMaterialData('right-nozzle', materialData.attachedFilaments[0].filamentTemperature, materialData.attachedFilaments[0].filamentName);
    else
        updatePanelMaterialData('right-nozzle', null, null);
    if (materialData.attachedFilaments.length > 1)
        updatePanelMaterialData('left-nozzle', materialData.attachedFilaments[1].filamentTemperature, materialData.attachedFilaments[1].filamentName);
    else
        updatePanelMaterialData('left-nozzle', null, null);
    return materialData;
}

function completePurgeUpdate(purgeData)
{
    // purgeData[0] is headData, purgeData[1] is materialData.
    
     var showPanel1 = (purgeData[1].attachedFilaments.length > 0 &&
        purgeData[1].attachedFilaments[0].materialLoaded &&
        purgeData[1].attachedFilaments[0].canEject);
        
    var showPanel2 = (purgeData[0].dualMaterialHead &&
        purgeData[1].attachedFilaments.length > 1 &&
        purgeData[1].attachedFilaments[1].materialLoaded &&
        purgeData[1].attachedFilaments[1].canEject);

    if (showPanel1 || showPanel2)
    {
        var filament1Temp = -1;
        var filament2Temp = -1;
        if (purgeData[1].attachedFilaments[0] !== undefined)
        {
            filament1Temp = purgeData[1].attachedFilaments[0].filamentTemperature;
        }
        if (purgeData[1].attachedFilaments[1] !== undefined)
        {
            filament2Temp = purgeData[1].attachedFilaments[1].filamentTemperature;
        }
        
        $('.purge-description').html(i18next.t('purge-instructions'));
        completePanelUpdate('right-nozzle', 
                            showPanel1, 
                            purgeData[0].nozzle0LastFTemp, 
                            filament1Temp);
        completePanelUpdate('left-nozzle',
                            showPanel2, 
                            purgeData[0].nozzle1LastFTemp,
                            filament2Temp);
        setPurgeButtonState();
    }
    else
    {
        $('.purge-description').html(i18next.t('purge-not-available'));
        //goToMainMenu;
    }
}

function purgeInit()
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        setMachineLogo();
        $('.rbx-spinner').on('click', onSpinnerClick);
        $('#left-nozzle-check').on('click', switchPanelState);
        $('#right-nozzle-check').on('click', switchPanelState);
        $('#right-button').on('click', initiatePurge);
        // Set back button to return to the correct page.
        var from =  getUrlParameter('from');
        if (from != null && from == 'maintenance')
            $('#left-button').attr('href', maintenanceMenu)
        else        
            $('#left-button').attr('href', mainMenu)
        
        var ph = promiseGetCommandToRoot(selectedPrinter + '/remoteControl/headEEPROM', null)
                    .then(updatePurgeHeadData);
        
        var pm = promiseGetCommandToRoot(selectedPrinter + '/remoteControl/materialStatus', null)
                    .then(updatePurgeMaterialData);

        Promise.all([ph, pm])
               .then(completePurgeUpdate)
               .catch(goToMainMenu);
    }
	else
		goToHomeOrPrinterSelectPage();
}
