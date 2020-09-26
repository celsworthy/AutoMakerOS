M129			;Head LED on

Macro:Home_all_Axis_in_sequence

Macro:Level_Gantry_(2-points)

; Z Speed Test
G0 X105 Y75
G28 Z
G0 Z100
G0 Z10
G28 Z
G0 Z5

Macro:Level_Gantry_(2-points)

Macro:Finish-Abort_Print