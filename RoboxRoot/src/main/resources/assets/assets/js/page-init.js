var pageInitMap = {'about':aboutInit,
                   'access-pin':accessPINInit,
                   'console':consoleInit,
                   'head-eeprom':headEEPromInit,
                   'home':homeInit,
                   'index':indexInit,
                   'job-progress':jobProgressInit,
                   'login':loginInit,
                   'menu-horz':menuHorzInit,
                   'control':controlInit,
                   'pin-reset':pinResetInit,
                   'print-adjust':printAdjustInit,
                   'printer-colour':printerColourInit,
                   'printer-name':printerNameInit,
                   'printer-select':printerSelectInit,
                   'purge-intro':purgeIntroInit,
                   'purge':purgeInit,
                   'reprint':reprintInit,
                   'server-name':serverNameInit,
                   'usb-print':usbPrintInit,
                   'wireless-settings':wirelessSettingsInit};
var pageTitleMap = {};

function page_initialiser() {
    var pageId = $('body').attr('id');
    titlei18n = "default";
    if (pageId != null)
    {
        titlei18n = pageTitleMap[pageId];
        if (titlei18n == null)
            titlei18n = pageId;
        var pageInit = pageInitMap[pageId];
        if (pageInit != null)
            pageInit();
    }
}