package frontend;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import authoring.frontend.exceptions.MissingPropertiesException;
import gameplayer.ScreenManager;

public class PromptReader {
    private final String DEFAULT_LANGUAGE_FILEPATH = "languages/";
    private final String DEFAULT_PROMPT_FILENAME = "/Prompts.properties";
    private final String DEFAULT_PROMPT_KEY = "PromptDNE";
    private final String DEFAULT_LANGUAGE = "English";
    public static final String DEFAULT_NO_FILE_MESSAGE = "No language files detected!";
    private String language = "English";
    private final PropertiesReader PROP_READER;
    private final View SCREEN_MANAGER;
    private Properties languageProperty;
    private Properties errorProperty;
    private Map<String, String> languageMap;
    private Map<String, String> errorMap;


    public PromptReader(String languageIn, View screenManager) {
	PROP_READER = new PropertiesReader();
	SCREEN_MANAGER = screenManager;
	languageMap = new TreeMap<String,String>();
	errorMap = new TreeMap<String,String>();
	languageProperty = new Properties();
	updateLanguage(languageIn);
    }

    public String resourceDisplayText(String key) {
	if(languageMap.containsKey(key)) {
	    return languageMap.get(key);
	}
	else if(languageMap.containsKey(DEFAULT_PROMPT_KEY)) {
	    return languageMap.get(DEFAULT_PROMPT_KEY);
	}
	return ""; //TODO make this reasonable
    }

    public void updateLanguage(String languageIn) {
	language = languageIn;
	try {
	    languageProperty = PROP_READER.loadProperties(DEFAULT_LANGUAGE_FILEPATH + language +DEFAULT_PROMPT_FILENAME );
	} catch (MissingPropertiesException e) {
	    try {
		languageProperty = PROP_READER.loadProperties(DEFAULT_LANGUAGE_FILEPATH + DEFAULT_LANGUAGE +DEFAULT_PROMPT_FILENAME );
	    } catch (MissingPropertiesException e1) {
		SCREEN_MANAGER.loadErrorScreen(DEFAULT_NO_FILE_MESSAGE);
	    }
	}
	languageMap = PROP_READER.read(languageProperty);
    }
    
    public void updateProperty(String languageIn) {
	language = languageIn;
	try {
	    errorProperty = PROP_READER.loadProperties(DEFAULT_LANGUAGE_FILEPATH + language +DEFAULT_PROMPT_FILENAME );
	} catch (MissingPropertiesException e) {
	    try {
		errorProperty = PROP_READER.loadProperties(DEFAULT_LANGUAGE_FILEPATH + DEFAULT_LANGUAGE +DEFAULT_PROMPT_FILENAME );
	    } catch (MissingPropertiesException e1) {
		SCREEN_MANAGER.loadErrorScreen(DEFAULT_NO_FILE_MESSAGE);
	    }
	}
	errorMap = PROP_READER.read(errorProperty);
    }

}