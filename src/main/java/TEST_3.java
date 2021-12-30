import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class TEST_3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<String> someCallable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("Go to sleep, waiting...");
                TimeUnit.SECONDS.sleep(3);
                return "SomeStringReturn";
            }
        };
        FutureTask<String> futureTask = new FutureTask<>(someCallable);
        System.out.println(System.identityHashCode(futureTask));
        Thread thread = new Thread(futureTask);
        thread.start();

        System.out.println(futureTask.get());
    }


}
