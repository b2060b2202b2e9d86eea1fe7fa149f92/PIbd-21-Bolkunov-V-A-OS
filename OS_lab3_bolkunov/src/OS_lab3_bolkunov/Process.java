package OS_lab3_bolkunov;

import java.util.Random;

import OS_lab3_bolkunov_Memory.MemoryDispatcher;
import OS_lab3_bolkunov_Memory.MemoryPage;

public class Process
{
    private static int nextPID = 0;
    private static final Random rnd = new Random();

    private static final int minDataAmount = 64;
    private static final int maxDataAmount = 128;

    private static final int minWorkAmount = 3;
    private static final int maxWorkAmount = 5;

    private int workAmount;
    public boolean isWorking() { return workAmount > 0; }
    private boolean disposed = false;
    public boolean isDisposed() { return disposed; }

    private final int memoryAmount;
    public int getRequiredMemoryAmount() { return memoryAmount; }

    private final int pid;
    public int getPID() { return pid; }

    private MemoryPage[] pages;

    public Process(MemoryDispatcher memoryDispatcher)
    {
        pid = nextPID++;
        workAmount = rnd.nextInt(maxWorkAmount-minWorkAmount) + minWorkAmount;
        memoryAmount = rnd.nextInt(maxDataAmount-minDataAmount) + minDataAmount;
        pages = memoryDispatcher.allocateMemory(this);
    }

    public void doWork()
    {
        if(isWorking())
        {
            workAmount--;
            for (int i = 0; i < pages.length; i++)
            {
                if (rnd.nextBoolean())
                {
                    byte[] bytes = new byte[rnd.nextInt(MemoryDispatcher.getPageSize())];
                    rnd.nextBytes(bytes);
                    pages[i].setBytes(rnd.nextInt(MemoryDispatcher.getPageSize()), bytes);
                }
            }
        }
        else
        {
            dispose();
        }
    }

    private void dispose()
    {
        for (int i = 0; i < pages.length; i++)
        {
            pages[i].dispose();
        }
        disposed = true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Процесс PID=" + pid);
        sb.append(System.lineSeparator());
        if(!isDisposed())
        {
            sb.append("Страницы:");
            sb.append(System.lineSeparator());
            for (int i = 0; i < pages.length; i++)
            {
                sb.append(pages[i].toString());
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
