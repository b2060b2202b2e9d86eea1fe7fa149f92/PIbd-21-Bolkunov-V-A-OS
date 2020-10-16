package OS_lab1_bolkunov;

public class Main
{
    private static final Stack<Object> stack = new Stack<Object>();
    private static final SystemCore core = new SystemCore(stack);

    public static void main(String[] args)
    {
        core.printCalls();

        stack.print();
        core.call(0);

        stack.push(2);
        stack.push(3);
        stack.print();
        core.call(1);

        stack.push(2);
        stack.push(3);
        stack.print();
        core.call(2);

        stack.push(2);
        stack.push(3);
        stack.print();
        core.call(3);

        stack.push(2);
        stack.push(3);
        stack.print();
        core.call(4);
    }
}
