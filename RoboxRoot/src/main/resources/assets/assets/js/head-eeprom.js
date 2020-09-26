var hpDebounceFlag = true;
var lastEData = null;

function getHeadText(typeCode, field)
{
    var headText = i18next.t(typeCode + field);
    if (typeCode === null || headText.length <1)
        headText = "&nbsp;";
    return headText;
}

function setHeadEPPROMData()
{
    if (!hpDebounceFlag && lastEData !== null)
    {
        hpDebounceFlag = true;
        var eData = lastEData;
		   eData.rightNozzleXOffset = $('#hp-right-x').val();
		   eData.rightNozzleYOffset = $('#hp-right-y').val();
		   eData.rightNozzleZOverrun = $('#hp-right-z').val();
        if (eData.valveFitted)
            eData.rightNozzleBOffset = $('#hp-right-b').val();
        if (eData.nozzleCount > 1)
        {
            eData.leftNozzleXOffset = $('#hp-left-x').val();
            eData.leftNozzleYOffset = $('#hp-left-y').val();
            eData.leftNozzleZOverrun = $('#hp-left-z').val();
            if (eData.valveFitted)
                eData.leftNozzleBOffset = $('#hp-left-b').val();
        }
        
        var selectedPrinter = localStorage.getItem(selectedPrinterVar);
        promisePostCommandToRoot(selectedPrinter + "/remoteControl/setHeadEEPROM", eData)
            .then(headEEPromInit)
            .catch(reportHEError);
    }
}

function reportHEError(error)
{
    if (error.name === 'InternalError')
        goToPrinterSelectPage();
    else
    {
        var transMessage = i18next.t('failed-to-write-to-head')
        alert(transMessage);
        headEEPromInit();
    }
}

function setSpinnerValue(spinner, value)
{
//     $(spinner).removeClass('disabled')
//               .val(value.toFixed(2))
//               .parent()
//               .find('.rbx-spinner')
//               .removeClass('disabled')
//               .on('click', onSpinnerClick);
     $(spinner).val(value.toFixed(2))
               .parent()
               .removeClass('rbx-invisible');
}

function disableSpinnerValue(spinner)
{
//     $(spinner).addClass('disabled')
//               .val('')
//               .parent()
//               .find('.rbx-spinner')
//               .addClass('disabled')
//               .off('click');
     $(spinner).val('')
               .parent()
               .addClass('rbx-invisible');
}

function getHeadSerialString(eData)
{
   return eData.typeCode +
            '-' + eData.week + eData.year +
            '-' + eData.ponumber +
            '-' + eData.serialNumber +
            '-' + eData.checksum;
}

function updateHeadEEPROMData(eData)
{
    var typeCode = eData.typeCode;

    $('#head-title-bold').html(getHeadText(typeCode, "-titleBold"));
    $('#head-title-light').html(getHeadText(typeCode, "-titleLight"));
    $('#head-title-edition').html(getHeadText(typeCode, "-titleEdition"));
    $('#head-description').html(getHeadText(typeCode, "-description"));
    $('#head-nozzles').html(getHeadText(typeCode, "-nozzles"));
    $('#head-feeds').html(getHeadText(typeCode, "-feeds"));
    
    if (typeCode !== null)
    {
        $('#head-icon').css('visibility', 'visible');
        $('#head-icon').attr('src', imageRoot + "Icon-" + typeCode + '.svg');
        $('#change-head-icon').attr('src', 'Icon-Head-Change-White.svg');
        
        $('#head-serial-number').html(getHeadSerialString(eData));
        var hoursUnit = i18next.t("hours");
        $('#head-print-hours').html(eData.hourCount.toFixed(0) + ' ' + hoursUnit);
        $('#head-max-temp').html(eData.maxTemp.toFixed(0) + '\xB0' + 'C');

        setSpinnerValue('#hp-right-x', eData.rightNozzleXOffset);
        setSpinnerValue('#hp-right-y', eData.rightNozzleYOffset);
        setSpinnerValue('#hp-right-z', eData.rightNozzleZOverrun);
        
        $('.require-head').removeClass('disabled');
        $('.tm-symbol').css('visibility', 'visible');
    }
    else
    {
        $('#head-icon').css('visibility', 'hidden');
        $('#change-head-icon').attr('src', 'Icon-Head-Change-Grey.svg');
        $('#head-serial-number').html("&nbsp;");
        $('#head-print-hours').html("&nbsp;");
        $('#head-max-temp').html("&nbsp;");
        disableSpinnerValue('#hp-right-x');
        disableSpinnerValue('#hp-right-y');
        disableSpinnerValue('#hp-right-z');
        
        $('.require-head').addClass('disabled');
        $('.tm-symbol').css('visibility', 'hidden');
    }
    
    if (eData.valveFitted)
    {
        $('#b-heading').removeClass('dimmed-section');
        setSpinnerValue('#hp-right-b', eData.rightNozzleBOffset);
    }
    else
    {
        $('#b-heading').addClass('dimmed-section');
        disableSpinnerValue('#hp-right-b');
    }
    if (eData.nozzleCount > 1)
    {
        $('#left-nozzle-heading').removeClass('dimmed-section');
        setSpinnerValue('#hp-left-x', eData.leftNozzleXOffset);
        setSpinnerValue('#hp-left-y', eData.leftNozzleYOffset);
        setSpinnerValue('#hp-left-z', eData.leftNozzleZOverrun);
        if (eData.valveFitted)
            setSpinnerValue('#hp-left-b', eData.leftNozzleBOffset);
        else
            disableSpinnerValue('#hp-left-b');
    }
    else
    {
        $('#left-nozzle-heading').addClass('dimmed-section');
        disableSpinnerValue('#hp-left-x');
        disableSpinnerValue('#hp-left-y');
        disableSpinnerValue('#hp-left-z');
        disableSpinnerValue('#hp-left-b');
    }
    
    hpDebounceFlag = false;
    lastEData = eData;
}

function checkForHeadChange(eData)
{
    var newSerial = getHeadSerialString(eData);
    var currentSerial = $('#head-serial-number').html();
    if (newSerial !== currentSerial)
        updateHeadEEPROMData(eData);
}

function removeHead()
{
    if ($(this).hasClass('disabled'))
        return;
    
    performPrinterAction('/removeHead',
                         removeHeadStatus,
                         safetiesOn().toString());
}

function headEEPromInit()
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        setMachineLogo();
        $('#right-button').on('click', setHeadEPPROMData);
        $('.rbx-spinner').on('click', onSpinnerClick);
        $('.rbx-head-change').on('click', removeHead);
        promiseGetCommandToRoot(selectedPrinter + '/remoteControl/headEEPROM', null)
            .then(updateHeadEEPROMData)
            .catch(function(error) { handleException(error, 'head-eprom-init-error', true); });
        setInterval(function() {
            promiseGetCommandToRoot(selectedPrinter + '/remoteControl/headEEPROM', null)
            .then(checkForHeadChange)
            .catch(function(error) { handleException(error, 'head-eprom-init-error', true); }) 
        }, 2000);
	}
	else
		goToHomeOrPrinterSelectPage();
}
