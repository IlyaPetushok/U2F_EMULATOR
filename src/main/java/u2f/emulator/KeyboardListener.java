package u2f.emulator;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class KeyboardListener {
    Scanner scanner = new Scanner(System.in);

    public boolean keyPressed = true;

    public KeyboardListener()  {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Time is up!");
                System.exit(0);
            }
        }, 30000);

        while (!keyPressed) {
            System.out.println("Click Enter to confirm your presence");
            String input = scanner.nextLine();
            if (input.equals("") || scanner.hasNext()) {
                keyPressed();
            }
        }
        timer.cancel();
    }

    public void keyPressed() {
        keyPressed = true;
    }

}
