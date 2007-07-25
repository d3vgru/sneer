package sneer.kernel.gui;

import static wheel.i18n.Language.translate;

import java.net.URL;
import java.util.List;

import sneer.SneerDirectories;
import sneer.kernel.business.BusinessSource;
import sneer.kernel.gui.contacts.ContactAction;
import sneer.kernel.gui.contacts.ShowContactsScreenAction;
import wheel.io.files.Directory;
import wheel.io.files.impl.DurableDirectory;
import wheel.io.ui.JFrameBoundsKeeper;
import wheel.io.ui.TrayIcon;
import wheel.io.ui.User;
import wheel.io.ui.ValueChangePane;
import wheel.io.ui.TrayIcon.Action;
import wheel.io.ui.User.Notification;
import wheel.io.ui.impl.BoundsPersistence;
import wheel.io.ui.impl.DirectoryBoundsPersistence;
import wheel.io.ui.impl.JFrameBoundsKeeperImpl;
import wheel.io.ui.impl.TrayIconImpl;
import wheel.lang.IntegerParser;
import wheel.lang.Omnivore;

public class Gui {


	public Gui(User user, BusinessSource businessSource, List<ContactAction> contactActions, JFrameBoundsKeeperImpl jframeboundsKeeper) throws Exception {
		_user = user;
		_businessSource = businessSource;
		_contactActions = contactActions;
		_jframeBoundsKeeper = jframeboundsKeeper;

		URL icon = Gui.class.getResource("/sneer/kernel/gui/traymenu/yourIconGoesHere.png");
		_trayIcon = new TrayIconImpl(icon, _user.catcher());
		
		tryToRun();
	}

	final User _user;

	private final TrayIcon _trayIcon;

	final BusinessSource _businessSource;

	private final List<ContactAction> _contactActions;

	private ShowContactsScreenAction _showContactsScreenAction;

	private JFrameBoundsKeeper _jframeBoundsKeeper;
	
	private void tryToRun() {
		
		filloutInitialValues();
		bindActionsToTrayIcon();
		
		showContactsScreenAction().run();
	}

	void bindActionsToTrayIcon() {
		ShowContactsScreenAction showContactsScreenAction = showContactsScreenAction();
		_trayIcon.clearActions();
		_trayIcon.setDefaultAction(showContactsScreenAction);
		_trayIcon.addAction(nameChangeAction());
		_trayIcon.addAction(showContactsScreenAction);
		_trayIcon.addAction(sneerPortChangeAction());
		_trayIcon.addAction(languageChangeAction());
		_trayIcon.addAction(exitAction());
	}

	private LanguageChangeAction languageChangeAction() {
		return new LanguageChangeAction(_user, _businessSource.output().language(), _businessSource.languageSetter());
	}

	private synchronized ShowContactsScreenAction showContactsScreenAction() {
		if (_showContactsScreenAction == null)
			_showContactsScreenAction = new ShowContactsScreenAction(_user, _businessSource.output().contactAttributes(), _contactActions, _businessSource.contactAdder(),_businessSource.contactRemover(), _businessSource.contactNickChanger(), _jframeBoundsKeeper);
		return _showContactsScreenAction;
	}

	private void filloutInitialValues() { // Refactor: remove this logic from the gui. Maybe move to Communicator;
		String ownName = _businessSource.output().ownName().currentValue();
		if (ownName == null || ownName.isEmpty())
			nameChangeAction().run();
	}

	private ValueChangePane sneerPortChangeAction() {
		String prompt=translate(
				"Change this only if you know what you are doing.\n" +
				"Sneer TCP port to listen:");
		return new ValueChangePane(translate("Sneer Port"), prompt, _user, _businessSource.output().sneerPort(), new IntegerParser(_businessSource.sneerPortSetter()));
	}

	private Action nameChangeAction() {
		String prompt = translate(
				"What is your name?\n" + 
				"(You can change it any time you like)");
		
		return new ValueChangePane(translate("Own Name"), prompt, _user, _businessSource.output().ownName(), _businessSource.ownNameSetter());
	}
	
	private Action exitAction() {
		return new Action() {

			public String caption() {
				return translate("Exit");
			}

			public void run() {
				System.exit(0);
			}
		};
	}

	public Omnivore<Notification> briefNotifier() {
		return new Omnivore<Notification>() { @Override public void consume(Notification notification) {
			_trayIcon.messageBalloon(notification._title, notification._notification);
		}};
	}
	
}
