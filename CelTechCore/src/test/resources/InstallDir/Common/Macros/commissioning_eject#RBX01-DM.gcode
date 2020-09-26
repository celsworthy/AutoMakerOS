T0 B0				;Select left hand nozzle				
M104 S160 T160		;Set & heat nozzles to eject temp
M109				;Wait for left nozzle to get to temp.
T1					;Select right hand nozzle
M109				;Wait for right nozzle to get to temp.
G0 D-50 E-50		;Eject Filament
M104 S125 T125		;Go to snap temperature
M109				;Wait for nozzles to get to temp.
G0 D-800 E-800		;Eject Filament
M104 S0 T0			;Switch off the heaters
M107				;Turn off Fan
M84					;Motors Off