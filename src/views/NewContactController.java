package views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import models.Contact;

/**
 * FXML controller class
 *
 * @ArshPreet
 */

public class NewContactController implements Initializable {
   //configure the table
    @FXML
    private Label error;
    @FXML
    private DatePicker birthday;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField address;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private Label heading;
    private File imageFile;
    Boolean imageFileChanged;
    Contact c;

    /**
     * This method will Initialize Controller class
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        birthday.setValue(LocalDate.now().minusYears(18));
        imageFileChanged = false;
        try {
            imageFile = new File("./src/images/defaultContact.png");
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * This method will Open dialog box to select Image for contact
     */
    public void chooseImageButtonPushed(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        // Filter that will be used to select certain type of image
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("Image (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("Image (*.png)", "*.png");
        fileChooser.getExtensionFilters().addAll(filter1, filter2);
        // To set default open directory
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);

        fileChooser.setInitialDirectory(userDirectory);

        File tempImage = fileChooser.showOpenDialog(stage);

        if (tempImage != null) {
            imageFile = tempImage;

            if (imageFile.isFile()) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(imageFile);
                    Image img = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageView.setImage(img);
                    imageFileChanged = true;
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * This method will Save/Update Data in Database
     */
    public void saveButtonClicked(ActionEvent event) throws IOException, SQLException {
        try {
            if (c != null) {
                c.setAddress(address.getText());
                c.setBirthday(birthday.getValue());
                c.setEmail(email.getText());
                c.setFirstName(firstName.getText());
                c.setLastName(lastName.getText());
                c.setPhone(phone.getText());
                c.setId(c.getId());
                if (imageFileChanged) {
                    c.setImageFile(imageFile);
                }
                c.copyImageFile();
                c.update();
            } else {
                if (imageFileChanged) {
                    c = new Contact(firstName.getText(), lastName.getText(), address.getText(), phone.getText(), email.getText(), birthday.getValue(), imageFile);
                } else {
                    c = new Contact(firstName.getText(), lastName.getText(), address.getText(), phone.getText(), email.getText(), birthday.getValue());
                }
                c.Save();
            }
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AllContacts.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("All Contacts");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            error.setText(ex.getMessage());
        }

    }

    /**
     * This method will return scene to All COntacts View
     */
    public void cancelButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AllContacts.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("All Contacts");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method will Set Data of selected contact to edit in NewContact Scene
     */
    public void setData(Contact c) {
        this.c = c;
        this.firstName.setText(c.getFirstName());
        this.lastName.setText(c.getLastName());
        this.birthday.setValue(c.getBirthday());
        this.phone.setText(c.getPhone());
        this.address.setText(c.getAddress());
        this.email.setText(c.getEmail());
        this.heading.setText("Edit Contact");
        try {
            String imgLocation = ".\\src\\images\\" + c.getImageFile().getName();
            imageFile = new File(imgLocation);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(img);
            this.imageFileChanged = true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
