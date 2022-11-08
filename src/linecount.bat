@Echo off
Set /a _Lines=0
for /R %_Dir% %%f in (*.*) do (
  for /f %%j in ('Find "" /v /c ^< "%%~f"') Do Set /a _Lines=_Lines+%%j
)
Echo %_File% has %_Lines% lines.
pause