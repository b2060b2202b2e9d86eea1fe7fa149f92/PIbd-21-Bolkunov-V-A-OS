package OS_lab1_bolkunov;

import java.util.function.Function;

public class SystemCall
{
    private final int id;
    private final String description;
    private final Object[] argsDescriptions;
    private final Function<Object[], Object> method;

    public int getID() { return id; }
    public int getArgumentsCount() { return argsDescriptions.length; }
    public String getDescription() { return  description; }

    public SystemCall(int id, String description, Object[] args, Function<Object[], Object> method)
    {
        this.id = id;
        this.description = description;
        argsDescriptions = args;
        this.method = method;
    }

    public SystemCall(int id, String description, Object[] args)
    {
        this.id = id;
        this.description = description;
        argsDescriptions =  args;
        this.method = null;
    }

    public boolean canExecute(Object[] args)
    {
        if(args.length == argsDescriptions.length)
        {
            for (int i = 0; i < args.length; i++)
            {
                if(args[i].getClass() != argsDescriptions[i].getClass() || args[i] == null)
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public void execute(Object[] args)
    {
        System.out.println("Выполнение вызова...");
        if(method != null)
        {
            System.out.println("Результат:");
            System.out.println(method.apply(args).toString());
        }
        System.out.println("Вызов выполнен!");
        System.out.println(System.lineSeparator());
    }

    public String toBaseString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ID:");
        sb.append(id);
        sb.append(System.lineSeparator());
        sb.append("Описание:");
        sb.append(description);
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    protected String toString(Object[] args)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(toBaseString());
        sb.append("Список аргументов:");
        sb.append(System.lineSeparator());
        if(args.length > 0)
        {
            for (int i = 0; i < args.length; i++)
            {
                String str;
                try {
                    str = args[i].toString();
                } catch (Exception ex) {
                    str = "Ошибка вывода";
                }
                sb.append('[');
                sb.append(i);
                sb.append("]=");
                sb.append(str);
                sb.append("; ");
            }
        }
        else
        {
            sb.append(" --отсутствуют-- ");
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(toBaseString());
        sb.append("Список принимаемых аргументов:");
        sb.append(System.lineSeparator());
        if(argsDescriptions.length > 0)
        {
            for (Object arg : argsDescriptions)
            {
                String str;
                try {
                    str = arg.getClass().toString();
                } catch (Exception ex) {
                    str = "-";
                }
                sb.append(str);
                sb.append("; ");
            }
        }
        else
        {
            sb.append(" --отсутствуют-- ");
        }
        return sb.toString();
    }
}
