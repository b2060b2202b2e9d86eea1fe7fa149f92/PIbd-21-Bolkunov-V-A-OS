package OS_lab2_bolkunov;

import java.util.ArrayList;

public class Process
{
    private final int pid;

    private ArrayList<Thread> threads = new ArrayList<Thread>();

    public Process(int pid, int threadCount)
    {
        this.pid = pid;

        for (int i = 0; i < threadCount; i++)
        {
            threads.add(new Thread(Core.getNextTID()));
        }
    }


    public int getThreadCount() { return threads.size(); };

    public int getPID() { return pid; }


    /*private Thread getLongestExecutableThread(int timeAmount)
    {
        Thread res = null;
        int maxAvaliableExecTime = -1;
        for (int i = 0; i < threads.size(); i++)
        {
            Thread current = threads.get(i);
            if(current.getExecutionTime() < timeAmount && current.getExecutionTime() > maxAvaliableExecTime)
            {
                maxAvaliableExecTime = current.getExecutionTime();
                res = current;
            }
            else if(threads.get(i).getExecutionTime() == timeAmount)
                return current;
        }
        return res;
    }

    public void execute(int timeAmount)
    {
        if(getThreadCount() > 0)
        {
            Thread current = getLongestExecutableThread(timeAmount);;
            while(current != null && timeAmount > 0)
            {
                System.out.println("Размер кванта = " + timeAmount);
                timeAmount = current.reduceExecutionTime(timeAmount);
                current.run();
                if(current.getExecutionTime() <= 0)
                {
                    threads.remove(current);
                }
                current = getLongestExecutableThread(timeAmount);
            }

            if(timeAmount > 0 && getThreadCount() > 0)
            {
                System.out.println("Размер кванта = " + timeAmount);
                current = threads.get(0);
                timeAmount = current.reduceExecutionTime(timeAmount);
                current.run();
            }
        }
    }*/

    public void removeThread(Thread thread)
    {
        if(thread != null)
            threads.remove(thread);
    }

    public Thread createThread(int timeAmount)
    {
        if(getThreadCount() > 0)
        {
            Thread res = threads.get(getThreadCount()-1);
            return res;
        }
        return null;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Процесс PID=");
        sb.append(pid);
        sb.append(" кол-во потоков=");
        sb.append(getThreadCount());
        return sb.toString();
    }
}
