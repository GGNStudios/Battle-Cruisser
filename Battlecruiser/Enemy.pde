import saito.objloader.*;

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
    vel = new PVector (0, 0, 50 + random(0, 50) + (millis() - startTime) * 0.001);
    rot = new PVector (random(-2.0, 2.0), random(-2.0, 2.0), random(-2.0, 2.0));
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
    rotateX(rot.x*millis()*0.01);
    rotateY(rot.y*millis()*0.01);
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

