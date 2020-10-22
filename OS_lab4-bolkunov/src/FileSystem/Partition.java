package FileSystem;

import Files.AbstractFile;
import Files.Directory;
import Files.File;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Partition {
    public final int sectorSize;
    public final int partitionSize;

    private HashMap<File, Integer> fileSectors; //Размещение с использованием связанного списка
    private boolean[] sectorsAvailability; //Битовый массив для отслеживания свободных секторов
    private Sector[] sectors;

    public Partition(int sectorSize, int partitionSize) {
        this.sectorSize = sectorSize;
        this.partitionSize = partitionSize;//512^2
        int sectorCount = (partitionSize - sectorSize) / sectorSize;//тк один блок на связанные списки и хранение массива свободных ячеек
        sectors = new Sector[sectorCount];
        sectorsAvailability = new boolean[sectorCount];
        for (int i = 0; i < sectorsAvailability.length; i++)
        {
            sectorsAvailability[i] = true;
        }
        fileSectors = new HashMap<File, Integer>();
        for (int i = 0; i < sectors.length; i++) {
            sectors[i] = new Sector(this);
        }
    }

    public int getSectorID(Sector sector) {
        for (int i = 0; i < sectors.length; i++) {
            if (sector == sectors[i]) {
                return i;
            }
        }
        return -1;
    }

    public Sector[] getSectors() {
        return sectors;
    }

    public Sector[] getSectors(File file) {
        if (fileSectors.containsKey(file) && file.getPartition() == this) {
            ArrayList<Sector> result = new ArrayList<Sector>();
            Sector currentSector = this.sectors[fileSectors.get(file)];
            while (currentSector.getNextSectorID() > -1) {
                result.add(currentSector);
                currentSector = sectors[currentSector.getNextSectorID()];
            }
            if (currentSector != null) {
                result.add(currentSector);
            }
            return result.toArray(new Sector[0]);
        } else {
            return null;
        }
    }

    public Sector[] getSectors(Directory directory) {
        if (directory.getPartition() == this) {
            ArrayList<Sector> result = new ArrayList<Sector>();
            AbstractFile[] abstractFiles = directory.getChildren();
            for (int i = 0; i < abstractFiles.length; i++) {
                if (abstractFiles[i] instanceof File) {
                    result.addAll(Arrays.asList(getSectors((File) abstractFiles[i])));
                } else if (abstractFiles[i] instanceof Directory) {
                    result.addAll(Arrays.asList(getSectors((Directory) abstractFiles[i])));
                }
            }
            return result.toArray(new Sector[0]);
        }
        return null;
    }

    public Sector[] getSectors(AbstractFile af) {
        if (af == null) {
            return null;
        }
        if (af instanceof Directory) {
            return getSectors((Directory) af);
        }
        if (af instanceof File) {
            return getSectors((File) af);
        }
        return null;
    }

    private Sector getLastSector(File file) {
        if (fileSectors.containsKey(file) && file.getPartition() == this) {
            Sector currentSector = this.sectors[fileSectors.get(file)];
            while (currentSector.getNextSectorID() > -1) {
                currentSector = sectors[currentSector.getNextSectorID()];
            }
            return currentSector;
        } else {
            return null;
        }
    }

    private Integer[] findFreeSectors(int sectorCount) {
        ArrayList<Integer> freeSectors = new ArrayList<Integer>();
        for (int i = 0; i < sectorsAvailability.length && freeSectors.size() < sectorCount; i++) {
            if (sectorsAvailability[i]) {
                freeSectors.add(i);
            }
        }
        return freeSectors.toArray(new Integer[0]);
    }

    public boolean allocateSectors(File file, int sectorCount) {
        if (file.getPartition() != this) {
            return false;
        }
        if (sectorCount == 0) {
            return true;
        }
        if (sectorCount < 0) {
            sectorCount = -sectorCount;
        }

        Integer[] freeSectors = findFreeSectors(sectorCount);
        if (freeSectors.length == sectorCount) {
            for (int i = 0; i < sectorCount; i++) {
                sectorsAvailability[freeSectors[i]] = false;
                if (i + 1 < sectorCount) {
                    sectors[freeSectors[i]].setNextSectorID(freeSectors[i + 1]);
                } else {
                    sectors[freeSectors[i]].setNextSectorID(-1);
                }
                sectors[freeSectors[i]].setData();
            }

            Sector lastSector = getLastSector(file);
            if (lastSector == null) {
                fileSectors.put(file, freeSectors[0]);
            } else {
                lastSector.setNextSectorID(freeSectors[0]);
            }

            return true;
        }
        return false;
    }

    public boolean disposeLastSectors(File file, int count) {
        Sector[] currentFileSectors = getSectors(file);
        if (currentFileSectors != null) {
            for (int i = 0; i < currentFileSectors.length && i < count; i++) {
                currentFileSectors[currentFileSectors.length - i - 1].resetData();
                currentFileSectors[currentFileSectors.length - i - 1].setNextSectorID(-1);
                sectorsAvailability[currentFileSectors[currentFileSectors.length - i - 1].getID()] = true;
            }
            if (count >= currentFileSectors.length) {
                fileSectors.remove(file);
            } else {
                currentFileSectors[currentFileSectors.length - count - 1].setNextSectorID(-1);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean copyFile(File source, File destination) {
        if (source == null || destination == null) {
            return false;
        }

        Sector[] sourceSectors = getSectors(source);
        Sector[] destinationSectors = getSectors(destination);
        if (destinationSectors != null && sourceSectors.length == destinationSectors.length) {
            for (int i = 0; i < sourceSectors.length; i++) {
                destinationSectors[i].copyDataFrom(sourceSectors[i]);
            }
            return true;
        } else if (destinationSectors == null || sourceSectors.length > destinationSectors.length) {
            int diff = sourceSectors.length;
            if(destinationSectors != null){
                diff -= destinationSectors.length;
            }
            if (!allocateSectors(destination, diff)) {
                return false;
            }
            return copyFile(source, destination);
        } else if (destinationSectors != null && sourceSectors.length < destinationSectors.length) {
            int diff = destinationSectors.length - sourceSectors.length;
            if (!disposeLastSectors(destination, diff)) {
                return false;
            }
            return copyFile(source, destination);
        }
        return false;
    }


    public int getFreeSpaceAmount() {
        int res = 0;
        for (int i = 0; i < sectorsAvailability.length; i++) {
            if (sectorsAvailability[i]) {
                res++;
            }
        }
        return res * sectorSize;
    }

    public boolean isSectorFree(Sector sector){
        return sectorsAvailability[sector.getID()];
    }
}
