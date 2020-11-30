package OS_lab5_bolkunov;

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

    private static final int quantSize = 50;
    private static Random rnd = new Random();
    private ArrayList<Process> processes;
    private ArrayList<Process> processesClone;

    public Core()
    {
        createProcesses();
        planProcesses();
        planProcessesBlocking();
    }

    public void createProcesses()
    {
        processes = new ArrayList<Process>();
        processesClone = new ArrayList<Process>();
        int count = 5 + rnd.nextInt(5);
        int ioThread1 = rnd.nextInt(count/2);
        int ioThread2 = rnd.nextInt(count/2)+count/2;
        for (int i = 0; i < count; i++)
        {
            Process proc;
            if(i == ioThread1 || i == ioThread2) { proc = new Process(i,rnd.nextInt(3)+1); }
            else { proc = new Process(i,0); }
            processes.add(proc);
            processesClone.add(proc.clone());
        }
    }

    public void planProcessesBlocking()//циклическое планирование, процессы блокируются
    {
        System.out.println("Циклическое планирование, при взаимодействии с устройством ввода/вывода процесс блокируется");
        System.out.println();

        int requiredTime = 0;

        ArrayList<Process> blockedProcesses = new ArrayList<Process>();

        while(processesClone.size() > 0)
        {
            Process currentProc;
            int currentQuant;
            int threadExecutionTime;

            for (int i = 0; i < processesClone.size(); i++)
            {
                currentProc = processesClone.get(i);
                if(blockedProcesses.contains(currentProc)) {
                    continue;
                }

                if(currentProc.isAwaitingIO()) {
                    System.out.print(currentProc.toString());
                    System.out.println(" был заблокирован, тк ожидает устроиства ввода/вывода");
                    System.out.println();
                    blockedProcesses.add(currentProc);
                    continue;
                }

                currentQuant = quantSize;

                System.out.println("Размер кванта до начала выполнения= " + currentQuant);

                threadExecutionTime = currentProc.run(currentQuant);
                requiredTime += threadExecutionTime;
                currentQuant -= threadExecutionTime;

                for (Process proc : blockedProcesses) {
                    proc.awaitIO(quantSize);
                }

                blockedProcesses.removeIf(proc -> !proc.isAwaitingIO());

                System.out.println("Размер кванта после выполнения= " + currentQuant);
                System.out.println();
            }

            processesClone.removeIf(proc -> proc.isCompleted());

            if(processesClone.size() > 0 && blockedProcesses.size() == processesClone.size()){
                System.out.println("Все процессы ожидают устроиства ввода/вывода, дождемся первого ожидающего процесса");
                int waitingTime = Integer.MAX_VALUE;
                for (Process proc : blockedProcesses) {
                    waitingTime = Integer.min(waitingTime, proc.getIoTime());
                }
                for (Process proc : blockedProcesses) {
                    proc.awaitIO(waitingTime);
                }
                blockedProcesses.removeIf(proc -> !proc.isAwaitingIO());
                requiredTime += waitingTime;
                System.out.println("Время ожидания : "+waitingTime);
                System.out.println();
            }
        }

        System.out.println("Требовалось времени : "+requiredTime);
        System.out.println();
    }

    public void planProcesses()//циклическое планирование, процессы не блокируются
    {
        System.out.println("Циклическое планирование, при взаимодействии с устройством ввода/вывода процесс не блокируется");
        System.out.println();

        int requiredTime = 0;

        while(processes.size() > 0)
        {
            Process currentProc;
            int currentQuant;
            int threadExecutionTime;

            for (int i = 0; i < processes.size(); i++)
            {
                currentQuant = quantSize;

                currentProc = processes.get(i);

                System.out.println("Размер кванта до начала выполнения= " + currentQuant);

                if(currentProc.isAwaitingIO()) {
                    requiredTime += currentProc.awaitIO();
                }

                threadExecutionTime = currentProc.run(currentQuant);
                requiredTime += threadExecutionTime;
                currentQuant -= threadExecutionTime;

                System.out.println("Размер кванта после выполнения= " + currentQuant);
                System.out.println();
            }
            processes.removeIf(proc -> proc.isCompleted());
        }
        System.out.println("Требовалось времени : "+requiredTime);
        System.out.println();
    }
}
