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


public class SongController implements Initializable {

    @FXML
    private TableView tableView;

    @FXML
    private VBox vBox;

    @FXML
    private TextField titleTextField, albumIDTextField, albumTextField; //where data gets entered by user

    @FXML
    Label footerLabel;
    @FXML
    TableColumn id = new TableColumn("ID");

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        try {
            loadData();
        } catch (SQLException ex) {
            
            System.out.println(ex.toString());
        }
        intializeColumns();
        CreateSQLiteTable();
    }

    String databaseURL = "jdbc:sqlite:src/main/resources/com/mycompany/databaseexample/music.db";

    /* Connect to a sample database
     */
    private ObservableList<Song> data; //object of records in DB

    /*
       ArrayList: Resizable-array implementation of the List interface. 
       Implements all optional list operations, and permits all elements, including null.
       ObservableList: A list that allows listeners to track changes when they occur
    */
    
    
    public SongController() throws SQLException {
        this.data = FXCollections.observableArrayList(); //a moveable list of songs
    }

    private void intializeColumns() {
        //make sure to add comments  here
        id = new TableColumn("ID");
        id.setMinWidth(50);
        id.setCellValueFactory(new PropertyValueFactory<Song, Integer>("id"));

        TableColumn title = new TableColumn("Title");
        title.setMinWidth(450);
        title.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
        
        TableColumn albumID  = new TableColumn("Album ID");
        albumID .setMinWidth(450);
        albumID .setCellValueFactory(new PropertyValueFactory<Song, Integer>("albumID"));

        TableColumn album = new TableColumn("Album");
        album.setMinWidth(100);
        album.setCellValueFactory(new PropertyValueFactory<Song, String>("album"));
        
        tableView.setItems(data);
        tableView.getColumns().addAll(id, title, albumID, album);

        
        //tableView.setOpacity(0.3);
        /* Allow for the values in each cell to be changable */
    }

    public void loadData() throws SQLException {

        Connection conn = null;
        Statement stmt = null;
 
        try {

            // create a connection to the database
            conn = DriverManager.getConnection(databaseURL);

            System.out.println("Connection to SQLite has been established.");
            String sql = "SELECT * FROM Songs;"; //selects everything, can change to only show a few feilds (important fields)
            // Ensure we can query the actors table
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Song song;
                song = new Song(rs.getInt("id"), rs.getString("title"), rs.getInt("albumID"), rs.getString("album"));
                System.out.println(song.getId() + " - " + song.getTitle()  + " - " + song.getAlbumID() + " - " + song.getAlbum());
                data.add(song); //adding to record(DB) of songs
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
        Text text = new Text("The Song Database");

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
     * Insert a new row into the songs table
     *
     * @param title
     * @param album
     * @param albumID

     * @throws java.sql.SQLException
     */
    public void insertSong(String title, int albumID, String album) throws SQLException {
        int last_inserted_id = 0;
        Connection conn = null;
        try {
            // create a connection to the database

            conn = DriverManager.getConnection(databaseURL);

            System.out.println("Connection to SQLite has been established.");

            System.out.println("Inserting one record!");

            String sql = "INSERT INTO Songs(title, albumID, album) VALUES(?,?,?)"; //values of question marks come from the variables

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setInt(2, albumID);
            pstmt.setString(3, album);
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
        System.out.println("last_inserted_id " + last_inserted_id);
  
        data.add(new Song(last_inserted_id, title, albumID, album));
    }

    @FXML
    public void handleAddSong(ActionEvent actionEvent) {

        System.out.println("Title: " + titleTextField.getText() +  "\nAlbum ID: " + albumIDTextField.getText() +   "\nAlbum: " + albumTextField.getText());//only prints in console (user doesn't see)

        try {
            // insert a new rows
            insertSong(titleTextField.getText(), Integer.parseInt(albumIDTextField.getText()), albumTextField.getText()); //these are the objects made to hold the input (from line 44 or 47) (use parse int for year bc read it as a string then converts to int)

            System.out.println("Data was inserted Successfully");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        titleTextField.setText("");
        albumIDTextField.setText("");
        albumTextField.setText("");

        //re-seting fields after sucessfully inputing info
        footerLabel.setText("Record inserted into table successfully!");
    }

    private void CreateSQLiteTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Songs (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	title varchar(100),\n"
                + "	albumID integer,\n"
                + "	album varchar(100)"
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

    public void deleteSong(int id, int selectedIndex) { //id to remove from DB, selectedIndex to remove from table

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(databaseURL);

            String sql = "DELETE FROM Songs WHERE id=" + Integer.toString(id); //id has to be made a string to concotinate it with the other statement
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
    private void handleDeleteSong(ActionEvent event) throws IOException {
        System.out.println("Delete Song");
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println("Handle Delete Action");
                System.out.println(index);
                Song song = (Song) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + song.getId());
                System.out.println("Title: " + song.getTitle());
                System.out.println("Album ID: " + song.getAlbumID());
                System.out.println("Album: " + song.getAlbum());
                deleteSong(song.getId(), selectedIndex);
            }

        }
    }

    Integer index = -1;

    @FXML
    private void showSongData() {

        index = tableView.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }

        System.out.println("showSongData");
        System.out.println(index);
        Song song = (Song) tableView.getSelectionModel().getSelectedItem(); //making empty object of song then casting song type to the selectedItem
        System.out.println("ID: " + song.getId());
        System.out.println("Title: " + song.getTitle());
        System.out.println("Album ID: " + song.getAlbumID());
        System.out.println("Album: " + song.getAlbum());

        titleTextField.setText(song.getTitle());
        albumIDTextField.setText(Integer.toString(song.getAlbumID()));
        albumTextField.setText(song.getAlbum());

        String content = "Id= " + song.getId() + "\nTitle= " + song.getTitle() + "\nAlbumID= " + song.getAlbumID() + "\nAlbum= " + song.getAlbum();

    }


    @SuppressWarnings("empty-statement")
    public ObservableList<Song> searchSong(String _title, String _albumID, String _album) throws SQLException {
        ObservableList<Song> searchResult = FXCollections.observableArrayList();
        // read data from SQLite database
        CreateSQLiteTable();
        String sql = "Select * from Songs where true";

        if (!_title.isEmpty()) {
            sql += " and title like '%" + _title + "%'";
        }
        if (! _albumID.isEmpty()) {
            sql += " and albumID ='" + _albumID + "'";
        }
        if (! _album.isEmpty()) {
            sql += " and album ='" + _album + "'";
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
                    int albumID = rs.getInt("albumID");
                    String album = rs.getString("album");

                    Song song = new Song(recordId, title, albumID, album);
                    searchResult.add(song);
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return searchResult;
    }

    @FXML
    private void handleSearchSong(ActionEvent event) throws IOException, SQLException {
        String _title = titleTextField.getText().trim();
        String _albumID = albumIDTextField.getText().trim();
        String _album = albumTextField.getText().trim();

        ObservableList<Song> tableItems = searchSong(_title, _albumID, _album);
        tableView.setItems(tableItems);

    }

    @FXML
    private void handleShowAllSongs(ActionEvent event) throws IOException, SQLException {
        tableView.setItems(data);

    }

    /**
     * Update a record in the songs table
     *
     * @param title
     * @param album
     * @param albumID

     * @throws java.sql.SQLException
     */
    public void updateSong(String title, int albumID, String album, int selectedIndex, int id) throws SQLException {


    Connection conn = null;
    try {
        // create a connection to the database
        conn = DriverManager.getConnection(databaseURL);
        String sql = "UPDATE songs SET title = ?, albumID = ?, album = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, title);
        pstmt.setInt(2, albumID);
        pstmt.setString(3, album);
        pstmt.setInt(4, id);

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
    private void handleUpdateSong(ActionEvent event) throws IOException, SQLException {

        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println(index);
                Song song = (Song) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + song.getId());

                try {
                    // insert a new rows
                    updateSong(titleTextField.getText(), Integer.parseInt(albumIDTextField.getText()), albumTextField.getText() , selectedIndex, song.getId());

                    System.out.println("Record updated successfully!");
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }

                titleTextField.setText("");
                albumIDTextField.setText("");
                albumTextField.setText("");

                footerLabel.setText("Record updated successfully!");
                data.clear();
                loadData();
                tableView.refresh();
            }

        }

    }

    @FXML
    private void sidebarShowAllSongs() {
        tableView.setItems(data);
    }

    @FXML
    private void sidebarAddNewSong() {
        System.out.println("Title: " + titleTextField.getText() + "\nAlbum ID : " + albumIDTextField.getText() + "\nAlbum: " + albumTextField.getText());

        try {
            // insert a new rows
            insertSong(titleTextField.getText(), Integer.parseInt(albumIDTextField.getText()), albumTextField.getText());

            System.out.println("Data was inserted Successfully");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        titleTextField.setText("");
        albumIDTextField.setText("");
        albumTextField.setText("");

        footerLabel.setText("Record inserted into table successfully!");

    }

    @FXML
    private void sidebarDeleteSong() {
        System.out.println("Delete Song");
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println("Handle Delete Action");
                System.out.println(index);
                Song song = (Song) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + song.getId());
                System.out.println("Title: " + song.getTitle());
                System.out.println("AlbumID: " + song.getAlbumID());
                System.out.println("Album: " + song.getAlbum());
                deleteSong(song.getId(), selectedIndex);
            }

        }
    }

    @FXML
    private void sidebarSearchSong() throws SQLException {
        String _title = titleTextField.getText().trim();
        String _albumID = albumIDTextField.getText().trim();
        String _album = albumTextField.getText().trim();

        ObservableList<Song> tableItems = searchSong(_title,_albumID, _album);
        tableView.setItems(tableItems);
    }

    
     @FXML
    private void sidebarUpdateSong() throws SQLException {
        //Check whether item is selected and set value of selected item to Label
        if (tableView.getSelectionModel().getSelectedItem() != null) {

            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected Index: " + selectedIndex);

            if (selectedIndex >= 0) {

                System.out.println(index);
                Song song = (Song) tableView.getSelectionModel().getSelectedItem();
                System.out.println("ID: " + song.getId());

                try {
                    // insert a new rows
                    updateSong(titleTextField.getText(), Integer.parseInt(albumIDTextField.getText()), albumTextField.getText(), selectedIndex, song.getId());

                    System.out.println("Record updated successfully!");
                } catch (SQLException ex) {
                    System.out.println(ex.toString());
                }

                titleTextField.setText("");
                albumIDTextField.setText("");
                albumTextField.setText("");

                footerLabel.setText("Record updated successfully!");
                data.clear();
                loadData();
                tableView.refresh();
            }
        }
    }   
        //lostupdate1 causes the gui to freeze so you cannot click
    //lostupdate2 while lostupdate1 is running!
