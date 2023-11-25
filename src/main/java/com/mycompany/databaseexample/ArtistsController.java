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


public class ArtistsController implements Initializable {

    @FXML
    private TableView tableView;

    @FXML
    private VBox vBox;

    @FXML
    private TextField nameTextField, genreTextField; //where data gets entered by user

    @FXML
    Label footerLabel;
    @FXML
    TableColumn id = new TableColumn("ID");

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        try {
            loadArtistsData();
        } catch (SQLException ex) {
            
            System.out.println(ex.toString());
        }
        intializeColumns();
        CreateSQLiteTable();
    }

    String databaseURL = "jdbc:sqlite:src/main/resources/com/mycompany/databaseexample/hop.db";

    /* Connect to a sample database
     */
    private ObservableList<Artist> data; //object of records in DB

    /*
       ArrayList: Resizable-array implementation of the List interface. 
       Implements all optional list operations, and permits all elements, including null.
       ObservableList: A list that allows listeners to track changes when they occur
    */
    
    
    public ArtistsController() throws SQLException {
        this.data = FXCollections.observableArrayList(); //a moveable list of artist
    }

    private void intializeColumns() {
        id = new TableColumn("ID");
        id.setMinWidth(50);
        id.setCellValueFactory(new PropertyValueFactory<Artist, Integer>("id"));

        TableColumn name = new TableColumn("Name");
        name.setMinWidth(450);
        name.setCellValueFactory(new PropertyValueFactory<Artist, String>("name"));
        
        TableColumn genre = new TableColumn("Genre");
        name.setMinWidth(450);
        name.setCellValueFactory(new PropertyValueFactory<Artist, String>("genre"));
        
        //tableView.setOpacity(0.3);
        /* Allow for the values in each cell to be changable */
    }

    public void loadArtistsData() throws SQLException {

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
                System.out.println(artist.getId() + " - " + artist.getName() + " - " + artist.getGenre());
                data.add(artist); //adding to record(DB) of artist
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

        //Setting the font of the text 
        text.setFont(Font.font("Edwardian Script ITC", 55));

        //Setting the position of the text 
//        text.setX(600);
//        text.setY(100);
        //Setting the linear gradient 
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
     * Insert a new row into the artist table
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

            String sql = "INSERT INTO Artists(name, genre) VALUES(?,?)"; //values of question marks come from the variables

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, genre);
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
        System.out.println("last_inserted_id " + last_inserted_id);
  
        data.add(new Artist(last_inserted_id, name, genre));
    }

    @FXML
    public void handleAddArtist(ActionEvent actionEvent) {

        System.out.println("Name: " + nameTextField.getText() + "Genre: " + genreTextField.getText());//only prints in console (user doesn't see)
        try {
            // insert a new rows
            insertArtist(nameTextField.getText(), genreTextField.getText()); //these are the objects made to hold the input (from line 44 or 47) (use parse int for year bc read it as a string then converts to int)

            System.out.println("Data was inserted Successfully");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        nameTextField.setText("");
        genreTextField.setText("");
        
        //re-seting fields after sucessfully inputing info
        footerLabel.setText("Record inserted into table successfully!");
    }

    private void CreateSQLiteTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Artists (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL\n"
                + "	genre text NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(databaseURL);
                Statement stmt = conn.createStatement()) {
            // create a new table
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

            String sql = "DELETE FROM Artists WHERE id=" + Integer.toString(id); //id has to be made a string to concotinate it with the other statement
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

                System.out.println("Handle Delete Action");
                System.out.println(index);
                Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + artist.getId());
                System.out.println("Name: " + artist.getName());
                System.out.println("Genre: " + artist.getGenre());
                deleteArtist(artist.getId(), selectedIndex);
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
        Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem(); //making empty object of artist then casting artist type to the selectedItem
        System.out.println("ID: " + artist.getId());
        System.out.println("Name: " + artist.getName());
        System.out.println("Genre: " + artist.getGenre());
        
        nameTextField.setText(artist.getName());

        String content = "Id= " + artist.getId() + "\nName= " + artist.getName() + "\nGenre= " + artist.getGenre();

    }


    @SuppressWarnings("empty-statement")
    public ObservableList<Artist> searchArtist(String _name, String _genre) throws SQLException {
        ObservableList<Artist> searchResult = FXCollections.observableArrayList();
        // read data from SQLite database
        CreateSQLiteTable();
        String sql = "Select * from Artists where true";

        if (!_name.isEmpty()) {
            sql += " and name like '%" + _name + "%'";
        }
        if (!_genre.isEmpty()) {
            sql += " and genre like '%" + _genre + "%'";
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
            String sql = "UPDATE Artists SET name = ?, genre =? Where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, genre);
            pstmt.setString(3, Integer.toString(id));

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
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

        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println(index);
                Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + artist.getId());

                try {
                    // insert a new rows
                    updateArtist(nameTextField.getText(), genreTextField.getText(), selectedIndex, artist.getId());

                    System.out.println("Record updated successfully!");
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }

                nameTextField.setText("");
                genreTextField.setText("");

                footerLabel.setText("Record updated successfully!");
                data.clear();
                loadArtistsData();
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
        System.out.println("Name: " + nameTextField.getText());

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

    @FXML
    private void sidebarDeleteArtist() {
        System.out.println("Delete Artist");
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println("Handle Delete Action");
                System.out.println(index);
                Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + artist.getId());
                System.out.println("Name: " + artist.getName());
                System.out.println("Genre: " + artist.getGenre());
                deleteArtist(artist.getId(), selectedIndex);
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
                System.out.println("ID: " + artist.getId());

                try {
                    // insert a new rows
                    updateArtist(nameTextField.getText(), genreTextField.getText(), selectedIndex, artist.getId());

                    System.out.println("Record updated successfully!");
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }

                nameTextField.setText("");
                genreTextField.setText("");

                footerLabel.setText("Record updated successfully!");
                data.clear();
                loadArtistsData();
                tableView.refresh();
            }
        }
    }   
    
}
