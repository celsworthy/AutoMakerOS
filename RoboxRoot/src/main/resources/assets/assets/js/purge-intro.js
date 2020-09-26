function purgeIntroInit()
{
    var selectedPrinter = localStorage.getItem(selectedPrinterVar);
	if (selectedPrinter !== null)
	{
        setMachineLogo();
        // Set back button to return to the correct page.
        var from =  getUrlParameter('from');
        if (from != null && from == 'maintenance')
        {
            $('#left-button').attr('href', maintenanceMenu)
            $('#right-button').attr('href', purgePage + '?from=maintenance')
        }
        else
        {
            $('#left-button').attr('href', mainMenu)
            $('#right-button').attr('href', purgePage + '?from=main')
        }
    }
	else
		goToHomeOrPrinterSelectPage();
}
