T0 B0			;Select left hand nozzle				
M104 S210		;Set & heat nozzle temp from SmartReel
M109			;Wait for Nozzle to get to temp.
G0 B1			;Fully Open Nozzle
G1 D150 F150	;Flush Nozzle
G0 B0			;Close Nozzle
M104 S160		;Set & heat nozzle to eject temp
M109			;Wait for Nozzle to get to temp.
G0 D-50			;Eject Filament
M104 S125		;Go to snap temperature
M109			;Wait for Nozzle to get to temp.
G0 D-1200		;Eject Filament
M104 S0			;Turn heater off
M107			;Turn off Fan
M84				;Motors Off