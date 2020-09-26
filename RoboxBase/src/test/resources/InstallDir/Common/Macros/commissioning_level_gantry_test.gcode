G39				;Clear the bed levelling points

Macro:Home_all_Axis_in_sequence

;Level_Gantry
T1			;Select Nozzle 1 (T1)
G0 X50 Y41		;Level Gantry Position 1
G28 Z			;Home Z
G0 Z4 			;Move up 4mm
G0 X200 Y41		;Level Gantry Position 2
G28 Z			;Home Z
G0 Z4 			;Move up 4mm
G38 			;Level gantry

;Level_Gantry
T1			;Select Nozzle 0 (T0)
G0 X50 Y41		;Level Gantry Position 1
G28 Z			;Home Z
G0 Z4 			;Move up 4mm
G0 X215 Y41		;Level Gantry Position 2
G28 Z			;Home Z
G0 Z4 			;Move up 4mm
G38 			;Level gantry

G0 X205 Y41		;Level Gantry Position 2

G28 Z?
M113