Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = "E:\桌面\iu\backend"
WshShell.Run "java -jar target\iyd-learning-1.0.0.jar", 0, False