// Project Originally developed by TapTap on Youtube
// Replicated and Customized by Leonardo Silva.

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // display the weather app GUI:
                new WeatherAppGui().setVisible(true);
                //System.out.println(WeatherApp.getLocationData("Tokyo"));

            }
        });
    }
}
