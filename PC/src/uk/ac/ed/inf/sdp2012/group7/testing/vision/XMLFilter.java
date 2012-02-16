package uk.ac.ed.inf.sdp2012.group7.testing.vision;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class XMLFilter extends FileFilter {

	public boolean accept(File f) {
		if (f.isDirectory()) return true;
		String s = f.getName().toLowerCase();
		if(s.endsWith(".xml")) return true;
		return false;
	}

	public String getDescription() {
		return "*.xml";
	}
}