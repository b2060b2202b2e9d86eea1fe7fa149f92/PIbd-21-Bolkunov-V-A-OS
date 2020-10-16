package OS_lab3_bolkunov_Memory;

import java.util.*;
import java.util.function.Function;
import OS_lab3_bolkunov.Process;

public class MemoryDispatcher
{
    //STATIC
    public static final int printBytesPerRow = 8;
    private static final int memoryAmount = 256; //Общее число байтов, доступных в вирутальной памяти
    public static int getMemoryAmount() { return memoryAmount; }
    private static final int swapFileSize = 2048; //Размер файла подкачки
    public static int getSwapFileSize(){ return swapFileSize; }
    private static final int pageSize = 32; //Размер страницы
    public static int getPageSize() { return pageSize; }
    private static int getTotalPageCount() { return memoryAmount/pageSize + swapFileSize/pageSize; }

    //NON-STATIC

    //FIELDS
    private MemoryPage[] physicalPages;
    protected byte[] physicalMemory;
    public HashMap<MemoryPage,byte[]> virtualPages;

    //CONSTRUCTOR
    public MemoryDispatcher()
    {
        physicalMemory = new byte[memoryAmount];
        physicalPages = new MemoryPage[memoryAmount/pageSize];

        virtualPages = new HashMap<MemoryPage,byte[]>();
    }

    //HELPER METHODS
    private int getPageCount(MemoryPage[] pages,Function<MemoryPage, Boolean> function)
    {
        int result = 0;
        for(int i = 0; i < pages.length; i++)
        {
            if (function.apply(pages[i]))
            {
                result++;
            }
        }
        return result;
    }
    private int getFreePhysicalPageCount() { return getPageCount(physicalPages, mp -> mp == null); }
    private int getUsedPhysicalPageCount() { return getPageCount(physicalPages, mp -> mp != null); }
    private int getTotalPhysicalPageCount() { return physicalPages.length; }
    private int getFreePhysicalPageID()
    {
        for (int i = 0; i < physicalPages.length; i++)
        {
            if(physicalPages[i] == null)
                return i;
        }
        return -1;
    }

