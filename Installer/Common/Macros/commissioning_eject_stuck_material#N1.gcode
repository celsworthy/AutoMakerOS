T1 B0			;Select right hand nozzle
M104 S210		;Set & heat nozzle temp from SmartReel
M109			;Wait for Nozzle to get to temp.
G0 B1			;Fully Open Nozzle
G1 E200 F150	;Flush right hand nozzle
T0				;Switch to left hand nozzle
G1 E80 F150		;Flush left hand nozzle
G0 B0			;Close Nozzle
M104 S160		;Set & heat nozzle to eject temp
M109			;Wait for Nozzle to get to temp.
G0 E-50			;Create 'neck' in filament
M104 S125		;Set Heater to snap temp.
M109			;Wait for Nozzle to get to temp.
G0 E-1200		;Eject Filament
M104 S0			;Turn off Heater
M107			;Turn off Fan
M84				;Motors Off