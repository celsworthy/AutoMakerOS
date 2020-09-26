var menuDetailsMap = 
{
	'axis-testing': {'menu-title': 'axis-testing',
					 'action-1': {'active-icon':'Icon-Menu-Speed-Test-White.svg',
								  'icon':'Icon-Menu-Speed-Test-Grey.svg',
								  'text':'speed-test',
	                              'action':function() { performPrinterAction('/runMacro', testStatus, "SPEED_TEST"); }},
					 'action-2': {'active-icon':'Icon-Menu-Axis-Test-White.svg',
								  'icon':'Icon-Menu-Axis-Test-Grey.svg',
								  'text':'x-axis-test',
	                              'action':function() { performPrinterAction('/runMacro', testStatus, "TEST_X"); }},
					 'action-3': {'active-icon':'Icon-Menu-Axis-Test-White.svg',
								  'icon':'Icon-Menu-Axis-Test-Grey.svg',
								  'text':'y-axis-test',
	                              'action':function() { performPrinterAction('/runMacro', testStatus, "TEST_Y"); }},
					 'action-4': {'active-icon':'Icon-Menu-Axis-Test-White.svg',
								  'icon':'Icon-Menu-Axis-Test-Grey.svg',
								  'text':'z-axis-test',
	                              'action':function() { performPrinterAction('/runMacro', testStatus, "TEST_Z"); }},
					 'left-button': {'icon':'Icon_Menu_Back.svg',
									 'href':maintenanceMenu}},
	'clean-nozzles': {'menu-title': 'clean-nozzles',
		              'action-1': {'active-icon':'Icon-Menu-Clean-White.svg',
						           'icon':'Icon-Menu-Clean-Grey.svg',
							       'text':'clean-nozzle-right',
				    			   'action':function() { performPrinterAction('/cleanNozzle', cleanNozzleStatus + '-right', 2); }},
		              'action-2': {'active-icon':'Icon-Menu-Clean-White.svg',
						    	   'icon':'Icon-Menu-Clean-Grey.svg',
							       'text':'clean-nozzle-left',
			    				   'action':function() { performPrinterAction('/cleanNozzle', cleanNozzleStatus + '-left', 1); }},
			          'left-button': {'icon':'Icon_Menu_Back.svg',
					    			  'href':maintenanceMenu}},
	'eject-stuck': {'menu-title': 'eject-stuck',
	    			'action-1': {'active-icon':'Icon-Menu-Eject-White.svg',
					    		 'icon':'Icon-Menu-Eject-Grey.svg',
						    	 'text':'eject-stuck-1',
							     'action':function() { performPrinterAction('/ejectStuckMaterial', ejectStuckStatus + '-1', 1); }},
				    'action-2': {'active-icon':'Icon-Menu-Eject-White.svg',
					    		 'icon':'Icon-Menu-Eject-Grey.svg',
					    		 'text':'eject-stuck-2',
					    	     'action':function() { performPrinterAction('/ejectStuckMaterial', ejectStuckStatus + '-2', 2); }},
				    'left-button': {'icon':'Icon_Menu_Back.svg',
					    			'href':maintenanceMenu}},
	'identity': {'menu-title': 'identity',
			     'action-1': {'active-icon':'Icon-Menu-Rename-White.svg',
				    		  'icon':'Icon-Menu-Rename-Grey.svg',
                              'href':printerNamePage,
							  'text':'printer-name'},
				 'action-2': {'active-icon':'Icon-Menu-Colour-White.svg',
							  'icon':'Icon-Menu-Colour-Grey.svg',
							  'text':'printer-colour',
							  'href':printerColourPage},
				 'left-button': {'icon':'Icon_Menu_Back.svg',
								 'href':settingsMenu}},
	'maintenance': {'menu-title': 'maintenance',
		            'action-1': {'active-icon':'Icon-Menu-Purge-White.svg',
			                     'icon':'Icon-Menu-Purge-Grey.svg',
				                 'text':'purge',
                                 'href':purgeIntroPage + '?from=maintenance'},
		            'action-2': {'active-icon':'Icon-Menu-Eject-White.svg',
					             'icon':'Icon-Menu-Eject-Grey.svg',
						         'text':'eject-stuck',
					             'href':ejectStuckMenu},
				    'action-3': {'active-icon':'Icon-Menu-Clean-White.svg',
		    					 'icon':'Icon-Menu-Clean-Grey.svg',
					    		 'text':'clean-nozzles',
				    			 'href':cleanNozzlesMenu},
				    'action-4': {'active-icon':'Icon-Menu-Remove-White.svg',
					    		 'icon':'Icon-Menu-Remove-Grey.svg',
						    	 'text':'remove-head',
							     'action':function() { performPrinterAction('/removeHead',
                                                                            removeHeadStatus, safetiesOn().toString()); }},
				    'action-5': {'active-icon':'Icon-Menu-Level-White.svg',
					    		 'icon':'Icon-Menu-Level-Grey.svg',
						    	 'text':'level-gantry',
                                 'action':function() { performPrinterAction('/runMacro',
                                                                            levelGantryStatus,
                                                                            'LEVEL_GANTRY'); }},
				    'action-6': {'active-icon':'Icon-Menu-Test-White.svg',
					    		 'icon':'Icon-Menu-Test-Grey.svg',
						    	 'text':'axis-testing',
							     'href':axisTestingMenu},
                    'left-button': {'icon':'Icon_Menu_Back.svg',
								    'action':goToMainMenu}},
	'print': {'menu-title': 'print',
				          'action-1': {'active-icon':'Icon-Menu-Reprint-White.svg',
								       'icon':'Icon-Menu-Reprint-Grey.svg',
                                       'href':reprintPage,
								       'text':'reprint'},
				          'action-2': {'active-icon':'Icon-Menu-USB-Print-White.svg',
								       'icon':'Icon-Menu-USB-Print-Grey.svg',
                                       'href':usbPrintPage,
								       'text':'usb-print'},
					      'left-button': {'icon':'Icon_Menu_Back.svg',
									      'action':goToMainMenu}},
    'security-settings': {'menu-title': 'security-settings',
				          'action-1': {'active-icon':'Icon-Menu-PIN-White.svg',
								       'icon':'Icon-Menu-PIN-Grey.svg',
                                       'href':accessPINPage,
								       'text':'access-pin'},
					      'left-button': {'icon':'Icon_Menu_Back.svg',
									      'action':goToPreviousPage},
                          'middle-button': null}, // Hide the home button.
	'settings': {'menu-title': 'settings',
                 'action-1': {'active-icon':'Icon-NameColour-White.svg',
                              'icon':'Icon-NameColour-Grey.svg',
                              'text':'identity',
                              'href':identityMenu},
                 'action-2': {'active-icon':'Icon-Wireless-White.svg',
                              'icon':'Icon-Wireless-Grey.svg',
                              'text':'wireless-settings',
                              'href':wirelessSettingsPage},
                 'action-3': {'active-icon':'Icon-Security-White.svg',
                              'icon':'Icon-Security-Grey.svg',
                              'text':'security-settings',
                              'href':securitySettingsMenu},
                 'action-4': {'active-icon':'Icon-Rubbish-Bin-White.svg',
                              'icon':'Icon-Rubbish-Bin-Grey.svg',
                              'text':'empty-rubbish-bin',
                              'action':function() { performPrinterAction('/tidyPrintJobDirs', homePage, null); }},
                 'action-5': {'active-icon':'Icon-About-White.svg',
                              'icon':'Icon-About-Grey.svg',
                              'text':'about',
                              'href':aboutPage},
				  'left-button': {'icon':'Icon_Menu_Back.svg',
								  'href':mainMenu}},
	'settings-s': {'menu-title': 'settings',
                   'action-1': {'active-icon':'Icon-NameColour-White.svg',
                              'icon':'Icon-NameColour-Grey.svg',
                              'text':'server-name',
                              'href':serverNamePage},
                   'action-2': {'active-icon':'Icon-Wireless-White.svg',
                                'icon':'Icon-Wireless-Grey.svg',
                                'text':'wireless-settings',
                                'href':wirelessSettingsPage + '?pdx=s'},
                   'action-3': {'active-icon':'Icon-Security-White.svg',
                                'icon':'Icon-Security-Grey.svg',
                                'text':'security-settings',
                                'href':securitySettingsMenu},
                   'action-4': {'active-icon':'Icon-About-White.svg',
                                'icon':'Icon-About-Grey.svg',
                                'text':'about',
                                'href':aboutPage},
			       'left-button': {'icon':'Icon_Menu_Back.svg',
								   'href':printerSelectPage},
                   'middle-button': null} // Hide the home button.
};

