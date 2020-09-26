;Home_all_Axis_in_sequence
G90 			;Use X Y Z Absolute positioning
G0 B0			;ensure nozzle is closed
G0 Z5			;Move up 5mm if homed
G28 Y			;Home Y
G0 Y175			;Position Y
T0				;Select Nozzle 0
G39				;Clear the bed leveling points
G28 Z			;Home Z
G0 Z5			;Move up 5mm
G28 X			;Home X