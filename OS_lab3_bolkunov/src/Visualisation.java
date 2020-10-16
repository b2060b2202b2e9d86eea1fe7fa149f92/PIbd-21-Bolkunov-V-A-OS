import OS_lab3_bolkunov.SystemCore;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Visualisation extends JDialog
{
    private JPanel contentPane;
    private JButton nextButton;
    private JTextPane virtualMemoryTextArea;
    private JTextPane processTextArea;
    private JTextPane physicalMemoryTextArea;
    private JButton buttonOK;
    private SystemCore core;

    public Visualisation()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        core = new SystemCore();
        processTextArea.setText(core.start());
        physicalMemoryTextArea.setText(core.getPhysicalMemoryCondition());
        virtualMemoryTextArea.setText(core.getVirtualMemoryCondition());

        nextButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                processTextArea.setText(core.next());
                physicalMemoryTextArea.setText(core.getPhysicalMemoryCondition());
                virtualMemoryTextArea.setText(core.getVirtualMemoryCondition());
            }
        });
    }

    public static void main(String[] args)
    {
        Visualisation dialog = new Visualisation();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
