;Level_Gantry
T0			;Select Nozzle 0 (T0)
G0 X20 Y105		;Level Gantry Position 1
G28 Z			;Home Z
G0 Z5 			;Move up 4mm
G0 X280 Y105	;Level Gantry Position 2
G28 Z			;Home Z
G0 Z5 			;Move up 4mm
G38 			;Level gantry