M103 S			;Set & heat first layer nozzle temp.

Macro:Home_all_Axis_in_sequence

M106			;Fan on Full
M109			;Wait for Nozzle to get to temp.
G4 S5			;Dwell

Macro:Short_Purge#N0

Macro:Short_Purge#N1

;Eject Sequence
M104 T160 			;Set & heat nozzle to eject temp
M109				;Wait for nozzle to get to temp.
G0 E-50				;Create a small ‘neck’ in the filament which can be snapped easily
M121 E				;Eject

Macro:Finish-Abort_Print