    //LOADING PAGES
    private boolean isPhysicalPageEqualToVirtualPage(MemoryPage page)
    {
        if(virtualPages.containsKey(page))
        {
            byte[] bytes = virtualPages.get(page);
            for (int i = 0; i < pageSize; i++)
            {
                if(bytes[i] != physicalMemory[page.getPhysicalPageID()*pageSize + i])
                    return false;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private int unloadMemoryPage(MemoryPage page)
    {
        int physicalMemoryID = page.getPhysicalPageID();
        if(!virtualPages.containsKey(page))
        {
            virtualPages.put(page, new byte[pageSize]);
        }
        physicalPages[physicalMemoryID] = null;

        if(!isPhysicalPageEqualToVirtualPage(page))
        {
            byte[] bytes = virtualPages.get(page);
            for (int i = 0; i < pageSize; i++)
            {
                bytes[i] = physicalMemory[physicalMemoryID * pageSize + i];
            }
            virtualPages.replace(page, bytes);
        }
        page.setPhysicalPageID(-1);
        return physicalMemoryID;
    }

    private void loadMemoryPage(MemoryPage page)
    {
        int freePage = getFreePhysicalPageID();
        if(freePage > -1)
        {
            physicalPages[freePage] = page;
            byte[] bytes = virtualPages.get(page);
            page.setPhysicalPageID(freePage);

            for (int i = 0; i < pageSize; i++)
            {
                physicalMemory[freePage*pageSize + i] = bytes[i];
            }
        }
    }

    public MemoryPage getLeastRecentlyUsedMemoryPage() //LRU
    {
        MemoryPage LRU = physicalPages[0];
        for (int i = 0; i < physicalPages.length; i++)
        {
            if(physicalPages[i] != null)
            {
                if(LRU == null || LRU.getLastAccess() > physicalPages[i].getLastAccess())
                {
                    LRU = physicalPages[i];
                }
            }
        }
        return LRU;
    }

    private void swapPages(MemoryPage pageToLoad)
    {
        MemoryPage pageToUnload = getLeastRecentlyUsedMemoryPage();
        unloadMemoryPage(pageToUnload);
        loadMemoryPage(pageToLoad);
    }

    //ALLOCATING MEMORY
    private MemoryPage[] allocatePhysicalMemory(Process process, int pageCount)
    {
        MemoryPage[] result = new MemoryPage[pageCount];
        for (int i = 0; i < pageCount; i++)
        {
            int freePageID = getFreePhysicalPageID();
            MemoryPage memoryPage = new MemoryPage(this, process, freePageID);
            result[i] = memoryPage;
            physicalPages[freePageID] = memoryPage;
            clearData(memoryPage);
        }
        return result;
    }

    public MemoryPage[] allocateMemory(Process process)
    {
        double pageCountRequiredDiv = process.getRequiredMemoryAmount() / pageSize;
        int pageCountRequiredMod = process.getRequiredMemoryAmount() % pageSize;
        int pageCountRequired = (int)pageCountRequiredDiv;
        if(pageCountRequiredMod > 0) { pageCountRequired++; }

        if(getFreePhysicalPageCount() >= pageCountRequired)
        {
            MemoryPage[] memoryPages = allocatePhysicalMemory(process, pageCountRequired);
            return memoryPages;
        }
        else
        {
            ArrayList<MemoryPage> memoryPages = new ArrayList<MemoryPage>();
            int physicalPageCount = getFreePhysicalPageCount();
            MemoryPage[] pages = allocatePhysicalMemory(process, physicalPageCount);
            Collections.addAll(memoryPages, pages);
            for(int i = 0; i < pageCountRequired - physicalPageCount; i++)
            {
                int physicalID = unloadMemoryPage(getLeastRecentlyUsedMemoryPage());
                MemoryPage memoryPage = new MemoryPage(this, process, physicalID);
                memoryPages.add(memoryPage);
                physicalPages[physicalID] = memoryPage;
                clearData(memoryPage);
            }
            return memoryPages.toArray(pages);
        }
    }

    //WORKING WITH MEMORY
    public void setBytes(MemoryPage page, int virtualPosition, byte[] data)
    {
        if(!page.isInPhysicalMemory())
        {
            if(getFreePhysicalPageCount() == 0)
            {
                swapPages(page);
            }
            else
            {
                loadMemoryPage(page);
            }
        }
        for (int i = 0; virtualPosition+i < pageSize && i < data.length; i++)
        {
            int physicalPosition = virtualPosition + i + page.getPhysicalPageID()*pageSize;
            physicalMemory[physicalPosition] = data[i];
        }
    }

    public byte[] getBytes(MemoryPage page)
    {
        if(!page.isInPhysicalMemory())
        {
            if(getFreePhysicalPageCount() == 0)
            {
                swapPages(page);
            }
            else
            {
                loadMemoryPage(page);
            }
        }
        byte[] data = new byte[pageSize];
        for (int i = 0; i < pageSize; i++)
        {
            int physicalPosition = i + page.getPhysicalPageID()*pageSize;
            data[i] = physicalMemory[physicalPosition];
        }
        return data;
    }

    //DISPOSING MEMORY PAGES
    private void clearData(MemoryPage page)
    {
        if(page.isInPhysicalMemory())
        {
            for (int i = 0; i < pageSize; i++)
            {
                int physicalPosition = i + page.getPhysicalPageID() * pageSize;
                physicalMemory[physicalPosition] = 0;
            }
        }
    }

    protected void dispose(MemoryPage page)
    {
        clearData(page);
        if(page.isInPhysicalMemory())
        {
            physicalPages[page.getPhysicalPageID()] = null;
        }
        else
        {
            virtualPages.remove(page);
        }
    }

    public String getPhysicalMemoryCondition() {
        StringBuilder sb = new StringBuilder();
        sb.append("Состояние физической памяти: ");
        sb.append(System.lineSeparator());
        for (int i = 0; i < physicalPages.length; i++)
        {
            if (physicalPages[i] == null)
            {
                sb.append("Страница свободна");
                sb.append(System.lineSeparator());
                for (int j = 0; j < pageSize; j++)
                {
                    sb.append(physicalMemory[j + pageSize * i] + " ");
                    if ((j + 1) % printBytesPerRow == 0)
                        sb.append(System.lineSeparator());
                }
            }
            else
            {
                sb.append(physicalPages[i].toString());
            }
        }
        return sb.toString();
    }

    public String getVirtualMemoryCondition()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Состояние файла подкачки: ");
        sb.append(System.lineSeparator());
        Set<MemoryPage> pages = virtualPages.keySet();
        Iterator<MemoryPage> pageIterator = pages.iterator();
        while(pageIterator.hasNext())
        {
            MemoryPage page = pageIterator.next();
            if(!page.isInPhysicalMemory())
                sb.append(page.toString());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
