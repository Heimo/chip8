package main;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Chip8 {
		
		Window screen ;
	
		private int opcode;
		private int[] memory = new int[4096];
		private int[] V = new int[16];
		
		private int I;
		private int pc;
		
		
		private int delay_timer;
		private int sound_timer;
		
		private int[] stack = new int[16];
		private int sp;
		
		
		
		Random r = new Random();
		
		Keyboard k;
		
		public Chip8(Keyboard k) {
			this.k = k;
			pc = 0x200;
			I = 0;
			sp = 0;
			opcode = 0;
			
			for(int i=0;i< memory.length;i++) {
				memory[i] = 0;
			}
			
			for(int i=0;i< V.length;i++){
				V[i] = 0;
			}
			for(int i=0; i<80;i++) {
				memory[i] = Keyboard.FONT[i];
			}
			
			delay_timer = 0;
			sound_timer = 0;
		}
		
		public void setScreen(Window screen) {
			this.screen = screen;
		}
		
		public void loadProgram(byte[] b) {
			for (int i = 0; i < b.length; i++) {
				memory[i + 512] = (b[i] & 0xFF);
				System.out.println(b[i] & 0xFF);
			}
		}
		
		public void emulateCycle() {
			
			//optional variables
			int x = 0;
			int y = 0;
			
			//get next opcode
			opcode = ((memory[pc] << 8) | (memory[pc + 1]));
			System.out.println(Integer.toHexString(opcode));
			//check opcode
			
			switch(opcode) {
			
			case 0x00E0:
				// Clear display
				screen.clear();
				pc += 2;
				
				return;
			case 0x00EE:
				//	Returns from a subroutine
				pc = stack[sp--];
				pc += 2;
				
				return;
			}
			
			switch(opcode & 0xF000) {
			case 0x1000:
				//Jumps to address NNN
				pc = opcode & 0x0FFF;
				
				return;
			case 0x2000:
				//Calls subroutine at NNN
				stack[++sp] = pc;
				pc = opcode & 0x0FFF;
				
				return;
			case 0x3000:
				// 3XNN - Skips the next instruction if VX equals NN. (Usually the next instruction is a jump to skip a code block)
				if (V[(opcode & 0x0F00) >>> 8] == (opcode & 0x00FF)) {
					pc += 4;
				} else {
					pc += 2;
				}
				
				return;
			case 0x4000:
				//4XNN - Skips the next instruction if VX doesn't equal NN. (Usually the next instruction is a jump to skip a code block)
				if (V[(opcode & 0x0F00) >>> 8] == (opcode & 0x00FF)) {
					pc += 2;
				} else {
					pc += 4;
				}
				
				return;
			case 0x5000:
				//5XY0 Skips the next instruction if VX equals VY
				if(V[(opcode & 0x0F00) >>> 8] == V[(opcode & 0x00F0) >>> 4]) {
					pc += 4;
				}else {
					pc +=2;
				}
				
				return;
			case 0x6000:
				//6XNN Sets VX to NN
				V[(opcode & 0x0F00) >>> 8] = opcode & 0x00FF;
				
				pc+=2;
				return;
			case 0x7000:
				//7XNN Adds NN to VX. (Carry flag is not changed)
				x = opcode & 0x0F00;
				int nn = opcode & 0x00FF;
				int result = V[x] + nn;
				if( result >256) {
					V[x] = result - 256;
				}else {
					V[x] = result;
				}
				
				pc+=2;
				return;
			}
			
			switch(opcode & 0xF00F) {
			
			case 0x8000:
				//8XY0	Sets VX to the value of VY
				V[(opcode & 0x0F00) >>> 8] = V[(opcode & 0x00F0) >>> 4];
				
				pc+=2;
				return;
			case 0x8001:
				//8XY1	Sets VX to VX or VY. (Bitwise OR operation)
				x = (opcode & 0x0F00) >>> 8;
				y = (opcode & 0x00F0) >>> 4;
				
				V[x] = V[x] | V[y];
				
				pc+=2;
				return;
			case 0x8002:
				//8XY2	Sets VX to VX and VY. (Bitwise AND operation)
				x = (opcode & 0x0F00) >>> 8;
				y = (opcode & 0x00F0) >>> 4;
				
				V[x] = V[x] & V[y];
				
				pc+=2;
				return;
			case 0x8003:
				//8XY3	Sets VX to VX xor VY.
				x = (opcode & 0x0F00) >>> 8;
				y = (opcode & 0x00F0) >>> 4;
				
				V[x] = V[x] ^ V[y];
				
				pc+=2;
				return;
			case 0x8004:
				//8XY4	Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
				x = (opcode & 0x0F00) >>> 8;
				y = (opcode & 0x00F0) >>> 4;
				int sum = V[x] + V[y];
				
				V[0xF] = (sum > 0xFF) ? 1 : 0;
				V[x] = sum & 0xFF;
				
				pc+=2;
				return;
			case 0x8005:
				//8XY5	VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
				x = (opcode & 0x0F00) >>> 8;
				y = (opcode & 0x00F0) >>> 4;

				if(V[y] > V[x]) {
					V[0xF] = 0;
				}else {
					V[0xF] = 1;
				}
				V[x] = (V[x] - V[y]) & 0xFF;
				
				pc+=2;
				return;
			case 0x8006:
				//8XY6	Shifts VY right by one and copies the result to VX. VF is set to the value of the least 
				//      significant bit of VY before the shift. (VX on modern interpreters)
				x = (opcode & 0x0F00) >>> 8;
				V[0xF] = (V[x] & 0x1) == 1 ? 1 : 0;
				V[x] = V[x] >>> 1;
				
				pc+=2;
				return;
			case 0x8007:
				//8XY7	Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
				x = (opcode & 0x0F00) >>> 8;
				y = (opcode & 0x00F0) >>> 4;

				if(V[x] > V[y]) {
					V[0xF] = 0;
				}else {
					V[0xF] = 1;
				}
				V[x] = (V[y] - V[x]) & 0xFF;
				
				pc+=2;
				return;
			case 0x800E:
				//8XYE  Shifts VY left by one and copies the result to VX. VF is set to the value of the most 
				//      significant bit of VY before the shift. (VX on modern interpreters)
				x = (opcode & 0x0F00) >>> 8;
				V[0xF] = (V[x] >>> 7) == 1 ? 1 : 0;
				V[x] = (V[x] << 1) & 0xFF;
				
				pc+=2;
				return;
			case 0x9000:
				//9XY0	Skips the next instruction if VX doesn't equal VY. (Usually the next instruction is a jump to skip a code block)
				x = (opcode & 0x0F00) >>> 8;
				y = (opcode & 0x00F0) >>> 4;
				
				if(V[x] != V[y]) {
					pc += 4;
				}else {
					pc += 2;
				}
				return;
			}
			
			switch(opcode & 0xF000) {
			
			case 0xA000:
				//ANNN	Sets I to the address NNN.
				I = opcode & 0xFFF;

				pc+=2;
				return;
			case 0xB000:
				//BNNN	Jumps to the address NNN plus V0.
				pc = (opcode & 0xFFF) + V[0];

				return;
			case 0xC000:
				//CXNN	Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN.
				x = (opcode & 0x0F00) >>> 8;
				V[x] = r.nextInt(256) & (opcode & 0xFF);
				
				pc+=2;
				return;
			case 0xD00:
				//DXYN	Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels.
				//      Each row of 8 pixels is read as bit-coded starting from memory location I; 
				//      I value doesn’t change after the execution of this instruction. 
				//        As described above, VF is set to 1 if any screen pixels are flipped from set to unset when the sprite is drawn, 
				//      and to 0 if that doesn’t happen
				// TODO finish 0xD00
				x = V[(opcode & 0x0F00) >>> 8];
				y = V[(opcode & 0x00F0) >>> 4];
				int height = opcode & 0xF;
				int pixel;
				V[0xF] = 0;
				
				for(int ypos=0;ypos <height;ypos++) {
					
					pixel = memory[I + ypos];
					for(int xpos=0;xpos <8;xpos++) {
						
						if((pixel & (0x80 >>> xpos)) != 0) {
							 if (screen.getPixel(x + xpos, y + ypos) == 1) {
		                            V[0xF] = 1;
		                        }
						}
						
					}
					
				}
				
				pc += 2;
				return;
			}
			
			switch(opcode & 0xF0FF) {
			
			case 0xE09E:
				//EX9E	Skips the next instruction if the key stored in VX is pressed. (Usually the next instruction is a jump to skip a code block)
				x = (opcode & 0x0F00) >>> 8;
				
				if(k.isPressed(V[x])) {
					pc += 4;
				}else {
					pc += 2;
				}
				
				return;
			case 0xE0A1:
				//EXA1	Skips the next instruction if the key stored in VX isn't pressed. 
				x = (opcode & 0x0F00) >>> 8;
				
				if(k.isPressed(V[x])) {
					pc += 2;
				}else {
					pc += 4;
				}
				
				return;
			case 0xF007:
				//FX07	Sets VX to the value of the delay timer.
				x = (opcode & 0x0F00) >>> 8;
				V[x] = delay_timer & 0xFF;
				
				pc += 2;
				return;
			case 0xF00A:
				//FX0A	A key press is awaited, and then stored in VX. (Blocking Operation. All instruction halted until next key event)
				x = (opcode & 0x0F00) >>> 8;
				k.setFlag(false);
				
				while(k.getFlag()==false) {
				}
				V[x] = k.getLastKey();
				
				pc += 2;
				return;
			case 0xF015:
				//FX15	Sets the delay timer to VX.
				x = (opcode & 0x0F00) >>> 8;
				this.delay_timer = V[x];

				pc +=2;
				return;
			case 0xF018:
				//FX18	sound_timer(Vx)	Sets the sound timer to VX.
				x = (opcode & 0x0F00) >>> 8;
				this.sound_timer = V[x];

				pc +=2;
				return;
			case 0xF01E:
				//FX1E	Adds VX to I.[3]
				x = (opcode & 0x0F00) >>> 8;
				
				if((I+V[x]) > 0xFFF) {
					V[0xF] = 1;
				}else {
					V[0xF] = 0;
				}
				I = (I + V[x]) & 0xFFF;
				
				pc +=2;
				return;
			case 0xF029:
				//FX29	Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font.
				x = (opcode & 0x0F00) >>> 8;
				I = V[x] * 0x5;
				
				pc += 2;
				return;
			case 0xF033:
				//FX33	Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, 
				//      the middle digit at I plus 1, and the least significant digit at I plus 2. 
				//      (In other words, take the decimal representation of VX, place the hundreds digit in memory at location in I, 
				//      the tens digit at location I+1, and the ones digit at location I+2.)
				x = (opcode & 0x0F00) >>> 8;
				memory[I] = (V[x] / 100);
				memory[I + 1] = ((V[x] % 100) / 10);
				memory[I + 2] = ((V[x] % 100) % 10);
				
				pc += 2;
				return;
			case 0xF055:
				//FX55	Stores V0 to VX (including VX) in memory starting at address I. I is increased by 1 for each value written.
				x = (opcode & 0x0F00) >>> 8;
				for(int i=0;i<=x;i++) {
					memory[I] = V[x];
					//TODO maybe not true :thinking:
					I++;
				}
				
				pc += 2;
				return;
			case 0xF065:
				//FX65	Fills V0 to VX (including VX) with values from memory starting at address I. I is increased by 1 for each value written.
				x = (opcode & 0x0F00) >>> 8;
				for(int i=0;i<=x;i++) {
					V[x] = memory[I] & 0xFF;
					//TODO maybe not true :thinking:
					I++;
				}
				
				pc += 2;
				return;
			}
			
		}

		public void updateTimers() throws InterruptedException {
			if(sound_timer != 0x0) {
				TimeUnit.MILLISECONDS.sleep(delay_timer * 16);
				sound_timer = 0;
			}
			
			if(delay_timer != 0x0) {
				TimeUnit.MILLISECONDS.sleep(delay_timer * 16);
				delay_timer = 0;
			}
			
		}
		
}