/**
 * Class for generating common front end UI elmenets, e.g. comboboxes, sliders
 * To ensure consistency of front end design
 * @author susiechoi 
 */

package authoring.frontend;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import authoring.frontend.exceptions.MissingPropertiesException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UIFactory {

	public static final String DEFAULT_BACK_IMAGE = "images/back.gif"; 
	public static final char[] NOT_ALLOWED_FILEPATH = {'*', '.', '\\', '/','\"', '[', ']', ':', ';', '|', '=', ',', ' '};
	
	/**
	 * Makes Text object for displaying titles of screens to the user
	 * @param titleText is String text displayed as title
	 * @return Text object with this String
	 */
	public Text makeScreenTitleText(String titleText) {
		Text screenTitle = new Text(titleText);
		screenTitle.setId("screenTitle");
		return screenTitle;
	}

	public Button makeImageButton(String id, Image buttonImage) {
		Button newButton = new Button();
		ImageView buttonImageView = new ImageView(buttonImage);
		newButton.setGraphic(buttonImageView);
		newButton.setId(id);
		return newButton; 
	}
	public TextField makeTextField(String description) {
		TextField tf = new TextField();
		tf.setId(description);
		return tf;
	}

	public Button makeTextButton(String id, String buttonText) {
		Button newButton = new Button(buttonText);
		newButton.setId(id);
		return newButton; 
	}

	public ComboBox<String> makeTextDropdownSelectAction(String id, List<String> dropdownOptions, EventHandler<ActionEvent> chooseAction,
			EventHandler<ActionEvent> noChoiceAction, String prompt){
		ComboBox<String> dropdown = makeTextDropdown(id, dropdownOptions);
		dropdown.setOnAction( e ->{
			if(!dropdown.getValue().equals(prompt)){
				chooseAction.handle(e);
			}
			else {
				noChoiceAction.handle(e);
			}
		});
		return dropdown;
	}

	public ComboBox<String> makeTextDropdown(String id, List<String> dropdownOptions) {
		ObservableList<String> options = FXCollections.observableArrayList(dropdownOptions);
		ComboBox<String> newDropdown = new ComboBox<String>(options);
		newDropdown.getSelectionModel().selectFirst(); 
		newDropdown.setId(id);
		return newDropdown; 
	}




	protected Slider setupSlider(String id, String promptString, int sliderMax) {
		Slider slider = new Slider(0, sliderMax, (0 + sliderMax) / 2);
		Text sliderValue = new Text(String.format("%03d", (int)(double)slider.getValue()));
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				sliderValue.setText(String.format("%03d", (int)(double)new_val));
			}
		});
		return slider; 
	}

	public ScrollPane makeTextScrollPane(String id, List<String> options) {
		ScrollPane newPane = new ScrollPane();
		String allOptions = "";
		for (String s : options) {
			allOptions += s+"\n";
		}
		newPane.setContent(new Text(allOptions));
		newPane.setId(id);
		return newPane; 
	}

	public ScrollPane makeImageScrollPane(String id, File[] files) {
		ScrollPane newPane = new ScrollPane();
		VBox imageConsolidator = new VBox();
		for (File f : files) {
			ImageView oneImageView = new ImageView();
			oneImageView.setImage(new Image(f.toURI().toString()));
			imageConsolidator.getChildren().add(oneImageView);
		}
		newPane.setContent(imageConsolidator);
		newPane.setId(id);
		return newPane; 
	}

	public VBox setupFileChooser(String id, String newFilePrompt, String newFileNamePrompt, String propertiesFilepath,
			EventHandler<ActionEvent> action, Map<String, String> keysAndVals) {
			VBox vbox = new VBox();
			TextField imageNameEntry = new TextField();
			HBox imageNamer = addPromptAndSetupHBox("",imageNameEntry, newFileNamePrompt);
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extensionFilter = 
                    new FileChooser.ExtensionFilter("image files","*.png");
            fileChooser.getExtensionFilters().add(extensionFilter);
			Button filePrompt = makeTextButton(id, newFilePrompt);
			filePrompt.setDisable(true);
			imageNameEntry.setOnAction(e -> {filePrompt.setDisable(false);});
			filePrompt.setOnAction(e -> {
				String imageName = imageNameEntry.getText();
				if(!imageName.equals(null)) {
					for(int k = 0; k< NOT_ALLOWED_FILEPATH.length; k+=1) {
						imageName.replace(NOT_ALLOWED_FILEPATH[k], 'a');
					}
					
					File file = fileChooser.showOpenDialog(new Stage());
					file.getAbsolutePath();
					System.out.println("here");
					File fileCopy = new File("images/" + imageName + ".png");
					System.out.println("not here");
					try{
						
						Files.copy(file.toPath(), fileCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
					catch(IOException e2) {
						//TODO: error
					}
					keysAndVals.put(imageName, fileCopy.getPath());
					PropertiesWriter writer = new PropertiesWriter(propertiesFilepath, keysAndVals);
					writer.write();
					action.handle(e);
				}
			});
			vbox.setId(id);
			vbox.getChildren().add(imageNamer);
			vbox.getChildren().add(filePrompt);
			return vbox;
	}
	
	public VBox setupSelector(PropertiesReader propertiesReader, String description, String propertiesFilepath,
			String newFilePrompt, String newFileNamePrompt, ComboBox<String> dropdown) throws MissingPropertiesException{
			VBox vb = new VBox();
			Map<String, String> options = propertiesReader.read(propertiesReader.loadProperties(propertiesFilepath));
			ArrayList<String> optionsList = new ArrayList<String>(options.keySet());
			VBox fc = setupFileChooser("", newFilePrompt, newFileNamePrompt, propertiesFilepath, e -> {
				optionsList.clear();
				Map<String, String> options2 = new HashMap<>();
				try {
				options2 = propertiesReader.read(propertiesReader.loadProperties(propertiesFilepath));
				}
				catch(MissingPropertiesException e2) {
					e2.printStackTrace();
					//TODO: temporary - error like in SLOGO??
				}
				ArrayList<String> optionsList2 = new ArrayList<String >(options2.keySet());
				optionsList.addAll(optionsList2);
				dropdown.getItems().clear();
				dropdown.getItems().addAll(optionsList);
				}, options);
			vb.getChildren().add(dropdown);
			vb.getChildren().add(fc);
			return vb;
	}

	public HBox setupImageSelector(PropertiesReader propertiesReader, String description, String propertiesFilepath, double imageSize,
			String loadImagePrompt, String newImagePrompt, String newImageNamePrompt, ComboBox<String> dropdown) throws MissingPropertiesException {
		Map<String, Image> enemyImageOptions = propertiesReader.keyToImageMap(propertiesFilepath, imageSize, imageSize);
		ArrayList<Image> images = new ArrayList<Image>(enemyImageOptions.values()); 
		ImageView imageDisplay = new ImageView(); 
		imageDisplay.setImage(images.get(0));
		VBox selector = setupSelector(propertiesReader, description, propertiesFilepath, newImagePrompt, newImageNamePrompt, dropdown);
		dropdown.setOnAction(e ->
		{try{
		imageDisplay.setImage(new Image((new File(propertiesReader.findVal(propertiesFilepath, dropdown.getValue())).toURI().toString()), imageSize, imageSize, false, false));
		}
		catch(Exception e2) {
			System.out.println("Here's the issue!");
			e2.printStackTrace();
			
			//TODO: error!!
		};
		});
		HBox hb = new HBox();
		hb.getChildren().add(selector);
		hb.getChildren().add(imageDisplay);
		return hb; 
	}
//	public HBox setupImageSelector(PropertiesReader propertiesReader, String description, String propertiesFilepath, double imageSize,
//			String loadImagePrompt, String imagePrompt) throws MissingPropertiesException {
//		Map<String, Image> enemyImageOptions;
//		enemyImageOptions = propertiesReader.keyToImageMap(propertiesFilepath, imageSize, imageSize);
//		ArrayList<String> imageNames = new ArrayList<String>(enemyImageOptions.keySet());
//		imageNames.add(loadImagePrompt);
//
//		final ArrayList<Image> images = new ArrayList<Image>(enemyImageOptions.values()); 
//		ImageView imageDisplay = new ImageView(); 
//		imageDisplay.setImage(images.get(0));
//		ComboBox<String> imageOptionsDropdown = makeTextDropdown("", imageNames);
//		imageOptionsDropdown.getSelectionModel().selectFirst();
//
//		HBox imageSelect = new HBox();
//		Text prompt = new Text(imagePrompt);
//
//		final FileChooser fileChooser = new FileChooser();
//		imageSelect.getChildren().add(prompt);
//		imageSelect.getChildren().add(imageOptionsDropdown);
//
//		imageOptionsDropdown.getSelectionModel().selectedIndexProperty().addListener(( arg0, arg1,  arg2) ->{
//			if ((int) arg2 == imageNames.size()-1) {
//				File file = fileChooser.showOpenDialog(new Stage());
//				imageDisplay.setImage(new Image(file.toURI().toString(), imageSize, imageSize, false, false));
//			}
//			else {
//				imageDisplay.setImage(images.get((int) arg2));
//			}
//		});
//		imageSelect.getChildren().add(imageDisplay);
//
//		return imageSelect; 
//	}
	public HBox addPromptAndSetupHBox(String id, Node node, String prompt) {
		HBox hbox = new HBox();
		hbox.setId(id);
		Text promptText = new Text(prompt);
		hbox.getChildren().add(promptText);
		hbox.getChildren().add(node);
		return hbox;	
	}
	public void applyTextFieldFocusAction(Scene screen, TextField textField) {
		screen.setOnMousePressed(event -> {
			if (!textField.equals(event.getSource())) {
				textField.getParent().requestFocus();
			}
		});
	}

	
	public Button setupBackButton(AuthoringView view, Screen currentScreen) {
		Image backbuttonImage = new Image((new File(DEFAULT_BACK_IMAGE)).toURI().toString(), 60, 40, true, false); // TODO move to css
		Button backButton = makeImageButton("backButton",backbuttonImage);
		backButton.setOnMouseClicked((event) -> { 
			view.goBackFrom(currentScreen);
		}); 
		return backButton; 
	}

}