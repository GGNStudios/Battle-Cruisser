import de.voidplus.leapmotion.*;

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
    coords[0].z = 30.0*PI/180;
    coords[1].z = 330.0*PI/180;
    coords[2].z = 210.0*PI/180;
    coords[3].z = 150.0*PI/180;
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

