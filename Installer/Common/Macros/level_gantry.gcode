M129			;Head LED on

G39				;Clear the bed levelling points

Macro:Home_all_Axis_in_sequence

Macro:Level_Gantry_(2-points)

G37 S			;Unlock door
M128			;Head Light off
M84				;Motors off