package OS_lab1_bolkunov;

public class SystemCore
{
    private final Stack<Object> stack;
    private final int callCount = 5;
    private final SystemCall[] systemCalls;

    public SystemCore(Stack<Object> stack)
    {
        this.stack = stack;
        systemCalls = new SystemCall[]
        {
            new SystemCall(2, "Выводит разность двух чисел", new Object[]{1,1}, objs -> (int)objs[0] - (int)objs[1]),
            new SystemCall(4, "Выводит частное двух чисел", new Object[]{1,1}, objs -> (int)objs[0] / (int)objs[1]),
            new SystemCall(0, "Ничего не делает", new Object[]{}),
            new SystemCall(1, "Выводит сумму двух чисел", new Object[]{1,1}, objs -> (int)objs[0] + (int)objs[1]),
            new SystemCall(3, "Выводит произведение двух чисел", new Object[]{1,1}, objs -> (int)objs[0] * (int)objs[1])
        };
    }

    private SystemCall findSystemCall(int id)
    {
        for(SystemCall call : systemCalls)
        {
            if(call.getID() == id)
                return call;
        }
        return null;
    }

    public void printCalls()
    {
        System.out.println("Список вызовов:");
        for(SystemCall call : systemCalls)
        {
            if(call != null)
            {
                System.out.println(call.toString());
                System.out.println(System.lineSeparator());
            }
        }
    }

    public void call(int id)
    {
        System.out.println("Выполняю системный вызов:");
        SystemCall call = findSystemCall(id);
        Object[] args = new Object[call.getArgumentsCount()];

        for (int i = 0; i < call.getArgumentsCount(); i++)
        {
            args[i] = stack.pop();
        }

        System.out.println(call.toString(args));

        if(call.canExecute(args))
        {
            call.execute(args);
        }
        else
        {
            System.out.println("Вызов не может быть выполнен");
        }
    }
}
