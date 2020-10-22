package FileManager;

import FileSystem.FileSystem;
import FileSystem.Sector;
import FileSystem.Partition;
import Files.AbstractFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SectorsPanel extends JPanel {
    private static final int panelWidth = 800;
    private static final int panelHeight = 600;

    private static final int sectorWidth = 10;
    private static final int sectorHeight = 10;

    private final FileSystem fileSystem;
    private final FileManagerPanel leftPanel;
    private final FileManagerPanel rightPanel;

    private JPanel contentPane;

    public SectorsPanel (FileSystem fileSystem, FileManagerPanel leftPanel, FileManagerPanel rightPanel){
        super();
        this.fileSystem = fileSystem;
        this.leftPanel = leftPanel;
        this.rightPanel = rightPanel;
        redraw();
    }

    public void redraw(){
        this.invalidate();
        this.removeAll();

        contentPane = new JPanel();

        Partition partition = fileSystem.getPartition();
        Sector[] sectors = partition.getSectors();
        int sectorWidthCount = panelWidth/sectorWidth;
        contentPane.setLayout(new GridLayout(sectors.length/sectorWidthCount, sectorWidthCount, 1, 1));

        ArrayList<Sector> redSectors = new ArrayList<Sector>();
        if(leftPanel.getSelectedAbstractFile() != null && partition.getSectors(leftPanel.getSelectedAbstractFile()) != null){
            redSectors.addAll(Arrays.asList(partition.getSectors(leftPanel.getSelectedAbstractFile())));
        }else if(rightPanel.getSelectedAbstractFile() != null && partition.getSectors(rightPanel.getSelectedAbstractFile()) != null){
            redSectors.addAll(Arrays.asList(partition.getSectors(rightPanel.getSelectedAbstractFile())));
        }

        for (int i = 0; i < sectors.length; i++)
        {
            int row = i / sectorWidthCount;
            int column = i % sectorWidthCount;

            JPanel sectorPanel = new JPanel();

            if(redSectors.contains(sectors[i])){
                sectorPanel.setBackground(Color.red);
            }else if(!partition.isSectorFree(sectors[i])) {
                sectorPanel.setBackground(Color.blue);
            }else{
               sectorPanel.setBackground(Color.gray);
            }

            sectorPanel.setSize(sectorWidth,sectorHeight);
            contentPane.add(sectorPanel);
        }

        contentPane.setSize(panelWidth, panelHeight);
        add(contentPane);

        this.validate();
        this.repaint();
    }
}
