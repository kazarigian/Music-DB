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


public class AlbumsController implements Initializable {

    @FXML
    private TableView tableView;

    @FXML
    private VBox vBox;

    @FXML
    private TextField titleTextField, yearTextField, artistIDTextField, artistTextField; //where data gets entered by user

    @FXML
    Label footerLabel;
    @FXML
    TableColumn id = new TableColumn("ID");

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        try {
            loadAlbumData();
        } catch (SQLException ex) {
            
            System.out.println(ex.toString());
        }
        intializeColumns();
        CreateSQLiteTable();
    }

    String databaseURL = "jdbc:sqlite:src/main/resources/com/mycompany/databaseexample/hop.db";

    /* Connect to a sample database
     */
    private ObservableList<Album> data; //object of records in DB

    /*
       ArrayList: Resizable-array implementation of the List interface. 
       Implements all optional list operations, and permits all elements, including null.
       ObservableList: A list that allows listeners to track changes when they occur
    */
    
    
    public AlbumsController() throws SQLException {
        this.data = FXCollections.observableArrayList(); //a moveable list of album
    }

    private void intializeColumns() {
        id = new TableColumn("ID");
        id.setMinWidth(50);
        id.setCellValueFactory(new PropertyValueFactory<Album, Integer>("id"));

        TableColumn title = new TableColumn("Title");
        title.setMinWidth(450);
        title.setCellValueFactory(new PropertyValueFactory<Album, String>("title"));
        
        TableColumn genre = new TableColumn("Year");
        title.setMinWidth(450);
        title.setCellValueFactory(new PropertyValueFactory<Album, String>("year"));
        
        TableColumn artistID = new TableColumn("Artist ID");
        title.setMinWidth(450);
        title.setCellValueFactory(new PropertyValueFactory<Album, Integer>("artistID"));
        
        TableColumn artist = new TableColumn("Artist");
        title.setMinWidth(450);
        title.setCellValueFactory(new PropertyValueFactory<Album, String>("artist"));
        
        //tableView.setOpacity(0.3);
        /* Allow for the values in each cell to be changable */
    }

    public void loadAlbumData() throws SQLException {

        Connection conn = null;
        Statement stmt = null;
 
        try {

            // create a connection to the database
            conn = DriverManager.getConnection(databaseURL);

            System.out.println("Connection to SQLite has been established.");
            String sql = "SELECT * FROM Albums;"; //selects everything, can change to only show a few feilds (important fields)
            // Ensure we can query the actors table
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Album album;
                album = new Album(rs.getInt("id"), rs.getString("title"), rs.getString("year"), rs.getInt("artistID"), rs.getString("artist"));
                System.out.println(album.getId() + " - " + album.getTitle() + " - " + album.getYear()+ " - " + album.getArtistID() + " - " + album.getArtist());
                data.add(album); //adding to record(DB) of album
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
        Text text = new Text("The Album Database");

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
     * Insert a new row into the album table
     *
     * @param title
     * @param year
     * @param artistID
     * @param artist
     * 
     * @throws java.sql.SQLException
     */
    public void insertAlbum(String title, String year, int artistID, String artist) throws SQLException {
        int last_inserted_id = 0;
        Connection conn = null;
        try {
            // create a connection to the database

            conn = DriverManager.getConnection(databaseURL);

            System.out.println("Connection to SQLite has been established.");

            System.out.println("Inserting one record!");

            String sql = "INSERT INTO Albums(title, year, artistID, artist) VALUES(?,?,?,?)"; //values of question marks come from the variables

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, year);
            pstmt.setString(3, Integer.toString(artistID));
            pstmt.setString(4, artist);
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
  
        data.add(new Album(last_inserted_id, title, year, artistID, artist));
    }

    @FXML
    public void handleAddAlbum(ActionEvent actionEvent) {

        System.out.println("Title: " + titleTextField.getText() + "\nYear: " + yearTextField.getText()+ "\nArtist ID: " + artistIDTextField.getText()+ "\nArtist: " + artistTextField.getText());//only prints in console (user doesn't see)
        try {
            // insert a new rows
            insertAlbum(titleTextField.getText(), yearTextField.getText(), Integer.parseInt(artistIDTextField.getText()), artistTextField.getText()); //these are the objects made to hold the input (from line 44 or 47) (use parse int for year bc read it as a string then converts to int)

            System.out.println("Data was inserted Successfully");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        titleTextField.setText("");
        yearTextField.setText("");
        artistIDTextField.setText("");
        artistTextField.setText("");
        
        //re-seting fields after sucessfully inputing info
        footerLabel.setText("Record inserted into table successfully!");
    }

    private void CreateSQLiteTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Albums (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	title text NOT NULL,\n"
                + "	year text NOT NULL,\n"
                + "     artistID integer NOT NULL\n"
                + "	artist text NOT NULL,\n"
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

    public void deleteAlbum(int id, int selectedIndex) { //id to remove from DB, selectedIndex to remove from table

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(databaseURL);

            String sql = "DELETE FROM Albums WHERE id=" + Integer.toString(id); //id has to be made a string to concotinate it with the other statement
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
    private void handleDeleteAlbum(ActionEvent event) throws IOException {
        System.out.println("Delete Album");
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println("Handle Delete Action");
                System.out.println(index);
                Album album = (Album) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + album.getId());
                System.out.println("Title: " + album.getTitle());
                System.out.println("Year: " + album.getYear());
                System.out.println("Artist ID: " + album.getArtistID());
                System.out.println("Artist: " + album.getArtist());
                deleteAlbum(album.getId(), selectedIndex);
            }

        }
    }

    Integer index = -1;

    @FXML
    private void showAlbumData() {

        index = tableView.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }

        System.out.println("showRowData");
        System.out.println(index);
        Album album = (Album) tableView.getSelectionModel().getSelectedItem(); //making empty object of album then casting album type to the selectedItem
        System.out.println("ID: " + album.getId());
        System.out.println("Title: " + album.getTitle());
        System.out.println("Year: " + album.getYear());
        System.out.println("Artist ID: " + album.getArtistID());
        System.out.println("Artist: " + album.getArtist());
        
        titleTextField.setText(album.getTitle());
        yearTextField.setText(album.getYear());
        artistIDTextField.setText(Integer.toString(album.getArtistID()));
        artistTextField.setText(album.getArtist());

        String content = "Id= " + album.getId() + "\nTitle= " + album.getTitle() + "\nYear= " + album.getYear()+ "\nArtist ID= " + album.getArtistID()+ "\nArtist= " + album.getArtist();

    }


    @SuppressWarnings("empty-statement")
    public ObservableList<Album> searchAlbum(String _title, String _year, String _artist) throws SQLException {
        ObservableList<Album> searchResult = FXCollections.observableArrayList();
        // read data from SQLite database
        CreateSQLiteTable();
        String sql = "Select * from Albums where true";

        if (!_title.isEmpty()) {
            sql += " and title like '%" + _title + "%'";
        }
        if (!_year.isEmpty()) {
            sql += " and year like '%" + _year + "%'";
        }
        if (!_artist.isEmpty()) {
            sql += " and artist like '%" + _artist + "%'";
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
                    String title = rs.getString("title");
                    String year = rs.getString("year");
                    int artistID = rs.getInt("artistID");
                    String artist = rs.getString("artist");

                    Album album = new Album(recordId, title, year, artistID, artist);
                    searchResult.add(album);
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return searchResult;
    }

    @FXML
    private void handleSearchAlbum(ActionEvent event) throws IOException, SQLException {
        String _title = titleTextField.getText().trim();
        String _year = yearTextField.getText().trim();
        String _artist = artistTextField.getText().trim();
        ObservableList<Album> tableItems = searchAlbum(_title, _year, _artist);
        tableView.setItems(tableItems);

    }

    @FXML
    private void handleShowAllAlbums(ActionEvent event) throws IOException, SQLException {
        tableView.setItems(data);

    }

    /**
     * Update a record in the albums table
     *
     * @param title
     * @param year
     * @param artistID
     * @param artist
     * @throws java.sql.SQLException
     */
    public void updateAlbum(String title, String year, int artistID, String artist, int selectedIndex, int id) throws SQLException {

        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(databaseURL);
            String sql = "UPDATE Albums SET title = ?, year = ?, artistID = ?, artist = ? Where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, year);
            pstmt.setString(3, Integer.toString(artistID));
            pstmt.setString(4, artist);
            pstmt.setString(5, Integer.toString(id));

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
    private void handleUpdateAlbum(ActionEvent event) throws IOException, SQLException {

        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println(index);
                Album album = (Album) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + album.getId());

                try {
                    // insert a new rows
                    updateAlbum(titleTextField.getText(), yearTextField.getText(),  Integer.parseInt(artistIDTextField.getText()), artistTextField.getText(), selectedIndex, album.getId());

                    System.out.println("Record updated successfully!");
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }

                titleTextField.setText("");
                yearTextField.setText("");
                artistIDTextField.setText("");
                artistTextField.setText("");

                footerLabel.setText("Record updated successfully!");
                data.clear();
                loadAlbumData();
                tableView.refresh();
            }

        }

    }

    @FXML
    private void sidebarShowAllAlbums() {
        tableView.setItems(data);
    }

    @FXML
    private void sidebarAddNewAlbum() {
        System.out.println("Title: " + titleTextField.getText()  + "\nYear: " + yearTextField.getText()+ "\nArtist ID: " + artistIDTextField.getText()+ "\nArtist: " + artistTextField.getText());

        try {
            // insert a new rows
            insertAlbum(titleTextField.getText(), yearTextField.getText(), Integer.parseInt(artistIDTextField.getText()), artistTextField.getText());

            System.out.println("Data was inserted Successfully");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        titleTextField.setText("");
        yearTextField.setText("");
        artistIDTextField.setText("");
        artistTextField.setText("");

        footerLabel.setText("Record inserted into table successfully!");

    }

    @FXML
    private void sidebarDeleteAlbum() {
        System.out.println("Delete Album");
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println("Handle Delete Action");
                System.out.println(index);
                Album album = (Album) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + album.getId());
                System.out.println("Title: " + album.getTitle());
                System.out.println("Year: " + album.getYear());
                System.out.println("Artist ID: " + album.getArtistID());
                System.out.println("Artist: " + album.getArtist());
                deleteAlbum(album.getId(), selectedIndex);
            }

        }
    }

    @FXML
    private void sidebarSearchAlbum() throws SQLException {
        String _title = titleTextField.getText().trim();
        String _year = yearTextField.getText().trim();
        String _artist = artistTextField.getText().trim();
        ObservableList<Album> tableItems = searchAlbum(_title, _year, _artist);
        tableView.setItems(tableItems);
    }

    
     @FXML
    private void sidebarUpdateAlbum() throws SQLException {
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println(index);
                Album album = (Album) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + album.getId());

                try {
                    // insert a new rows
                    updateAlbum(titleTextField.getText(), yearTextField.getText(), Integer.parseInt(artistIDTextField.getText()), artistTextField.getText(), selectedIndex, album.getId());

                    System.out.println("Record updated successfully!");
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }

                titleTextField.setText("");
                yearTextField.setText("");
                artistIDTextField.setText("");
                artistTextField.setText("");

                footerLabel.setText("Record updated successfully!");
                data.clear();
                loadAlbumData();
                tableView.refresh();
            }
        }
    }   
    
}
