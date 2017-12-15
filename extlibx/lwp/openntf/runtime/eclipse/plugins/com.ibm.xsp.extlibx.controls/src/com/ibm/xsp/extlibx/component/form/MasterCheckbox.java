package com.ibm.xsp.extlibx.component.form;


import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.component.xp.XspSelectManyCheckbox;

public class MasterCheckbox extends XspSelectManyCheckbox {

	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.MasterCheckbox";

	private String parentSelectorClick;
	private String parentSelectorStyleClass;
	private String parentStyleClassChecked;
	private String parentStyleClassUnchecked;

	public MasterCheckbox() {
		setRendererType(RENDERER_TYPE);
	}

	public String getParentSelectorClick() {

		if (this.parentSelectorClick != null) {
			return this.parentSelectorClick;
		}

		ValueBinding vb = getValueBinding("parentSelectorClick");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setParentSelectorClick(String parentSelectorClick) {
		this.parentSelectorClick = parentSelectorClick;
	}

	public String getParentSelectorStyleClass() {

		if (this.parentSelectorStyleClass != null) {
			return this.parentSelectorStyleClass;
		}

		ValueBinding vb = getValueBinding("parentSelectorStyleClass");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setParentSelectorStyleClass(String parentSelectorStyleClass) {
		this.parentSelectorStyleClass = parentSelectorStyleClass;
	}

	public String getParentStyleClassChecked() {

		if (this.parentStyleClassChecked != null) {
			return this.parentStyleClassChecked;
		}

		ValueBinding vb = getValueBinding("parentStyleClassChecked");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setParentStyleClassChecked(String parentStyleClassChecked) {
		this.parentStyleClassChecked = parentStyleClassChecked;
	}

	public String getParentStyleClassUnchecked() {

		if (this.parentStyleClassUnchecked != null) {
			return this.parentStyleClassUnchecked;
		}

		ValueBinding vb = getValueBinding("parentStyleClassUnchecked");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setParentStyleClassUnchecked(String parentStyleClassUnchecked) {
		this.parentStyleClassUnchecked = parentStyleClassUnchecked;
	}

	@Override
	public void validate(FacesContext arg0) {
		try {
			super.validate(arg0);
		} catch (Exception e) {

		}
	}

	@Override
	protected void validateValue(FacesContext arg0, Object arg1) {
		/*
		 * We are Avoiding Validation because we are not validating against a
		 * list of known values (as in the normal group checkbox) The values are
		 * provided by the checkedValue of the Slave Checkboxes, and we are
		 * happy for those to be anything that they want
		 */
		try {
			// super.validateValue(arg0, arg1);
		} catch (Exception e) {

		}
	}

	@Override
	public void restoreState(FacesContext context, Object state) {

		Object[] values = (Object[]) state;

		super.restoreState(context, values[0]);

		parentSelectorClick = (String) values[1];
		parentSelectorStyleClass = (String) values[2];
		parentStyleClassChecked = (String) values[3];
		parentStyleClassUnchecked = (String) values[4];

	}

	@Override
	public Object saveState(FacesContext context) {

		Object[] values = new Object[5];

		values[0] = super.saveState(context);
		values[1] = parentSelectorClick;
		values[2] = parentSelectorStyleClass;
		values[3] = parentStyleClassChecked;
		values[4] = parentStyleClassUnchecked;

		return values;

	}

}
