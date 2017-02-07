import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import saito.objloader.*; 
import de.voidplus.leapmotion.*; 
import ddf.minim.*; 
import saito.objloader.*; 
import de.voidplus.leapmotion.*; 
import saito.objloader.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Battlecruiser extends PApplet {




public final static int PLAY = 0;
public final static int RANKING = 1;
public final static int EXIT = 2;

/* Menu */
boolean mouseOverPlay, mouseOverRanking, mouseOverExit, mouseOverBack;
float mX, mY;
PFont font;
PImage logo;
Hand menuHand;

/* Seleccion nave */
int selectedShip = 1;
long tParticle = 0;
float alpha;
float gir;
boolean ready;
boolean izquierda = false;
boolean derecha = false;
boolean impact = false;
boolean kill = false;
boolean noSound = true;
PImage imgHangar;
PImage imgShield;
PImage hud;
PImage[] bc;
float imNum = 0;

OBJModel battleship1;
OBJModel battleship2;
OBJModel battleship3;
OBJModel battleship4;

private int menuSelect = -1;
private boolean startGame = false;

/* Juego */
String typedText = "Player1";
OBJModel battleship;
OBJModel meteorite;
OBJModel shot1;
PImage texture;
PImage mask;
PImage explosion;
Audio menuMusic;
Audio gameMusic;
Audio menuSound;
Audio shotSound;
Audio startSound;
Audio meteoriteSound;
Audio crashSound;
Audio shieldSound;
Audio bateriesSound;
Audio nuclearSound;
Audio identifySound;
PShader starShader;
PShader menuShader;
LeapMotion leap;
int puntos;
Particle cameraShake;
int charge;

xmlManager xml;

Player spaceShip;
ArrayList<Player> rank = new ArrayList<Player>();;
ArrayList<Shot> shots = new ArrayList<Shot>();
ArrayList aEnemies = new ArrayList();

long time = 0;
long tiempo = 0;
long startTime = 0;
PVector cs;

public void setup() 
{
  //size(1024, 768, P3D);
  size(displayWidth, displayHeight, P3D);
  logo = loadImage("logo.png");
  font = createFont("airstrike.ttf", 70);
  textFont(font);
  
  background(255);
  noStroke(); fill(50);
  frameRate(60);
  ambientLight(200, 200, 200);

  loadModels();
  loadShader();
  loadShaderMenu();
  loadMusic();

  leap = new LeapMotion(this);
  leap.withGestures();
  spaceShip = new Player();
  puntos = 0;

  alpha = PI/4;
  gir = alpha;
  ready = true;
  cs = new PVector(0,0,0);

  xml = new xmlManager();
  xml.load();
  xml.sortRanking();

  imgHangar = loadImage("Hangar.jpg");
  imgShield = loadImage("shield.png");
  hud = loadImage("hud.png");

  bc = new PImage[16];
  
  bc[0] = loadImage("bc1.png");
  bc[1] = loadImage("bc2.png");
  bc[2] = loadImage("bc3.png");
  bc[3] = loadImage("bc4.png");
  bc[4] = loadImage("bc5.png");
  bc[5] = loadImage("bc6.png");
  bc[6] = loadImage("bc7.png");
  bc[7] = loadImage("bc8.png");
  bc[8] = loadImage("bc9.png");
  bc[9] = loadImage("bc10.png");
  bc[10] = loadImage("bc11.png");
  bc[11] = loadImage("bc12.png");
  bc[12] = loadImage("bc13.png");
  bc[13] = loadImage("bc14.png");
  bc[14] = loadImage("bc15.png");
  bc[15] = loadImage("bc16.png");

  //meteorExplode = new Particle();
}

public boolean sketchFullScreen() {
  return true;
}

public void draw() {
  if(impact){
    tParticle = millis();
    impact = false;
  }

 if(500 - (millis() - tParticle) > 0){
    cs.x = random(-40,40)*((500 - ((float)millis() - tParticle))/500);
    cs.y = random(-40,40)*((500 - ((float)millis() - tParticle))/500);
  }

  perspective(PI/4, PApplet.parseFloat(width)/PApplet.parseFloat(height), 1, 12000);

  switch(menuSelect)
  {
    case PLAY:
      if (startGame)
      {
        if(!gameMusic.isOnsound()){
          menuMusic.stop();
          gameMusic.play();
          startSound = new Audio(this,"start.mp2");
          startSound.play();
        }
        if (startGame && !kill)
        {
          drawGame();
          if (spaceShip.lives == 0) {
            kill = true ;
            identifySound = new Audio(this,"identify.mp2");
            identifySound.play();
          }
        }else{
          drawScore();
        }
      }
      else 
      {
        drawSpaceshipSelection();
        resetTimer();
      }
      break ;
    case RANKING:
      xml.load();
      xml.sortRanking();
      drawRanking();
      break ;
    case EXIT: 
        exit();
      break ;
    default :
      drawMenu();
    break;  
  }
  

}

public void drawMenu()
{
  background(0);

  perspective(1, PApplet.parseFloat(width)/PApplet.parseFloat(height), 1, 10000);

  menuShaderParams();
  image(logo, width/2 - 480, 150);

  textSize(70);

  if(mouseOverRanking){
    fill(255, 250*sin(millis()*0.01f), 250*sin(millis()*0.01f));
  }
  else{
    fill(255, 255, 255);
  }
  textAlign(LEFT, CENTER);
  text("Ranking", 150, 550);

  if(mouseOverPlay){
    fill(255, 250*sin(millis()*0.01f), 250*sin(millis()*0.01f));
  }
  else{
    fill(255, 255, 255);
  }
  textAlign(CENTER, CENTER);
  text("Play", width/2, 550);

  if(mouseOverExit){
    fill(255, 250*sin(millis()*0.01f), 250*sin(millis()*0.01f));
  }
  else{
    fill(255, 255, 255);
  }
  textAlign(RIGHT, CENTER);
  text("Exit", width - 150, 550);

  noCursor();
  fill(245, 50, 50);

  if(leap.hasHands())
  {
    menuHand = leap.getHands().get(0);
    Finger pointer = menuHand.getFrontFinger();

    comprova(pointer.getPosition().x, pointer.getPosition().y);
    fill(255);
    camera(width/2 + (pointer.getPosition().x - width/2)*0.2f, height/2 + (pointer.getPosition().y - height/2)*0.4f, (height/2) / tan(PI/6), width/2 + cs.x, height/2 + cs.y, 0, 0, 1, 0);
    ellipse(pointer.getPosition().x, pointer.getPosition().y, 10, 10);
  }

}

public void drawSpaceshipSelection() 
{
  background(150);

  fill(150);

  noStroke();
  hint(DISABLE_DEPTH_TEST);
  textureMode(NORMAL);
  beginShape();
    texture(imgHangar);
    vertex(0, 0, 0, 0);
    vertex(width, 0, 1, 0);
    vertex(width ,height, 1, 1);
    vertex(0, height, 0, 1);
  endShape();
  hint(ENABLE_DEPTH_TEST);
  
  translate(width/2, height/2+10, 150);
  rotateX(PI/2);
  rotateX(-PI/10);
  rotateZ(gir);
  pushMatrix(); // nave 3
    translate(-200, -200, 20);
    scale(0.2f);
    //rotateY(PI/2);
    rotateX(-PI/2);
    rotateY(millis()*0.002f);
    battleship3.draw();
  popMatrix();
  pushMatrix(); // nave 2
    translate(+200, -200, 20);
    scale(0.2f);
    //rotateY(PI/2);
    rotateX(-PI/2);
    rotateY(millis()*0.002f);
    battleship2.draw();
  popMatrix();
  pushMatrix(); // nave 4
    translate(-200, +200, 20);
    scale(0.2f);
    //rotateY(PI/2);
    rotateX(-PI/2);
    rotateY(millis()*0.002f);
    battleship4.draw();
  popMatrix();
  pushMatrix(); // nave 1
    translate(+200, +200, 20);
    scale(0.2f);
    //rotateY(PI/2);
    rotateX(-PI/2);
    rotateY(millis()*0.002f);
    battleship1.draw();
  popMatrix();

  if ( gir < alpha && derecha)
  {
    gir += 0.1f;
  }
  else if (gir > alpha && izquierda) {
    gir -= 0.1f;
  }
  else {
    izquierda = false;
    derecha = false;
    ready = true;
  }

  if(leap.hasHands())
  {
    menuHand = leap.getHands().get(0);
    Finger pointer = menuHand.getFrontFinger();

    fill(255, 255, 150);
    ellipse(pointer.getPosition().x, pointer.getPosition().y, 10, 10);
  }
}

public void drawRanking()
{
  background(0);
  perspective(1, PApplet.parseFloat(width)/PApplet.parseFloat(height), 1, 10000);
  menuShaderParams();
  
  fill(255);
  textSize(120);
  textAlign(CENTER, CENTER);
  text("Ranking", width/2, 100);

  textSize(50);
  int numRank = rank.size() < 5 ? rank.size():5;

  for (int i = 0; i < numRank; i++)
  {
    if (i == 0) 
    {
      textSize(60);
      fill(255, 0, 0);
    }
    else
    {
      textSize(50);
      fill(255);
    }

    text(rank.get(i).name + " ---> " + rank.get(i).points, width/2, 130 + 70*(i+1));
  }

  if(leap.hasHands())
  {
    menuHand = leap.getHands().get(0);
    Finger pointer = menuHand.getFrontFinger();

    fill(255, 255, 150);
    ellipse(pointer.getPosition().x, pointer.getPosition().y, 10, 10);
    float x = pointer.getPosition().x;
    float y = pointer.getPosition().y;

    camera(width/2 + (pointer.getPosition().x - width/2)*0.2f, height/2 + (pointer.getPosition().y - height/2)*0.4f, (height/2) / tan(PI/6), width/2 + cs.x, height/2 + cs.y, 0, 0, 1, 0);

    mouseOverBack = false;

    if(y > height - 500 && y < height) mouseOverBack = true;
  }

  fill(255);
  textSize(100);
  textAlign(CENTER, CENTER);
  if(mouseOverBack){
    fill(255, 250*sin(millis()*0.01f), 250*sin(millis()*0.01f));
  }
  text("Back to main menu", width/2, height - 200);

}

public void drawGame()
{

  leap.withoutGestures();

  background(255);
  lights();

  shaderParams();

  if (millis() - time > 1000 - (millis() - startTime)*0.005f) {

    Enemy e = new Enemy(this);

    if ((short)random(1,5) == 1)
    {
      e.powerUp = new PowerUp();
    }

    aEnemies.add(e);
    time= millis();
  }

  if (millis() - tiempo > 100) {
    puntos += 3;
    tiempo = millis();
  }  

  if(leap.hasHands())
  {
    
    spaceShip.shipHand = leap.getHands().get(0);
    spaceShip.position =  spaceShip.shipHand.getPosition();
    spaceShip.playerPitch();

    camera(width/2 + (spaceShip.shipHand.getPosition().x - width/2)*-0.1f, height/2 + (spaceShip.shipHand.getPosition().y - height/2)*-0.2f, (height/2) / tan(PI/6), width/2 + cs.x, height/2 + cs.y, 0, 0, 1, 0);

    if (spaceShip.isShooting())
    {
      if (!spaceShip.isGestureActive)
      {
        shots.add(new Shot(spaceShip.position,1));
        shotSound = new Audio(this,"shot.mp2");
        shotSound.play();
        spaceShip.isGestureActive = true;
        puntos -= 10;
      }
    }
    else {spaceShip.isGestureActive = false;}
    
    if(leap.countHands() > 1)
    {

      spaceShip.assistHand = leap.getHands().get(1);

      if (spaceShip.isArmorActive() && spaceShip.escudo.life > 0)
      {

        if(charge == 2000){
          charge = 0;
          nuclearSound = new Audio(this,"nuclear.mp3");
          nuclearSound.play();
          killAllMeteorites();
        }
        spaceShip.escudo.life -= 50;

        pushMatrix();
          translate(spaceShip.position.x, spaceShip.position.y, spaceShip.position.z - 30);
          hint(DISABLE_DEPTH_TEST);
          fill(255, 255, 255, 70);
          sphere(150);
          hint(ENABLE_DEPTH_TEST);
        popMatrix();
      }

      if (spaceShip.strafeLeft())
      {
        textSize(40);
        fill(255, 255, 255);
        text("STRAFE LEFT !", 650, 200, -30);
      }

      if (spaceShip.strafeRight())
      {
        textSize(40);
        fill(255, 255, 255);
        text("STRAFE RIGHT !", 650, 200, -30);
      }

      if (spaceShip.isGrabbing())
      {
        if (!spaceShip.isGestureActive)
        {
          spaceShip.isAssistGestureActive = true;
        }
      } else {spaceShip.isAssistGestureActive = false;}
    }           
  }

  imNum += 0.2f;
  if(charge<2000){
    charge++;
    noSound = true;
  }else if(charge == 2000 && noSound){
    bateriesSound = new Audio(this,"charge.mp2");
    bateriesSound.play();
    noSound = false;
  }
  paintShots();
  paintMeteorites();
  moveShip();
  paintHUD();
}

public void drawScore (){
  background(0);
    
    perspective(1, PApplet.parseFloat(width)/PApplet.parseFloat(height), 1, 10000);
    
    menuShaderParams();
    pushMatrix();
      fill(255);
      textAlign(CENTER);
      textSize(120);
      text ("GAME OVER", width/2, height/2);
      textSize(40);
      text ("SCORE: " + puntos + "\nYour name down please:", width/2, (height/2) + 100);
      
      nameInput();
      
    popMatrix();
}

public void nameInput(){
  textFont(font,18);
  textSize(60);
  text(typedText+(frameCount/10 % 2 == 0 ? "_" : ""), width/2, (height/2) + 250);
}
 
public void keyReleased() {
  if (key != CODED) {
    switch(key) {
      case BACKSPACE:
        typedText = typedText.substring(0,max(0,typedText.length()-1));
      break;
      case TAB:
        typedText += " ";
      break;
      case ENTER:
      case RETURN:
        xml.addPlayer(puntos,typedText);
        resetAssets();
      break;
      case ESC:
      case DELETE:
      break;
      default:
        typedText += key;
    }
  }
}

public void resetAssets(){
  typedText = "Player1";
  menuSelect = -1;
  leap.withGestures();
  startGame = false;
  spaceShip.lives = 3;
  kill = false;
  puntos = 0;
  charge = 0;
  spaceShip.escudo.life = 0;
}

public void resetTimer(){
  startTime = millis();
}

public void loadModels()
{
  battleship1 = new OBJModel(this, "battle.obj", "absolute", TRIANGLES);
  battleship1.enableDebug();
  battleship1.scale(1);
  battleship1.translateToCenter();

  battleship1.enableTexture();
  noStroke();

  battleship2 = new OBJModel(this, "battle2.obj", "absolute", TRIANGLES);
  battleship2.enableDebug();
  battleship2.scale(1);
  battleship2.translateToCenter();

  battleship2.enableTexture();
  noStroke();

  battleship3 = new OBJModel(this, "battle3.obj", "absolute", TRIANGLES);
  battleship3.enableDebug();
  battleship3.scale(1);
  battleship3.translateToCenter();

  battleship3.enableTexture();
  noStroke();

  battleship4 = new OBJModel(this, "battle4.obj", "absolute", TRIANGLES);
  battleship4.enableDebug();
  battleship4.scale(1);
  battleship4.translateToCenter();

  battleship4.enableTexture();
  noStroke();

  meteorite = new OBJModel(this, "meteor.obj", "absolute", TRIANGLES);
  meteorite.enableDebug();
  meteorite.scale(1);
  meteorite.translateToCenter();

  meteorite.enableTexture();
  noStroke();
  
  shot1 = new OBJModel(this, "shot2.obj", "absolute", TRIANGLES);
  shot1.enableDebug();
  shot1.scale(1);
  shot1.translateToCenter();

  shot1.enableTexture();
  noStroke();
  
  texture = loadImage("fireball1.jpg");
  mask = loadImage("fireball1m.jpg");
  texture.mask(mask);
  explosion = loadImage("explosion_opaque.png");
}

public void loadShader()
{
  starShader = loadShader("star_shader.glsl");
  starShader.set("resolution", PApplet.parseFloat(width), PApplet.parseFloat(height));
}

public void loadShaderMenu()
{
  menuShader = loadShader("shader_menu.glsl");
  menuShader.set("resolution", PApplet.parseFloat(width*2), PApplet.parseFloat(height*2));
}

public void loadMusic()
{
  gameMusic = new Audio(this,"game.MP3");
  menuSound = new Audio(this,"bco.mp2");
  menuSound.play();
  menuMusic = new Audio(this,"menu.mp3");
  menuMusic.play();
  shotSound = new Audio(this,"shot.mp2");
  bateriesSound = new Audio(this,"charge.mp2");
  nuclearSound = new Audio(this,"nuclear.mp3");
}

public void shaderParams()
{
  pushMatrix();
    starShader.bind();
    starShader.set("time", (millis() - startTime)/(1000.0f - millis()*0.002f));
    starShader.set("X", width/2);
    starShader.set("Y", height/2);
    starShader.set("R", ((millis() - startTime)*0.00001f));
    starShader.set("G", 0.5f);
    starShader.set("B", abs(cos((millis() - startTime)*0.00002f)));
  
    shader(starShader);
    translate(-width*5, -height*5, -9000);
    rect(0, 0, width*12, height*12);
    starShader.unbind();
    resetShader();
  popMatrix();
}

public void menuShaderParams()
{
  pushMatrix();
   menuShader.bind();
       menuShader.set("time", (millis() - startTime)/(1000.0f - millis()*0.001f));
       menuShader.set("X", width/2);
       menuShader.set("Y", height/2);
       menuShader.set("R", 0);
       menuShader.set("G", 0);
       menuShader.set("B", 0);
  
    shader(menuShader);
    translate(-width*5, -height*5, -900);
    rect(0, 0, width*12, height*12);
    menuShader.unbind();
    resetShader();
  popMatrix();
}

public void paintMeteorites()
{
  for (int nMeteoritos=0; nMeteoritos < aEnemies.size(); nMeteoritos++) 
  {
    Enemy e = (Enemy) aEnemies.get(nMeteoritos);
    e.render(meteorite);
    e.moveEnemy();
    if (e.getZPos() > 0) 
    {
      aEnemies.remove(nMeteoritos);
    }
    if(checkMeteoritesCollision(e))
    {
      aEnemies.remove(nMeteoritos);
      meteoriteSound = new Audio(this,"meteorite.mp2");
      meteoriteSound.play();
    }

    if (e.getZPos() > spaceShip.position.z - 1000 && e.getZPos() < spaceShip.position.z)
    {
      if(spaceShip.checkColision(e) && !spaceShip.isArmorActive())
      {
        spaceShip.lives--;
        impact = true;
        crashSound = new Audio(this,"crash.mp2");
        crashSound.play();
        aEnemies.remove(nMeteoritos);
      }
    }
  }
}

public void killAllMeteorites(){
  for (int nMeteoritos=0; nMeteoritos < aEnemies.size(); nMeteoritos++) 
  aEnemies.remove(nMeteoritos);
}

public void paintHUD(){
  pushMatrix();
    translate(0, 0, -500);
    hint(DISABLE_DEPTH_TEST);
    imageMode(CENTER);
    image(hud,width/2,height/2,width,height);
    imageMode(CORNER);
    fill(255,0,0,150);
    stroke(255,0,0,150);
    hint(ENABLE_DEPTH_TEST);
    translate(0, 0, -10);
    rect(175*(PApplet.parseFloat(width)/1920),830*(PApplet.parseFloat(height)/1080),490*(PApplet.parseFloat(width)/1920)*(PApplet.parseFloat(spaceShip.escudo.life)/10000),70*(PApplet.parseFloat(height)/1080),30);
    rect(635*(PApplet.parseFloat(width)/1920),945*(PApplet.parseFloat(height)/1080),-490*(PApplet.parseFloat(width)/1920)*(PApplet.parseFloat(charge)/2000),70*(PApplet.parseFloat(height)/1080),30);
    imageMode(CORNER);
    hint(DISABLE_DEPTH_TEST);
    if(imNum>=16){
      imNum = 0;
    }
    for(int j = 0; j < spaceShip.lives ; j++){
      image(bc[PApplet.parseInt(imNum)],1510*(PApplet.parseFloat(width)/1920)+(j*120),820*(PApplet.parseFloat(height)/1080),100,100);
    }
    textSize(100);
    textAlign(CORNER, CORNER);
    text(puntos, 1450*(PApplet.parseFloat(width)/1920), 1000*(PApplet.parseFloat(height)/1080), 0);
    hint(ENABLE_DEPTH_TEST);
    imageMode(CORNER);
  popMatrix();
}

public void moveShip()
{
  pushMatrix();
    translate(width/2, height/2, 0);
    translate(spaceShip.position.x*2-width, spaceShip.position.y*1.5f-height/1.5f, -1000);
    rotateY(PI);
    rotateZ(spaceShip.shipHand.getPitch()/180*PI*-1);
    battleship.draw();
  popMatrix();
}

private void paintShots()
{
  for(int i=0; i< shots.size(); i++)
  {
    Shot s = shots.get(i);
    s.update();
    s.render();
    
    if(abs(s.getZPosition()) > 10000)
    {
      shots.remove(i);
    }
    
  }
}

public void comprova(float x, float y)
{
 
  mouseOverPlay = mouseOverRanking = mouseOverExit = mouseOverBack = false;

  if(x > 100 && x < width/2 - 150) mouseOverRanking = true;
  if(x > width/2 - 150 && x < width/2 + 150) mouseOverPlay = true;
  if(x > width/2 + 150 && x < width - 50) mouseOverExit = true;
}

// SWIPE GESTURE
public void leapOnSwipeGesture(SwipeGesture g, int state)
{
  int       id               = g.getId();
  Finger    finger           = g.getFinger();
  PVector   position         = g.getPosition();
  PVector   position_start   = g.getStartPosition();
  PVector   direction        = g.getDirection();
  float     speed            = g.getSpeed();
  long      duration         = g.getDuration();
  float     duration_seconds = g.getDurationInSeconds();

  switch(state){
    case 1: // Start
      break;
    case 2: // Update
      break;
    case 3: // Stop
      if (position_start.x < 0 && ready)
      {
        //alpha -= PI/2;
        //izquierda = true;
        //ready = false;
      } else if (ready) {
        alpha += PI/2;
        derecha = true;
        ready = false;

        if (selectedShip == 5)
        {
          selectedShip = 0;
        } 
        selectedShip += 1;
      }

      break;
  }
}

// SCREEN TAP GESTURE
public void leapOnScreenTapGesture(ScreenTapGesture g)
{

  if (menuSelect == PLAY && !startGame)
  {
    switch (selectedShip) {
      case 1 :
        battleship = battleship1;
      break;  
      case 2 :
        battleship = battleship2;
      break;  
      case 3 :
        battleship = battleship3;
      break;  
      case 4 :
        battleship = battleship4;
      break;  
    }
    startGame = true;
  }

  if(mouseOverPlay)
  {
    menuSelect = PLAY;
  }

  if (mouseOverExit)
    menuSelect = EXIT;

  if (mouseOverRanking)
    menuSelect = RANKING;

  if (mouseOverBack)
  {
    menuSelect = -1;
  }
}

private boolean checkMeteoritesCollision(Enemy e)
{
  for(int i = 0; i < shots.size() ; i++)
  {
    Shot s = shots.get(i);
    PVector posEnemy = e.getPosition();
    PVector posShot = s.getPosition();
    if(abs(posShot.z - posEnemy.z) < 200)
    {
      int d = calculaDistancia(posEnemy, posShot);
      if(d < 200)
      {

        if (e.powerUp != null)
        {
          if (e.powerUp.type == 1){
            spaceShip.escudo.life = 10000;
            shieldSound = new Audio(this,"shield.mp2");
            shieldSound.play();
          }
        }
        shots.remove(i);
        puntos += 15;
        return true;
      }
    }
  }
  return false;
}

private int calculaDistancia(PVector a, PVector b)
{
  float d = sqrt(pow( a.x - b.x , 2 ) + pow( a.y - b.y , 2 ));
  return (int)d;
}


public class Audio {
  
  private AudioPlayer player;
  private Minim minim;
  private boolean onSound;

  public Audio(Battlecruiser b, String file) {
    minim = new Minim(b);
    player = minim.loadFile(file, 2048);
    onSound = false;
  }

  private void play() {
    if (player != null) {
      player.play();
      onSound = true;
    }
    else {
      print("no se pudo cargar el audio");
    }
  }
  
  private void stop() {
    if (player != null) {
      minim.stop();
      onSound = false;
    }
    else {
      print("no se pudo cargar el audio");
    }
  }

  private boolean isOnsound(){
    return onSound;
  }
  
}



public class Enemy {

  //Declaracion de atributos
  PVector pos;
  PVector vel;
  PVector rot;
  int timeGenP = 50;
  int timeReset = 0;
  ArrayList aParticles = new ArrayList();
  PowerUp powerUp;

  public Enemy(Battlecruiser b) {
    pos = randStart();
    vel = new PVector (0, 0, 50 + random(0, 50) + (millis() - startTime) * 0.001f);
    rot = new PVector (random(-2.0f, 2.0f), random(-2.0f, 2.0f), random(-2.0f, 2.0f));
    aParticles.add(new Particle(pos));
  }

  private PVector randStart() {    
    return new PVector(random(displayWidth/10, displayWidth - displayWidth/10), random(displayHeight/10, displayHeight - displayHeight/10), -9000);
  }

  private void moveEnemy() {
    pos.x += vel.x;
    pos.y += vel.y;
    pos.z += vel.z;
  }

  private void render(OBJModel meteorite) {
    
    //Particles!
    for (int nParticles = aParticles.size() -1; nParticles >= 0 ; nParticles--) {
      Particle p = (Particle) aParticles.get(nParticles);
      p.render();
  
      if (p.age() > p.time + p.maxTimeP) {
        aParticles.remove(nParticles);
      }
    }
    if (millis() - timeReset > timeGenP) {
      aParticles.add(new Particle(pos));
      timeReset = millis();
    }
    
    
    pushMatrix();
    translate(pos.x, pos.y, pos.z);
    rotateX(rot.x*millis()*0.01f);
    rotateY(rot.y*millis()*0.01f);
    meteorite.draw();
    popMatrix();
    
  }

  private float getZPos() {
    return pos.z;
  }
  
  private PVector getPosition(){
    return pos;
  }
  
}



public class Particle {

  public  PVector pos = new PVector(0, 0, 0);
  public  PVector rot = new PVector (0,0,random(-10,10));
  public  int time = millis();
  public  int maxTimeP = 300;
  public  int decimas;
  public  float maxw = 5;
  public  float maxh = 5;
  public  int actualw = 0;
  public  int actualh = 0;

  public Particle(PVector p) {
    pos.x = p.x + random(-20,20);
    pos.y = p.y + random(-20,20);
    pos.z = p.z;
    time = millis();
  }

  public Particle() {
    pos.x = 0;
    pos.y = 0;
    pos.z = 0;
    time = millis();
  }

  private void selectSprite(int w, int h, PImage t) {
    pushMatrix();
    noStroke();
    beginShape();
    blendMode(SCREEN);
    hint(DISABLE_DEPTH_TEST);
    texture(t);
    vertex(0, 0, 0, 0);
    vertex(100, 0, 127, 0);
    vertex(100, 100, 127, 127);
    vertex(0, 100, 0, 127);
    endShape();
    hint(ENABLE_DEPTH_TEST);
    blendMode(BLEND);
    popMatrix();
  }

  private void spriteSelection(PImage t, long decimas) {
    pushMatrix();
    translate(pos.x - 200, pos.y - 200, pos.z-10);

    pushMatrix();
    noStroke();
    textureMode(NORMAL);
    beginShape();
    blendMode(SCREEN);
    hint(DISABLE_DEPTH_TEST);
    texture(t);
    vertex(0,0,(actualw/maxw),(actualh/maxh));
    vertex(0,400,(actualw/maxw),(actualh/maxh)+(1/maxh));
    vertex(400,400,(actualw/maxw)+(1/maxw),(actualh/maxh)+(1/maxh));
    vertex(400,0,(actualw/maxw)+(1/maxw),(actualh/maxh));
    endShape();
    hint(ENABLE_DEPTH_TEST);
    blendMode(BLEND);
    popMatrix();
    actualw++;
    if(actualw == maxw){
      actualw = 0;
      actualh++;
    }
    if(actualh == maxh){
      actualh = 0;
    }
    decimas = millis();
    
    popMatrix();
  }

  private boolean finalAnimacion(){
    return ((actualw+actualh) == 0);
  }

  private long age() {
    return millis();
  }

  private void render() {
    pushMatrix();
    translate(pos.x - 60, pos.y - 60, pos.z-10);
    selectSprite(0, 0, texture);
    popMatrix();
  }
}



public class Player implements Comparable<Player> {

  PVector position;
  PVector[] coords;
  Hand shipHand = null;
  Hand assistHand = null;

  boolean isGestureActive = false;
  boolean isAssistGestureActive = false;

  int lives;
  float aShip;

  String name;
  int points;

  PowerUp escudo;

  public Player()
  {
    position = new PVector(0, 0, 0);
    coords = new PVector[4];
    coords[0] = new PVector();
    coords[1] = new PVector();
    coords[2] = new PVector();
    coords[3] = new PVector();
    coords[0].z = 30.0f*PI/180;
    coords[1].z = 330.0f*PI/180;
    coords[2].z = 210.0f*PI/180;
    coords[3].z = 150.0f*PI/180;
    lives = 3;
    escudo = new PowerUp((short)1);
    aShip = 0;
  }

  public Player(int p, String n){
    name = n;
    points = p;
  }

  public void playerPitch(){
    aShip = leap.getHands().get(0).getPitch();
  }

  public boolean isShooting()
  {
    return pinchGesture(shipHand);
  }

  public boolean isGrabbing()
  {
    return pinchGesture(assistHand);
  }

  public boolean isArmorActive()
  {
    if (shipHand != null && assistHand != null) {
      float angleShip = shipHand.getRoll();
      float angleAssist = assistHand.getRoll();

      if (angleShip > 35 && angleAssist > 35)
        return true;
      else 
        return false;
    }
    else {
      return false;
    }
  }

  public boolean strafeLeft()
  {
    float angleShip = shipHand.getPitch();
    float angleAssist = assistHand.getPitch();

    if (angleShip < -55 && angleAssist < -55) {
      position.x = 100;
      return true;
    }
    else 
      return false;
  }

  public boolean strafeRight()
  {
    float angleShip = shipHand.getPitch();
    float angleAssist = assistHand.getPitch();

    if (angleShip > 55 && angleAssist > 55) {
      position.x = displayWidth - 100;
      return true;
    }
    else 
      return false;
  }

  private boolean isHandOpen(Hand hand)
  {
    if (hand.countFingers() > 3)
      return true;
    else 
      return false;
  }

  /*
   * Controla si se realiza el gesto Pinch
   * retorna true si se ha realizado
   */
  private boolean pinchGesture(Hand hand)
  {
    Finger frontFinger = hand.getFrontFinger();
    Finger leftFinger = hand.getLeftFinger();

    float d = frontFinger.getPosition().x - leftFinger.getPosition().x;

    if (d == 0 && !hand.hasFingers() )
    {
      return true;
    }
    else 
    {
      return false;
    }
  }

  private boolean twoFinger(Hand hand) {

    for (Finger finger : hand.getFingers()) {
    }

    for (int i = 0; i < hand.countFingers(); ++i) {
    }

    return false;
  }

  public boolean checkColision(Enemy e)
  {

    PVector[] vectors = new PVector[4];
    PVector target = new PVector();
    PVector rot= new PVector(0, 0, 0);
    float norma = 0;
    boolean ok[] = new boolean[4];

    vectors[0] = new PVector();
    vectors[1] = new PVector();
    vectors[2] = new PVector();
    vectors[3] = new PVector();

    for (int i = 0; i<4 ; i++) 
    {
      coords[i].x = cos(coords[i].z + aShip/180*PI);
      coords[i].y = sin(coords[i].z + aShip/180*PI);
      coords[i].x *= 100;
      coords[i].y *= 100;

      coords[i].x += position.x;
      coords[i].y += position.y;
    }
    println(position.x + " - " + position.y + " - " + aShip);

    vectors[0].x = coords[0].x - coords[3].x;
    vectors[0].y = coords[0].y - coords[3].y;
    line(coords[0].x, coords[0].y, coords[3].x, coords[3].y);
    target.x = e.pos.x - coords[3].x;
    target.y = e.pos.y - coords[3].y;
    norma = sqrt(pow(vectors[0].x, 2) + pow(vectors[0].y, 2));
    vectors[0].x = vectors[0].x/norma;
    vectors[0].y = vectors[0].y/norma;
    norma = sqrt(pow(target.x, 2) + pow(target.y, 2));
    target.x = target.x/norma;
    target.y = target.y/norma;
    vectors[0].z = (( (vectors[0].x * (target.x) + (vectors[0].y * (target.y)) ) / ( sqrt(pow(vectors[0].x, 2) + pow(vectors[0].y, 2)) + sqrt(pow((target.x), 2) + pow((target.y), 2)))));

    if (acos(vectors[0].z) *(180/PI) <= 90 && acos(vectors[0].z) *(180/PI) >= 0)
    {
      ok[0] = true;
    } 
    else
    {
      ok[0] = false;
    }

    for (int i = 1 ; i < 4 ; i++) 
    {
      vectors[i].x = coords[i].x - coords[i-1].x;
      vectors[i].y = coords[i].y - coords[i-1].y;
      line(coords[i].x, coords[i].y, coords[i-1].x, coords[i-1].y);
      target.x = e.pos.x - coords[i-1].x;
      target.y = e.pos.y - coords[i-1].y;
      norma = sqrt(pow(vectors[i].x, 2) + pow(vectors[i].y, 2));
      vectors[i].x = vectors[i].x/norma;
      vectors[i].y = vectors[i].y/norma;
      norma = sqrt(pow(target.x, 2) + pow(target.y, 2));
      target.x = target.x/norma;
      target.y = target.y/norma;
      vectors[i].z = (( (vectors[i].x * (target.x) + (vectors[i].y * (target.y)) ) / ( sqrt(pow(vectors[i].x, 2) + pow(vectors[i].y, 2)) + sqrt(pow((target.x), 2) + pow((target.y), 2)))));

      if (acos(vectors[i].z) *(180/PI) <= 90 && acos(vectors[i].z) *(180/PI) >= 0) {
        ok[i] = true;
      }
      else {
        ok[i] = false;
      }
    }

    if (ok[0] == true && ok[1] == true && ok[2] == true && ok[3] == true) {
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public int compareTo(Player p) 
  {
    return p.points - this.points;
  }
}


public class PowerUp{
  
  private short type;
  private int life;
  
  /**
  *Esta funci\u00f3n est\u00e1 pensada para ser el constructor para los meteoritos
  */
  public PowerUp(){
    type = (short)random(1,2);
    life = 10000;
  }
  
  /*
  *Esta funci\u00f3n est\u00e1 pensada para ser el constructor del Player
  *
  */
  public PowerUp(short type){
    this.type = type;
    life = 0;
  }
  
}


public class Shot{
  
  private PVector pos;
  private PVector dir;
  private int type;
  private boolean isTargeting;
  private Enemy e;
  
  public Shot(PVector position, int t){
    pos = position;
    pos.z = -1400;
    type = t;
    if(type == 1){
      dir = new PVector(0,0,-1);
    }
  }
  
  private void update(){
    if(isTargeting){
      PVector ePos = e.getPosition();
      
    }else{
      if(type == 1){
        pos.z += dir.z*100;
        //print(pos.z + "\n");
      }
    }
  }
  
  private void render(){
    pushMatrix();
    translate(pos.x, pos.y, pos.z);
    scale(0.8f);
    shot1.draw();
    popMatrix();
  }
  
  private float getZPosition(){
    return (int)pos.z;
  }
  
  private PVector getPosition(){
    return pos;
  }
  
}



public class xmlManager{

	XML xml;

	xmlManager(){
		xml = loadXML("ranking.xml");
	}

	public void load()
	{
		rank = new ArrayList<Player>();

		XML[] children = xml.getChildren("player");
		if(children.length > 0)
		{
			for (int i = 0; i < children.length; i++) 
			{
				int p = children[i].getInt("points");
				String n = children[i].getString("name");
				rank.add(new Player(p,n));
			}
		}
	}

	public void addPlayer(int p, String n)
	{
	  XML last = xml.addChild("player");
	  last.setString("name",n);
	  last.setInt("points",p);
	  saveXML(xml,"./data/ranking.xml");
	}

	public void sortRanking()
	{
		Collections.sort(rank);
	}
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Battlecruiser" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
