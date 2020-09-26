M83				;Set Extruder to Relative moves

Macro:Home_all_Axis_in_sequence

M106			;Fan on Full

T0
G0 Y-1 X0		;Move to start of short purge
G28 Z
G0 Z1

M103 S			;Set & heat first layer nozzle temp.
M109			;Wait for Nozzle to get to temp.
G4 S5			;Dwell

;Short_Purge#RBXDV-S1 (SingleX Head)
G0 Z0.5
G36 E1500 F1200
G1 X40 E70 F100
G0 Z1
G1 X35 F50

;Eject Sequence
M104 S160 			;Set & heat nozzle to eject temp
M109				;Wait for nozzle to get to temp.
G0 E-50				;Create a small ‘neck’ in the filament which can be snapped easily
M104 S125			;Go to snap temperature
M109				;Wait for nozzle to get to temp.
G0 E-1500			;Eject Filament

Macro:Finish-Abort_Print