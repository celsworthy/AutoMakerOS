@echo off
set isConnected=
for /f "tokens=2*delims=: " %%a in ('netsh wlan show interfaces ^| findstr State') do @set isConnected=%%a

set radioStatus=
for /f "tokens=2*delims=:" %%a in ('netsh wlan show interfaces ^| findstr Radio') do @set radioStatus=%%a

set ssid=
for /f "tokens=2*delims=: " %%a in ('netsh wlan show interfaces ^| findstr /C:" SSID"') do @set ssid=%%a

set associated=false
set powered=false

if "%isConnected%"=="connected" (
	set associated=true
	set powered=true
) else (
	if "%radioStatus%"==" Hardware On" (
		set powered=true
	)
)

echo {"poweredOn":"%powered%", "associated":"%associated%", "ssid":"%ssid%"}