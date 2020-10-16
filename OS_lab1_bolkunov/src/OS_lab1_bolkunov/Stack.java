package OS_lab1_bolkunov;

public class Stack<T>
{
    private final int maxStackSize = 64;
    private Object[] array = new Object[maxStackSize];
    private int currentPointer = -1;

    public void push(T value)
    {
        if(currentPointer+1 < maxStackSize)
        {
            currentPointer++;
            array[currentPointer] = value;
        }
        else
        {
            System.out.println("Переполнение стека!");
            throw new StackOverflowError();
        }
    }

    public T peek()
    {
        if(currentPointer < 0 || currentPointer >= maxStackSize)
            return null;
        return (T)array[currentPointer];
    }

    public T pop()
    {
        if(currentPointer < 0 || currentPointer >= maxStackSize)
            return null;
        T res = (T)array[currentPointer];
        currentPointer--;
        return res;
    }

    public void print()
    {
        System.out.println("Состояние стека:");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i <= currentPointer; i++)
        {
            String str;
            try
            {
                str = array[i].toString();
            }
            catch (Exception ex)
            {
                str = "-";
            }
            sb.append(str);
            sb.append(System.lineSeparator());
        }
        System.out.println(sb.toString());
    }
}
