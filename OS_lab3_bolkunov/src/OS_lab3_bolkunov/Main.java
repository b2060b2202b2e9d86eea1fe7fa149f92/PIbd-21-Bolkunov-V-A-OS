package OS_lab3_bolkunov;

import java.util.Scanner;

public class Main
{
    private static SystemCore core;
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        core = new SystemCore();
        System.out.println(core.start());
        System.out.println(core.getPhysicalMemoryCondition());
        System.out.println(core.getVirtualMemoryCondition());
        byte num = 1;
        while (num == 1)
        {
            System.out.println(core.next());
            System.out.println("Введите число:");
            System.out.println("1 - продолжить");
            System.out.println("Любое другое число - выход");
            num = scanner.nextByte();
            if(num != 1) break;

            System.out.println(core.getPhysicalMemoryCondition());
            System.out.println("Введите число:");
            System.out.println("1 - продолжить");
            System.out.println("Любое другое число - выход");
            num = scanner.nextByte();
            if(num != 1) break;

            System.out.println(core.getVirtualMemoryCondition());

            System.out.println("Введите число:");
            System.out.println("1 - продолжить");
            System.out.println("Любое другое число - выход");
            num = scanner.nextByte();
        }
    }
}
