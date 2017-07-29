package in.ashwanthkumar.manthan.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SchedulerService {
    private ScheduledThreadPoolExecutor context;

//    private static SchedulerService SINGLETON = new SchedulerService(new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2));
    private static SchedulerService SINGLETON = new SchedulerService(new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors()));
//    private static SchedulerService SINGLETON = new SchedulerService(new ScheduledThreadPoolExecutor(1));

    public static SchedulerService get() {
        return SINGLETON;
    }

    private SchedulerService(ScheduledThreadPoolExecutor context) {
        this.context = context;
    }

    public void doLater(Runnable method) {
        context.execute(method);
    }

    public int pendingItems() {
        return context.getQueue().size();
    }

    public void shutdown() {
        context.shutdown();
    }
}
