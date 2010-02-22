package sneer.bricks.pulp.keymanager;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.exceptions.Refusal;

@Brick(Prevalent.class)
public interface ContactSeals {

	Seal ownSeal();

	void put(String contactNickname, Seal seal) throws Refusal;

	Signal<Seal> sealGiven(Contact contact);
	Contact contactGiven(Seal peersSeal);
}
