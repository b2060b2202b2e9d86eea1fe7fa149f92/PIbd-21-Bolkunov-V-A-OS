package FileManager;

import FileSystem.FileSystem;
import Files.AbstractFile;
import Files.Directory;
import Files.File;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class FileManagerPanel extends JPanel {
    private static final int listWidth = 400;
    private static final int listHeight = 600;

    private static final int pathLabelWidth = 80;
    private static final int pathLabelHeight = 40;

    private SectorsPanel sectorsPanel;
    private FileManagerPanel otherPanel;

    private JPanel contentPane;
    private JPanel pathPanel;

    private final FileSystem fileSystem;

    private Directory currentDirectory;

    private JList list;

    public FileManagerPanel(FileSystem fileSystem, FileManagerPanel otherPanel) {
        super();
        this.fileSystem = fileSystem;
        currentDirectory = fileSystem.getRootDirectory();
        this.otherPanel = otherPanel;
        redraw();
    }

    public FileManagerPanel(FileSystem fileSystem) {
        this(fileSystem, null);
    }

    public void setSectorsPanel(SectorsPanel sectorsPanel) {
        this.sectorsPanel = sectorsPanel;
    }

    public void setOtherPanel(FileManagerPanel otherPanel){
        this.otherPanel = otherPanel;
    }

    public AbstractFile getSelectedAbstractFile() {
        return (AbstractFile)list.getModel().getElementAt(list.getSelectedIndex());
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public void redraw() {
        this.invalidate();
        this.removeAll();

        contentPane = new JPanel();
        //contentPane.setLayout(new GridLayout(2, 1, 10, 10));
        contentPane.setLayout(new BorderLayout());

        pathPanel = new JPanel();
        JLabel pathLabel = new JLabel(currentDirectory.toFullPathString());
        pathPanel.setLayout(new BorderLayout());
        pathPanel.add(pathLabel, BorderLayout.WEST);
        if(currentDirectory != fileSystem.getRootDirectory()) {
            JButton backButton = new JButton();
            backButton.setText("Назад");
            backButton.setSize(pathLabelWidth,pathLabelHeight);
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    currentDirectory = currentDirectory.getParentDirectory();
                    otherPanel.list.clearSelection();
                    list.clearSelection();
                    redraw();
                    if(otherPanel != null)
                        otherPanel.redraw();
                    if(sectorsPanel != null)
                        sectorsPanel.redraw();
                }
            });
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(backButton);
            pathPanel.add(buttonPanel, BorderLayout.EAST);
        }

        AbstractFile[] currentDirectoryFiles = currentDirectory.getChildren();
        list = new JList<AbstractFile>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setModel(new ListModel<AbstractFile>() {
            @Override
            public int getSize() {
                return currentDirectoryFiles.length;
            }

            @Override
            public AbstractFile getElementAt(int i) {
                if(i >= 0)
                    return currentDirectoryFiles[i];
                return null;
            }

            @Override
            public void addListDataListener(ListDataListener listDataListener) {
            }

            @Override
            public void removeListDataListener(ListDataListener listDataListener) {
            }
        });
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                otherPanel.list.clearSelection();
                if(sectorsPanel != null)
                    sectorsPanel.redraw();
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int index = list.locationToIndex(e.getPoint());
                list.setSelectedIndex(index);
                otherPanel.list.clearSelection();
                if (e.getClickCount() == 2) {
                    if (getSelectedAbstractFile() != null) {
                        if (getSelectedAbstractFile() instanceof Directory) {
                        currentDirectory = (Directory) getSelectedAbstractFile();
                        list.clearSelection();
                        redraw();
                        if (otherPanel != null)
                            otherPanel.redraw();
                        }else if (getSelectedAbstractFile() instanceof File) {
                            FileInfoDialog.showFileInfoDialog((File)getSelectedAbstractFile());
                        }
                    }
                }
                if(sectorsPanel != null)
                    sectorsPanel.redraw();
            }
        });
        list.setFixedCellWidth(listWidth);

        Dimension dimension = new Dimension();
        dimension.setSize(listWidth, pathLabelHeight);
        pathPanel.setPreferredSize(dimension);
        pathPanel.setMaximumSize(dimension);
        contentPane.add(pathPanel, BorderLayout.NORTH);
        contentPane.add(list, BorderLayout.CENTER);
        add(contentPane);

        this.validate();
        this.repaint();
    }
}
