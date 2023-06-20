package u2f.emulator;

import java.util.Scanner;

public class KeyboardListener {
    Scanner scanner = new Scanner(System.in);

    public boolean keyPressed = true;

    public KeyboardListener() {
        while (!keyPressed) {
            System.out.println("Click Enter to confirm your presence");
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (input.equals("") || scanner.hasNext()) {
                    keyPressed();
                }
            }
        }


    }

    private void keyPressed() {
        keyPressed = true;
    }
}