@FXML
private void lostUpdate1(ActionEvent event) {
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(databaseURL);
        conn.setAutoCommit(false); // Start transaction block

        // Transaction 1: First Read
        Statement stmt1 = conn.createStatement();
        ResultSet rs1 = stmt1.executeQuery("SELECT title FROM songs WHERE id = 1"); // Assuming 'id = 1' is the correct identifier
        String originalTitle = rs1.next() ? rs1.getString("title") : null;
        
        //  delay
        Thread.sleep(10000);
        footerLabel.setText("Sleep pressed");

        

        // Transaction 1: Update after delay
        if(originalTitle != null) {
            String newTitle1 = "If this is the title, there was a lost update!";
            stmt1.executeUpdate("UPDATE songs SET title = '" + newTitle1 + "' WHERE id = 1");
            footerLabel.setText("T1:Title is '" + newTitle1 + "'");
        }

        conn.commit();
    } catch (SQLException | InterruptedException e) {
        e.printStackTrace();
        footerLabel.setText("Transaction 1 Error: " + e.getMessage());
    } finally {
        // Close connections
        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}

@FXML
private void lostUpdate2(ActionEvent event) {
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(databaseURL);
        conn.setAutoCommit(false); // Start transaction block

        // Transaction 2: Update Immediately
        Statement stmt2 = conn.createStatement();
        String newTitle2 = "Originally Intended Song Title";
        stmt2.executeUpdate("UPDATE songs SET title = '" + newTitle2 + "' WHERE id = 1");
        footerLabel.setText("T2:Title is '" + newTitle2 + "'");

        conn.commit();
    } catch (SQLException e) {
        e.printStackTrace();
        footerLabel.setText("Transaction 2 Error: " + e.getMessage());
    } finally {
        // Close connections
        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
    @FXML
private void phantomRead1(ActionEvent event){
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(databaseURL);
        conn.setAutoCommit(false); // Start the transaction block

        // Execute the first SELECT statement
        Statement stmt1 = conn.createStatement();
        ResultSet rs1 = stmt1.executeQuery("SELECT COUNT(*) AS songCount FROM songs");
        rs1.next();
        int initialCount = rs1.getInt("songCount");
        footerLabel.setText("Initial song count: " + initialCount);
        System.out.println("Initial song count: " + initialCount);

        // Simulate delay
        Thread.sleep(10000); //10 second delay

        // Execute the second SELECT statement
        ResultSet rs2 = stmt1.executeQuery("SELECT COUNT(*) AS songCount FROM songs");
        rs2.next();
        int newCount = rs2.getInt("songCount");
        footerLabel.setText("New song count after delay: " + newCount);
        System.out.println("New song count after delay: " + initialCount);


        conn.commit(); // Commit transaction
    } catch (SQLException | InterruptedException e) {
        e.printStackTrace();
        footerLabel.setText("Error: " + e.getMessage());
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
@FXML
private void phantomRead2(ActionEvent event){
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(databaseURL);
        Statement stmt2 = conn.createStatement();
        
        // Insert new song)
        stmt2.executeUpdate("INSERT INTO songs (title, albumId, album) VALUES ('New Song', 1, 'New Album')");
        footerLabel.setText("New song inserted.");
        
    } catch (SQLException e) {
        e.printStackTrace();
        footerLabel.setText("Error: " + e.getMessage());
    } finally {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
    @FXML
private void deadlock1(ActionEvent event) {
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(databaseURL);
        conn.setAutoCommit(false); // Start transaction block

        // Transaction 1: Update first row
        Statement stmt1 = conn.createStatement();
        stmt1.executeUpdate("UPDATE songs SET title = 'Updated by T1' WHERE id = 1");
        System.out.println("Transaction 1 has locked the first row.");

        // Simulate delay
        Thread.sleep(5000); // 5 seconds delay

        // Transaction 1: Attempt to update the second row
        stmt1.executeUpdate("UPDATE songs SET title = 'Updated by T1' WHERE id = 2");
        System.out.println("Transaction 1 has locked the second row.");

        conn.commit();
    } catch (SQLException | InterruptedException e) {
        e.printStackTrace();
        footerLabel.setText("Transaction 1 Error: " + e.getMessage());
    } finally {
        // Close connections
        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}

@FXML
private void deadlock2(ActionEvent event) {
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(databaseURL);
        conn.setAutoCommit(false); // Start transaction block

        // Transaction 2: Update  second row
        Statement stmt2 = conn.createStatement();
        stmt2.executeUpdate("UPDATE songs SET title = 'Updated by T2' WHERE id = 2");
        System.out.println("Transaction 2 has locked the second row.");

        // lock  first row immediately
        stmt2.executeUpdate("UPDATE songs SET title = 'Updated by T2' WHERE id = 1");
        System.out.println("Transaction 2 has locked the first row.");

        conn.commit();
    } catch (SQLException e) {
        e.printStackTrace();
        footerLabel.setText("Transaction 2 Error: " + e.getMessage());
    } finally {
        // Close connections
        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}

    @FXML
private void dirtyRead1(ActionEvent event){
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(databaseURL);
        conn.setAutoCommit(false); // Begin transaction block

        // Update a record but do not commit immediately
        Statement stmt1 = conn.createStatement();
        stmt1.executeUpdate("UPDATE songs SET title = 'Uncommitted Title' WHERE id = 1");
        footerLabel.setText("dirtyRead1: Updated but not committed");

        // not committing.
        // conn.commit();
    } catch (SQLException e) {
        e.printStackTrace();
        footerLabel.setText("dirtyRead1 Error: " + e.getMessage());
    } finally {
        try {
            if (conn != null) {
                // conn.commit(); /
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
@FXML
private void dirtyRead2(ActionEvent event){
    
       Connection conn = null;
    try {
        conn = DriverManager.getConnection(databaseURL);
        // Set transaction isolation level to READ_UNCOMMITTED to allow dirty reads
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        Statement stmt2 = conn.createStatement();

        // Read the uncommitted data
        ResultSet rs2 = stmt2.executeQuery("SELECT title FROM songs WHERE id = 1");
        if (rs2.next()) {
            String title = rs2.getString("title");
            footerLabel.setText("this is uncommitted: " + title);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        footerLabel.setText("dirtyRead2 Error: " + e.getMessage());
    } finally {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}


}
