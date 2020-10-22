package FileSystem;

import Files.AbstractFile;
import Files.Directory;
import Files.File;

public class FileSystem {
    private final Partition partition;
    private final Directory rootDirectory;

    public FileSystem(int sectorSize, int partitionSize) {
        partition = new Partition(sectorSize, partitionSize);
        rootDirectory = new Directory(partition, null, " ");
    }

    public Directory getRootDirectory() {
        return rootDirectory;
    }

    public Partition getPartition() {
        return partition;
    }

    public Directory createDirectory(Directory directory, String name) {
        if (name == null || name == "")
            name = "Новая папка";
        Directory res = new Directory(partition, directory, name);
        directory.addDirectChildren(res);
        return res;
    }

    public File createFile(Directory directory, String name) {
        if (name == null || name == "")
            name = "Новый файл";
        File res = new File(partition, directory, name);
        directory.addDirectChildren(res);
        return res;
    }

    public boolean expandFile(File file, int count) {
        return partition.allocateSectors(file, count);
    }

    public boolean reduceFile(File file, int count) {
        return partition.disposeLastSectors(file, count);
    }

    public boolean copyFile(File sourceFile, Directory parent) {
        File newFile;
        if (parent == sourceFile.getParentDirectory()) {
            newFile = createFile(parent, sourceFile.getName() + " Копия");
        } else {
            newFile = createFile(parent, sourceFile.getName());
        }
        return partition.copyFile(sourceFile, newFile);
    }

    public boolean copyDirectory(Directory sourceDirectory, Directory destinationFolder) {
        if (getPhysicalSize(sourceDirectory) <= partition.getFreeSpaceAmount()) {
            Directory newDirectory;
            if (sourceDirectory.getParentDirectory() == destinationFolder) {
                newDirectory = createDirectory(destinationFolder, sourceDirectory.getName() + " Копия");
            } else {
                newDirectory = createDirectory(destinationFolder, sourceDirectory.getName());
            }

            AbstractFile[] children = sourceDirectory.getChildren();
            for (int i = 0; i < children.length; i++) {
                if(children[i] instanceof File){
                    copyFile((File)children[i], newDirectory);
                }else if(children[i] instanceof Directory) {
                    copyDirectory((Directory) children[i], newDirectory);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void moveFileOrDirectory(AbstractFile file, Directory destinationParent) {
        file.getParentDirectory().removeDirectChildren(file);
        AbstractFile[] abstractFiles = destinationParent.getChildren();
        for(int i = 0; i < abstractFiles.length; i++){
            if(abstractFiles[i].getName().equals(file.getName())){
                file.rename(abstractFiles[i].getName()+" Перемещенный");
                break;
            }
        }
        destinationParent.addDirectChildren(file);
        file.setParentDirectory(destinationParent);
    }

    private int getPhysicalSize(Directory directory) {
        int res = 0;
        AbstractFile[] children = directory.getChildren();
        for (int i = 0; i < children.length; i++) {
            res += getPhysicalSize(children[i]);
        }
        return res;
    }

    private int getPhysicalSize(File file) {
        return partition.getSectors(file).length * partition.sectorSize;//в байтах
    }

    public int getPhysicalSize(AbstractFile af) {
        if (af instanceof File) {
            return getPhysicalSize((File) af);
        } else if (af instanceof Directory) {
            return getPhysicalSize((Directory) af);
        }
        return 0;
    }

    public void renameFileOrDirectory(AbstractFile abstractFile, String newName) {
        abstractFile.rename(newName);
    }

    public void deleteFile(File file) {
        file.getParentDirectory().removeDirectChildren(file);
        partition.disposeLastSectors(file, partition.partitionSize/partition.sectorSize);
    }

    public void deleteDirectory(Directory directory){
        directory.getParentDirectory().removeDirectChildren(directory);
        AbstractFile[] children = directory.getChildren();
        for (int i = 0; i < children.length; i++) {
            if(children[i] instanceof File){
                deleteFile((File)children[i]);
            }else if(children[i] instanceof Directory) {
                deleteDirectory((Directory) children[i]);
            }
        }
    }
}
