public class Stopwatch {

    private long start = System.nanoTime();

    public Stopwatch() {
    }

    public long getTime() {
        return System.nanoTime() - start;
    }
}