package main;

public class Main {
    public static void main(String[] args) {
        OpenGLApp app = new OpenGLApp();
        app.init();         // initialise application
        app.renderLoop();   // rendering loop
        app.terminate();    // terminate application
    }
}
