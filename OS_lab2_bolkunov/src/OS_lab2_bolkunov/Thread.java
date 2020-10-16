package OS_lab2_bolkunov;

import java.util.Random;

public class Thread
{
    private static Random rnd = new Random();

    private final int tid;

    private int executionTime;

    public Thread(int tid)
    {
        this.tid = tid;
        this.executionTime = 5 + rnd.nextInt(25);
    }

    public int getExecutionTime()
    {
        return executionTime;
    }

    public int reduceExecutionTime(int amount)
    {
        int res;
        if(amount >= executionTime)
        {
            res = amount - executionTime;
            executionTime = 0;
        }
        else
        {
            res = 0;
            executionTime -= amount;
        }
        return res;
    }

    public void run()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Поток TID = ");
        sb.append(tid);
        sb.append(" Время выполнения = ");
        sb.append(executionTime);
        System.out.println(sb.toString());
    }
}
