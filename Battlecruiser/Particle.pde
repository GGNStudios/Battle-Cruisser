

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

