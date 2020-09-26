G0 B0				;Select left hand nozzle				
M104 S160 			;Set & heat nozzle to eject temp
M109				;Wait for nozzle to get to temp.
G0 E-50				;Eject Filament
M104 S125			;Go to snap temperature
M109				;Wait for nozzle to get to temp.
G0 E-800			;Eject Filament
M104 S0				;Switch off the heaters
M107				;Turn off Fan
M84					;Motors Off