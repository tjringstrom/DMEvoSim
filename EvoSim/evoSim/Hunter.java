package evoSim;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Hunter extends Globals {
	Hex current;
	int health = 100;
	BufferedImage imgHunter = null;{ //FIX??
		
	try {
		imgHunter = ImageIO.read(new File("mouse000.jpg"));
	} catch(Exception ex){}
	}
	
	//movement
	boolean hunterMoved = true;
	boolean q = false;
	boolean w = false;
	boolean e = false;
	boolean a = false;
	boolean s = false;
	boolean d = false;
	
	Hunter(Hex currentHex) {
		current = currentHex;
		current.hunter = this;
	}
	
	public void hit(int damage){
		health -= damage;
		if(health<0){
			health = 0;
		}
	}
	
	public void gainHealth(int amount){
		this.health += amount;
		if(this.health > 100){
			this.health = 100;
		}
	}
	
	public void moveHunterAuto(int move){ //controls the hunters ability to move automatically wrt probability samples.  moveHunter() just executes the move
		move++;
		System.out.println("Mouse at: " + this.current.id + "  MOUSE DECISION: " + move);
		switch (move) {
		case 0:
			this.hunterMoved = true; //no move
		case 1:
			if(this.current.ul != null){
				this.current = this.current.ul;
			}
			this.hunterMoved = true;
			break;
		case 2:
			if(this.current.u != null){
				this.current = this.current.u;
			}
			this.hunterMoved = true;
			break;
		case 3:
			if(this.current.ur != null){
				this.current = this.current.ur;
			}
			this.hunterMoved = true;
			break;
		case 4:
			if(this.current.br != null){
				this.current = this.current.br;
			}
			this.hunterMoved = true;
			break;
		case 5:
			if(this.current.b != null){
				this.current = this.current.b;
			}
			this.hunterMoved = true;
			break;
		case 6:
			if(this.current.bl != null){
				this.current = this.current.bl;
			}
			this.hunterMoved = true;
			break;

		default:
			this.hunterMoved = false;
			break;
		}
	}
	
	public void moveHunter (){//probably should have used switch 
		if(q == true && this.current.ul != null){
			clearControlsExcept('q');
			this.current = this.current.ul;
			q = false;
			this.hunterMoved = false;
		}
		else if(w == true && this.current.u != null){
			clearControlsExcept('w');
			this.current = this.current.u;
			w = false;
			this.hunterMoved = false;
		}
		else if(e == true && this.current.ur != null){
			clearControlsExcept('e');
			this.current = this.current.ur;
			e = false;
			this.hunterMoved = false;
		}
		else if(d == true && this.current.br != null){
			clearControlsExcept('d');
			this.current = this.current.br;
			d = false;
			this.hunterMoved = false;
		}
		else if(s == true && this.current.b != null){
			clearControlsExcept('s');
			this.current = this.current.b;
			s = false;
			this.hunterMoved = false;
		}
		else if(a == true && this.current.bl != null){
			clearControlsExcept('a');
			this.current = this.current.bl;
			a = false;
			this.hunterMoved = false;
		}
	}
	
	public void clearControlsExcept(char direction){
 		q = false;
		w = false;
		e = false;
		d = false;
		s = false;
		a = false;
		if(direction == 'q'){
			q = true;
		}
		if(direction == 'w'){
			w = true;
		}
		if(direction == 'e'){
			e = true;
		}
		if(direction == 'd'){
			d = true;
		}
		if(direction == 's'){
			s = true;
		}
		if(direction == 'a'){
			a = true;
		}
	}

	
	void moveHunter(int key) {
		if (key == KeyEvent.VK_D && current.br != null) {
			current.hunter = null;
			current.br.hunter = this;
			current = current.br;
		}
		if (key == KeyEvent.VK_S && current.b != null) {
			current.hunter = null;
			current.b.hunter = this;
			current = current.b;
		}
		if (key == KeyEvent.VK_A && current.bl != null) {
			current.hunter = null;
			current.bl.hunter = this;
			current = current.bl;
		}
		if (key == KeyEvent.VK_Q && current.ul != null) {
			current.hunter = null;
			current.ul.hunter = this;
			current = current.ul;
		}
		if (key == KeyEvent.VK_W && current.u != null) {
			current.hunter = null;
			current.u.hunter = this;
			current = current.u;
		}
		if (key == KeyEvent.VK_E && current.ur != null) {
			current.hunter = null;
			current.ur.hunter = this;
			current = current.ur;
		}

	}

}
