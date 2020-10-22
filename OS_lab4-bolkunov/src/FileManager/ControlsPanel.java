package FileManager;

import FileSystem.FileSystem;
import Files.AbstractFile;
import Files.Directory;
import Files.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ControlsPanel extends JPanel {
    private final int panelWidth = 400;
    private final int panelHeight = 50;

    private final SectorsPanel sectorsPanel;
    private final FileManagerPanel leftPanel;
    private final FileManagerPanel rightPanel;
    private final FileSystem fileSystem;

    private JPanel contentPane;

    public ControlsPanel(FileSystem fileSystem, FileManagerPanel leftPanel, FileManagerPanel rightPanel, SectorsPanel sectorsPanel) {
        this.sectorsPanel = sectorsPanel;
        this.leftPanel = leftPanel;
        this.rightPanel = rightPanel;
        this.fileSystem = fileSystem;
        redraw();
    }

    private void redrawAll() {
        leftPanel.redraw();
        rightPanel.redraw();
        sectorsPanel.redraw();
    }

    private AbstractFile getCurrentSelectedFile() {
        if (leftPanel.getSelectedAbstractFile() != null) {
            return leftPanel.getSelectedAbstractFile();
        }
        if (rightPanel.getSelectedAbstractFile() != null) {
            return rightPanel.getSelectedAbstractFile();
        }
        return null;
    }

    public void redraw() {
        this.invalidate();
        this.removeAll();

        contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(2, 5, 10, 10));

        JFormattedTextField fileNameTextField = new JFormattedTextField();
        fileNameTextField.setText("new");
        fileNameTextField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (fileNameTextField.getText().contains("/")) {
                    fileNameTextField.getText().replace("/", "");
                } else {
                    errorPopup("Ошибка, нельзя ипользовать символ '/' в названии файл или папки");
                }
            }
        });
        contentPane.add(fileNameTextField);

        JButton createFileButton = new JButton();
        createFileButton.setText("Создать файл");
        createFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File result = null;
                AbstractFile abstractFile = getCurrentSelectedFile();
                if (!fileNameTextField.getText().isEmpty() || !fileNameTextField.getText().isBlank()) {
                    if (abstractFile != null) {
                        result = fileSystem.createFile(abstractFile.getParentDirectory(), fileNameTextField.getText());
                    } else if (abstractFile == null && leftPanel.getCurrentDirectory().getChildren().length == 0) {
                        result = fileSystem.createFile(leftPanel.getCurrentDirectory(), fileNameTextField.getText());
                    } else if (abstractFile == null && rightPanel.getCurrentDirectory().getChildren().length == 0) {
                        result = fileSystem.createFile(rightPanel.getCurrentDirectory(), fileNameTextField.getText());
                    } else {
                        errorPopup("Ошибка, не ясно где требуется создать файл");
                        return;
                    }
                    fileNameTextField.setText("");
                    redrawAll();
                } else {
                    errorPopup("Ошибка, не было указано имя файла");
                }
                if (result == null) {
                    JPopupMenu popupMenu = new JPopupMenu("Ошибка");
                    JLabel jLabel = new JLabel("Произошла ошибка при создании файла");
                    popupMenu.add(jLabel);
                    popupMenu.show(leftPanel, 10, 10);
                }
            }
        });
        contentPane.add(createFileButton);

        JButton createFolderButton = new JButton();
        createFolderButton.setText("Создать папку");
        createFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Directory result = null;
                AbstractFile abstractFile = getCurrentSelectedFile();
                if(!fileNameTextField.getText().isBlank() && !fileNameTextField.getText().isEmpty()) {
                    if (abstractFile != null) {
                        result = fileSystem.createDirectory(abstractFile.getParentDirectory(), fileNameTextField.getText());
                    } else if (abstractFile == null && leftPanel.getCurrentDirectory().getChildren().length == 0) {
                        result = fileSystem.createDirectory(leftPanel.getCurrentDirectory(), fileNameTextField.getText());
                    } else if (abstractFile == null && rightPanel.getCurrentDirectory().getChildren().length == 0) {
                        result = fileSystem.createDirectory(rightPanel.getCurrentDirectory(), fileNameTextField.getText());
                    } else {
                        errorPopup("Ошибка, не ясно где требуется создать папку");
                        return;
                    }
                    fileNameTextField.setText("");
                    redrawAll();
                } else {
                    errorPopup("Ошибка, не было указано имя папки");
                }
                if (result == null) {
                    errorPopup("Произошла ошибка при создании папки");
                }
            }
        });
        contentPane.add(createFolderButton);

        JButton deleteButton = new JButton();
        deleteButton.setText("Удалить");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractFile abstractFile = getCurrentSelectedFile();
                if (abstractFile != null) {
                    if (abstractFile instanceof Directory) {
                        fileSystem.deleteDirectory((Directory) abstractFile);
                    } else if (abstractFile instanceof File) {
                        fileSystem.deleteFile((File) abstractFile);
                    }
                    redrawAll();
                } else {
                    errorPopup("Ошибка, не был выбран исходный файл");
                }
            }
        });
        contentPane.add(deleteButton);

        JButton expandFileButton = new JButton();
        expandFileButton.setText("Увеличить файл");
        expandFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean result = false;
                AbstractFile abstractFile = getCurrentSelectedFile();
                if (abstractFile != null && abstractFile instanceof File) {
                    result = fileSystem.expandFile((File) abstractFile, 1);
                    redrawAll();
                } else {
                    errorPopup("Ошибка, не был выбран исходный файл либо выьрана папка");
                    return;
                }
                if (!result) {
                    errorPopup("Произошла ошибка при увеличении файла, недостаточно места");
                }
            }
        });
        contentPane.add(expandFileButton);

        JButton reduceFileButton = new JButton();
        reduceFileButton.setText("Уменьшить файл");
        reduceFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean result = false;
                AbstractFile abstractFile = getCurrentSelectedFile();
                if (abstractFile != null && abstractFile instanceof File) {
                    result = fileSystem.reduceFile((File) abstractFile, 1);
                    redrawAll();
                } else {
                    errorPopup("Ошибка, не был выбран исходный файл либо выьрана папка");
                    return;
                }
                if (!result) {
                    errorPopup("Произошла ошибка при уменьшении файла");
                }
            }
        });
        contentPane.add(reduceFileButton);

        JButton copyButton = new JButton();
        copyButton.setText("Копировать");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean result = false;
                AbstractFile abstractFile = getCurrentSelectedFile();
                if (leftPanel.getSelectedAbstractFile() == null && abstractFile != null) {
                    if (abstractFile instanceof Directory) {
                        if((Directory) abstractFile == leftPanel.getCurrentDirectory()){
                            errorPopup("Ошибка, вы пытаетесь скопировать папку в саму себя");
                            return;
                        }
                        result = fileSystem.copyDirectory((Directory) abstractFile, leftPanel.getCurrentDirectory());
                    } else if (abstractFile instanceof File) {
                        result = fileSystem.copyFile((File) abstractFile, leftPanel.getCurrentDirectory());
                    }
                    redrawAll();
                } else if (rightPanel.getSelectedAbstractFile() == null && abstractFile != null) {
                    if (abstractFile instanceof Directory) {
                        if((Directory) abstractFile == rightPanel.getCurrentDirectory()){
                            errorPopup("Ошибка, вы пытаетесь скопировать папку в саму себя");
                            return;
                        }
                        result = fileSystem.copyDirectory((Directory) abstractFile, rightPanel.getCurrentDirectory());
                    } else if (abstractFile instanceof File) {
                        result = fileSystem.copyFile((File) abstractFile, rightPanel.getCurrentDirectory());
                    }
                    redrawAll();
                }
                if (!result) {
                    if (abstractFile == null) {
                        errorPopup("Ошибка, не был выбран исходный файл");
                    } else {
                        errorPopup("Произошла ошибка при копировании файла, возможно недостаточно места");
                    }
                }
            }
        });
        contentPane.add(copyButton);

        JButton moveButton = new JButton();
        moveButton.setText("Переместить");
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractFile abstractFile = getCurrentSelectedFile();
                if (leftPanel.getSelectedAbstractFile() == null && abstractFile != null) {
                    if (abstractFile instanceof Directory && leftPanel.getCurrentDirectory() == (Directory) abstractFile) {
                        errorPopup("Ошибка, вы пытаетесь скопировать папку в саму себя");
                        return;
                    }
                    fileSystem.moveFileOrDirectory(abstractFile, leftPanel.getCurrentDirectory());
                } else if (rightPanel.getSelectedAbstractFile() == null && abstractFile != null) {
                    if (abstractFile instanceof Directory && rightPanel.getCurrentDirectory() == (Directory) abstractFile) {
                        errorPopup("Ошибка, вы пытаетесь скопировать папку в саму себя");
                        return;
                    }
                    fileSystem.moveFileOrDirectory(abstractFile, rightPanel.getCurrentDirectory());
                }
                redrawAll();
            }
        });
        contentPane.add(moveButton);

        JButton renameButton = new JButton();
        renameButton.setText("Переименовать");
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractFile abstractFile = getCurrentSelectedFile();
                if (abstractFile != null && !fileNameTextField.getText().isBlank() && !fileNameTextField.getText().isEmpty()) {
                    fileSystem.renameFileOrDirectory(abstractFile, fileNameTextField.getText());
                    fileNameTextField.setText("");
                    redrawAll();
                } else {
                    errorPopup("Ошибка, не был выбран файл, либо не было указано имя файла");
                }
            }
        });
        contentPane.add(renameButton);

        add(contentPane);

        this.validate();
        this.repaint();
    }

    private void errorPopup(String message) {
        try {
            JPopupMenu popupMenu = new JPopupMenu("Ошибка");
            JLabel jLabel = new JLabel(message);
            popupMenu.add(jLabel);
            popupMenu.show(leftPanel, 10, 10);
        } catch (Exception ex) {
        }
    }
}
