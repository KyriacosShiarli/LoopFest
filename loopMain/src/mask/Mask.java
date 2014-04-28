package mask;

import oscP5.OscMessage;
import oscP5.OscP5;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.*;




public class Mask {
//image variables
	PApplet parent;
	PGraphics image_up;
	PGraphics image_down;
	PGraphics image_up_flipped;
	PGraphics image_down_flipped;
	PImage masked_image;
	PGraphics mask;
	int n_blocks=3; 
	PGraphics[] image_block_up;
	PGraphics[] image_block_down;
	PGraphics[] image_block_up_flipped;
	PGraphics[] image_block_down_flipped;
	PGraphics[]	display_block;
	//
	PGraphics[] image_block_up3;
	PGraphics[] image_block_down3;
	PGraphics[] image_block_up_flipped3;
	PGraphics[] image_block_down_flipped3;
	//
	PGraphics[] image_block_up15;
	PGraphics[] image_block_down15;
	PGraphics[] image_block_up_flipped15;
	PGraphics[] image_block_down_flipped15;
	//
//mask related variables
	int inner_circle=0;
	int circle_x;
	int circle_y;
	int num_arcs=8;
	float overlap=0.2f;
	float starting_theta=0;
	int rotation_speed=40; //the lower the more it rotatesl
	int background_mask=100;
//block manipulation variables
	boolean bool_n_blocks=false;
	float [] rotation_counter;
	int [] roll_counter;
	boolean[] roll_right;
	boolean[] rotate_up;
	int h_block;
	int w_block;
	float separation=1.02f;
	 int block;//index used to cycle between the different blocks
	boolean rotate=true; 
	boolean roll=false;
//FFT declarations
	float sensitivity=5f;//difference between the two will look as if circle is on a different plane
	public Minim minim;
	public AudioInput audioIn;
	AudioPlayer audioPlay;
	public FFT fft;
	int bassMap,midMap,trebleMap,allMap;
//arrays for moving averages
	int moving_average_window=7;
	float [] avg1= new float[16];
	float [] avg2= new float[16];
	float [] avg3= new float[16];
	float [] avg4= new float[16];
	float [] avg5= new float[16];
	float [] avg6= new float[16];
	float [] avg7= new float[16];
	float [] avg8= new float[16];
	float [] avg9= new float[16];
	float [] avg10= new float[16];
	float [] avg11= new float[16];
	float [] avg12= new float[16];
	float [] avg13= new float[16];
	float [] avg14= new float[16];
	float [] avg15= new float[16];
	float [][] avg= new float[][]{avg1,avg2,avg3,avg4,avg5,avg6,avg7,avg8,avg9,avg10,avg11,avg12,avg13,avg14,avg15};
	float[] allMap_avg=new float[60];
//
int red=230;
int blue=50;
int green=250;
float brightness=100;
float block_threshold=3;
//
OscP5 oscP5;
public Boolean aistemeisixo=false;





