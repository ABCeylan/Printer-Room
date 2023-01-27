import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class PrinterQueue implements IMPMCQueue<PrintItem>
{
    // TODO: This is all yours

    private static volatile boolean isClosed = false;

    private Semaphore semaphore;

    private static int CAPACITY;

    private static volatile ArrayBlockingQueue<PrintItem> studentQueue;
    private static volatile ArrayBlockingQueue<PrintItem> instructorQueue;


    public PrinterQueue(int maxElementCount)
    {
        studentQueue = new ArrayBlockingQueue<>(maxElementCount);
        instructorQueue = new ArrayBlockingQueue<>(maxElementCount);
        CAPACITY = maxElementCount;
        semaphore = new Semaphore(maxElementCount);
    }

    @Override
    public void Add(PrintItem data) throws QueueIsClosedExecption {
        try {

            if (isClosed) {
                throw new QueueIsClosedExecption();
            }
            semaphore.acquire();
            if (isClosed) {
                throw new QueueIsClosedExecption();
            }

            if(data.getPriority() == 2){
                studentQueue.put(data);
            }
            else if(data.getPriority() == 1){
                instructorQueue.put(data);
            }
            System.out.println(studentQueue.size());
            System.out.println(instructorQueue.size());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public PrintItem Consume() throws QueueIsClosedExecption {
        PrintItem item;

        try {
            if (isClosed && (studentQueue.size() + instructorQueue.size() == 0)) {
                throw new QueueIsClosedExecption();
            }
            if(instructorQueue.size() > 0){
                item = instructorQueue.take();
            }
            else{
                item = studentQueue.take();
            }
            semaphore.release();
            if (isClosed)
            {
                int i = 0;
                while( i < semaphore.getQueueLength()){
                    semaphore.release();
                    i++;
                }
                i = 0;
            }

            return item;
        }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

    }

    @Override
    public int RemainingSize() {
        return semaphore.availablePermits();
    }

    @Override
    public void CloseQueue() {
        isClosed = true;

    }

}
