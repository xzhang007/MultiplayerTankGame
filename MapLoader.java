import java.io.*;
import java.util.List;
//import java.util.LinkedList;
import java.util.ArrayList;
import java.net.*;

public class MapLoader {
	// MapLoader is a singleton class!
	private static final MapLoader mapLoader = new MapLoader();
	private BufferedReader br = null;
	private static final int SIZE = 40;
	int lineNumber = 0;
	int  columnNumber = 0;
	List<Location> wallMap = new ArrayList<Location>();
	
	public void read() {
		URL url = MapLoader.class.getResource("Resources/wallMap");
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream())); // BufferedReader should read from URL
			
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				
				char [] charArray = line.toCharArray();
				for (int i = 0; i < SIZE; i++) {
					if (charArray[i] == 'P') {
						wallMap.add(new Location(i, lineNumber, "Wall"));
					} else if (charArray[i] == 'D') {
						wallMap.add(new Location(i, lineNumber, "DestructableWall"));
					}
				}
				
				lineNumber++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<Location> getWallMap() {
		return wallMap;
	}
	
	public static MapLoader getInstance() {
		return mapLoader;
	}
}
