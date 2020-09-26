T1
M103 T			;Set & heat first layer nozzle temp.

Macro:Home_all_Axis_in_sequence

T1
M109			;Wait for Nozzle to get to temp.
M106			;Fan on Full
G4 S5			;Dwell

Macro:Short_Purge#RBX01-DM#N1
G0 Z10

;Eject Sequence
M104 T160 			;Set & heat nozzle to eject temp
M109				;Wait for nozzle to get to temp.
G0 E-50				;Create a small ‘neck’ in the filament which can be snapped easily
M121 E				;Eject

Macro:Finish-Abort_Print