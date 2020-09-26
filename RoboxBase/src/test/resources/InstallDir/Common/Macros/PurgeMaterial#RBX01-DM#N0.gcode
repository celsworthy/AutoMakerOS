Macro:Home_all_Axis_in_sequence

Macro:Level_Gantry_(2-points)

M190			;wait to get to Bed temp
M109			;wait to get to nozzle temp

M129			;Head LED on
M106			;Fan on

G36 E1000 F12000 ; Un-Park

G0 X20 Y15
Macro:Purge_T0
G0 X20 Y45
Macro:Purge_T0
G0 X20 Y75
Macro:Purge_T0
G0 X20 Y105
Macro:Purge_T0
G0 X20 Y135
Macro:Purge_T0

Macro:Finish-Abort_Print