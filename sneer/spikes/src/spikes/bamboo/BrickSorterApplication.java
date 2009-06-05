package spikes.bamboo;

import static sneer.commons.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.Sneer;

import sneer.brickness.Brickness;
import sneer.commons.environments.CachingEnvironment;
import sneer.commons.environments.Environments;
import spikes.bamboo.bricksorter.BrickSorter;

public class BrickSorterApplication {

	public static void main(String[] args) {
		
		Environments.runWith(new CachingEnvironment(Brickness.newBrickContainer()), new Runnable() { public void run() {
			try {
				List<Class<?>> originalBricks = new ArrayList<Class<?>>();
				for (Class<?> brick : bricksToSortAccordingToDependencies())
					originalBricks.add(brick);
				
				for (Class<?> brick : my(BrickSorter.class).sort(originalBricks.toArray(new Class<?>[]{})))
					if (originalBricks.contains(brick))
						System.out.println(brick.getName() + ".class,");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}});
	}

	private static Class<?>[] bricksToSortAccordingToDependencies() {
		return Sneer.platformBricks();
	}

}
