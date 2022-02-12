## 线程协作
* Join
* Wait/Notify
* Condition
* CountDownLatch
* CyclicBarrier

## 线程strack trace

	adb shell "logcat -b main,system,events,crash" >log.txt
	//
	android.os.Process.sendSignal(android.os.Process.myPid(), android.os.Process.SIGNAL_QUIT);
	adb bugreport

## 线程信息
线程的状态、CPU 时间片、优先级、堆栈和锁的信息应有尽有。其中 utm 代表 utime，HZ 代表 CPU 的时钟频率，将 utime 转换为毫秒的公式是“time * 1000/HZ”。例子中 utm=218，也就是 218*1000/100=2180 毫秒。


	// 线程名称; 优先级; 线程id; 线程状态
	"main" prio=5 tid=1 Suspended
	// 线程组;  线程suspend计数; 线程debug suspend计数; 
	| group="main" sCount=1 dsCount=0 obj=0x74746000 self=0xf4827400
	// 线程native id; 进程优先级; 调度者优先级;
	| sysTid=28661 nice=-4 cgrp=default sched=0/0 handle=0xf72cbbec
	// native线程状态; 调度者状态; 用户时间utime; 系统时间stime; 调度的CPU
	| state=D schedstat=( 3137222937 94427228 5819 ) utm=218 stm=95 core=2 HZ=100
	// stack相关信息
	| stack=0xff717000-0xff719000 stackSize=8MB