package OS_lab2_bolkunov;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Core
{
    private static int currentTID = -1;

    public static int getNextTID()
    {
        currentTID++;
        return currentTID;
    }

    private final int quantSize = 50;
    private static Random rnd = new Random();
    private ArrayList<Process> processes;

    public Core()
    {
        createProcesses();
        planProcesses();
    }

    public void createProcesses()
    {
        processes = new ArrayList<Process>();
        int count = 5 + rnd.nextInt(5);
        for (int i = 0; i < count; i++)
        {
            Process proc = new Process(i,2+rnd.nextInt(5));
            processes.add(proc);
        }
    }

    public void planProcesses()//циклическое
    {
        int givenTime = 0;
        int requiredTime = 0;

        while(processes.size() > 0)
        {
            Process currentProc;
            Thread curentThread;
            int currentQuant;
            for (int i = 0; i < processes.size(); i++)
            {
                currentQuant = quantSize;
                givenTime += currentQuant;

                currentProc = processes.get(i);

                System.out.println("Процесс PID = " + currentProc.getPID() + " выполняет: ");

                while(currentQuant > 0 && currentProc.getThreadCount() > 0)
                {
                    System.out.println("Размер кванта = " + currentQuant);

                    curentThread = currentProc.createThread(currentQuant);
                    requiredTime += curentThread.getExecutionTime();

                    currentQuant = curentThread.reduceExecutionTime(currentQuant);
                    requiredTime -= curentThread.getExecutionTime();

                    if(curentThread != null)
                        curentThread.run();

                    if(curentThread.getExecutionTime() == 0)
                        currentProc.removeThread(curentThread);
                }

                System.out.println("Процесс завершил свое выполнение!");
                if(currentProc.getThreadCount() == 0)
                    processes.remove(currentProc);
                System.out.println();
            }
        }
        System.out.println("Требовалось времени : "+requiredTime);
        System.out.println("Затрачено времени : "+givenTime);
    }
}
