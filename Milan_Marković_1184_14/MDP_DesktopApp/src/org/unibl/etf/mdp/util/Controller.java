package org.unibl.etf.mdp.util;

import org.unibl.etf.mdp.model.DataModel;

public class Controller {
	private DataModel model;

	public void initModel(DataModel model) {
		if (this.model != null) {
			throw new IllegalStateException("Model is not initialized");
		}
		this.model = model;
	}
}
