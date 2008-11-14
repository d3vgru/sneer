package sneer.skin.widgets.reactive.impl;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import sneer.pulp.reactive.signalchooser.SignalChooser;
import sneer.skin.widgets.reactive.LabelProvider;
import sneer.skin.widgets.reactive.ListSignalModel;
import sneer.skin.widgets.reactive.ListWidget;
import wheel.reactive.Signal;
import wheel.reactive.lists.ListSignal;

class RListImpl<ELEMENT> extends JList implements ListWidget<ELEMENT> {

	private static final long serialVersionUID = 1L;

	protected final ListSignal<ELEMENT> _source;
	protected LabelProvider<ELEMENT> _labelProvider;

	RListImpl(ListSignal<ELEMENT> source, LabelProvider<ELEMENT> labelProvider, ListCellRenderer cellRenderer ) {
		_source = source;
		_labelProvider = labelProvider;
		initModel();
		
		setCellRenderer(cellRenderer == null
			? new RListSimpleCellRenderer<ELEMENT>(this)
			: cellRenderer
		);
	}

	private void initModel() {
		SignalChooser<ELEMENT> chooser = new SignalChooser<ELEMENT>(){	@Override public Signal<?>[] signalsToReceiveFrom(ELEMENT element) {
			return new Signal<?>[]{_labelProvider.imageFor(element), 
								   	   _labelProvider.labelFor(element)};}};
		ListSignalModel<ELEMENT> model = new ListSignalModelImpl<ELEMENT>(_source, chooser);
		setModel(model);
	}

	@Override
	public JList getMainWidget() {
		return this;
	}

	@Override
	public JComponent getComponent() {
		return this;
	}
}