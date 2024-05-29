import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        // set up the title
        super("Weather App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        // add the components here
        JTextField searchTextField = new JTextField();

        // set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        //change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature label
        JLabel temperatureLabel = new JLabel("25°C");
        temperatureLabel.setBounds(0, 350, 450, 54);
        temperatureLabel.setFont(new Font("Dialog", Font.PLAIN, 48));

        // center the text
        temperatureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureLabel);

        // weather condition label
         JLabel weatherConditionLabel = new JLabel("Cloudy");
        weatherConditionLabel.setBounds(0, 405, 450, 36);
        weatherConditionLabel.setFont(new Font("Dialog", Font.PLAIN, 24));
        weatherConditionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionLabel);

        // Humidity image
        JLabel humidityImage = new JLabel(loadImage("assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // Humidity label
        JLabel humidityLabel = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityLabel.setBounds(90, 500, 85, 55);
        humidityLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityLabel);

        // windspeed image
        JLabel windSpeedImage = new JLabel(loadImage("assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        // windspeed label
        JLabel windSpeedLabel = new JLabel("<html><b>Wind Speed</b> 10 km/h</html>");
        windSpeedLabel.setBounds(310, 500, 85, 55);
        windSpeedLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedLabel);

        // search button
        JButton searchButton = new JButton(loadImage("assets/search.png"));

        // change the cursor to a hand cursos when hovering over the button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(370, 15, 60, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                //validade input
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    // there is nothing that came from the user
                    return;
                }

                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // update gui

                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // depending on the condition, we will update the weather image that corresponds with the condition
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("assets/snow.png"));
                        break;
                }

                // update temperature text
                double temperature = (Double) weatherData.get("temperature");
                temperatureLabel.setText(temperature + "°C");

                // weather temperature text
                weatherConditionLabel.setText(weatherCondition);

                // update the humidity text:
                long humidity = (Long) weatherData.get("humidity");
                humidityLabel.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed
                double windSpeed = (double) weatherData.get("windspeed");
                windSpeedLabel.setText("<html><b>Wind Speed</b> " + windSpeed + "km/h</html>");

            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String path) {
        try {
            // Use class loader to get resource
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Failed to load image");
        return null;
    }
}
