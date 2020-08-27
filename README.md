# 线程strack trace
	adb shell "logcat -b main,system,events,crash" >log.txt
	//
	android.os.Process.sendSignal(android.os.Process.myPid(), android.os.Process.SIGNAL_QUIT);
	adb bugreport