package models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
/**
 *
 * @ArshPreet
 */
public class Contact {

    private int id;
    private String firstName, lastName, address, phone, email;
    private LocalDate birthday;
    private File imageFile;

    public Contact(String firstName, String lastName, String address, String phone, String email, LocalDate birthday) {
        setFirstName(firstName);
        setLastName(lastName);
        setAddress(address);
        setPhone(phone);
        setEmail(email);
        setBirthday(birthday);
        setImageFile(new File("./src/images/defaultContact.png"));
    }

    public Contact(String firstName, String lastName, String address, String phone, String email, LocalDate birthday, File imageFile) throws IOException {
        this(firstName, lastName, address, phone, email, birthday);
        setImageFile(imageFile);
        copyImageFile();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName.isEmpty()) {
            throw new IllegalArgumentException("First Name field cannot Be Empty");
        } else {
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName.isEmpty()) {
            throw new IllegalArgumentException("last Name field cannot Be Empty");
        } else {
            this.lastName = lastName;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address.isEmpty()) {
            throw new IllegalArgumentException("Address field cannot Be Empty");
        } else {
            this.address = address;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone.matches("[2-9]\\d{2}[-.]?\\d{3}[-.]\\d{4}")) {
            this.phone = phone;
        } else {
            throw new IllegalArgumentException("Phone numbers must be in the pattern NXX-XXX-XXXX");
        }

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email.isEmpty()) {
            throw new IllegalArgumentException("E-mail field cannot Be Empty");
        } else {
            this.email = email;
        }

    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        if (birthday.isBefore(LocalDate.now())) {
            this.birthday = birthday;
        } else {
            throw new IllegalArgumentException("Birthday Cannot be After today's Date");
        }
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

        public void copyImageFile() throws IOException {
        Path path = imageFile.toPath();

        String uniqueFileName = getUniqueFileName(imageFile.getName());

        Path path1 = Paths.get("./src/images/" + uniqueFileName);

        Files.copy(path, path1, StandardCopyOption.REPLACE_EXISTING);

        imageFile = new File(path1.toString());
    }

    private String getUniqueFileName(String FileName) {
        String Name;

        SecureRandom sr = new SecureRandom();

        do {
            Name = "";

            for (int count = 1; count <= 32; count++) {
                int c;

                do {
                    c = sr.nextInt(123);
                } while (!isAValidCharacter(c));

                Name = String.format("%s%c", Name, c);
            }
            Name += FileName;

        } while (!uniqueFileName(Name));

        return Name;
    }

    public boolean uniqueFileName(String fileName) {
        File dir = new File("./src/images/");

        File[] content = dir.listFiles();

        for (File file : content) {
            if (file.getName().equals(fileName)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAValidCharacter(int i) {

        if (i >= 48 && i <= 57) {
            return true;
        }

        if (i >= 65 && i <= 90) {
            return true;
        }

        if (i >= 97 && i <= 122) {
            return true;
        }

        return false;
    }

    public void Save() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Arshpreet", "root", "123");

            String sql = "insert into contacts(firstName,lastName,address,email,phone,birthday,image) Values(?,?,?,?,?,?,?)";

            preparedStatement = conn.prepareStatement(sql);
            Date date = Date.valueOf(birthday);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phone);
            preparedStatement.setDate(6, date);
            preparedStatement.setString(7, imageFile.toString());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (conn != null) {
                conn.close();
            }
        }
    }

    public void update() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Arshpreet", "root", "123");

            String sql = "Update contacts Set firstName = ? , lastName = ?, address = ?, email= ?, phone= ?, birthday= ?, image = ?  Where id = " + id;

            preparedStatement = conn.prepareStatement(sql);
            Date date = Date.valueOf(birthday);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phone);
            preparedStatement.setDate(6, date);
            preparedStatement.setString(7, imageFile.getName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (conn != null) {
                conn.close();
            }
        }
    }

}
