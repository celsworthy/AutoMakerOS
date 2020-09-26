M103 S			;Set & heat first layer nozzle temp.

Macro:Home_all_Axis_in_sequence

M106			;Fan on Full
M109			;Wait for Nozzle to get to temp.
G4 S5			;Dwell

;Stuck Short_Purge#N0 (Single Material Head)
G0 Y-6 X11 Z8
T0
G0 Z4
G1 Y-4 F400
G36 E1200 F400
G0 B1
G1 E2 F200
G1 E70 X23 F50
G0 B0
G0 Z4.5
G0 Y3

;Stuck Short_Purge#N1 (Single Material Head)
G0 Y-6 X36 Z8
T1
G0 Z4
G1 Y-4 F400
G36 E1200 F400
G0 B1
G1 E4 F200
G1 E70 X23 F50
G0 B0
G0 Z4.5
G0 Y3

;Eject Sequence
M104 S140	 		;Set & heat nozzle to eject temp
M109				;Wait for nozzle to get to temp.
G0 E-50				;Create a small ‘neck’ in the filament which can be snapped easily
M104 S125			;Go to snap temperature
M109				;Wait for nozzle to get to temp.
G36 E-1200 F2100	;Eject Filament
G0 E-20 F2100		;Eject Filament

Macro:Finish-Abort_Print