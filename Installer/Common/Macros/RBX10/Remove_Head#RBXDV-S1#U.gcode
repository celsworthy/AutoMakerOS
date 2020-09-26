Macro:Home_all_Axis_in_sequence

Move to Eject position;
T0 B0
G0 X150 Y105 Z8

Eject all material;
M104 S160 			;Set & heat nozzle to eject temp
M109				;Wait for nozzle to get to temp.
G0 E-1500			;Eject Filament

G0 X308 Y210

M106			;Fan on Full
G37 S			;Open Door
M107			;Fan Off