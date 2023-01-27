public class PrintItem  {
    private int         printDuration; // in milliseconds
    private PrintType   type;
    private int         id;

    private int priority;

    public enum PrintType
    {
        INSTRUCTOR,
        STUDENT
    };

    public PrintItem(int printDuration, PrintType t, int id)
    {
        this.printDuration = printDuration;
        this.type = t;
        this.id = id;
        if(t == PrintType.STUDENT) this.priority = 2;
        else if (t == PrintType.INSTRUCTOR) this.priority = 1;
    }

    /**
     * 'Prints' the item (actually does nothing, only does sleeping)
     * @return true if print event is succeeded, return false when sleep operation is interrupted
     */
    public boolean print()
    {
        try
        {
            Thread.sleep(printDuration);
        }
        catch (InterruptedException e) { return false; }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("{%s, ID:%03d, %03dms}",
                             (type == PrintType.STUDENT) ? "Student   " : "Instructor",
                             id, printDuration);
    }

    PrintType getPrintType()
    {
        return type;
    }

    int getPrintDuration()
    {
        return printDuration;
    }

    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }
}
