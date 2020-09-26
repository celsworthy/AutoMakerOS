Macro:Home_all_Axis_in_sequence

Macro:Level_Gantry_(2-points)

M190			;wait to get to Bed temp
M109			;wait to get to nozzle temp

M129			;Head LED on
M106			;Fan on

G36 E1000 F12000 ; Un-Park

G0 X20 Y20
Macro:Purge_T0
G0 Y23
Macro:Purge_T1
G0 Y26
Macro:Purge_T0
G0 Y29
Macro:Purge_T1
G0 Y32
Macro:Purge_T0
G0 Y35
Macro:Purge_T1
G0 Y38
Macro:Purge_T0
G0 Y41
Macro:Purge_T1
G0 Y44
Macro:Purge_T0
G0 Y47
Macro:Purge_T1

Macro:Finish-Abort_Print