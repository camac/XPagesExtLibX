package com.ibm.xsp.extlibx.component.form;


import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.component.xp.XspInputCheckbox;

public class SlaveCheckbox extends XspInputCheckbox  {

	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.SlaveCheckbox";
	public static final String STYLEKIT_FAMILY = "SlaveCheckbox";
	
	private String master;

	public SlaveCheckbox() {
		super();
		setRendererType(RENDERER_TYPE);
	}
	
	public String getMaster() {

		if (this.master != null) {
			return this.master;
		}

		ValueBinding vb = getValueBinding("master");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}
	
	

	@Override
	public String getStyleKitFamily() {
		return STYLEKIT_FAMILY;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	@Override
	public String getCustomHtmlValue() {
		return getCheckedValue();
	}

	@Override
	public boolean useCustomHtmlValue() {
		return true;
	}

	@Override
	public void restoreState(FacesContext context, Object state) {

		Object[] values = (Object[]) state;

		super.restoreState(context, values[0]);
		master = (String) values[1];

	}

	@Override
	public Object saveState(FacesContext context) {

		Object[] values = new Object[2];

		values[0] = super.saveState(context);
		values[1] = master;

		return values;

	}

}
