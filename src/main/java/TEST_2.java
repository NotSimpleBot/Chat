public class TEST_2 {
    public static void main(String[] args) throws Exception{
        Object o1 = new Object();
        Object o2 = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (o2) {
                while (true) {
                }
            }
        });
        thread1.start();

        Thread.sleep(3);

        Thread thread2 = new Thread(() -> {
            someMethod(o1, o2);
        });
        thread2.start();

        Thread.sleep(10);
        System.out.println(thread2.getState());
    }


    private static void someMethod(Object o1, Object o2) {
        synchronized (o1) {
            System.out.println("TYT1");
            synchronized (o2) {
                System.out.println("TYT2");
            }
        }
    }

}
