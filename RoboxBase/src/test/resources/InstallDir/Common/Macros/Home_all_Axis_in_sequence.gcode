;Home_all_Axis_in_sequence
G90 			;Use X Y Z Absolute positioning
G0 Z5			;Move up 5mm if homed
G28 Y			;Home Y
G0 Y115			;Position Y
T0				;Select Nozzle 0
G39				;Clear the bed levelling points
G28 Z			;Home Z
G0 Z10			;Move up 10mm if homed
G28 X			;Home X