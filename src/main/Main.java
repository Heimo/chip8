package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Main {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Keyboard k = new Keyboard();
		Chip8 c8 = new Chip8(k);
		c8.loadProgram(Files.readAllBytes(Paths.get("C:/Users/Emil/Desktop/c8games/PONG")));
		TimeUnit.MILLISECONDS.sleep(5000);
		Window screen = new Window(k,c8);
		screen.run();
		System.out.println("Program ended");
		
	}	
	
}