function setMenuText(details, field)
{
    var item = '#' + field;
    text = details[field];
    if (text == null)
    {
        $(item).html("&nbsp;")
               .closest('.row')
               .addClass('rbx-hidden');      
    }
    else
    {
        $(item).html(i18next.t(text))
               .closest('.row')
               .removeClass('rbx-hidden');
    }
}

function setActionButton(details, field)
{
    var item = '#' + field;
    button = details[field];
    if (button == null)
    {
        $(item).closest('.row')
               .off('click')
			   .addClass('disabled rbx-hidden');      
    }
    else
    {
        var activeIcon = button['active-icon'];
        var icon = button['icon'];
        var text = button['text'];
        var href = button['href'];
        var action = button['action'];
        if (text == null)
            text = '&nbsp;';
        else
            text = i18next.t(text);
        
        if (href == null)
            href = '#';
        
        if (icon != null)
            icon = 'url("' + imageRoot + icon + '")';
        $(item).css('background-image', icon);
        if (icon != null && activeIcon != null)
        {
            activeIcon = 'url("' + imageRoot + activeIcon + '")';
            $(item).hover(function() { $(this).css('background-image', activeIcon); },
                          function() { $(this).css('background-image', icon); });
        }

        $(item).html(text)
               .attr('href', href)
               .off('click') // Remove all callbacks
               .closest('.row')
			   .removeClass('disabled rbx-hidden');
        if (action !== null)
            $(item).on('click', action)
            
    }
}

function menuHorzInit()
{
    var menuDetails = null;
    var menuId = getUrlParameter('id');
    if (menuId != null)
        menuDetails = menuDetailsMap[menuId];
    if (menuDetails != null)
    {
        setMachineLogo();
        setMenuText(menuDetails, 'menu-title');
        setActionButton(menuDetails, 'action-1');
        setActionButton(menuDetails, 'action-2');
        setActionButton(menuDetails, 'action-3');
        setActionButton(menuDetails, 'action-4');
        setActionButton(menuDetails, 'action-5');
        setActionButton(menuDetails, 'action-6');
        setFooterButton(menuDetails, 'left-button');
        setFooterButton(menuDetails, 'right-button');

        // Default middle button to go to the home page.
        if (!menuDetails.hasOwnProperty('middle-button'))
            setHomeButton()
        else
            setFooterButton(menuDetails, 'middle-button');
    }
    else
        goToPage(mainMenu);
}
