package FileSystem;

import java.util.Arrays;
import java.util.Random;

public class Sector {
    private final Partition partition;

    private byte[] data;

    private int nextSectorID;

    public Sector(Partition partition) {
        this.partition = partition;
        nextSectorID = -1;
        resetData();
    }

    public void setNextSectorID(int nextSector) {
        this.nextSectorID = nextSector;
    }

    public int getNextSectorID() {
        return nextSectorID;
    }

    public int getID(){
        return partition.getSectorID(this);
    }

    public byte[] getData() {
        return data;
    }

    public void setData() {
        Random rnd = new Random();
        rnd.nextBytes(data);
    }

    public void resetData() {
        data = new byte[partition.sectorSize];
    }

    public void copyDataFrom(Sector source)
    {
        data = Arrays.copyOf(source.data,data.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("    Номер_сектора= ");
        sb.append(partition.getSectorID(this));
        sb.append(System.lineSeparator());
        int bytesInRow = 8;
        for (int i = 0; i < data.length; i++)
        {
            sb.append(data[i]);
            sb.append(' ');
            if((i+1)%bytesInRow == 0)
                sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
