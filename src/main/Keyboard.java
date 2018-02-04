package main;

import static org.lwjgl.glfw.GLFW.*;
public class Keyboard {

	private boolean[] keys = new boolean[16];
	private boolean pressedFlag = false;
	int lastKey =-1;
	
	public static final int[] FONT = {
			0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x50, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
	};
	
	public Keyboard() {
		for (int i = 0; i < 15; i++) {
			keys[i] = false;
		}
	}
	
	public boolean setKeyDown(int k) {
		pressedFlag=true;
		
		switch (k) {
		case GLFW_KEY_1:
			keys[0] = true;
			lastKey = 0;
			break;
		case GLFW_KEY_2:
			keys[1] = true;
			lastKey = 1;
			break;
		case GLFW_KEY_3:
			keys[2] = true;
			lastKey = 2;
			break;
		case GLFW_KEY_4:
			keys[3] = true;
			lastKey = 3;
			break;
		case GLFW_KEY_Q:
			keys[4] = true;
			lastKey = 4;
			break;
		case GLFW_KEY_W:
			keys[5] = true;
			lastKey = 5;
			break;
		case GLFW_KEY_E:
			keys[6] = true;
			lastKey = 6;
			break;
		case GLFW_KEY_R:
			keys[7] = true;
			lastKey = 7;
			break;
		case GLFW_KEY_A:
			keys[8] = true;
			lastKey = 8;
			break;
		case GLFW_KEY_S:
			keys[9] = true;
			lastKey = 9;
			break;
		case GLFW_KEY_D:
			keys[10] = true;
			lastKey = 10;
			break;
		case GLFW_KEY_F:
			keys[11] = true;
			lastKey = 11;
			break;
		case GLFW_KEY_Z:
			keys[12] = true;
			lastKey = 12;
			break;
		case GLFW_KEY_X:
			keys[13] = true;
			lastKey = 13;
			break;
		case GLFW_KEY_C:
			keys[14] = true;
			lastKey = 14;
			break;
		case GLFW_KEY_V:
			keys[15] = true;
			lastKey = 15;
			break;
		default:
			break;
		}
		
        return true;
	}
	
	public boolean setKeyUp(int k) {
		switch (k) {
		case GLFW_KEY_1:
			keys[0] = false;
			break;
		case GLFW_KEY_2:
			keys[1] = false;
			break;
		case GLFW_KEY_3:
			keys[2] = false;
			break;
		case GLFW_KEY_4:
			keys[3] = false;
			break;
		case GLFW_KEY_Q:
			keys[4] = false;
			break;
		case GLFW_KEY_W:
			keys[5] = false;
			break;
		case GLFW_KEY_E:
			keys[6] = false;
			break;
		case GLFW_KEY_R:
			keys[7] = false;
			break;
		case GLFW_KEY_A:
			keys[8] = false;
			break;
		case GLFW_KEY_S:
			keys[9] = false;
			break;
		case GLFW_KEY_D:
			keys[10] = false;
			break;
		case GLFW_KEY_F:
			keys[11] = false;
			break;
		case GLFW_KEY_Z:
			keys[12] = false;
			break;
		case GLFW_KEY_X:
			keys[13] = false;
			break;
		case GLFW_KEY_C:
			keys[14] = false;
			break;
		case GLFW_KEY_V:
			keys[15] = false;
			break;
		default:
			break;
		}
		
        return true;
	}

	public boolean isPressed(int j) {
		return keys[j];
	}
	
	public void setFlag(boolean b) {
		pressedFlag = b;
	}
	
	public boolean getFlag() {
		return pressedFlag;
	}

	public int getLastKey() {
		return lastKey;
	}
}
