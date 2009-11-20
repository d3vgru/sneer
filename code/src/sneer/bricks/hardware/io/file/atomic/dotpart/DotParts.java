package sneer.bricks.hardware.io.file.atomic.dotpart;

import java.io.File;
import java.io.IOException;

import sneer.foundation.brickness.Brick;

@Brick
public interface DotParts {

	File openDotPartFor(File actualFile) throws IOException;

	void closeDotPart(File dotPartFile, long lastModified) throws IOException;

}
