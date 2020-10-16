package OS_lab3_bolkunov;

import OS_lab3_bolkunov_Memory.MemoryDispatcher;

import java.util.ArrayList;
import java.util.Random;

public class SystemCore
{
    private static final Random rnd = new Random();

    private static final int minProcessCount = 3;
    private static final int maxProcessCount = 6;

    private int processCount;
    private ArrayList<Process> processes;

    private MemoryDispatcher memoryDispatcher;

    public SystemCore()
    {
        processes = new ArrayList<Process>();
        processCount = rnd.nextInt(maxProcessCount-minProcessCount) + minProcessCount;
        memoryDispatcher = new MemoryDispatcher();
    }

    public String start()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < processCount; i++)
        {
            Process process = new Process(memoryDispatcher);
            processes.add(process);
            sb.append("Создается процесс:");
            sb.append(System.lineSeparator());
            sb.append(process.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private boolean working = false;
    private int I = 0;
    public String next()
    {
        if (processes.size() > 0)
        {
            if(I < processes.size())
            {
                StringBuilder sb = new StringBuilder();
                Process current = processes.get(I);
                sb.append("Выполняется:");
                sb.append(System.lineSeparator());
                if(working)
                {
                    current.doWork();
                    I++;
                }
                working = !working;
                sb.append(current.toString());
                if(current.isDisposed())
                {
                    sb.append("Процесс завершил свою работу!");
                    processes.remove(current);
                }
                return sb.toString();
            }
            else
            {
                I = 0;
                return next();
            }
        }
        else
        {
            return "Все процессы завершили свое выполнение!";
        }
    }

    public String getPhysicalMemoryCondition()
    {
        return memoryDispatcher.getPhysicalMemoryCondition();
    }

    public String getVirtualMemoryCondition()
    {
        return memoryDispatcher.getVirtualMemoryCondition();
    }
}
