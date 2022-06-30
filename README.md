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

anr日志
	
	usage: CPU usage 5000ms(from 23:23:33.000 to 23:23:38.000):
	System TOTAL: 2.1% user + 16% kernel + 9.2% iowait + 0.2% irq + 0.1% softirq + 72% idle
	CPU Core: 8
	Load Average: 8.74 / 7.74 / 7.36
	
	Process:com.sample.app 
	  50% 23468/com.sample.app(S): 11% user + 38% kernel faults:4965
	
	Threads:
	  43% 23493/singleThread(R): 6.5% user + 36% kernel faults：3094
	  3.2% 23485/RenderThread(S): 2.1% user + 1% kernel faults：329
	  0.3% 23468/.sample.app(S): 0.3% user + 0% kernel faults：6
	  0.3% 23479/HeapTaskDaemon(S): 0.3% user + 0% kernel faults：982
	  \.\.\.

上面的示例展示了一段在 5 秒时间内 CPU 的 usage 的情况。初看这个日志，你可以收集到几个重要信息。

1. 在 System Total 部分 user 占用不多，CPU idle 很高，消耗多在 kernel 和 iowait。
2. CPU 是 8 核的，Load Average 大约也是 8，表示 CPU 并不处于高负载情况。
3. 在 Process 里展示了这段时间内 sample app 的 CPU 使用情况：user 低，kernel 高，并且有 4965 次 page faults。
4. 在 Threads 里展示了每个线程的 usage 情况，当前只有 singleThread 处于 R 状态，并且当前线程产生了 3096 次 page faults，其他的线程包括主线程（Sample 日志里可见的）都是处于 S 状态。

根据内核中的线程状态的宏的名字和缩写的对应，R 值代表线程处于 Running 或者 Runnable 状态。Running 状态说明线程当前被某个 Core 执行，Runnable 状态说明线程当前正在处于等待队列中等待某个 Core 空闲下来去执行。从内核里看两个状态没有区别，线程都会持续执行。日志中的其他线程都处于 S 状态，S 状态代表TASK_INTERRUPTIBLE，发生这种状态是线程主动让出了 CPU，如果线程调用了 sleep 或者其他情况导致了自愿式的上下文切换（Voluntary Context Switches）就会处于 S 状态。常见的发生 S 状态的原因，可能是要等待一个相对较长时间的 I/O 操作或者一个 IPC 操作，如果一个 I/O 要获取的数据不在 Buffer Cache 或者 Page Cache 里，就需要从更慢的存储设备上读取，此时系统会把线程挂起，并放入一个等待 I/O 完成的队列里面，在 I/O 操作完成后产生中断，线程重新回到调度序列中。但只根据文中这个日志，并不能判定是何原因所引起的。

还有就是 SingleThread 的各项指标都相对处于一个很高的情况，而且产生了一些 faults。page faluts 分为三种：minor page fault、major page fault 和 invalid page fault，下面我们来具体分析。

minor page fault 是内核在分配内存的时候采用一种 Lazy 的方式，申请内存的时候并不进行物理内存的分配，直到内存页被使用或者写入数据的时候，内核会收到一个 MMU 抛出的 page fault，此时内核才进行物理内存分配操作，MMU 会将虚拟地址和物理地址进行映射，这种情况产生的 page fault 就是 minor page fault。

major page fault 产生的原因是访问的内存不在虚拟地址空间，也不在物理内存中，需要从慢速设备载入，或者从 Swap 分区读取到物理内存中。需要注意的是，如果系统不支持zRAM来充当 Swap 分区，可以默认 Android 是没有 Swap 分区的，因为在 Android 里不会因为读取 Swap 而发生 major page fault 的情况。另一种情况是 mmap 一个文件后，虚拟内存区域、文件磁盘地址和物理内存做一个映射，在通过地址访问文件数据的时候发现内存中并没有文件数据，进而产生了 major page fault 的错误。

根据 page fault 发生的场景，虚拟页面可能有四种状态：

第一种，未分配；

第二种，已经分配但是未映射到物理内存；

第三种，已经分配并且已经映射到物理内存；

第四种，已经分配并映射到 Swap 分区（在 Android 中此种情况基本不存在）。

通过上面的讲解并结合 page fault 数据，你可以看到 SingleThread 你一共发生了 3094 次 fault，根据每个页大小为 4KB，可以知道在这个过程中 SingleThread 总共分配了大概 12MB 的空间。