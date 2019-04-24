package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Controller {

    private JSONParser parser = new JSONParser();
    private Object obj;
    private JSONArray array = null;
    private URL url;
    private HttpURLConnection con;
    private BufferedReader in;

    private ArrayList<Country> countries = new ArrayList<>();
    @FXML
    private ListView<Country> countriesListView;
    @FXML
    private Label country;
    @FXML
    private Label capital;
    @FXML
    private TextField search;

    @FXML
    private void initialize() {
        Thread init = new Thread(() -> getData());
        init.start();
        country.setText("LOADING");
        capital.setText("DATA");
        ObservableList<Country> list = FXCollections.observableArrayList();
        list.add(new Country("loading...", " "));
        countriesListView.setItems(list);


        countriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            country.setText("country: " + newValue.getName());
            capital.setText("capital: " + newValue.getCapital());
        });


        search.setOnKeyReleased((ob) -> {

            String src = search.getCharacters().toString();
            list.clear();
            countriesListView.getItems().clear();
            for (int j = 0; j < countries.size(); j++) {
                if (countries.get(j).getName().contains(src)) {
                    list.add(new Country(countries.get(j).getName(), countries.get(j).getCapital()));
                }
            }
            countriesListView.setItems(list);
        });

    }

    private void getData() {

        try {

            url = new URL("https://restcountries.eu/rest/v2/all");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            obj = parser.parse(content.toString());
            array = (JSONArray) obj;

        } catch (Exception e) {
            e.printStackTrace();
        }


        Iterator it = array.iterator();

        ObservableList<Country> list = FXCollections.observableArrayList();
        while (it.hasNext()) {
            JSONObject slide = (JSONObject) it.next();
            String name = (String) slide.get("name");
            String capital = (String) slide.get("capital");
            Country country = new Country(name, capital);
            list.add(country);
            countries.add(country);
        }

        Platform.runLater(() -> {
            countriesListView.setItems(list);
            country.setText("country: Romania");
            capital.setText("capital: Bucuresti");
        });

    }
}
