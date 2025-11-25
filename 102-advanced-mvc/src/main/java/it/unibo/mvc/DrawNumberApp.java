package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private static Configuration config;
    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }

        config = setConfigurationFromFile();

        this.model = new DrawNumberImpl(config.getMin(), config.getMax(), config.getAttempts());
    }

    private Configuration setConfigurationFromFile() {

        final Configuration.Builder builder = new Configuration.Builder();

        final ClassLoader cl = getClass().getClassLoader();
        try (
            final InputStreamReader is = new InputStreamReader(cl.getResourceAsStream("config.yml"));
            final BufferedReader in = new BufferedReader(is)
        ) {
            String sMin = in.readLine().split(":")[1]; //Save only the second part of the String, the one containing the number.
            String sMax = in.readLine().split(":")[1];
            String sAttempts = in.readLine().split(":")[1];

            builder.setMin(Integer.parseInt(sMin));
            builder.setMax(Integer.parseInt(sMax));
            builder.setAttempts(Integer.parseInt(sAttempts));
        } catch (IOException e) {
            System.err.println(e);
        }
        
        return builder.build();
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl());
    }

}
