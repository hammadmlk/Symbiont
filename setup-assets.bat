@echo off
SET ASSETS="%HOMEPATH%\Dropbox\CS4152 Assets"
IF EXIST %ASSETS% GOTO LINK

ECHO Where is your CS4152 Assets folder?

set /p ASSETS=: 

:LINK
mklink /j %~dp0\android\assets %ASSETS%