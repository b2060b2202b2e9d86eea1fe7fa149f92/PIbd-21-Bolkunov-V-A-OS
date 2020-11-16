package OS_lab5_bolkunov;

import java.util.Random;

public class Process {
    private static final Random random = new Random();

    private static final int minExecutionTime = 30;
    private static final int maxExecutionTime = 100;

    private static final int minIOTime = 20;
    private static final int maxIOTime = 300;

    private final int pid;
    private int executionTime;

    private int ioTime;

    private Process(int pid, int executionTime, int ioTime){
        this.pid = pid;
        this.executionTime = executionTime;
        this.ioTime = ioTime;
    }

    public Process(int pid, boolean ioThread) {
        this.pid = pid;
        this.executionTime = random.nextInt(maxExecutionTime - minExecutionTime) + minExecutionTime;
        if (ioThread) {
            this.ioTime = random.nextInt(maxIOTime - minIOTime) + minIOTime;
        } else {
            this.ioTime = 0;
        }
    }

    public int getPID() {
        return pid;
    }

    public boolean isCompleted() {
        return executionTime <= 0;
    }

    public boolean isAwaitingIO() {
        return ioTime > 0;
    }

    public int awaitIO(){
        if(isAwaitingIO()) {
            System.out.print(this.toString());
            System.out.println(" ожидает устроиство ввода/вывода");
            return ioTime;
        }else{
            return 0;
        }
    }

    public int getIoTime(){
        return ioTime;
    }

    public int awaitIO(int amount){
        if(isAwaitingIO()) {
            System.out.print(this.toString());
            System.out.println(" ожидает устроиство ввода/вывода");
            if (ioTime <= amount) {
                int res = amount - ioTime;
                ioTime = 0;
                return res;
            } else {
                ioTime -= amount;
                return amount;
            }
        }else{
            return 0;
        }
    }

    public int run(int quantTime){
        System.out.print(this.toString());
        System.out.print(" время выполненния: ");
        if (executionTime >= quantTime) {
            System.out.println(quantTime);
            executionTime -= quantTime;
            return quantTime;
        } else {
            int res = executionTime;
            System.out.println(executionTime);
            executionTime = 0;
            System.out.println("Процесс завершил свое выполнение");
            return res;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Процесс PID=");
        sb.append(pid);
        return sb.toString();
    }

    public Process clone(){
        return new Process(this.pid, this.executionTime, this.ioTime);
    }
}
