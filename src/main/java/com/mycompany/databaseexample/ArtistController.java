package com.mycompany.databaseexample;



import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class ArtistController implements Initializable {

    @FXML
    private TableView tableView;

    @FXML
    private VBox vBox;

    @FXML
    private TextField nameTextField, genreTextField; //where data gets entered by user

    @FXML
    Label footerLabel;
    @FXML
    //TableColumn id = new TableColumn("ID");

    @Override
     public void initialize(URL location, ResourceBundle rb) {
        try {
            loadData();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        intializeColumns();
        createSQLiteTable();
    }

    //the database below is what was connected to my personal laptop, i used that database to make the non repeatable read transaction
    //String databaseURL = "jdbc:sqlserver://localhost:1433;databaseName=CustomersDatabase;user=Jalaun2;password=sa;";
    //the database below is the one that Kiara is using, I don't want to change it and i'm not sure what to replace it with
    String databaseURL = "jdbc:sqlite:src/main/resources/com/mycompany/databaseexample/music.db";

    /* Connect to a sample database
     */
    private ObservableList<Artist> data; //object of records in DB

    /*
       ArrayList: Resizable-array implementation of the List interface. 
       Implements all optional list operations, and permits all elements, including null.
       ObservableList: A list that allows listeners to track changes when they occur
    */
    
    
    public ArtistController() throws SQLException {
        this.data = FXCollections.observableArrayList(); //a moveable list of artists
    }

   // /*
    private void intializeColumns() {
    TableColumn id = new TableColumn("ID");
    id.setMinWidth(50);
    id.setCellValueFactory(new PropertyValueFactory<Artist, Integer>("ID"));
    
    TableColumn name = new TableColumn("Name");
    name.setMinWidth(450);
    name.setCellValueFactory(new PropertyValueFactory<Artist, String>("name"));

    TableColumn genre = new TableColumn("Genre");
    genre.setMinWidth(100);
    genre.setCellValueFactory(new PropertyValueFactory<Artist, String>("genre"));
    
    tableView.setItems(data);
    tableView.getColumns().addAll(id, name, genre);

        //tableView.setOpacity(0.3);
        ///* Allow for the values in each cell to be changable 
}


    public void loadData() throws SQLException {

        Connection conn = null;
        Statement stmt = null;
 
        try {

            // create a connection to the database
            conn = DriverManager.getConnection(databaseURL);

            System.out.println("Connection to SQLite has been established.");
            String sql = "SELECT * FROM Artists;"; //selects everything, can change to only show a few feilds (important fields)
            // Ensure we can query the actors table
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

         while (rs.next()) {
                Artist artist;
                artist = new Artist(rs.getInt("id"), rs.getString("name"), rs.getString("genre"));
                System.out.println(artist.getID() + " - " + artist.getName() + " - " + artist.getGenre());
                data.add(artist);
            }
            rs.close(); //if you open a connection should close it too
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void drawText() {
        //Drawing a text 
        Text text = new Text("The Artist Database");
 
        text.setFont(Font.font("Edwardian Script ITC", 55));

        Stop[] stops = new Stop[]{
            new Stop(0, Color.DARKSLATEBLUE),
            new Stop(1, Color.RED)
        };
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        //Setting the linear gradient to the text 
        text.setFill(linearGradient);
        // Add the child to the grid
        vBox.getChildren().add(text);

    }

    /**
     * Insert a new row into the artists table
     *
     * @param name
     * @param genre

     * @throws java.sql.SQLException
     */
    public void insertArtist(String name, String genre) throws SQLException {
        int last_inserted_id = 0;
        Connection conn = null;
        try {
            // create a connection to the database

            conn = DriverManager.getConnection(databaseURL);

            System.out.println("Connection to SQLite has been established.");

            System.out.println("Inserting one record!");

          
            String sql = "INSERT INTO artists(name,genre) VALUES(?,?)";
            
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, genre);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                last_inserted_id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        //System.out.println("last_inserted_id " + last_inserted_id);
  
        System.out.println("last_inserted_id " + last_inserted_id);
        data.add(new Artist(last_inserted_id, name, genre));
    }

    @FXML
    public void handleAddArtist(ActionEvent actionEvent) {

        //System.out.println("Title: " + titleTextField.getText() + "\nArtist: " + artistTextField.getText());//only prints in console (user doesn't see)
       
        System.out.println("Name: " + nameTextField.getText() + "\nGenre: " + genreTextField.getText());
        
        try {
            // insert a new rows
            insertArtist(nameTextField.getText(), genreTextField.getText());
            System.out.println("Data was inserted Successfully");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        nameTextField.setText("");
        genreTextField.setText("");
        footerLabel.setText("Record inserted into table successfully!");
    }

      private void createSQLiteTable() {
        String sql = "CREATE TABLE IF NOT EXISTS artists (\n"
                + " id integer PRIMARY KEY,\n"
                + " name varchar(100),\n"
                + " genre varchar(50)"
                + ");";

        try (Connection conn = DriverManager.getConnection(databaseURL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table Created Successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteArtist(int id, int selectedIndex) { //id to remove from DB, selectedIndex to remove from table

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(databaseURL);
            String sql = "DELETE FROM Artists WHERE id=" + Integer.toString(id);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {

            tableView.getItems().remove(selectedIndex); //selectedIndex is row from table view (id won't match the current row)
            System.out.println("Record Deleted Successfully");
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    @FXML
    private void handleDeleteArtist(ActionEvent event) throws IOException {
        System.out.println("Delete Artist");
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println(index);
                Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + artist.getID());
                System.out.println("Name: " + artist.getName());
                System.out.println("Genre: " + artist.getGenre());
                deleteArtist(artist.getID(), selectedIndex);
            }

        }
    }

    Integer index = -1;

    @FXML
    private void showArtistData() {

        index = tableView.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }

        System.out.println("showRowData");
        System.out.println(index);
        Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
        System.out.println("ID: " + artist.getID());
        System.out.println("Name: " + artist.getName());
        System.out.println("Genre: " + artist.getGenre());

        nameTextField.setText(artist.getName());
        genreTextField.setText(artist.getGenre());

        String content = "Id= " + artist.getID() + "\nName= " + artist.getName() + "\nGenre= " + artist.getGenre();

    }


    @SuppressWarnings("empty-statement")
    public ObservableList<Artist> searchArtist(String _name, String _genre) throws SQLException {
        ObservableList<Artist> searchResult = FXCollections.observableArrayList();
        // read data from SQLite database
        createSQLiteTable();
      String sql = "Select * from Artists where true";
        if (!_name.isEmpty()) {
            sql += " and name like '%" + _name + "%'";
        }
        if (!_genre.isEmpty()) {
            sql += " and genre ='" + _genre + "'";
        }
        System.out.println(sql);
        try (Connection conn = DriverManager.getConnection(databaseURL);
                Statement stmt = conn.createStatement()) {
            // create a ResultSet

            ResultSet rs = stmt.executeQuery(sql);
            // checking if ResultSet is empty
            if (rs.next() == false) {
                System.out.println("ResultSet in empty");
            } else {
                // loop through the result set
                do {
                    
                    int recordId = rs.getInt("id");
                    String name = rs.getString("name");
                    String genre = rs.getString("genre");

                    
                    Artist artist = new Artist(recordId, name, genre);
                    searchResult.add(artist);
                    
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return searchResult;
    }

    @FXML
    private void handleSearchArtist(ActionEvent event) throws IOException, SQLException {
        String _name = nameTextField.getText().trim();
        String _genre = genreTextField.getText().trim();
        ObservableList<Artist> tableItems = searchArtist(_name, _genre);
        tableView.setItems(tableItems);
    }

    @FXML
    private void handleShowAllArtists(ActionEvent event) throws IOException, SQLException {
        tableView.setItems(data);

    }

    /**
     * Update a record in the artists table
     *
     * @param name
     * @param genre

     * @throws java.sql.SQLException
     */
    public void updateArtist(String name, String genre, int selectedIndex, int id) throws SQLException {


    Connection conn = null;
    try {
        // create a connection to the database
conn = DriverManager.getConnection(databaseURL);
            String sql = "UPDATE Artists SET name = ?, genre = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, genre);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    } finally {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

}


   

    @FXML
    private void handleUpdateArtist(ActionEvent event) throws IOException, SQLException {

         if (tableView.getSelectionModel().getSelectedItem() != null) {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);
            if (selectedIndex >= 0) {
                System.out.println(index);
                Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + artist.getID());
                try {
                    updateArtist(nameTextField.getText(), genreTextField.getText(), selectedIndex, artist.getID());
                    System.out.println("Record updated successfully!");
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }
                
                nameTextField.setText("");
                genreTextField.setText("");
 
                footerLabel.setText("Record updated successfully!");
                data.clear();
                loadData();
                tableView.refresh();
            }
        }
    }

    @FXML
    private void sidebarShowAllArtists() {
        tableView.setItems(data);
    }

    @FXML
    private void sidebarAddNewArtist() {
System.out.println("Name: " + nameTextField.getText() + "\nGenre: " + genreTextField.getText());
        try {
            insertArtist(nameTextField.getText(), genreTextField.getText());
            System.out.println("Data was inserted Successfully");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        nameTextField.setText("");
        genreTextField.setText("");

        footerLabel.setText("Record inserted into table successfully!");
    }  
      
    @FXML
    private void sidebarDeleteArtist() {
         System.out.println("Delete Artist");
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);
            if (selectedIndex >= 0) {
                System.out.println(index);
                Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + artist.getID());
                System.out.println("Name: " + artist.getName());
                System.out.println("Genre: " + artist.getGenre());

                deleteArtist(artist.getID(), selectedIndex);
            }
        }
    }

    @FXML
    private void sidebarSearchArtist() throws SQLException {
        String _name = nameTextField.getText().trim();
        String _genre = genreTextField.getText().trim();

        ObservableList<Artist> tableItems = searchArtist(_name, _genre);
        tableView.setItems(tableItems);
    }

    
     @FXML
    private void sidebarUpdateArtist() throws SQLException {
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);
            if (selectedIndex >= 0) {
                System.out.println(index);
                Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
                System.out.println("id: " + artist.getID());
                try {
                    updateArtist(nameTextField.getText(), genreTextField.getText(), selectedIndex, artist.getID());
                    System.out.println("Record updated successfully!");
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }
                nameTextField.setText("");
                genreTextField.setText("");

                footerLabel.setText("Record updated successfully!");
                data.clear();
                loadData();
                tableView.refresh();
            }
        }
    }

    @FXML
    private void handleNonRepeatableRead(ActionEvent event) {
        //For the nonrepeatableread method I used one button instead of two and broke it down into steps
   Connection conn = null;
    String initialArtistName = "";
    try {
        // Open a connection
        conn = DriverManager.getConnection(databaseURL);
        conn.setAutoCommit(false); // Begin transaction block

        // Part 1: First Read 
        Statement stmt1 = conn.createStatement();
        ResultSet rs1 = stmt1.executeQuery("SELECT * FROM artist WHERE id = 1");
        if (rs1.next()) {
            initialArtistName = rs1.getString("name");
            System.out.println("Initial Artist name: " + initialArtistName);
        } else {
            System.out.println("No artist found with ID 1");
            footerLabel.setText("No artist found with ID 1");
            return; // Exit if no artist  found
        }

        // Part 2: Update 
        Statement stmt2 = conn.createStatement();
        stmt2.executeUpdate("UPDATE artist SET name = 'New Name' WHERE id = 1");
        //not committing

        // Part 3: Second Read 
        ResultSet rs2 = stmt1.executeQuery("SELECT * FROM artist WHERE id = 1");
        if (rs2.next()) {
            String updatedArtistName = rs2.getString("name");
            System.out.println("Updated Artist name: " + updatedArtistName);

            // Check if non-repeatable read occurred
            if (!initialArtistName.equals(updatedArtistName)) {
                footerLabel.setText("NRR:\"" + initialArtistName + "\" to \"" + updatedArtistName + "\"");
            } else {
                footerLabel.setText("No non-repeatable read condition detected.");
            }
        }

        conn.commit(); // Commit the transaction
    } catch (SQLException e) {
        e.printStackTrace();
        footerLabel.setText("SQL Error: " + e.getMessage());
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close(); // Close the connection
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
private void displayMessage(String message) {
    // display message
    footerLabel.setText(message);
}
    
}
