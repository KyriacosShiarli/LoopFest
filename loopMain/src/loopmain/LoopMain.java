package loopmain;

import mask.Mask;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import imagetoparticles.ImageToParticles;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;


public class LoopMain extends PApplet {
boolean visualOne=false;

Mask visualMask;
ImageToParticles visualImageToParticles;
Minim genMinim;
AudioInput audioIn;

OscP5 oscP5;

	public void setup() {
		size(1280,768,P3D);
		oscP5 = new OscP5(this,8000);
		genMinim = new Minim(this);
		audioIn = genMinim.getLineIn(Minim.STEREO,512,20000);
		visualMask=new Mask(this,genMinim,audioIn);
		visualMask.setup();
		visualImageToParticles = new ImageToParticles(this, genMinim,audioIn);
		visualImageToParticles.setup();
	}

	public void draw() {
		if (visualOne==true){
			visualMask.draw();			
		}
		else{
			visualImageToParticles.draw();
		}
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { "--present",loopmain.LoopMain.class.getName() });
	}
	
	public void stop()	{
		  
		  audioIn.close();
		  genMinim.stop();
		  super.stop();
		}

	public void oscEvent(OscMessage theOscMessage){//oscP5 variables in class files should not be initialised
		String address = theOscMessage.addrPattern();
		if(address.indexOf("/1/toggle")!=-1){
			String list[]  = PApplet.split(address,'/');
			int toggle = Math.round(list[2].charAt(6) - 0x30);
			if(toggle==4){
				visualOne=!visualOne;
			}
		}
		if (visualOne==true){
				if(visualMask.aistemeisixo==false){
					visualMask.oscEvent(theOscMessage);
				}		
		}
		else{
			visualImageToParticles.oscEvent(theOscMessage);
		}
				
	}
			
}
