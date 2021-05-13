public class StartGame {
    public static void main(final String[] args) {

        Thread serverThread = new Thread() {
            public void run() {
                SimpleServer.main(args);
            }
        };

        Thread clientThread = new Thread() {
            public void run() {
                Minesweeper.main(args);
            }
        };

        serverThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clientThread.start();
    }
}