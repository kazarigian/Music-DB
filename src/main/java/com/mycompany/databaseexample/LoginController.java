package com.mycompany.databaseexample;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author erick
 */
public class LoginController {

    private static Stage stage;
    private static Scene scene;

    @FXML
    private TextField user_name;
    @FXML
    private PasswordField password;
    @FXML
    private Text actiontarget;
    
    @FXML
    private Text resultText;

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {

        String userNameText = user_name.getText();
        String passwordText = password.getText();

        String jdbcUrl = "jdbc:sqlserver://localhost:1433;databaseName=Music;user=sa;password=123";
   

        try ( Connection connection = DriverManager.getConnection(jdbcUrl)) {

            String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

            try ( PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, userNameText);
                preparedStatement.setString(2, passwordText);

                try ( ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        resultText.setText("Sign in successful");

                        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } else {
                        resultText.setText("Sign in unsuccessful");
                    }
                }

            } catch (IOException ex) {
                System.out.println(ex.toString());
                //Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            //e.printStackTrace();
            System.out.println(ex.toString());

        }

    }

}
