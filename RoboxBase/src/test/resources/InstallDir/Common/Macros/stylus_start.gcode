begin HEADER

G39				;Clear the bed levelling points
G90 			;Use X Y Z Absolute positioning

T2				;set homing tool

;Move up and Home_X and Y
G92 Z20			;Off set of the Pen to the Home
G0 Z30			;Move up to clear all
G28 Y			;Home Y
G0 Y115			;Position Y
G28 X			;Home X

;Move to down leg
G0 X32
G0 Y-1
G28 Z
G39				;Clear the bed levelling points
G92 Z8			;Off set of the Pen to the Home
G0 Y6.5
G0 Z3
G0 Y4 Z0
G0 Y6 Z2

;Home Z
G28 Z
G39				;Clear the bed levelling points
G0 Y10 Z8

;Level_Gantry
G39				;Clear the bed levelling points
G0 X27 Y75		;Level Gantry Position 1
G28 Z			;Home Z
G0 Z4 			;Move up 4mm
G0 X197 Y75		;Level Gantry Position 2
G28 Z			;Home Z
G0 Z4 			;Move up 4mm
G38 			;Level gantry

;7_point_Bed_probing-Set_Washout
G0 Y20
G28 Z
G0 Z2
G0 X112
G28 Z
G0 Z2
G0 X27
G28 Z
G0 Z2
G0 Y130
G28 Z
G0 Z2
G0 X112
G28 Z
G0 Z2
G0 X197
G28 Z
G0 Z2
G0 X112 Y75
G28 Z
G0 Z2

;G39 S0.5		;set washout over the first 2mm

;Move to up leg
G0 X32 Y10 Z11	;go behind the homing peg
G1 Y3 F300		;slide over the homing peg to retract
G1 Y-2 Z5.5		;slide over and onto the homing peg to fully retract
G0 Z8
G0 Y75 Z20		;move away from the homing Peg

;Tool path
T0