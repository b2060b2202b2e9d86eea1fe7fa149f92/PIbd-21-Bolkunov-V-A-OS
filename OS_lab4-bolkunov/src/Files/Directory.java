package Files;

import java.util.ArrayList;
import java.util.Arrays;
import FileSystem.Partition;

public class Directory extends AbstractFile {

    protected ArrayList<AbstractFile> children = new ArrayList<AbstractFile>();

    public Directory(Partition partition, Directory parentDirectory, String name) {
        super(partition, parentDirectory, name);
    }

    public Directory(Partition partition, Directory parentDirectory, String name, File[] files) {
        super(partition, parentDirectory, name);
        addDirectChildren(files);
    }

    public AbstractFile[] getChildren() {
        return children.toArray(new AbstractFile[children.size()]);
    }

    public boolean isEmpty() { return children.isEmpty(); }

    public void addDirectChildren(AbstractFile[] files) {
        children.addAll(Arrays.asList(files));
    }

    public void addDirectChildren(AbstractFile file) {
        children.add(file);
    }

    public boolean removeDirectChildren(AbstractFile file) {
        return children.remove(file);
    }

    public boolean containsDirectChildren(AbstractFile file) {
        return children.contains(file);
    }

    public boolean removeChildren(AbstractFile file) {
        if (containsDirectChildren(file)) {
            return children.remove(file);
        } else {
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) instanceof Directory
                        && ((Directory) children.get(i)).removeChildren(file)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean containsChildren(AbstractFile file) {
        if (containsDirectChildren(file)) {
            return true;
        } else {
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) instanceof Directory
                        && ((Directory) children.get(i)).containsChildren(file)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return "<DIR> " + super.toString();

    }
}
