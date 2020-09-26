M139 S80
M190

M103 S235
M109

;Home_all_Axis_in_sequence
G90 			;Use X Y Z Absolute positioning
G0 B0			;ensure nozzle is closed
G0 Z5			;Move up 5mm if homed
G28 Y			;Home Y
G0 Y115			;Position Y
T0				;Select Nozzle 0
G39				;Clear the bed levelling points
G28 Z			;Home Z
G0 Z10			;Move up 10mm if homed
G28 X			;Home X

;Level_Gantry
T0			;Select Nozzle 0 (T0)
G0 X20 Y75		;Level Gantry Position 1
G28 Z			;Home Z
G0 Z4 			;Move up 4mm
G0 X190 Y75		;Level Gantry Position 2
G28 Z			;Home Z
G0 Z4 			;Move up 4mm
G38 			;Level gantry

M190			;wait to get to Bed temp
M109			;wait to get to nozzle temp

M129			;Head LED on
M106			;Fan on

G36 E1000 F12000 ; Un-Park

G0 X20 Y15
;Purge_T0
T0
G0 Z0.3
G0 B1
T0
T0
G1 E15 F100
G0 Z1
G1 X190 E80 F800
G0 Z0.3
G1 E15 F100
G0 B0
G0 Z8
G0 Y30
;Purge_T1
T1
G0 Z0.3
G0 B1
T1
T1
G1 E15 F150
G0 Z1.5
G1 X20 E150 F800
G0 Z0.3
G1 E15 F150
G0 B0
G0 Z8
G0 Y45
;Purge_T0
T0
G0 Z0.3
G0 B1
T0
T0
G1 E15 F100
G0 Z1
G1 X190 E80 F800
G0 Z0.3
G1 E15 F100
G0 B0
G0 Z8
G0 Y60
;Purge_T1
T1
G0 Z0.3
G0 B1
T1
T1
G1 E15 F150
G0 Z1.5
G1 X20 E150 F800
G0 Z0.3
G1 E15 F150
G0 B0
G0 Z8
G0 Y75
;Purge_T0
T0
G0 Z0.3
G0 B1
T0
T0
G1 E15 F100
G0 Z1
G1 X190 E80 F800
G0 Z0.3
G1 E15 F100
G0 B0
G0 Z8
G0 Y90
;Purge_T1
T1
G0 Z0.3
G0 B1
T1
T1
G1 E15 F150
G0 Z1.5
G1 X20 E150 F800
G0 Z0.3
G1 E15 F150
G0 B0
G0 Z8
G0 Y105
;Purge_T0
T0
G0 Z0.3
G0 B1
T0
T0
G1 E15 F100
G0 Z1
G1 X190 E80 F800
G0 Z0.3
G1 E15 F100
G0 B0
G0 Z8
G0 Y120
;Purge_T1
T1
G0 Z0.3
G0 B1
T1
T1
G1 E15 F150
G0 Z1.5
G1 X20 E150 F800
G0 Z0.3
G1 E15 F150
G0 B0
G0 Z8
G0 Y135
;Purge_T0
T0
G0 Z0.3
G0 B1
T0
T0
G1 E15 F100
G0 Z1
G1 X190 E80 F800
G0 Z0.3
G1 E15 F100
G0 B0
G0 Z8

;Finish-Abort_Print
M104 S0 T0		;Nozzle Heater Off
M140 S0			;Bed Heater Off
M106			;Fan on full
G0 B0			;Close Nozzle
G91				;Relative positioning
G0 Z5			;Move up 5mm
G90 			;Absolute positioning
G0 X15 Y115		;Move to front corner

;Open Door
G37	S			;Unlock door

;Every thing off
M170 S0			;Ambient control off
M107			;Fan off
M128			;Head Light off

M84				;Motors off