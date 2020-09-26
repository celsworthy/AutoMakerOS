function setSpinnerData(spinner, value, delta)
{
    var minValue = value - delta;
    var maxValue = value + delta;
    $(spinner).val(value.toFixed(0))
              .attr({'min':minValue.toFixed(0), 'max':maxValue.toFixed(0)});
}

function reportPAError(error)
{
    handleException(error, 'print-adjust-error', false);
    setPrintAdjust();
}

function setPrintAdjustData(n, t, v)
{
    var data = {'name': n,
                'tag': t,
                'value': v};
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
    promisePostCommandToRoot(selectedPrinter + "/remoteControl/setPrintAdjust", data)
        //.then(setPrintAdjust)
        .catch(reportPAError);
}

function printSpeedChanged(id, value)
{
    setPrintAdjustData("feedRate",
                       $('#' + id).attr("nozzle"),
                       value);
}

function flowRateChanged(id, value)
{
    setPrintAdjustData("extrusionRate",
                       $("#" + id).attr("nozzle"),
                       value);
}

function nozzleTempChanged(id, value)
{
    setPrintAdjustData("temp",
                       $("#" + id).attr("nozzle"),
                       value);
}

function bedTempChanged(id, value)
{
    setPrintAdjustData("temp", "bed", value);
}

function updatePrintAdjustData(paData)
{
    setSpinnerData('#pa-bed-temp', paData.bedTargetTemp, 15.0);
    
    if (paData.usingMaterial1)
    //if (true)
    {
        $('#pa-right-nozzle').removeClass("hidden");
        $('#pa-material-1-name').html(paData.material1Name);
        setSpinnerData('#pa-print-speed-r', paData.rightFeedRateMultiplier, 100.0);
        setSpinnerData('#pa-flow-rate-r', paData.rightExtrusionRateMultiplier, 100.0);
        setSpinnerData('#pa-temp-r', paData.rightNozzleTargetTemp, 15.0);
    }
    else
    {
        $('#pa-right-nozzle').addClass("hidden");
    }
    
    if (paData.dualMaterialHead && paData.usingMaterial2)
    //if (true)
    {
        $('#pa-left-nozzle').removeClass("hidden");
        $('#pa-material-2-name').html(paData.material2Name);
        setSpinnerData('#pa-print-speed-l', paData.leftFeedRateMultiplier, 100.0);
        setSpinnerData('#pa-flow-rate-l', paData.leftExtrusionRateMultiplier, 100.0);
        setSpinnerData('#pa-temp-l', paData.leftNozzleTargetTemp, 15.0);
    }
    else
    {
        $('#pa-left-nozzle').addClass("hidden");
    }
}

function setPrintAdjust()
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        promiseGetCommandToRoot(selectedPrinter + '/remoteControl/printAdjust', null)
            .then(updatePrintAdjustData)
            .catch(function(error) { handleException(error, 'print-adjust-set-error', true); });
	}
	else
		goToHomeOrPrinterSelectPage();
}

function printAdjustInit()
{
    $('.rbx-spinner').on('click', onSpinnerClick);        
    setPrintAdjust();
	setInterval(setPrintAdjust, 500);
}
