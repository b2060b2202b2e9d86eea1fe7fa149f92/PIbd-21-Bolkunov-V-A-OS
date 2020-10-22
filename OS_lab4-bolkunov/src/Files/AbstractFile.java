package Files;

import FileSystem.FileSystem;
import FileSystem.Partition;

public abstract class AbstractFile {
    protected Directory parentDirectory;

    protected final Partition partition;

    private String name;

    public AbstractFile(Partition partition, Directory parentDirectory, String name) {
        this.partition = partition;
        this.name = name.replace("/","");
        this.parentDirectory = parentDirectory;
    }

    public Directory getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public Partition getPartition() {
        return partition;
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        name = newName;
    }

    public String toFullPathString()
    {
        StringBuilder sb = new StringBuilder();
        if(parentDirectory != null) {
            sb.append(parentDirectory.toFullPathString());

        }
        sb.append(name);
        sb.append('/');
        return sb.toString();
    }

    @Override
    public String toString() {
        return name;
    }
}
