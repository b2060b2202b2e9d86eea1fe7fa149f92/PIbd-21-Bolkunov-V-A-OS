package Files;

import FileSystem.Partition;
import FileSystem.Sector;

public class File extends AbstractFile {
    public File(Partition partition, Directory parentDirectory, String name) {
        super(partition,parentDirectory, name);
    }

    @Override
    public String toString() {
        return "<FILE> " + super.toString();
    }

    public String toContentString(){
        StringBuilder sb = new StringBuilder();
        Sector[] sectors = partition.getSectors(this);
        for (int i = 0; sectors != null && i < sectors.length; i++)
        {
            sb.append(sectors[i].toString());
        }
        return sb.toString();
    }
}
