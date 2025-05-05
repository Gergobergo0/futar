package futar.futar.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.function.Consumer;

public class OtpServiceManager {
    private Process otpProcess;

    public void startOtpServer() {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "java", "-Xmx4G", "-jar", "otp-2.3.0-shaded.jar", "--serve", "--load", "otp-data/graph.obj"

            );
            builder.directory(new File("otp-demo"));
            //builder.redirectErrorStream(true); // stderr → stdout
            Process process = builder.start();

            // háttérben olvassuk a kimenetet, de nem írjuk ki
            Thread outputReader = new Thread(() -> {
                try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("Started OTP") || line.contains("started")) {
                            System.out.println("✅ OTP szerver elindult.");
                        }
                        if (line.toLowerCase().contains("error") || line.toLowerCase().contains("exception")) {
                            System.err.println("❌ OTP hiba: " + line);
                        }
                        // egyébként csendben vagyunk
                    }
                } catch (IOException ignored) {}
            });

            outputReader.setDaemon(true); // ne blokkolja a kilépést
            outputReader.start();

            this.otpProcess = process;
        } catch (IOException e) {
            System.err.println("❌ OTP szerver indítása sikertelen: " + e.getMessage());
        }
    }


    public void stopOtpServer() {
        if (otpProcess != null && otpProcess.isAlive()) {
            otpProcess.destroy();
            System.out.println("🛑 OTP szerver leállítva.");
        }
    }

    public void startOtpServerBlocking(Consumer<Double> progressCallback) {
        try {
            progressCallback.accept(0.1); // Előkészület
            Thread.sleep(400); // csak szimuláció!

            progressCallback.accept(0.3); // Graph töltés
            Thread.sleep(600);

            progressCallback.accept(0.6); // Routing és hálózat inicializálása
            Thread.sleep(1000);



            ProcessBuilder builder = new ProcessBuilder(
                    "java", "-Xmx4G", "-jar", "otp.jar", "--serve", "--load", "otp-data/"
            );
            builder.directory(new File("otp-demo"));

            //builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            //builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            builder.redirectErrorStream(true);

            otpProcess = builder.start();

            // További indulási idő szimulálása (opcionális)
            Thread.sleep(1000);
            progressCallback.accept(1.0); // Kész

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ OTP szerver indítása sikertelen: " + e.getMessage());
        }
    }


}
