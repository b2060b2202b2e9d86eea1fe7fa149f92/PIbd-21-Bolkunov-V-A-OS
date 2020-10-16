package OS_lab3_bolkunov_Memory;
import OS_lab3_bolkunov.Process;
import OS_lab3_bolkunov.SystemCore;

import java.sql.Time;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

public class MemoryPage
{
    private static final int printBytesPerRow = MemoryDispatcher.printBytesPerRow;

    private static int pageSize = MemoryDispatcher.getPageSize();
    private final MemoryDispatcher memoryDispatcher;

    private final Process owner;
    public boolean isOwnedBy(Process process) { return owner == process; }

    private int physicalPageID;
    public int getPhysicalPageID() { return physicalPageID; }
    protected void setPhysicalPageID(int ID) { physicalPageID = ID; }
    public int getPhysicalMemoryStartPosition() { return physicalPageID * pageSize; }

    private long lastEdit;
    public long getLastEdit() { return lastEdit; }
    private long lastRead;
    public long getLastRead() { return lastRead; }
    public long getLastAccess()
    {
        if(lastRead > lastEdit)
            return lastRead;
        else
            return lastEdit;
    }
    public boolean isInPhysicalMemory()
    {
        if(physicalPageID > -1)
            return true;
        else
            return false;
    }

    private static long getNextTimeStamp() { return System.nanoTime(); }

    public void setByte(int virtualPosition, byte data)
    {
        setBytes(virtualPosition, new byte[] { data });
    }

    public void setBytes(int virtualPosition, byte[] data)
    {
        lastEdit = getNextTimeStamp();
        memoryDispatcher.setBytes(this, virtualPosition, data);
    }

    public byte[] getBytes()
    {
        lastRead = getNextTimeStamp();
        return memoryDispatcher.getBytes(this);
    }

    public MemoryPage(MemoryDispatcher memoryDispatcher, Process owner, int pageID)
    {
        this.memoryDispatcher = memoryDispatcher;
        this.owner = owner;
        this.physicalPageID = pageID;
        lastRead = getNextTimeStamp();
        lastEdit = getNextTimeStamp();
    }

    public void dispose()
    {
        memoryDispatcher.dispose(this);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Страница, PID="+owner.getPID()+" Page_ID="+physicalPageID);
        sb.append(System.lineSeparator());
        if(isInPhysicalMemory())
        {
            sb.append("Находится в физической памяти");
            sb.append(System.lineSeparator());
            //так, а не getBytes(), что-бы не перезаписывать поле lastRead
            for (int i = 0; i < pageSize; i++)
            {
                sb.append(memoryDispatcher.physicalMemory[getPhysicalMemoryStartPosition()+i] + " ");
                if ((i + 1) % printBytesPerRow == 0)
                    sb.append(System.lineSeparator());
            }
            sb.append(System.lineSeparator());
        }
        else
        {
            sb.append("Находится в файле подкачки");
            sb.append(System.lineSeparator());
            byte[] bytes = memoryDispatcher.virtualPages.get(this);
            for (int i = 0; i < pageSize; i++)
            {
                sb.append(bytes[i] + " ");
                if ((i + 1) % printBytesPerRow == 0)
                    sb.append(System.lineSeparator());
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
