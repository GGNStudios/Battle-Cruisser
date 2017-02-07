
import java.util.*;

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