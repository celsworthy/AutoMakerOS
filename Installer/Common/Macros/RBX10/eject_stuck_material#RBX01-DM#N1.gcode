M83				;Set Extruder to Relative moves

T1
M103 T			;Set & heat first layer nozzle temp.

Macro:Home_all_Axis_in_sequence

T1
M109			;Wait for Nozzle to get to temp.
M106			;Fan on Full
G4 S5			;Dwell

;Short_Purge_T1 (Dual Material)
G0 Y-1 X0
T1
M109
G0 Z0.5
G36 E1500 F400
G0 B1
G1 E2 F200
G1 E65 X40 F100
G0 B0
G0 Z1
G0 X35
G0 Z5

;Eject Sequence
M104 T160 			;Set & heat nozzle to eject temp
M109				;Wait for nozzle to get to temp.
G0 E-50				;Create a small ‘neck’ in the filament which can be snapped easily
M104 T125			;Go to snap temperature
M109				;Wait for nozzle to get to temp.
G0 E-1500			;Eject Filament

Macro:Finish-Abort_Print