	public Mask(PApplet p,Minim tempMinim,AudioInput tempAudioIn){
		parent = p;
		minim=tempMinim;
		audioIn = tempAudioIn;
	}
//
	public void setup() {
		parent.frameRate(20);
		//loading the images to be used
		int image_width=(int) (parent.width/separation);
		int image_height=(int) (parent.height/separation);
		w_block=Math.round(image_width/n_blocks); //declare block size
		h_block=Math.round(image_height/n_blocks); 
		//setting up the background arrays
	 	image_block_up= new PGraphics [25];
		image_block_down= new PGraphics [25];
		image_block_up_flipped= new PGraphics [25];
		image_block_down_flipped= new PGraphics [25];
		display_block=new PGraphics [100];
		//
		image_block_up3= new PGraphics [25];
		image_block_down3= new PGraphics [25];
		image_block_up_flipped3= new PGraphics [25];
		image_block_down_flipped3= new PGraphics [25];
		
		image_block_up15= new PGraphics [25];
		image_block_down15= new PGraphics [25];
		image_block_up_flipped15= new PGraphics [25];
		image_block_down_flipped15= new PGraphics [25];
		//
		
		rotation_counter=new float [25];
		roll_counter=new int [25];
		roll_right=new boolean [25];
		rotate_up=new boolean [25];
	//
	 image_up=parent.createGraphics(image_width,image_height);
	 image_up.beginDraw();
	 PImage temp=parent.loadImage("silvio6.jpg");
	 temp.resize(image_width, image_height);
	 image_up.image(temp,0,0);
	 image_up.endDraw();
	 
	 image_down=parent.createGraphics(image_width,image_height);
	 image_down.beginDraw();
	 temp=parent.loadImage("silvio6.jpg");
	 temp.resize(image_width, image_height);
	 image_down.image(temp,0,0);
	 image_down.filter(PConstants.INVERT);
	 image_down.endDraw();
	 

	 masked_image=parent.loadImage("silvio6.jpg");
	// masked_image.filter(GRAY);
	 masked_image.resize(parent.width,parent.height);
	 
	 circle_x=parent.width/2;
	 circle_y=parent.height/2;

//create a mask of the same size as the background and make it black
		mask=parent.createGraphics(parent.width,parent.height);
		mask.beginDraw();
		mask.background(0f);
		mask.endDraw();
		
//FFT stuff
	 	 fft= new FFT(audioIn.bufferSize(),audioIn.sampleRate());
		 fft.logAverages(20,2);
// create block images
		 image_block_up=create_blocks(n_blocks,image_up,true);
		 image_block_down=create_blocks(n_blocks,image_down,true);
		 image_block_up_flipped=create_blocks(n_blocks,image_up,false);
		 image_block_down_flipped=create_blocks(n_blocks,image_down,false);
		 for (int i=0;i<n_blocks*n_blocks;i++){			 
			 display_block[i]=parent.createGraphics(w_block,h_block);
			 display_block[i].beginDraw();
			 image_block_down[i].beginDraw();
			 display_block[i].image(image_block_down[i].get(),0,0);
			 image_block_down[i].endDraw();
			 display_block[i].endDraw();
		 }
		 //
		 image_block_up3=create_blocks(3,image_up,true);
		 image_block_down3=create_blocks(3,image_down,true);
		 image_block_up_flipped3=create_blocks(3,image_up,false);
		 image_block_down_flipped3=create_blocks(3,image_down,false);
		 
		 image_block_up15=create_blocks(5,image_up,true);
		 image_block_down15=create_blocks(5,image_down,true);
		 image_block_up_flipped15=create_blocks(5,image_up,false);
		 image_block_down_flipped15=create_blocks(5,image_down,false);
		 
//initialising counter for rotation
	for (int i=0;i<rotation_counter.length;i++){
		rotation_counter[i]=0;
			}
//interface
	//oscP5 = new OscP5(this,8000);
	}
	public PGraphics[] create_blocks(int num_of_blocks,PGraphics whole_image,boolean notinvert){
		int block=0;
		int wBlock=whole_image.width/num_of_blocks;
		int hBlock=whole_image.height/num_of_blocks;
		PGraphics[] blocks= new PGraphics [num_of_blocks*num_of_blocks];
	if (notinvert){
		for (int i=0;i<num_of_blocks;i++){
			for (int k=0; k<num_of_blocks;k++){
					block=num_of_blocks*i+k;
					blocks[block]=parent.createGraphics(wBlock,hBlock);
					blocks[block].beginDraw();
					whole_image.beginDraw();
					blocks[block].image(whole_image.get(k*wBlock,i*hBlock,wBlock,wBlock),0,0);//divide the original image into 9 blocks
					whole_image.endDraw();
					blocks[block].endDraw();
					}
				}
			}
	else {//creating the fllipped  version of the image
		for (int m=0;m<num_of_blocks;m++){
			for (int n=0; n<num_of_blocks;n++){
					block=num_of_blocks*m+n;
					blocks[block]=parent.createGraphics(wBlock,hBlock);
					
					PGraphics temp =parent.createGraphics(wBlock,hBlock);
					temp.beginDraw();
					whole_image.beginDraw();
					temp.image(whole_image.get(n*wBlock,m*hBlock,wBlock,hBlock),0,0);//divide the original image into 9 blocks
					whole_image.endDraw();
					temp.endDraw();
					for (int l=0;l<image_up.height;l++){
						blocks[block].beginDraw();
						 blocks[block].copy(temp, 0, l, temp.width, 1,0,blocks[block].height-l, blocks[block].width, 1);
						 blocks[block].endDraw();
							 }
					temp=null;
			}
				}
	}
	 return blocks;
 }
	public void draw() {
//redrawing the background and the mask
		parent.background(0);	
//FFT
		fft.forward(audioIn.mix);
		map_frequencies();
//creating background blocks
		parent.tint(brightness); //setting transparency to being half opaque
		manipulate_blocks();
		parent.noTint();//transparency=none
 //mask arcs
		parent.tint(red,green,blue);
		edit_mask();		
//displaying the masked image
		
		mask.beginDraw();
		masked_image.mask(mask.get());
		parent.pushMatrix();
		parent.translate(0,0,150);
		parent.image(masked_image,0,0);		
		parent.popMatrix();
		mask.endDraw();
		parent.noTint();
	
	}
	public void start_screen(){
		parent.tint(20f);
		parent.textSize(32);
		parent.text("Play and see what happens", 100, 100); 
		parent.text("Παίξε τζιαι δε δάμπου εννά γίνει", 500, 300); 
	}	
	public float moving_average(float[] array,float new_value,int mov_ave_window){
		
		for (int j=mov_ave_window-1;j>0;j--){
			array[j]=array[j-1];
		}
		array[0]=new_value;
		float average=0;
		for (int i=0;i<(mov_ave_window)-1;i++){
			average=average+array[i];
		}
		return (average/mov_ave_window);
	}
	public void mask_rectangles(int sx,int sy,int r_width, int r_height){
		mask.beginDraw();
		mask.fill(250f);
		mask.rect((float)sx,(float)sy,(float)r_width,(float)r_height);
		mask.endDraw();
		}
	public void mask_arc(int sx,int sy,int r_width, int r_height,float theta1, float theta2){
		parent.stroke(0);
		mask.beginDraw();
		mask.fill(255f);
		mask.arc((float)sx,(float)sy,(float)r_width,(float)r_height,theta1,theta2,PConstants.PIE);// ston kiko vale mask.arc((float)sx,(float)sy,(float)r_width,(float)r_height,theta1,theta2,PIE);
		mask.endDraw();
		}
	public void map_frequencies(){
		//bass map
		bassMap=0;
		for (int i=0; i<4;i++){ //moving averages
			if ( fft.getAvg(i)> bassMap){
				bassMap = (int)fft.getAvg(i);
				}
			}
		
		//mid map
		midMap=0;
		for (int i=5; i<10;i++){
			if ( fft.getAvg(i)> midMap){
				midMap = (int) fft.getAvg(i);
	
			}
		}
		
		//treble map
		trebleMap=0;
		for (int i=11; i<fft.avgSize();i++){
			if ( fft.getAvg(i)> trebleMap){
						trebleMap = (int) (4* fft.getAvg(i));
					}
				}
		
	//All map
		allMap=bassMap+midMap+trebleMap;
		allMap=(int)moving_average(allMap_avg,(float)allMap,allMap_avg.length);
		
	}
	public PImage slide_image(PImage slide1, PImage slide2,int position){//takes in two images, and returns an image which is a mixture of the two
		PImage result=slide1.get();
		result.copy(slide2, 0, 0, position, slide2.height, 0, 0, position, slide2.height);
		
		return result;
	}
	public PGraphics rollimage_right(PGraphics rolling_image,PGraphics static_image,int num_pixels_h,int position){
			parent.imageMode(PConstants.CORNER);
			static_image.beginDraw();
			rolling_image.beginDraw();
			static_image.copy(static_image.get(),0,0,static_image.width-num_pixels_h,static_image.height,num_pixels_h,0,static_image.width-num_pixels_h,static_image.height);
			static_image.copy(rolling_image.get(),position,0,num_pixels_h,rolling_image.height,0,0,num_pixels_h,static_image.height);//Copy the first column,
			static_image.endDraw();
			rolling_image.endDraw();
			return static_image;
		
		}
	public  PGraphics rollimage_left(PGraphics rolling_image,PGraphics static_image,int num_pixels_h,int position){
		parent.imageMode(PConstants.CORNER);
		static_image.beginDraw();
		rolling_image.beginDraw();
		static_image.copy(static_image.get(),num_pixels_h,0,static_image.width-num_pixels_h,static_image.height,0,0,static_image.width-num_pixels_h,static_image.height);
		static_image.copy(rolling_image.get(),rolling_image.width-position,0,num_pixels_h,rolling_image.height,static_image.width-num_pixels_h,0,num_pixels_h,static_image.height);//Copy the first column,
		static_image.endDraw();
		rolling_image.endDraw();
		return static_image;
		
	}
	public void edit_mask(){
		 mask.beginDraw();
		 mask.background(0);
		 mask.endDraw();
		 float mov_ave;
		 for (int i=0;i<num_arcs;i++){
		 
	 if (i<4){ //creating arcs based on individual bands
		  mov_ave = moving_average(avg[i],fft.getAvg(i),moving_average_window);// create a moving average value of the FFT to make it smoother, starting from average 1 to ignore the DC value of FFT
		}
		else{
			if(i<9){
			 mov_ave = moving_average(avg[i],2*fft.getAvg(i),moving_average_window);//multiply fff.getAvg by a factor to get different weightings
			}
			else { mov_ave = moving_average(avg[i],3*fft.getAvg(i),moving_average_window);
			}
		}
		
	//creating the mask
	 if (mov_ave>0.1f){
	
		int circle_xx=(int)( circle_x +inner_circle*PApplet.cos(i*2*PApplet.PI/num_arcs+starting_theta+PApplet.PI/num_arcs)); // to separate the different sectors
		int circle_yy=(int)(circle_y + inner_circle*PApplet.sin(i*2*PApplet.PI/num_arcs+starting_theta+PApplet.PI/num_arcs));
		int width_x=(int)(PApplet.map(mov_ave,1,80,10,100)*sensitivity);
		int width_y=(int)(PApplet.map(mov_ave,1,80,10,100)*sensitivity);
		float starting_angle=(i)*2*PApplet.PI/num_arcs+starting_theta;
		float ending_angle=(i+1)*((2f+overlap)*PApplet.PI/num_arcs)+starting_theta;
		
		 mask_arc(circle_xx,circle_yy,width_x,width_y,starting_angle,ending_angle);
		 	
	 } 
	}
	
		starting_theta+=(midMap/rotation_speed);//rotates according to the music
	  
		//masking in the corners
		 
		// mask_arc(0,0,allMap,allMap,0,PI/2);
		//mask_arc(width,0,allMap,allMap,PI/2,PI);
		//mask_arc(width,height,allMap,allMap,PI,3*PI/2);
		//mask_arc(0,height,allMap,allMap,3*PI/2,2*PI); 
	 }
	public void manipulate_blocks(){
		
		 fftMapToBlock();
		 for (int i=0;i<n_blocks;i++){//integers i and k are used to place blocks in their position on the screen 
				for (int k=0; k<n_blocks;k++){ // i is column, k is row
					block=n_blocks*i+k;
						if (rotate==true){rotate_block(i,k);}
						else {if (roll==true){roll_block(i,k);}}
						}	
				}
	 }
	public void roll_block(int i, int k){
		 	if((roll_counter[block]>0)){
			display_block[block].beginDraw();
			if(roll_right[block]==true){
				image_block_down[block].beginDraw();
				display_block[block]=rollimage_right(image_block_down[block],display_block[block],40,(int)roll_counter[block]);
				roll_counter[block]-=40;
				if (roll_counter[block]<40){
					display_block[block]=rollimage_right(image_block_down[block],display_block[block],40,(int)roll_counter[block]);
					display_block[block].image(image_block_down[block].get(),0,0);
					roll_counter[block]=0;
					 rotate_up[block]=!rotate_up[block];
					 roll_right[block]=!roll_right[block];
					}
				image_block_down[block].endDraw();
			}
			else {if (roll_right[block]==false){//default value of roll_right is false
				image_block_up[block].beginDraw();
				display_block[block]=rollimage_left(image_block_up[block],display_block[block],40,(int)roll_counter[block]);
				roll_counter[block]-=40;
				if (roll_counter[block]<40){
					display_block[block].image(image_block_up[block].get(),0,0);
					roll_counter[block]=0;
					 rotate_up[block]=!rotate_up[block];
					 roll_right[block]=!roll_right[block];
					}
				image_block_up[block].endDraw();
				}
			}
			parent.pushMatrix();
			parent.translate(separation*k*w_block,separation*i*h_block);
			parent.image(display_block[block],0,0);
			display_block[block].endDraw();
			parent.popMatrix();
						}
			else{
			parent.pushMatrix();
			parent.translate(separation*k*w_block,separation*i*h_block);
			display_block[block].beginDraw();
			parent.image(display_block[block],0,0);
			display_block[block].endDraw();
			parent.popMatrix();
			}
		}
	public void rotate_block_up(int i, int k){
		if ((rotation_counter[block]>Math.PI/2) & (rotation_counter[block]<3*Math.PI/2)){
			if (rotation_counter[block]>Math.PI){//decrement the rotation counter
				rotation_counter[block]-= (2*Math.PI)/15;
				}
			else {//if it reaches a 180 degree rotation, exchange the images to simulate flipping cards and set rotation to 0
				display_block[block].beginDraw();
				image_block_down[block].beginDraw();
				display_block[block]=image_block_down[block];
				//display_block[block].image(image_block_down[block].get(),0,0);
				image_block_down[block].endDraw();
				display_block[block].endDraw();
				 rotation_counter[block]=0;
				 rotate_up[block]=!rotate_up[block];
				 roll_right[block]=!roll_right[block];
				}
			}
		else{ if(rotation_counter[block]>0){
					rotation_counter[block]-= (2*Math.PI)/15;
					if(rotation_counter[block]<(3*Math.PI/2)+(0.1+2*Math.PI/15)){
						display_block[block].beginDraw();
						image_block_down_flipped[block].beginDraw();
						display_block[block]=image_block_down_flipped[block];
						//display_block[block].image(image_block_down_flipped[block].get(),0,0);
						display_block[block].endDraw();	
						image_block_down_flipped[block].endDraw();
						}		
					}
				}
		if (rotation_counter[block]>0){
			parent.pushMatrix();
			parent.translate(separation*k*w_block,separation*i*h_block);
			parent.translate(w_block/2,h_block/2);//translating to rotate x about centre
			parent.rotateX((float)(2*Math.PI-rotation_counter[block]));
			parent.translate(-w_block/2,-h_block/2);
			display_block[block].beginDraw();
			parent.image(display_block[block],0,0);
			display_block[block].endDraw();
			parent.popMatrix();
		}
		else{
			parent.pushMatrix();
			parent.translate(separation*k*w_block,separation*i*h_block);
			display_block[block].beginDraw();
			parent.image(display_block[block],0,0);
			display_block[block].endDraw();
			parent.popMatrix();
		}	
		
	}
	public void rotate_block_down(int i, int k){
			if ((rotation_counter[block]>Math.PI/2) & (rotation_counter[block]<3*Math.PI/2)){
			if (rotation_counter[block]>Math.PI){//decrement the rotation counter
				rotation_counter[block]-= (2*Math.PI)/15;
				}
			else {//if it reaches a 180 degree rotation, exchange the images to simulate flipping cards and set rotation to 0
				display_block[block].beginDraw();
				image_block_up[block].beginDraw();
				display_block[block]=image_block_up[block];
				//display_block[block].image(image_block_up[block].get(),0,0);
				image_block_up[block].endDraw();
				display_block[block].endDraw();
				 rotation_counter[block]=0;
				 rotate_up[block]=!rotate_up[block];
				 roll_right[block]=!roll_right[block];
				}
			}
		else{ if(rotation_counter[block]>0){
					rotation_counter[block]-= (2*Math.PI)/15;
					if(rotation_counter[block]<(3*Math.PI/2)+(0.05+2*Math.PI/15)){
						display_block[block].beginDraw();
						image_block_up_flipped[block].beginDraw();
						display_block[block]=image_block_up_flipped[block];
						//display_block[block].image(image_block_up_flipped[block].get(),0,0);
						display_block[block].endDraw();
						image_block_up_flipped[block].endDraw();
						}		
					}
				}
			if (rotation_counter[block]>0){
				parent.pushMatrix();
				parent.translate(separation*k*w_block,separation*i*h_block);
				parent.translate(w_block/2,h_block/2);//translating to rotate x about centre
				parent.rotateX(rotation_counter[block]);
				parent.translate(-w_block/2,-h_block/2);
				display_block[block].beginDraw();
				parent.image(display_block[block],0,0);
				display_block[block].endDraw();
				parent.popMatrix();
				}
				else{
					parent.pushMatrix();
					parent.translate(separation*k*w_block,separation*i*h_block);
					display_block[block].beginDraw();
					parent.image(display_block[block],0,0);
					display_block[block].endDraw();
					parent.popMatrix();
					}	
	}
	public void rotate_block(int i, int k){
		if (rotate_up[block]==false){rotate_block_down(i,k);}
		 else{rotate_block_up(i,k);}
	 }
	 public void fftMapToBlock(){
			int x=(int) parent.random(0,n_blocks*n_blocks);						 				
			if(rotate==true && trebleMap>=block_threshold){
			 if (rotation_counter[x]==0){
				 rotation_counter[x]= (float)(2*Math.PI);
			 }
		
			}
			else {if(roll==true && trebleMap>=block_threshold){
				if (roll_counter[x]==0){roll_counter[x]=w_block;}
				
				
						}
					}
				
				}

	 
	 public void oscEvent(OscMessage theOscMessage){
		 float[] fader=new float[10];
			String address = theOscMessage.addrPattern();
			if(address.indexOf("/1/push")!=-1){//only one pushbutton
				//resetting
				moving_average_window=4;
				sensitivity=15;
				num_arcs=8;
				block_threshold=3;
				brightness=128f;
				inner_circle=0;
				circle_x=parent.width/2;
				circle_y=parent.height/2;
				rotation_speed=40;

			}
				
				
			if(address.indexOf("/1/fader")!=-1){
				String list[]  = PApplet.split(address,'/');
				int xFader = Math.round(list[2].charAt(5) - 0x30);
				fader[xFader] = theOscMessage.get(0).floatValue();
				switch (xFader){
				case 1:
					num_arcs =(int)PApplet.map(fader[xFader],0,1,2,15);
				break;
				case 5:
					if(rotate==true){
						rotate=false;roll=true;
						PGraphics tempg=parent.createGraphics(w_block,h_block);
						for (int i=0;i<n_blocks*n_blocks;i++){
						     display_block[i].beginDraw();
						     tempg.beginDraw();
						     tempg.image(display_block[i].get(),0,0);
							 display_block[i]=parent.createGraphics(w_block,h_block);
							 display_block[i].image(tempg.get(),0,0);
							 display_block[i].endDraw();
							 tempg.endDraw();
					  		}
					}
					int roll_many =(int)PApplet.map(fader[xFader],0,1,0,n_blocks*n_blocks);
					for (int i=0;i<roll_many;i++){
						int x=(int)parent.random(0,n_blocks*n_blocks);
						if (roll_counter[x]==0){roll_counter[x]=w_block;}	
					}
				break;
				case 3:
					inner_circle=(int)PApplet.map(fader[xFader],0,1,0,30);
				break;
				case 4:
					if(fader[xFader]>0){
					sensitivity=PApplet.map(fader[xFader],0,1,0.4f,30f);
					}
				break;
				case 2:
					if (fader[xFader]<15){
					moving_average_window= 16-(int)PApplet.map(fader[xFader],0,1,1,14);
					rotation_speed=101- (int)PApplet.map(fader[xFader],0,1,10,100);
					}
				break;
					}
			}
			if(address.indexOf("/1/toggle")!=-1){
				String list[]  = PApplet.split(address,'/');
				int toggle = Math.round(list[2].charAt(6) - 0x30);
				switch (toggle) {
				case 3:
					if (rotate==false){rotate=true; roll=false;}
					else{
					  rotate=false;	roll=true;
					  for (int i=0;i<roll_counter.length;i++){roll_counter[i]=0;}	
					  PGraphics tempg=parent.createGraphics(w_block,h_block);
					  for (int i=0;i<n_blocks*n_blocks;i++){
						     display_block[i].beginDraw();
						     tempg.beginDraw();
						     tempg.image(display_block[i].get(),0,0);
							 display_block[i]=parent.createGraphics(w_block,h_block);
							 display_block[i].image(tempg.get(),0,0);
							 display_block[i].endDraw();
							 tempg.endDraw();
					  		}			
						}
						break;			  
			case 2: 
				parent.noLoop(); 
				int temp_n_blocks;
				 aistemeisixo=true;
				 
				 for (int i=0;i<n_blocks;i++){
						for (int k=0; k<n_blocks;k++){
								block=n_blocks*i+k;
									if (rotate==false){while (roll_counter[block]>0){roll_block(i,k);}}
									else{while (rotation_counter[block]>0){rotate_block(i,k);}}
								}
				 	}
				 
				if (bool_n_blocks==false){
					temp_n_blocks=5;bool_n_blocks=!bool_n_blocks;
					 w_block=Math.round(parent.width/(separation*temp_n_blocks)); //declare block size
					 h_block=Math.round(parent.height/(separation*temp_n_blocks)); 
					 image_block_up=image_block_up15;          
					 image_block_down=image_block_down15;        
					 image_block_up_flipped=image_block_up_flipped15;  
					 image_block_down_flipped=image_block_down_flipped15;
				 }
				
				else{temp_n_blocks=3;bool_n_blocks=!bool_n_blocks;
					 w_block=Math.round(parent.width/(separation*temp_n_blocks)); //declare block size
					 h_block=Math.round(parent.height/(separation*temp_n_blocks));
					 image_block_up=image_block_up3;         
					 image_block_down=image_block_down3;      
					 image_block_up_flipped=image_block_up_flipped3; 
					 image_block_down_flipped=image_block_down_flipped3;
					 }
			 w_block=Math.round(parent.width/(separation*temp_n_blocks)); //declare block size
			 h_block=Math.round(parent.height/(separation*temp_n_blocks)); 
			 for (int i=0;i<temp_n_blocks*temp_n_blocks;i++){			 
				 display_block[i]=parent.createGraphics(w_block,h_block);
				 display_block[i].beginDraw();
				 image_block_down[i].beginDraw();
				 display_block[i].image(image_block_down[i],0,0);
				 image_block_down[i].endDraw();
				 display_block[i].endDraw();
			 }
			 n_blocks=temp_n_blocks;
			 aistemeisixo=false;
			 parent.loop(); 
		 break;
 		
				case 1: 
					for (int i=0;i<n_blocks*n_blocks;i++){
						rotate=true; roll=false;
						rotation_counter[i]=(float)(2*Math.PI);
					}
			break;
		
					}
				}
			if (address.indexOf("/1/xy")!=-1){
							circle_x = (int) PApplet.map(theOscMessage.get(1).floatValue(),0,1,0,parent.width);
							circle_y = (int) PApplet.map(1-theOscMessage.get(0).floatValue(),0,1,0,parent.height);
										}
			if(address.indexOf("/1/rotary")!=-1){
				String list[]  = PApplet.split(address,'/');
				int xRotary = Math.round(list[2].charAt(6) - 0x30);
				fader[xRotary] = theOscMessage.get(0).floatValue();
				if (xRotary==1){starting_theta =PApplet.map(fader[xRotary],0,1,0,(float)(2*Math.PI));}
				if (xRotary==2){brightness= 250-PApplet.map(1-theOscMessage.get(0).floatValue(),0,1,00,250);}
			}
 }
}
