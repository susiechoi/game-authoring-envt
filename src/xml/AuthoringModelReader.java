package xml;

import java.io.File;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import authoring.AuthoringModel;

/**
 * Reads an XML document using XStream to reconstruct an AuthoringModel object to either be further edited by authoring environment or to create a game engine
 * in the game play environment
 * 
 * @author Brendan Cheng
 *
 */

public class AuthoringModelReader implements XMLReader {

	private XStream parser;
	
	public AuthoringModelReader() {
		parser = new XStream(new StaxDriver());
	}
	
	@Override
	public AuthoringModel createModel(String filename) {
	    File f = new File("SavedModels/" + filename + ".xml");
	    AuthoringModel g = (AuthoringModel) parser.fromXML(f);
	    return g;
	}

}
