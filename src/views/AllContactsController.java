package views;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Contact;

public class AllContactsController implements Initializable {

    @FXML
    private TableView contactstable;
    @FXML
    private TableColumn<Contact, Integer> contactID;
    @FXML
    private TableColumn<Contact, String> firstName;
    @FXML
    private TableColumn<Contact, String> lastName;
    @FXML
    private TableColumn<Contact, String> Address;
    @FXML
    private TableColumn<Contact, String> phone;
    @FXML
    private TableColumn<Contact, String> email;
    @FXML
    private TableColumn<Contact, LocalDate> birthday;
    @FXML
    private Button createnew;
    @FXML
    TextField search;

    public static Contact c1 = new Contact("Arshpreet", "Dhaliwal", "toronto", "705-984-5014", "arshpreetdhaliwal63@gmail.com", LocalDate.of(1998, Month.SEPTEMBER, 30));

    public static Contact c2 = new Contact("Naaz", "Mann", "ontario", "789-456-1234", "hello123@gmail.com", LocalDate.of(2002, Month.DECEMBER, 14));

    public static Contact c3 = new Contact("Crystal", "Brown", "Brampton", "345-378-1234", "Friend's.emailid@gmail.com", LocalDate.of(1998, Month.MARCH, 10));

    /**
     * Method to initialize controller class , activate search bar ,and populate
     * all contacts in table view
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        firstName.setCellValueFactory(new PropertyValueFactory<Contact, String>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<Contact, String>("lastName"));
        Address.setCellValueFactory(new PropertyValueFactory<Contact, String>("address"));
        phone.setCellValueFactory(new PropertyValueFactory<Contact, String>("phone"));
        email.setCellValueFactory(new PropertyValueFactory<Contact, String>("email"));
        birthday.setCellValueFactory(new PropertyValueFactory<Contact, LocalDate>("birthday"));
        contactID.setCellValueFactory(new PropertyValueFactory<Contact, Integer>("id"));
        try {
            loadContacts();
            ObservableList<Contact> ol = contactstable.getItems();
            // filtered list for Search Bar Manipulation in table view
            FilteredList<Contact> filteredData = new FilteredList<>(ol, s -> true);
            search.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super Contact>) contact -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (contact.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (contact.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            contactstable.setItems(filteredData);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * This method will return List of Statically made contacts
     */
    public ObservableList<Contact> getContact() {
        ObservableList<Contact> ol = FXCollections.observableArrayList();
        c1.setId(1000);
        c2.setId(1001);
        c3.setId(1002);
        ol.add(c1);
        ol.add(c2);
        ol.add(c3);
        return ol;
    }

    /**
     * This method will change scene to NewContactView
     */
    public void createButtonClicked(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NewContact.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Create New Contact");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method will load contacts from Database
     */
    private void loadContacts() throws SQLException {
        ObservableList<Contact> Contacts = FXCollections.observableArrayList();
        Contacts.addAll(getContact());
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Arshpreet", "root", "123");
            statement = conn.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM Contacts");

            while (resultSet.next()) {
                Contact newContact = new Contact(resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("address"), resultSet.getString("phone"), resultSet.getString("email"), resultSet.getDate("birthday").toLocalDate());
                newContact.setId(resultSet.getInt("id"));
                Contacts.add(newContact);
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        contactstable.getItems().addAll(Contacts);
    }

    /**
     * This method will change scene to NewContactView with selected contact as
     * parameter to load details in further scene
     */
    public void editButtonclicked(ActionEvent event) throws IOException {
        Contact c = (Contact) this.contactstable.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NewContact.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        NewContactController controller = loader.getController();
        controller.setData(c);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setTitle("Edit Contact");
        stage.setScene(scene);
        stage.show();
    }

}
