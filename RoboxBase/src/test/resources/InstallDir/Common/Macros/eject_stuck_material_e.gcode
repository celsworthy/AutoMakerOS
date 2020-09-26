M103			;Set & heat first layer nozzle temp.

Macro:Home_all_Axis_in_sequence

M106			;Fan on Full
M109			;Wait for Nozzle to get to temp.
G4 S5			;Dwell

Macro:Short_Purge_T0

Macro:Short_Purge_T1

G0 E-1200		;Eject Filament

Macro:Finish-Abort_Print