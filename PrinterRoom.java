import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrinterRoom
{
    private class Printer implements Runnable
    {
        // TODO: Implement
        private int id;

        @Override
        public void run() {
            while (true) {
                try {
                    PrintItem item = roomQueue.Consume();
                    SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, this.id, String.format(SyncLogger.FORMAT_PRINT_DONE, item));

                    item.print();
                }
                catch (QueueIsClosedExecption e) {
                    SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, this.id, String.format(SyncLogger.FORMAT_TERMINATING));
                    //System.out.println(roomQueue.RemainingSize());
                    break;
                }
            }
        }

        public Printer(int id, IMPMCQueue<PrintItem> roomQueue)
        {
            // TODO: Implement
            this.id = id;
            SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0, String.format(SyncLogger.FORMAT_PRINTER_LAUNCH, id));
        }
    }

    public IMPMCQueue<PrintItem> roomQueue;
    private final List<Printer> printers;

    public PrinterRoom(int printerCount, int maxElementCount)
    {

        // Instantiating the shared queue
        roomQueue = new PrinterQueue(maxElementCount);

        // Let's try streams
        // Printer creation automatically launches its thread ***** wrong comment **** not launched we need to launch them
        printers = Collections.unmodifiableList(IntStream.range(0, printerCount)
                                                         .mapToObj(i -> new Printer(i, roomQueue))
                                                         .collect(Collectors.toList()));

        //creating thread with id and shared queue
        ExecutorService executor = Executors.newFixedThreadPool(printerCount);
        for(int i  = 0; i < printerCount; i++){
            executor.execute(printers.get(i));
        }
        executor.shutdown();
        // Printers are launched using the same queue
    }

    public boolean SubmitPrint(PrintItem item, int producerId)
    {
        SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerId, String.format(SyncLogger.FORMAT_ADD, item));

        try {

            roomQueue.Add(item);
            return true;
        }
        catch (QueueIsClosedExecption e){
            //SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerId, String.format(SyncLogger.FORMAT_ADD, item));

            SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerId, String.format(SyncLogger.FORMAT_ROOM_CLOSED, item));
            return false;
        }
    }

    public void CloseRoom()
    {
        // TODO: Implement
        roomQueue.CloseQueue();
    }
}
